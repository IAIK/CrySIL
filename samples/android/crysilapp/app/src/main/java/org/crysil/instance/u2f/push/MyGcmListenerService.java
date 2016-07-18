package org.crysil.instance.u2f.push;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.common.base.Strings;

import org.crysil.commons.Interlink;
import org.crysil.communications.websocket.WebSocketReceiver;
import org.crysil.instance.CrySILElementFactory;

import java.net.URI;
import java.net.URISyntaxException;

import org.crysil.instance.u2f.database.DatabaseHandler;
import org.crysil.instance.u2f.database.webservice.WebserviceEntry;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = MyGcmListenerService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String uri = data.getString("uri");
        Log.d(TAG, String.format("Got from '%s', uri '%s'", from, uri));
        if (!Strings.isNullOrEmpty(uri)) {
            handleCrysilMessage(uri);
        }
    }

    private void handleCrysilMessage(String uriString) {
        Interlink receiver = CrySILElementFactory.getReceiver();
        if (!(receiver instanceof WebSocketReceiver)) {
            Log.e(TAG, "WebSocketReceiver from CrySIL instance is null", new NullPointerException());
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
        ((WebSocketReceiver) receiver).connect(uriString, alias);
    }
}