package org.crysil;

import android.app.Activity;
import android.os.Bundle;

import org.crysil.authentication.auth_android.ui.AndroidConfirmationNotificationHandler;
import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;

/**
 * Gets called from the {@link org.crysil.authentication.auth_android.ui.AndroidConfirmationNotificationHandler} after the user selects
 * <code>accept</code> or <code>reject</code> in the notification.
 */
public class NotificationBarAcceptActivity extends Activity {

    public static final String CLICK = "click";
    public static final String ACCEPT = "accept";
    public static final String REJECT = "reject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String click = (String) getIntent().getExtras().get(CLICK);
        AndroidConfirmationNotificationHandler bar = CurrentActivityTracker.getNotificationHandler();

        if (ACCEPT.equals(click)) {
            bar.confirm(true);
        } else {
            bar.confirm(false);
        }

        finish();
    }
}
