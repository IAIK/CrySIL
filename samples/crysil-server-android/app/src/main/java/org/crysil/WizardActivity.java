package org.crysil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Creates a certificate to use for mail signatures which is signed by a (self-signed) CA certificate
 */
public class WizardActivity extends AbstractActivity {

    private static final String TAG = WizardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
    }

    public void btImportOnClick(View view) {
        startActivity(new Intent(this, ImportKeyActivity.class));
        finish();
    }

    public void btCreateOnClick(View view) {
        startActivity(new Intent(this, WizardCreateActivity.class));
        finish();
    }
}