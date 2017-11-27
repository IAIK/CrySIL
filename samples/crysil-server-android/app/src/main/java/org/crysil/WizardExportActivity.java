package org.crysil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.crysil.utils.CertificateUtils;
import iaik.x509.X509Certificate;

/**
 * Exports the (self-signed) CA certificate to use as a trusted root for mail signatures.
 */
public class WizardExportActivity extends AbstractActivity {

    private static final String TAG = WizardExportActivity.class.getSimpleName();
    private static final String INTENT_TYPE = "application/octet-stream";

    private EditText etCertificate;
    private EditText etEmail;
    private Button btExport;
    private X509Certificate caCertificate;
    private boolean certificateDetailsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_export);

        etCertificate = (EditText) findViewById(R.id.etCertificate);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btExport = (Button) findViewById(R.id.btExport);

        etCertificate.setKeyListener(null);

        caCertificate = CertificateUtils.getCaCertIfValid();
        if (caCertificate != null) {
            btExport.setEnabled(true);
            etEmail.setEnabled(true);
        } else {
            etCertificate.setText(getString(R.string.wizard_export_error_no_file));
            btExport.setEnabled(false);
            etEmail.setEnabled(false);
        }
    }

    public void etCertificateOnClick(View view) {
        if (caCertificate != null && !certificateDetailsVisible) {
            etCertificate.setText(caCertificate.toString());
            certificateDetailsVisible = true;
        }
    }

    public void btExportClick(View view) {
        Uri attachment = CertificateUtils.getCaCertFile();
        if (attachment == null) {
            Toast.makeText(WizardExportActivity.this, getResources().getString(R.string.export_certificate_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(INTENT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{etEmail.getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.wizard_export_mail_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.wizard_export_mail_body));
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment);
        try {
            startActivity(Intent.createChooser(emailIntent,
                    getResources().getString(R.string.wizard_export_choose_application)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(WizardExportActivity.this, getResources().getString(R.string.no_mail_clients_available),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void btSkipClick(View view) {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
