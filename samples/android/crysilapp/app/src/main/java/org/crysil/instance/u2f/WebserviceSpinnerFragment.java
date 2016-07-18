package org.crysil.instance.u2f;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import org.crysil.instance.u2f.database.DatabaseHandler;
import org.crysil.instance.u2f.database.webservice.WebserviceEntry;
import org.crysil.instance.u2f.database.webservice.WebserviceEntryStatus;
import org.crysil.instance.u2f.push.PushHelper;
import org.crysil.instance.u2f.tasks.WebserviceManagementAction;
import org.crysil.instance.u2f.tasks.WebserviceManagementAsyncTask;
import org.crysil.instance.u2f.utils.WebserviceListAdapter;

/**
 * Handles management of webservices: Showing spinner and management buttons
 */
public class WebserviceSpinnerFragment extends Fragment {

    public static final String SELECTED_WEBSERVICE = "selectedWebservice";

    private static final String TAG = WebserviceSpinnerFragment.class.getSimpleName();

    private TextView tvMessage;
    private SimpleCursorAdapter webserviceCursorAdapter;
    private Spinner spWebservice;
    private TextView tvWebservice;
    private int selectedWebservice = 0;
    private WebsocketCertificateCallback certificateCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedWebservice = savedInstanceState.getInt(SELECTED_WEBSERVICE, 0);
        }
        certificateCallback = new WebsocketCertificateCallback(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webservices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spWebservice = (Spinner) view.findViewById(R.id.spWebservice);
        tvWebservice = (TextView) view.findViewById(R.id.tvWebservice);
        tvMessage = (TextView) view.findViewById(R.id.tvWebserviceMessage);
        view.findViewById(R.id.btPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeWebserviceManagementAction(WebserviceManagementAction.PAUSE);
            }
        });
        view.findViewById(R.id.btRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeWebserviceManagementAction(WebserviceManagementAction.REGISTER);
            }
        });
        view.findViewById(R.id.btUnregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeWebserviceManagementAction(WebserviceManagementAction.UNREGISTER);
            }
        });
        view.findViewById(R.id.btResume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeWebserviceManagementAction(WebserviceManagementAction.RESUME);
            }
        });
        view.findViewById(R.id.bt_main_manage_webservices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), WebserviceListActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupWebserviceAdapter();
        if (spWebservice.getItemAtPosition(selectedWebservice) != null) {
            spWebservice.setSelection(selectedWebservice);
        }
        DatabaseHandler databaseHandler = new DatabaseHandler(this.getActivity());
        showMessage(webserviceCursorAdapter.isEmpty() ? getString(R.string.warn_webservices_empty) : "");
        databaseHandler.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(SELECTED_WEBSERVICE, selectedWebservice);
        }
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
                    getView().findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.btUnregister).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.btPause).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
                    break;
                case PAUSED:
                    getView().findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.btUnregister).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.btResume).setVisibility(View.VISIBLE);
                    break;
                case UNKNOWN:
                    getView().findViewById(R.id.btRegister).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.btUnregister).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
                    break;
            }
            webserviceCursorAdapter.getCursor().moveToPosition(oldPos);
        } else {
            getView().findViewById(R.id.btRegister).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.btUnregister).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.btPause).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.btResume).setVisibility(View.INVISIBLE);
        }
    }

    private void setupWebserviceAdapter() {
        if (webserviceCursorAdapter == null || spWebservice.getAdapter() == null) {
            DatabaseHandler databaseHandler = new DatabaseHandler(this.getActivity());
            webserviceCursorAdapter = new WebserviceListAdapter(this.getActivity(),
                    databaseHandler.getWebserviceCursor());
            spWebservice.setAdapter(webserviceCursorAdapter);
            databaseHandler.close();
            spWebservice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WebserviceSpinnerFragment.this.selectedWebservice = position;
                    WebserviceSpinnerFragment.this.updateManagementButtonVisibility();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    WebserviceSpinnerFragment.this.selectedWebservice = 0;
                }
            });
        } else {
            DatabaseHandler databaseHandler = new DatabaseHandler(this.getActivity());
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

    private void executeWebserviceManagementAction(final WebserviceManagementAction action) {
        Log.d(TAG, "Starting management action on webservice ...");
        getActivity().findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                String result = null;
                String gcmRegId = PushHelper.getRegistrationId();
                try {
                    if (gcmRegId != null) {
                        WebserviceManagementAsyncTask webserviceTask = new WebserviceManagementAsyncTask(
                                spWebservice.getSelectedItemId(), action, certificateCallback);
                        webserviceTask.execute(gcmRegId);
                        result = webserviceTask.get(60, TimeUnit.SECONDS);
                        showMessage("");
                    } else {
                        showMessage(getString(R.string.error_gcm_id));
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error on webservice management task", e);
                    result = null;
                }
                final boolean success = result != null;
                Activity activity = WebserviceSpinnerFragment.this.getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupWebserviceAdapter();
                            getActivity().findViewById(R.id.ctrlActivityIndicatorView).setVisibility(View.INVISIBLE);
                            updateManagementButtonVisibility();
                            showMessage(success ? "" : getString(R.string.warn_webservices_error));
                        }
                    });
                }
            }
        }.start();
    }

    private void showMessage(final String text) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessage.setText(text);
                }
            });
        }
    }
}
