package at.tugraz.iaik.skytrust.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import at.tugraz.iaik.skytrust.ImportKeyActivity;
import at.tugraz.iaik.skytrust.R;
import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import at.tugraz.iaik.skytrust.utils.CertificateUtils;
import at.tugraz.iaik.skytrust.utils.KeyStoreHandler;

/**
 * Imports an existing Keystore into the Android Keystore
 *
 * @see at.tugraz.iaik.skytrust.utils.KeyStoreHandler#importKeyStore(String, String)
 */
public class KeystoreImportAsyncTask extends AsyncTask<String, Void, List<String>> {

    private final Context context;
    private final ImportKeyActivity activity;

    public KeystoreImportAsyncTask(Context context, ImportKeyActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, R.string.keystore_import_toast_wait, Toast.LENGTH_LONG).show();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        if (params.length != 2) {
            return null;
        }
        String path = params[0];
        String password = params[1];
        return KeyStoreHandler.getInstance().importKeyStore(path, password);
    }

    @Override
    protected void onPostExecute(List<String> imported) {
        Toast.makeText(context, R.string.keystore_import_toast_finished, Toast.LENGTH_SHORT).show();
        if (imported != null) {
            DatabaseHandler handler = new DatabaseHandler(activity);
            for (String alias : imported) {
                handler.insertKey(alias);
            }
            handler.close();
        }
        activity.showImported(imported);
    }
}