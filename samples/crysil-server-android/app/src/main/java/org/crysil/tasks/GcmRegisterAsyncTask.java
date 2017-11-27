package org.crysil.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import org.crysil.push.PushHelper;

/**
 * Registers the application with GCM servers asynchronously.
 * <p/>
 * Stores the registration ID and app versionCode in the application's
 * shared preferences.
 *
 * @see PushHelper
 */
public class GcmRegisterAsyncTask extends AsyncTask<String, Void, String> {

    private static final String SENDER_ID = "138782634208";
    private static final String TAG = GcmRegisterAsyncTask.class.getSimpleName();

    private final GoogleCloudMessaging gcm;

    public GcmRegisterAsyncTask(GoogleCloudMessaging gcm) {
        this.gcm = gcm;
    }

    @Override
    protected String doInBackground(String... existingRegId) {
        String regId;
        try {
            if (gcm == null) {
                Log.e(TAG, "GCM object not usable");
                return null;
            }
            if (existingRegId[0].isEmpty()) {
                regId = gcm.register(SENDER_ID);
                Log.i(TAG, String.format("Device registered, registration ID='%s'", regId));
                PushHelper.storeRegistrationId(regId);
            } else {
                Log.d(TAG, String.format("Device is already registered with ID='%s'", existingRegId[0]));
                regId = existingRegId[0];
            }
        } catch (IOException ex) {
            Log.e(TAG, "IOException on registering for GCM", ex);
            regId = null;
        }
        return regId;
    }

    @Override
    protected void onPostExecute(String regId) {
        if (regId != null && regId.length() > 16) {
            regId = regId.substring(0, 16) + "...";
        }
        Log.d(TAG, String.format("Got GCM id '%s'", regId));
    }
}
