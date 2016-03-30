package org.crysil.instance.u2f.utils;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Provides the application context to all classes (not just activities)
 */
public class ApplicationContextProvider extends MultiDexApplication {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationContextProvider.context = getApplicationContext();
    }

    public static void setAppContext(Context context) {
        ApplicationContextProvider.context = context;
    }

    public static Context getAppContext() {
        return context;
    }
}