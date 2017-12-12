package org.crysil.utils;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crysil.communications.websocket.KeyStoreInterface;
import org.crysil.actor.softwarecrypto.FileKeyStore;
import org.crysil.ErrorActivity;
import org.crysil.R;
import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;
import org.crysil.builders.KeyBuilder;
import org.crysil.database.DatabaseHandler;
import org.crysil.database.keys.KeyEntry;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import iaik.x509.X509Certificate;

/**
 * Singleton for handling access to the Android KeyStore, and for importing keys
 */
public class KeyStoreHandler extends FileKeyStore implements KeyStoreInterface {

    private static final String TAG = KeyStoreHandler.class.getSimpleName();
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String ANDROID_PROVIDER = "AndroidOpenSSL";
    private static final String IMPORT_KEYSTORE = "PKCS12";
    private static final String IMPORT_KEYSTORE_PROVIDER = "IAIK";

    protected static KeyStoreHandler instance;

    public static synchronized KeyStoreHandler getInstance() {
        if (instance == null) {
            instance = new KeyStoreHandler();
        }
        return instance;
    }

    protected KeyStoreHandler() {
        try {
            keystore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keystore.load(null, null);
            Log.d(TAG, "Key store loaded successfully: " + keystore);
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
        return keystore;
    }

    public boolean deleteKey(String alias) {
        try {
            if (keystore.containsAlias(alias)) {
                keystore.deleteEntry(alias);
                return true;
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public boolean hasKey(String alias) {
        try {
            return keystore.containsAlias(alias);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public boolean addKey(String alias, Key key, Certificate[] certChain, boolean overwrite) {
        try {
            if (!keystore.containsAlias(alias) || overwrite) {
                keystore.setKeyEntry(alias, key, null, certChain);
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    public X509Certificate getCertificate(String alias) {
        try {
            Key key = keystore.getKey(alias, null);
            if (key != null) {
                Certificate cert = keystore.getCertificate(alias);
                if (cert != null) {
                    return new X509Certificate(cert.getEncoded());
                }
            }
        } catch (UnrecoverableKeyException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (RuntimeException e) {
            // this sometimes happens when launching the emulator
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

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

    public List<String> importKeyStore(String path, String password) {
        List<String> imported = Lists.newArrayList();
        if (path != null && password != null) {
            try {
                KeyStore existingStore = KeyStore.getInstance(IMPORT_KEYSTORE, IMPORT_KEYSTORE_PROVIDER);
                FileInputStream fis = new FileInputStream(path);
                existingStore.load(fis, password.toCharArray());
                fis.close();
                List<String> keyList = Collections.list(existingStore.aliases());
                for (String alias : keyList) {
                    try {
                        Key key = existingStore.getKey(alias, password.toCharArray());
                        Certificate[] certChain = existingStore.getCertificateChain(alias);
                        if (addKey(alias, key, certChain, false)) {
                            imported.add(alias);
                        }
                    } catch (UnrecoverableKeyException e) {
                        Log.e(TAG, e.getMessage(), e);
                    } catch (NoSuchAlgorithmException e) {
                        Log.e(TAG, e.getMessage(), e);
                    } catch (KeyStoreException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            } catch (KeyStoreException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (CertificateException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (NoSuchProviderException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return imported;
    }

    @Override
    public List<KeyHandle> getKeyList() {
        List<KeyHandle> result = new ArrayList<>();

        DatabaseHandler databaseHandler = new DatabaseHandler(CurrentActivityTracker.getActivity());
        Cursor cursor = databaseHandler.getKeyCursor();

        while(cursor.moveToNext()) {
            String alias = cursor.getString(cursor.getColumnIndex(KeyEntry.COLUMN_NAME_ALIAS));
            result.add(KeyBuilder.buildKeyHandle(alias, ""));
        }
        databaseHandler.close();

        return result;
    }
}
