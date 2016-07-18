package org.crysil.instance.u2f;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import org.crysil.communications.websocket.interfaces.CertificateCallback;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.instance.u2f.utils.CertificateUtils;
import org.crysil.instance.u2f.utils.KeyStoreHandler;

/**
 * Handles the certificate trust callback for websocket actions, e.g. shows a dialog to the user
 *
 * @see org.crysil.instance.u2f.WebsocketCertificateDialog
 */
public class WebsocketCertificateCallback implements CertificateCallback, WebsocketCertificateDialog.WebsocketCertificateDialogListener {

    private Lock certLock = new ReentrantLock();
    private Condition certCond = certLock.newCondition();
    private final boolean[] result = {false};
    private Activity activity;
    private Map<DialogFragment, X509Certificate> certificates = new HashMap<>();

    public WebsocketCertificateCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean checkServerTrusted(final X509Certificate cert, String authType) {
        try {
            certLock.lock();
            final Bundle bundle = new Bundle();
            bundle.putString(WebsocketCertificateDialog.CERTIFICATE, CertificateUtils.printCertificate(cert));
            final WebsocketCertificateDialog newFragment = WebsocketCertificateDialog.newInstance(
                    WebsocketCertificateCallback.this);
            newFragment.setArguments(bundle);
            certificates.put(newFragment, cert);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction transaction;
                    transaction = activity.getFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                }
            });
            certCond.await(60, TimeUnit.SECONDS);
            certLock.unlock();
            newFragment.dismiss();
            return result[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        result[0] = false;
        certLock.lock();
        certCond.signalAll();
        certLock.unlock();
    }

    @Override
    public void onDialogOnceClick(DialogFragment dialog) {
        result[0] = true;
        certLock.lock();
        certCond.signalAll();
        certLock.unlock();
    }

    @Override
    public void onDialogAlwaysClick(DialogFragment dialog) {
        result[0] = true;
        if (certificates.containsKey(dialog)) {
            KeyStoreHandler.getInstance().addCertificate(certificates.get(dialog));
            certificates.remove(dialog);
        }
        certLock.lock();
        certCond.signalAll();
        certLock.unlock();
    }
}
