package org.crysil.authentication.auth_android.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.crysil.authentication.auth_android.R;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthUI;

import java.util.Map;

/**
 * Displays a notification in the Android system, so that the user can accept an incoming SkyTrust request.
 *
 */
public class AndroidConfirmationNotificationHandler implements IAuthUI<String, Void> {

    public static int NOTIFACTION_ID = 1;
    private ActionPerformedCallback callback = null;
    private boolean confirmed;
    private NotificationManager notificationManager;
    private Activity activity;

    public AndroidConfirmationNotificationHandler(Activity activity) {
        this.activity = activity;
        this.notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void confirm(boolean confirmed) {
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
    public void dismiss() {
        notificationManager.cancel(NOTIFACTION_ID);
    }

    @Override
    public String getAuthValue() {
        return "allow";
    }

    @Override
    public void present() {
        Intent acceptIntent = new Intent(activity, NotificationBarAcceptActivity.class);
        acceptIntent.putExtra(NotificationBarAcceptActivity.CLICK, NotificationBarAcceptActivity.ACCEPT);
        PendingIntent pAcceptIndent = PendingIntent.getActivity(activity, 0, acceptIntent, 0);
        Intent rejectIntent = new Intent(activity, NotificationBarAcceptActivity.class);
        rejectIntent.putExtra(NotificationBarAcceptActivity.CLICK, NotificationBarAcceptActivity.REJECT);
        PendingIntent pRejectIndent = PendingIntent.getActivity(activity, 0, rejectIntent, 0);

        Notification n = new Notification.Builder(activity)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(activity.getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_accept_light, activity.getString(R.string.notification_accept), pAcceptIndent)
                .addAction(R.drawable.ic_cancel_light, activity.getString(R.string.notification_reject), pRejectIndent)
                .setAutoCancel(false)
                .build();

        notificationManager.notify(NOTIFACTION_ID, n);
    }
}
