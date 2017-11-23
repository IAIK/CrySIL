package at.tugraz.iaik.skytrust;

import android.app.Activity;
import android.view.MenuItem;

import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;

/**
 * For consistently handling "back" in the action bar
 */
public abstract class AbstractActivity extends Activity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
