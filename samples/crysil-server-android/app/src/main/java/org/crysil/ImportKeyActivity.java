package org.crysil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;

import java.util.List;

import org.crysil.tasks.KeystoreImportAsyncTask;
import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;

/**
 * Imports existing keys in an PKCS12 keystore file into the Android keystore
 *
 * @see org.crysil.utils.KeyStoreHandler#importKeyStore(String, String)
 */
public class ImportKeyActivity extends Activity {

    private final static String TAG = ImportKeyActivity.class.getSimpleName();
    private final static int READ_REQUEST_CODE = 333;

    private EditText edPassword;
    private TextView tvImported;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_keys);
        edPassword = (EditText) findViewById(R.id.edKeystorePassword);
        tvImported = (TextView) findViewById(R.id.tvKeystoreImported);
    }

    public void btChooseFileOnClick(View view) {
        if (edPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.keystore_import_password_warning),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.keystore_import_intent_chooser)),
                    READ_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Something went wrong - no activity can handle our intent", ex);
        }
    }

    public void showImported(List<String> imported) {
        if (imported != null && !imported.isEmpty()) {
            tvImported.setText(
                    getString(R.string.keystore_import_success_message) + " " + Joiner.on(", " + "").join(imported));
        } else {
            tvImported.setText(getString(R.string.keystore_import_failed_message));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                tvImported.setText("");
                final Uri uri = data.getData();
                Log.d(TAG, "Uri of keystore to import is: " + uri.toString());
                new KeystoreImportAsyncTask(getApplicationContext(), this).execute(uri.getPath(),
                        edPassword.getText().toString());
            }
        }
    }

    public void btFinishOnClick(View view) {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrentActivityTracker.onActivityResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CurrentActivityTracker.onActivityStop(this);
    }
}
