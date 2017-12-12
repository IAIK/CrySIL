package org.crysil.authentication.auth_android.ui;

import android.app.Activity;
import android.util.Log;


/**
 * Tracks the current activity, to check whether the application is running to display a notification
 *
 */
public class CurrentActivityTracker {

    private static Activity currentActivity;
    private static AndroidConfirmationNotificationHandler currentNotificationHandler;

    public static Activity getActivity() {
        return currentActivity;
    }

    public static AndroidConfirmationNotificationHandler getNotificationHandler() {
        return currentNotificationHandler;
    }

    public synchronized static void onActivityResume(Activity activity) {
        Log.d("ActivityTracker", "onActivityResume()");
        currentActivity = activity;
        if (currentNotificationHandler == null) {
            currentNotificationHandler = new AndroidConfirmationNotificationHandler(activity);
        }
    }

    public synchronized static void onActivityStop(Activity activity) {
        Log.d("ActivityTracker", "onActivityStop()");
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    public synchronized static boolean inBackground() {
        return currentActivity == null;
    }
}
