package org.crysil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.crysil.database.DatabaseHandler;
import org.crysil.database.webservice.WebserviceEntry;

/**
 * Creates a certificate to use for mail signatures which is signed by a (self-signed) CA certificate
 */
public class WizardActivity extends AbstractActivity {

    private static final String TAG = WizardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler databaseHandler = new DatabaseHandler(WizardActivity.this);
        databaseHandler.insertWebserviceInfo(new WebserviceEntry("CrySIL Demo Service", "crysil.iaik.tugraz.at", "129.27.142.139", 443, "/tomcat/CrysilAndroidRelayService"));
        databaseHandler.close();
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