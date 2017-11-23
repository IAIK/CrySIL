package at.tugraz.iaik.skytrust;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.security.Security;

import at.tugraz.iaik.skytrust.utils.CertificateUtils;
import at.tugraz.iaik.skytrust.utils.KeyInputValidation;
import iaik.security.provider.IAIK;

/**
 * Creats a certificate to use for mail signatures which is signed by a (self-signed) CA certificate
 */
public class WizardCreateActivity extends AbstractActivity {

    private static final String TAG = WizardCreateActivity.class.getSimpleName();

    private EditText etCommonName;
    private EditText etEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_create);

        etCommonName = (EditText) findViewById(R.id.etCommonName);
        etEmailAddress = (EditText) findViewById(R.id.etEmailAdress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Insert IAIK at the last position to fix TLS connections in Gatekeeper
                Security.insertProviderAt(IAIK.getInstance(), Security.getProviders().length);
            }
        }).start();
    }

    public void btNextOnClick(final View view) {
        final String commonName = etCommonName.getText().toString();
        final String emailAddress = etEmailAddress.getText().toString();

        if (!KeyInputValidation.isValidEmail(emailAddress)) {
            etEmailAddress.setError(getResources().getString(R.string.email_error));
            return;
        }

        final RelativeLayout activityBlocker = (RelativeLayout) findViewById(R.id.ctrlActivityIndicatorView);
        activityBlocker.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CertificateUtils.createInitialKeyAndCert(WizardCreateActivity.this, commonName, emailAddress)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityBlocker.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(WizardCreateActivity.this, WizardExportActivity.class));
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityBlocker.setVisibility(View.INVISIBLE);
                            Toast.makeText(WizardCreateActivity.this.getApplicationContext(),
                                    getResources().getString(R.string.certificate_initialization_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}