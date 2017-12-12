package org.crysil.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;

import org.crysil.R;
import org.crysil.database.keys.KeyEntry;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;
import iaik.x509.X509Certificate;

/**
 * Adapter to display all keys from the key store in a list on the UI
 */
public class KeyListAdapter extends SimpleCursorAdapter {

    private static String TAG = KeyListAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private KeyStore keystore;

    public KeyListAdapter(Context context, Cursor cursor) {
        super(context, 0, cursor, new String[]{}, new int[]{}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.keystore = KeyStoreHandler.getInstance().getKeyStore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_key_item, parent, false);
        }

        Cursor cursor = (Cursor) getItem(position);
        String alias = cursor.getString(cursor.getColumnIndex(KeyEntry.COLUMN_NAME_ALIAS));

        TextView viewName = (TextView) view.findViewById(R.id.keylist_item_text1);
        viewName.setText(alias);
        if (keystore != null) {
            TextView viewDetails = (TextView) view.findViewById(R.id.keylist_item_text2);
            List<String> details = new ArrayList<String>();
            try {
                Key key = keystore.getKey(alias, null);
                if (key != null) {
                    Certificate cert = keystore.getCertificate(alias);
                    if (cert != null) {
                        X509Certificate iaikCert = new X509Certificate(cert.getEncoded());
                        Name subject = (Name) iaikCert.getSubjectDN();
                        if (subject != null) {
                            String cn = subject.getRDN(ObjectID.commonName);
                            if (!Strings.isNullOrEmpty(cn)) {
                                details.add(cn);
                            }
                            String m = subject.getRDN(ObjectID.emailAddress);
                            if (!Strings.isNullOrEmpty(m)) {
                                details.add(m);
                            }
                        }
                    }
                    if (key.getAlgorithm() != null) {
                        details.add(key.getAlgorithm());
                    }
                    if (key instanceof RSAPrivateKey && ((RSAPrivateKey) key).getModulus() != null) {
                        String modulus = ((RSAPrivateKey) key).getModulus().toString(16);
                        if (modulus.length() >= 32) {
                            modulus = modulus.substring(0, 31) + "...";
                        }
                        details.add(modulus);
                    }
                }
            } catch (KeyStoreException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (UnrecoverableKeyException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (CertificateEncodingException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (CertificateException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (RuntimeException e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                viewDetails.setText(Joiner.on(", ").skipNulls().join(details));
            }
        }
        return view;
    }
}

