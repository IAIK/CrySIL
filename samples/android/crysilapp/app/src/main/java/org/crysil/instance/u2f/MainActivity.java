package org.crysil.instance.u2f;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.crysil.instance.CrySILElementFactory;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import org.crysil.instance.u2f.push.PushHelper;
import org.crysil.instance.u2f.push.RegistrationIntentService;
import org.crysil.instance.u2f.utils.ApplicationContextProvider;
import org.crysil.instance.u2f.utils.KeyStoreHandler;

/**
 * Shows webservices (as a fragment), handles NFC communication (as a fragment)
 *
 * @see WebserviceSpinnerFragment
 * @see NFCHandlerFragment
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private NFCHandlerFragment nfcListener;
    private ActorChooserFragment actorChooser;
    private WebsocketCertificateCallback certificateCallback;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PushHelper.checkPlayServices(this)) {
            showErrorActivity(getString(R.string.error_gcm));
            return;
        }
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        if (NfcAdapter.getDefaultAdapter(this) == null) {
            showErrorActivity(getString(R.string.error_nfc));
            return;
        }

        nfcListener = (NFCHandlerFragment) getFragmentManager().findFragmentById(R.id.fragment_nfc);
        actorChooser = (ActorChooserFragment) getFragmentManager().findFragmentById(R.id.fragment_actor_chooser);
        certificateCallback = new WebsocketCertificateCallback(this);

        new Thread(new Runnable() {
            public void run() {
                Security.addProvider(new BouncyCastleProvider());
                CrySILElementFactory.initialize(KeyStoreHandler.getInstance().getKeyStore(),
                        KeyStoreHandler.getInstance().getKeyStore(), KeyStoreHandler.getInstance().getProvider(),
                        KeyStoreHandler.getInstance().getType(), nfcListener, actorChooser, certificateCallback);
            }
        }).start();

        // Fix TLS by installing latest SSLProvider from Google Play Services
        ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstalled() {
            }

            @Override
            public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                GooglePlayServicesUtil.showErrorNotification(errorCode, MainActivity.this);
            }
        });
    }

    private void showErrorActivity(String string) {
        Log.e(TAG, string);
        Intent intent = new Intent(ApplicationContextProvider.getAppContext(), ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ErrorActivity.MSG, string);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            nfcListener.onNFCPresent(IsoDep.get(tag));
        }
    }
}
