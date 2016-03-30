package org.crysil.instance.u2f;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.crysil.instance.u2f.database.DatabaseHandler;
import org.crysil.instance.u2f.database.webservice.WebserviceEntry;
import org.crysil.instance.u2f.database.webservice.WebserviceEntryStatus;
import org.crysil.instance.u2f.push.PushHelper;
import org.crysil.instance.u2f.tasks.ResolveWebServiceAsyncTask;
import org.crysil.instance.u2f.tasks.WebserviceManagementAction;
import org.crysil.instance.u2f.tasks.WebserviceManagementAsyncTask;
import org.crysil.instance.u2f.utils.KeyStoreHandler;
import org.crysil.instance.u2f.utils.WebserviceListAdapter;

/**
 * Displays a list of all known webservices (WebVPN)
 */
public class WebserviceListActivity extends AbstractActivity {

    private static final String TAG = WebserviceListActivity.class.getSimpleName();

    private ListView listView;
    private WebserviceListAdapter listAdapter;
    private AlertDialog createDialog;
    private ActionMode mActionMode;
    private WebsocketCertificateCallback certificateCallback;

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            listView.setItemChecked(position, true);
            mActionMode = startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_webservice_list_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    if (listAdapter == null) {
                        return false;
                    }
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            onDeleteWebservice(mode, listView.getCheckedItemPosition());
                            return true;
                        case R.id.action_edit:
                            onEditWebservice(mode, listView.getCheckedItemPosition());
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mActionMode = null;
                    listView.setItemChecked(listView.getCheckedItemPosition(), false);
                }
            });
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice_list);
        listView = (ListView) findViewById(R.id.lvWebservice);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode == null) {
                    listView.setItemChecked(position, false);
                }
            }
        });
        refreshListview();
        certificateCallback = new WebsocketCertificateCallback(this);
    }

    private void refreshListview() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Cursor cursor = databaseHandler.getWebserviceCursor();
        if (listAdapter == null) {
            listAdapter = new WebserviceListAdapter(this, cursor);
            listView.setAdapter(listAdapter);
        } else {
            listAdapter.changeCursor(cursor);
        }
        databaseHandler.close();
    }

    private void onEditWebservice(final ActionMode mode, final int selectedPosition) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_webservice_new, null);
        final TextView tvMessage = (TextView) dialogView.findViewById(R.id.edDialogWebserviceMessage);
        final EditText edTitle = (EditText) dialogView.findViewById(R.id.edDialogWebserviceTitle);
        final EditText edAddress = (EditText) dialogView.findViewById(R.id.edDialogWebserviceAddress);
        Cursor cursor = (Cursor) listAdapter.getItem(selectedPosition);
        final Long webserviceId = cursor.getLong(cursor.getColumnIndex(WebserviceEntry._ID));
        String webserviceTitle = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_TITLE));
        String webserviceAddress = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_HOSTNAME));

        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(R.string.webservicelist_edit_confirm);
        edTitle.setText(webserviceTitle);
        edAddress.setText(webserviceAddress);

        new AlertDialog.Builder(this).setTitle(R.string.dialog_webservice_edit_title).setView(
                dialogView).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String title = edTitle.getText().toString();
                String address = edAddress.getText().toString();
                if (!address.isEmpty() && !title.isEmpty()) {
                    DatabaseHandler databaseHandler = new DatabaseHandler(WebserviceListActivity.this);
                    databaseHandler.updateWebserviceInfo(webserviceId, title, address);
                    databaseHandler.close();
                    refreshListview();
                } else {
                    Toast.makeText(WebserviceListActivity.this,
                            getResources().getString(R.string.webservicelist_create_error), Toast.LENGTH_SHORT).show();
                }
                refreshListview();
                mode.finish();
            }
        }).setNegativeButton(android.R.string.no, null).create().show();
    }

    private void onDeleteWebservice(final ActionMode mode, final int selectedPosition) {
        new AlertDialog.Builder(this).setMessage(R.string.webservicelist_delete_confirm).setPositiveButton(
                android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.VISIBLE);
                        mode.finish();
                        new Thread() {
                            @Override
                            public void run() {
                                Cursor cursor = (Cursor) listAdapter.getItem(selectedPosition);
                                Long webserviceId = cursor.getLong(cursor.getColumnIndex(WebserviceEntry._ID));
                                String certAlias = cursor.getString(
                                        cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_ALIAS));
                                WebserviceEntryStatus status = WebserviceEntryStatus.UNKNOWN;
                                int columnIndex = cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_STATUS);
                                if (columnIndex > -1) {
                                    try {
                                        status = WebserviceEntryStatus.valueOf(cursor.getString(columnIndex));
                                    } catch (Exception ex) {
                                        status = WebserviceEntryStatus.UNKNOWN;
                                    }
                                }
                                if (status != WebserviceEntryStatus.UNKNOWN) {
                                    boolean success;
                                    try {
                                        WebserviceManagementAsyncTask webserviceTask = new WebserviceManagementAsyncTask(
                                                webserviceId, WebserviceManagementAction.UNREGISTER,
                                                certificateCallback);
                                        webserviceTask.execute(PushHelper.getRegistrationId());
                                        String result = webserviceTask.get(30, TimeUnit.SECONDS);
                                        success = result != null;
                                    } catch (Exception ex) {
                                        success = false;
                                        Log.e(TAG, "Unregistering from deleted webservice failed", ex);
                                    }
                                    if (!success) {
                                        WebserviceListActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        R.string.webservice_delete_error, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                                try {
                                    if (certAlias != null) {
                                        KeyStoreHandler.getInstance().deleteKey(certAlias);
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "Deleting certificate from deleted webservice failed", ex);
                                }
                                DatabaseHandler databaseHandler = new DatabaseHandler(WebserviceListActivity.this);
                                databaseHandler.deleteWebserviceInfo(webserviceId);
                                databaseHandler.close();
                                WebserviceListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshListview();
                                        findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }.start();
                    }
                }).setNegativeButton(android.R.string.no, null).create().show();
    }

    public void btAddWebserviceOnClick(View view) {
        if (createDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_webservice_new, null);
            final TextView tvMessage = (TextView) dialogView.findViewById(R.id.edDialogWebserviceMessage);
            final EditText edTitle = (EditText) dialogView.findViewById(R.id.edDialogWebserviceTitle);
            final EditText edAddress = (EditText) dialogView.findViewById(R.id.edDialogWebserviceAddress);
            tvMessage.setVisibility(View.INVISIBLE);
            builder.setTitle(R.string.dialog_webservice_title).setView(dialogView).setPositiveButton(
                    android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String title = edTitle.getText().toString();
                            String address = edAddress.getText().toString();
                            if (!address.isEmpty() && !title.isEmpty()) {
                                ResolveWebServiceAsyncTask resolveWebServiceAsyncTask = new ResolveWebServiceAsyncTask();
                                resolveWebServiceAsyncTask.execute(title, address);
                                WebserviceEntry entry = null;
                                try {
                                    entry = resolveWebServiceAsyncTask.get(30, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Could not resolve webservice address", e);
                                } catch (ExecutionException e) {
                                    Log.e(TAG, "Could not resolve webservice address", e);
                                } catch (TimeoutException e) {
                                    Log.e(TAG, "Could not resolve webservice address", e);
                                }

                                if (entry != null) {
                                    DatabaseHandler databaseHandler = new DatabaseHandler(WebserviceListActivity.this);
                                    databaseHandler.insertWebserviceInfo(entry);
                                    databaseHandler.close();
                                    refreshListview();
                                }
                            } else {
                                Toast.makeText(WebserviceListActivity.this, R.string.webservicelist_create_error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(android.R.string.no, null);
            createDialog = builder.create();
        }
        createDialog.show();
    }
}
