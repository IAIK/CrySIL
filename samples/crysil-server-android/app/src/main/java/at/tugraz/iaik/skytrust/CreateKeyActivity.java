package at.tugraz.iaik.skytrust;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import at.tugraz.iaik.skytrust.utils.KeyInputValidation;
import at.tugraz.iaik.skytrust.utils.KeyStoreHandler;

/**
 * Creates a new key with input from the user
 *
 * @see at.tugraz.iaik.skytrust.tasks.KeyCreateAsyncTask
 */
public class CreateKeyActivity extends AbstractActivity {

    public static String ALIAS = "alias";
    public static String COMMON_NAME = "commonName";
    public static String EMAILADDRESS = "eMailAddress";
    public static String COUNTRY = "country";
    public static String ORGANIZATION = "organization";
    public static String ORGANIZATIONALUNIT = "ogranizationalUnit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_key);
    }

    public void btCreateKeyOnClick(View view) {
        EditText edAlias = (EditText) findViewById(R.id.edAlias);
        String alias = edAlias.getText().toString();
        if (KeyStoreHandler.getInstance().hasKey(alias)) {
            edAlias.setError(getResources().getString(R.string.alias_error));
            return;
        }
        EditText edMailAddress = (EditText) findViewById(R.id.edEmailAdress);
        String emailAddress = edMailAddress.getText().toString();
        if (!KeyInputValidation.isValidEmail(emailAddress)) {
            edMailAddress.setError(getResources().getString(R.string.email_error));
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(ALIAS, edAlias.getText().toString());
        resultIntent.putExtra(COMMON_NAME, ((EditText) findViewById(R.id.edCommonName)).getText().toString());
        resultIntent.putExtra(EMAILADDRESS, edMailAddress.getText().toString());
        resultIntent.putExtra(COUNTRY, ((EditText) findViewById(R.id.edCountry)).getText().toString());
        resultIntent.putExtra(ORGANIZATION, ((EditText) findViewById(R.id.edOrganization)).getText().toString());
        resultIntent.putExtra(ORGANIZATIONALUNIT,
                ((EditText) findViewById(R.id.edOrganizationalUnit)).getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
