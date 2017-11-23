package at.tugraz.iaik.skytrust.tasks;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.communications.websocket.ForwardingWebSocket;
import org.crysil.communications.websocket.JsonUtils;
import org.crysil.communications.websocket.SimpleMessage;
import org.crysil.communications.websocket.WebSocketListener;
import org.crysil.communications.websocket.WebsocketKeyManagerFactory;
import org.crysil.communications.websocket.WebsocketTrustManagerFactory;
import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntry;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntryStatus;
import at.tugraz.iaik.skytrust.utils.ApplicationContextProvider;
import at.tugraz.iaik.skytrust.utils.CertificateUtils;
import at.tugraz.iaik.skytrust.utils.KeyStoreHandler;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;
import iaik.pkcs.pkcs10.CertificateRequest;
import iaik.x509.X509Certificate;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Registers the application with the web service asynchronously
 */
public class WebserviceManagementAsyncTask extends AsyncTask<String, Void, String> implements WebSocketListener {

    private static final String TAG = WebserviceManagementAsyncTask.class.getSimpleName();

    private static final String PARAM_GCM = "gcm";
    private static final String MSG_CSR = "csr";
    private static final String MSG_CERT = "cert";
    private static final String MSG_REGISTER = "register";
    private static final String MSG_UNREGISTER = "unregister";
    private static final String MSG_PAUSE = "pause";
    private static final String MSG_RESUME = "resume";

    private final String websocketUrl;
    private final WebserviceManagementAction action;
    private final long webserviceId;
    private final Cursor cursor;
    private final String hostname;
    private String port;
    private String gcmDeviceId;
    private String skytrustId;
    private String certAlias;
    private ReentrantLock lock;
    private Condition condition;
    private String result;

    public WebserviceManagementAsyncTask(long webserviceId, WebserviceManagementAction action) {
        this.webserviceId = webserviceId;
        this.action = action;
        DatabaseHandler databaseHandler = new DatabaseHandler(ApplicationContextProvider.getAppContext());
        cursor = databaseHandler.getWebserviceInfo(webserviceId);
        if (!cursor.moveToFirst()) {
            Log.e(TAG, String.format("No cursor available for webserviceId %d", webserviceId));
            hostname = "";
        } else {
            port = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_PORT));
            hostname = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_HOSTNAME));
        }
        String websocketSuffix = action == WebserviceManagementAction.REGISTER ? "/api/register" : "/api/manage";
        this.websocketUrl = String.format("wss://%s:%s%s", hostname.replace(" ", "").trim(), port, websocketSuffix);
        databaseHandler.close();
    }

    @Override
    protected String doInBackground(String... params) {
        gcmDeviceId = params[0];
        DatabaseHandler databaseHandler = new DatabaseHandler(ApplicationContextProvider.getAppContext());
        Cursor cursor = databaseHandler.getWebserviceInfo(webserviceId);
        if (!cursor.moveToFirst()) {
            Log.e(TAG, String.format("No cursor available for webserviceId %d", webserviceId));
            return null;
        }
        certAlias = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_ALIAS));
        databaseHandler.close();

        try {
            lock = new ReentrantLock();
            condition = lock.newCondition();
            Log.d(TAG, String.format("Connecting a websocket to %s", websocketUrl));
            String uriString = String.format("%s?%s=%s", websocketUrl, PARAM_GCM, gcmDeviceId);
            ForwardingWebSocket websocket = new ForwardingWebSocket(uriString, this, new WebsocketTrustManagerFactory(),
                    new WebsocketKeyManagerFactory(KeyStoreHandler.getInstance(), certAlias));
            lock.lock();
            websocket.start();
            condition.await(30, TimeUnit.SECONDS);
            lock.unlock();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Exception while waiting for WebSocket to finish management action", e);
            return null;
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, String.format("Action %s on %s got result %s", action, hostname, s));
        if (s != null) {
            WebserviceEntryStatus newStatus = getWebserviceEntryStatus();
            String skytrustId = newStatus == WebserviceEntryStatus.UNKNOWN ? null : s;
            DatabaseHandler databaseHandler = new DatabaseHandler(ApplicationContextProvider.getAppContext());
            if (!databaseHandler.updateWebserviceInfo(webserviceId, skytrustId, newStatus)) {
                Log.e(TAG, "Could not update database entry for webservice entry with ID " + webserviceId);
            }
            databaseHandler.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private WebserviceEntryStatus getWebserviceEntryStatus() {
        switch (action) {
            case REGISTER:
            case RESUME:
                return WebserviceEntryStatus.ACTIVE;
            case PAUSE:
                return WebserviceEntryStatus.PAUSED;
            default:
            case UNREGISTER:
                return WebserviceEntryStatus.UNKNOWN;
        }
    }

    @Override
    public void onMessage(Channel websocketChannel, String message) {
        SimpleMessage msg = JsonUtils.parseStringToObject(message, SimpleMessage.class);
        try {
            if (msg.getHeader().equalsIgnoreCase(MSG_REGISTER)) {
                skytrustId = msg.getPayload();
                Certificate cert = KeyStoreHandler.getInstance().getCertificate(certAlias);
                if (cert == null) {
                    Log.d(TAG, String.format("No existing WebVPN certificate for alias '%s', sending CSR", certAlias));
                    CertificateRequest csr = CertificateUtils.createWebserviceCsr(certAlias, skytrustId);
                    send(websocketChannel, MSG_CSR, Base64.encodeToString(csr.toByteArray(), Base64.DEFAULT));
                } else {
                    Log.d(TAG, String.format("Got existing WebVPN certificate for alias '%s', sending certificate",
                            certAlias));
                    send(websocketChannel, MSG_CERT, Base64.encodeToString(cert.getEncoded(), Base64.DEFAULT));
                }
            } else if (msg.getHeader().equalsIgnoreCase(MSG_CSR)) {
                byte[] certResponse = Base64.decode(msg.getPayload(), Base64.DEFAULT);
                X509Certificate signedCert = new X509Certificate(certResponse);
                Principal subject = signedCert.getSubjectDN();
                if (subject instanceof Name) {
                    String certSkytrustId = ((Name) subject).getRDN(ObjectID.commonName);
                    if (certSkytrustId == null || !certSkytrustId.equalsIgnoreCase(skytrustId)) {
                        Log.d(TAG, String.format("Got some weird certificate with skytrustId %s", certSkytrustId));
                        finished();
                        return;
                    }
                } else {
                    Log.d(TAG, String.format("Got some weird certificate with subject %s", subject));
                    finished();
                    return;
                }
                CertificateUtils.storeWebserviceCert(certAlias, signedCert);
                Log.d(TAG, String.format("Got a signed certificate with skytrustId %s", skytrustId));
                finished(skytrustId);
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_CERT)) {
                Log.d(TAG, String.format("Our certificate for skytrustId %s is valid", skytrustId));
                finished(skytrustId);
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_UNREGISTER)) {
                Log.d(TAG, String.format("Accepting unregistration for skytrustId %s", msg.getPayload()));
                KeyStoreHandler.getInstance().deleteKey(certAlias);
                finished(msg.getPayload());
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_PAUSE)) {
                Log.d(TAG, String.format("Accepting pause for skytrustId %s", msg.getPayload()));
                finished(msg.getPayload());
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_RESUME)) {
                Log.d(TAG, String.format("Accepting resume for skytrustId %s", msg.getPayload()));
                finished(msg.getPayload());
                return;
            } else {
                finished();
            }
        } catch (Exception e) {
            Log.e(TAG, "Registration not successful", e);
            KeyStoreHandler.getInstance().deleteKey(certAlias);
            finished();
        }
    }

    private void finished(String result) {
        this.result = result;
        finished();
    }

    private void finished() {
        lock.lock();
        condition.signalAll();
        lock.unlock();
    }

    private void send(Channel websocketChannel, String header, String payload) {
        websocketChannel.writeAndFlush(
                new TextWebSocketFrame(JsonUtils.parseObjectToString(new SimpleMessage(header, payload))));
    }

    @Override
    public void onConnect(Channel websocketChannel) {
        switch (action) {
            case REGISTER:
                send(websocketChannel, MSG_REGISTER, gcmDeviceId);
                break;
            case UNREGISTER:
                send(websocketChannel, MSG_UNREGISTER, gcmDeviceId);
                break;
            case PAUSE:
                send(websocketChannel, MSG_PAUSE, gcmDeviceId);
                break;
            case RESUME:
                send(websocketChannel, MSG_RESUME, gcmDeviceId);
                break;
        }
    }

    @Override
    public void onClose(Channel websocketChannel) {
        finished();
    }
}
