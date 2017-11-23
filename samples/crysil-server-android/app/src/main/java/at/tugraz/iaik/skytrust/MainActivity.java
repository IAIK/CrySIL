package at.tugraz.iaik.skytrust;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.security.ProviderInstaller;

import java.security.Security;
import java.util.concurrent.TimeUnit;

import at.iaik.skytrust.CrySILElementFactory;
import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntry;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntryStatus;
import at.tugraz.iaik.skytrust.push.PushHelper;
import at.tugraz.iaik.skytrust.tasks.GcmRegisterAsyncTask;
import at.tugraz.iaik.skytrust.tasks.WebserviceManagementAction;
import at.tugraz.iaik.skytrust.tasks.WebserviceManagementAsyncTask;
import at.tugraz.iaik.skytrust.utils.ApplicationContextProvider;
import at.tugraz.iaik.skytrust.utils.KeyStoreHandler;
import at.tugraz.iaik.skytrust.utils.WebserviceListAdapter;

/**
 * Shows the spinner for webservices we are registered on, and buttons for managing keys, accounts and webservices
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String SELECTED_WEBSERVICE = "selectedWebservice";
    public static final String GCM_REGID = "gcmRegId";

    private GcmRegisterAsyncTask gcmRegisterAsyncTask;
    private SimpleCursorAdapter webserviceCursorAdapter;
    private Spinner spWebservice;
    private TextView tvWebservice;
    private TextView tvMessage;
    private int selectedWebservice = 0;
    private String gcmRegId;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spWebservice = (Spinner) findViewById(R.id.spWebservice);
        tvWebservice = (TextView) findViewById(R.id.tvWebservice);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        if (!PushHelper.checkPlayServices(this)) {
            Log.e(TAG, getString(R.string.error_gcm));
            Intent intent = new Intent(ApplicationContextProvider.getAppContext(), ErrorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ErrorActivity.MSG, getString(R.string.error_gcm));
            startActivity(intent);
            finish();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                // Insert IAIK at the last position to fix TLS connections (WebSocket, Gatekeeper)
                //Security.removeProvider(IAIK.getInstance().getName());
                //Security.insertProviderAt(IAIK.getInstance(), Security.getProviders().length + 1);
                // Start SkyTrust afterwards, else it will insert IAIK as the first security provider
                CrySILElementFactory.initialize(KeyStoreHandler.getInstance().getKeyStore(),
                        KeyStoreHandler.getInstance().getKeyStore());
            }
        }).start();

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        // May start the wizard activity first
        if (databaseHandler.getKeyCount() == 0) {
            this.startActivity(new Intent(this, WizardActivity.class));
            finish();
        }
        databaseHandler.close();

        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (savedInstanceState != null) {
            selectedWebservice = savedInstanceState.getInt(SELECTED_WEBSERVICE, 0);
            gcmRegId = savedInstanceState.getString(GCM_REGID);
        }

        // Fix TLS by installing latest SSLProvider from Google Play Services
        ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstalled() {
            }

            @Override
            public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                GooglePlayServicesUtil.showErrorNotification(errorCode, MainActivity.this);
            }
        });
    }

    private void setupWebserviceAdapter() {
        if (webserviceCursorAdapter == null || spWebservice.getAdapter() == null) {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            webserviceCursorAdapter = new WebserviceListAdapter(this, databaseHandler.getWebserviceCursor());
            spWebservice.setAdapter(webserviceCursorAdapter);
            databaseHandler.close();
            spWebservice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.this.selectedWebservice = position;
                    MainActivity.this.updateManagementButtonVisibility();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    MainActivity.this.selectedWebservice = 0;
                }
            });
        } else {
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            webserviceCursorAdapter.swapCursor(databaseHandler.getWebserviceCursor());
            databaseHandler.close();
        }
        if (webserviceCursorAdapter.isEmpty()) {
            spWebservice.setVisibility(View.INVISIBLE);
            tvWebservice.setVisibility(View.INVISIBLE);
            updateManagementButtonVisibility();
        } else {
            spWebservice.setVisibility(View.VISIBLE);
            tvWebservice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupWebserviceAdapter();
        if (spWebservice.getItemAtPosition(selectedWebservice) != null) {
            spWebservice.setSelection(selectedWebservice);
        }

        if (gcmRegId == null && PushHelper.checkPlayServices(this)) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String existingRegId = PushHelper.getRegistrationId();
            gcmRegisterAsyncTask = new GcmRegisterAsyncTask(gcm);
            gcmRegisterAsyncTask.execute(existingRegId);
            new Thread() {
                @Override
                public void run() {
                    try {
                        gcmRegId = gcmRegisterAsyncTask.get();
                    } catch (Exception e) {
                        Log.e(TAG, "Error on getting gcm ID", e);
                        gcmRegId = null;
                    }
                }
            }.start();
        }

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        tvMessage.setText("");
        if (databaseHandler.getKeyCount() == 0) {
            tvMessage.append(getString(R.string.warn_keys_empty));
        }
        int countGoogle = databaseHandler.getAccountGoogleCount();
        int countUsername = databaseHandler.getAccountUsernameCount();
        if (countGoogle == 0 && countUsername == 0) {
            tvMessage.append(getString(R.string.warn_accounts_empty));
        }
        if (webserviceCursorAdapter.isEmpty()) {
            tvMessage.append(getString(R.string.warn_webservices_empty));
        }
        databaseHandler.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(SELECTED_WEBSERVICE, selectedWebservice);
            outState.putString(GCM_REGID, gcmRegId);
        }
    }

    private void executeWebserviceManagementAction(final WebserviceManagementAction action) {
        Log.d(TAG, "Starting management action on webservice ...");
        findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                String result = null;
                try {
                    if (gcmRegId == null && gcmRegisterAsyncTask != null) {
                        gcmRegId = gcmRegisterAsyncTask.get();
                    }
                    if (gcmRegId != null) {
                        WebserviceManagementAsyncTask webserviceTask = new WebserviceManagementAsyncTask(
                                spWebservice.getSelectedItemId(), action);
                        webserviceTask.execute(gcmRegId);
                        result = webserviceTask.get(30, TimeUnit.SECONDS);
                    } else {
                        tvMessage.append("No GCM id known, can't continue\n");
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error on webservice management task", e);
                    result = null;
                }
                final boolean success = result != null;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupWebserviceAdapter();
                        findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.INVISIBLE);
                        updateManagementButtonVisibility();
                        if (!success) {
                            tvMessage.append(getString(R.string.warn_webservices_error));
                        }
                    }
                });
            }
        }.start();
    }

    private void updateManagementButtonVisibility() {
        if (webserviceCursorAdapter.getCursor().getCount() > 0) {
            int oldPos = webserviceCursorAdapter.getCursor().getPosition();
            webserviceCursorAdapter.getCursor().moveToPosition(selectedWebservice);
            WebserviceEntryStatus status = WebserviceEntryStatus.UNKNOWN;
            int columnIndex = webserviceCursorAdapter.getCursor().getColumnIndex(WebserviceEntry.COLUMN_NAME_STATUS);
            if (columnIndex > -1) {
                try {
                    status = WebserviceEntryStatus.valueOf(webserviceCursorAdapter.getCursor().getString(columnIndex));
                } catch (Exception ex) {
                    status = WebserviceEntryStatus.UNKNOWN;
                }
            }
            switch (status) {
                case ACTIVE:
                    findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btUnregister).setVisibility(View.VISIBLE);
                    findViewById(R.id.btPause).setVisibility(View.VISIBLE);
                    findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
                    break;
                case PAUSED:
                    findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btUnregister).setVisibility(View.VISIBLE);
                    findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btResume).setVisibility(View.VISIBLE);
                    break;
                case UNKNOWN:
                    findViewById(R.id.btRegister).setVisibility(View.VISIBLE);
                    findViewById(R.id.btUnregister).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
                    break;
            }
            webserviceCursorAdapter.getCursor().moveToPosition(oldPos);
        } else {
            findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
            findViewById(R.id.btUnregister).setVisibility(View.INVISIBLE);
            findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
            findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
        }
    }

    public void btRegisterOnClick(View view) {
        executeWebserviceManagementAction(WebserviceManagementAction.REGISTER);
    }

    public void btUnregisterOnClick(View view) {
        executeWebserviceManagementAction(WebserviceManagementAction.UNREGISTER);
    }

    public void btPauseOnClick(View view) {
        executeWebserviceManagementAction(WebserviceManagementAction.PAUSE);
    }

    public void btResumeOnClick(View view) {
        executeWebserviceManagementAction(WebserviceManagementAction.RESUME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_wizard_rerun:
                this.startActivity(new Intent(this, WizardActivity.class));
                finish();
                break;
            case R.id.action_export_certificate:
                startActivity(new Intent(this, WizardExportActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btManageAccountsOnClick(View view) {
        this.startActivity(new Intent(this, AccountListActivity.class));
    }

    public void btManageKeysOnClick(View view) {
        this.startActivity(new Intent(this, KeyListActivity.class));
    }

    public void btManageWebservicesOnClick(View view) {
        this.startActivity(new Intent(this, WebserviceListActivity.class));
    }
}
