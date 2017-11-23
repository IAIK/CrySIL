package at.tugraz.iaik.skytrust.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import at.tugraz.iaik.skytrust.CreateKeyActivity;
import at.tugraz.iaik.skytrust.KeyListActivity;
import at.tugraz.iaik.skytrust.R;
import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import at.tugraz.iaik.skytrust.utils.CertificateUtils;

/**
 * Creates a new key in the background (because it takes a long time on some devices).
 *
 * @see at.tugraz.iaik.skytrust.utils.CertificateUtils
 */
public class KeyCreateAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = KeyCreateAsyncTask.class.getSimpleName();

    private final Context context;
    private final KeyListActivity activity;
    private final String mAlias;
    private final String mCommonName;
    private final String mEmailAddress;
    private final String mCountry;
    private final String mOrganization;
    private final String mOrganizationalUnit;

    public KeyCreateAsyncTask(Intent data, Context context, KeyListActivity activity) {
        mAlias = data.getStringExtra(CreateKeyActivity.ALIAS);
        mCommonName = data.getStringExtra(CreateKeyActivity.COMMON_NAME);
        mEmailAddress = data.getStringExtra(CreateKeyActivity.EMAILADDRESS);
        mCountry = data.getStringExtra(CreateKeyActivity.COUNTRY);
        mOrganization = data.getStringExtra(CreateKeyActivity.ORGANIZATION);
        mOrganizationalUnit = data.getStringExtra(CreateKeyActivity.ORGANIZATIONALUNIT);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, R.string.keylist_create_toast_wait, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(Void... params) {
        boolean result = CertificateUtils.createKeyAndCert(mAlias, mCountry, mOrganization, mOrganizationalUnit,
                mCommonName, mEmailAddress);
        return result ? mAlias : null;
    }

    @Override
    protected void onPostExecute(String alias) {
        Toast.makeText(context, R.string.keylist_create_toast_finished, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Created a new key with alias " + alias);
        if (alias != null) {
            DatabaseHandler handler = new DatabaseHandler(activity);
            handler.insertKey(alias);
            handler.close();
        }
        activity.refreshKeyList();
    }
}
