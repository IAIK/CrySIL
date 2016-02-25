package org.crysil.instance.u2f.utils;

import android.content.Intent;
import android.util.Log;

import com.google.common.io.BaseEncoding;

import org.crysil.communications.websocket.ssl.KeyStoreInterface;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.crysil.instance.u2f.ErrorActivity;
import org.crysil.instance.u2f.R;

/**
 * Singleton for handling access to the Android KeyStore, and for importing keys
 */
public class KeyStoreHandler implements KeyStoreInterface {

    private static final String TAG = KeyStoreHandler.class.getSimpleName();
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String ANDROID_PROVIDER = "AndroidOpenSSL";
    private static final String IMPORT_KEYSTORE = "PKCS12";
    private static final String IMPORT_KEYSTORE_PROVIDER = "IAIK";

    protected static KeyStoreHandler instance;
    private KeyStore keyStore;

    public static synchronized KeyStoreHandler getInstance() {
        if (instance == null) {
            instance = new KeyStoreHandler();
        }
        return instance;
    }

    protected KeyStoreHandler() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null, null);
            Log.d(TAG, "Key store loaded successfully: " + keyStore);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Intent intent = new Intent(ApplicationContextProvider.getAppContext(), ErrorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ErrorActivity.REASON, e);
            intent.putExtra(ErrorActivity.MSG,
                    ApplicationContextProvider.getAppContext().getString(R.string.error_keystore));
            ApplicationContextProvider.getAppContext().startActivity(intent);
        }
    }

    public String getType() {
        return ANDROID_KEYSTORE;
    }

    public String getProvider() {
        return ANDROID_PROVIDER;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public boolean deleteKey(String alias) {
        try {
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias);
                return true;
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public boolean hasKey(String alias) {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public boolean addCertificate(Certificate cert) {
        try {
            byte[] digest = CertificateUtils.calculateFingerprint(cert);
            String alias = BaseEncoding.base64().encode(digest);
            if (!keyStore.containsAlias(alias)) {
                keyStore.setCertificateEntry(alias, cert);
                return true;
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public boolean addKey(String alias, Key key, Certificate[] certChain, boolean overwrite) {
        try {
            if (!keyStore.containsAlias(alias) || overwrite) {
                keyStore.setKeyEntry(alias, key, null, certChain);
                return true;
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public X509Certificate getCertificate(String alias) {
        try {
            Key key = keyStore.getKey(alias, null);
            if (key != null) {
                Certificate cert = keyStore.getCertificate(alias);
                if (cert != null) {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                    return (X509Certificate) certFactory.generateCertificate(
                            new ByteArrayInputStream(cert.getEncoded()));
                }
            }
        } catch (RuntimeException e) {
            // this sometimes happens when launching the emulator
            Log.e(TAG, e.getMessage(), e);
        } catch (UnrecoverableKeyException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PrivateKey getKey(String alias) {
        try {
            return (PrivateKey) getKeyStore().getKey(alias, null);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (UnrecoverableKeyException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }
}
