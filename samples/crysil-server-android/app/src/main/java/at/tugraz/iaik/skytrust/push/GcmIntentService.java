package at.tugraz.iaik.skytrust.push;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.URI;
import java.net.URISyntaxException;

import at.iaik.skytrust.CrySILElementFactory;
import org.crysil.communications.websocket.WebSocketReceiver;
import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntry;

/**
 * Gets executed when a token from the GCM push services is received from {@link at.tugraz.iaik.skytrust.push.GcmBroadcastReceiver}.
 * Connects the {@link org.crysil.communications.websocket.WebSocketReceiver} to the webservice where a request for this server is waiting.
 */
public class GcmIntentService extends IntentService {

    private static String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String uri = extras.getString("uri");
                Log.i(TAG, String.format("Received uri '%s'", uri));
                handleSkyTrustMessage(uri);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void handleSkyTrustMessage(String uriString) {
        WebSocketReceiver webSocketReceiver = (WebSocketReceiver) CrySILElementFactory.getReceiver();
        if (webSocketReceiver == null) {
            Log.e(TAG, "WebSocketReceiver from SkyTrust is null", new NullPointerException());
            return;
        }
        String alias = null;
        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            Log.e(TAG, String.format("Can't parse URI: '%s'", uriString), e);
            return;
        }
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Cursor cursor = databaseHandler.getWebserviceInfoForHostname(uri.getHost());
        if (cursor.moveToFirst()) {
            alias = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_ALIAS));
        } else {
            cursor = databaseHandler.getWebserviceInfoForIP(uri.getHost());
            if (cursor.moveToFirst()) {
                alias = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_ALIAS));
            }
        }
        databaseHandler.close();
        if (alias == null) {
            Log.e(TAG, String.format("No key alias found for uri '%s'", uriString));
            return;
        }
        Log.d(TAG, String.format("Connecting for uri '%s'", uriString));
        Log.d(TAG, String.format("Using key alias '%s'", alias));
        webSocketReceiver.connect(uriString, alias);
    }
}
