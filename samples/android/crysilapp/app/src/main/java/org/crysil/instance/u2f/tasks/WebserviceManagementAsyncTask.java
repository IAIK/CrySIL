package org.crysil.instance.u2f.tasks;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.websocket.ForwardingWebSocket;
import org.crysil.communications.websocket.interfaces.CertificateCallback;
import org.crysil.communications.websocket.interfaces.WebSocketListener;
import org.crysil.communications.websocket.ssl.WebsocketKeyManagerFactory;
import org.crysil.communications.websocket.ssl.WebsocketTrustManagerFactory;
import org.crysil.instance.u2f.database.DatabaseHandler;
import org.crysil.instance.u2f.database.webservice.WebserviceEntry;
import org.crysil.instance.u2f.database.webservice.WebserviceEntryStatus;
import org.crysil.instance.u2f.utils.ApplicationContextProvider;
import org.crysil.instance.u2f.utils.CertificateUtils;
import org.crysil.instance.u2f.utils.KeyStoreHandler;
import org.spongycastle.asn1.x500.RDN;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x500.style.IETFUtils;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
    private final CertificateCallback certificateCallback;
    private String port;
    private String gcmDeviceId;
    private String crysilId;
    private String certAlias;
    private ReentrantLock lock;
    private Condition condition;
    private String result;

    public WebserviceManagementAsyncTask(long webserviceId,
                                         WebserviceManagementAction action,
                                         CertificateCallback certificateCallback) {
        this.webserviceId = webserviceId;
        this.action = action;
        this.certificateCallback = certificateCallback;
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
            ForwardingWebSocket websocket = new ForwardingWebSocket(uriString, this,
                    new WebsocketTrustManagerFactory(certificateCallback, KeyStoreHandler.getInstance().getKeyStore()),
                    new WebsocketKeyManagerFactory(KeyStoreHandler.getInstance(), certAlias));
            lock.lock();
            websocket.start();
            condition.await(60, TimeUnit.SECONDS);
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
            String crysilId = newStatus == WebserviceEntryStatus.UNKNOWN ? null : s;
            DatabaseHandler databaseHandler = new DatabaseHandler(ApplicationContextProvider.getAppContext());
            if (!databaseHandler.updateWebserviceInfo(webserviceId, crysilId, newStatus)) {
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
        SimpleMessage msg = JsonUtils.fromJson(message, SimpleMessage.class);
        try {
            if (msg.getHeader().equalsIgnoreCase(MSG_REGISTER)) {
                crysilId = msg.getPayload();
                Certificate cert = KeyStoreHandler.getInstance().getCertificate(certAlias);
                if (cert == null) {
                    Log.d(TAG, String.format("No existing WebVPN certificate for alias '%s', sending CSR", certAlias));
                    PKCS10CertificationRequest csr = CertificateUtils.createWebserviceCsr(certAlias, crysilId);
                    send(websocketChannel, MSG_CSR, Base64.encodeToString(csr.getEncoded(), Base64.DEFAULT));
                } else {
                    Log.d(TAG, String.format("Got existing WebVPN certificate for alias '%s', sending certificate",
                            certAlias));
                    send(websocketChannel, MSG_CERT, Base64.encodeToString(cert.getEncoded(), Base64.DEFAULT));
                }
            } else if (msg.getHeader().equalsIgnoreCase(MSG_CSR)) {
                byte[] certResponse = Base64.decode(msg.getPayload(), Base64.DEFAULT);
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                X509Certificate signedCert = (X509Certificate) factory.generateCertificate(
                        new ByteArrayInputStream(certResponse));
                X509CertificateHolder holder = new X509CertificateHolder(certResponse);
                X500Name subject = holder.getSubject();
                RDN cn = subject.getRDNs(BCStyle.CN)[0];
                String certCrysilId = IETFUtils.valueToString(cn.getFirst().getValue());
                if (certCrysilId == null || !certCrysilId.equalsIgnoreCase(crysilId)) {
                    Log.d(TAG, String.format("Got some weird certificate with crysilId %s", certCrysilId));
                    finished();
                    return;
                }
                CertificateUtils.storeWebserviceCert(certAlias, signedCert);
                Log.d(TAG, String.format("Got a signed certificate with crysilId %s", crysilId));
                finished(crysilId);
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_CERT)) {
                Log.d(TAG, String.format("Our certificate for crysilId %s is valid", crysilId));
                finished(crysilId);
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_UNREGISTER)) {
                Log.d(TAG, String.format("Accepting unregistration for crysilId %s", msg.getPayload()));
                KeyStoreHandler.getInstance().deleteKey(certAlias);
                finished(msg.getPayload());
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_PAUSE)) {
                Log.d(TAG, String.format("Accepting pause for crysilId %s", msg.getPayload()));
                finished(msg.getPayload());
                return;
            } else if (msg.getHeader().equalsIgnoreCase(MSG_RESUME)) {
                Log.d(TAG, String.format("Accepting resume for crysilId %s", msg.getPayload()));
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
        websocketChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(new SimpleMessage(header, payload))));
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
