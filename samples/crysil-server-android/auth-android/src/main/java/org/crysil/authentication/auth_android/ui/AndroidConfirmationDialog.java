package org.crysil.authentication.auth_android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.crysil.authentication.auth_android.R;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.authentication.ui.ActionPerformedCallback;

import java.util.Map;

/**
 * Shows a dialog to the user, whether she wants to accept an incoming SkyTrust request or not.
 * <p/>
 * If the app is paused, a {@link AndroidConfirmationNotificationHandler}
 * is used instead.
 */
public class AndroidConfirmationDialog implements IAuthUI<String, Void> {

    private static final String TAG = AndroidConfirmationDialog.class.getSimpleName();

    private ActionPerformedCallback callback;
    private AlertDialog dialog;
    private String confirmed;
    private Activity activity;

    public AndroidConfirmationDialog(final Activity activity) {
        this.activity = activity;
    }

    public void confirm(String confirmed) {
        this.confirmed = confirmed;
        callback.actionPerformed();
    }

    @Override
    public void init(Map<String, Void> map) {

    }

    @Override
    public ActionPerformedCallback getCallbackAuthenticate() {
        return callback;
    }

    @Override
    public void setCallbackAuthenticate(ActionPerformedCallback actionPerformedCallback) {
        this.callback = actionPerformedCallback;
    }

    @Override
    public void present() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.app_name))
                        .setMessage(activity.getString(R.string.notification_text))
                        .setPositiveButton(activity.getString(R.string.notification_accept),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirm("allow");
                            }
                        }).setNegativeButton(activity.getString(R.string.notification_reject),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                confirm("reject");
                            }
                        })
                        .create();
                try {
                    dialog.show();
                } catch (Exception e) {
                    Log.e(TAG, "Can't show dialog, maybe the activity isn't running anymore", e);
                    confirm("reject");
                }
            }
        });
    }


    @Override
    public void dismiss() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public String getAuthValue() {
        return confirmed;
    }

}
