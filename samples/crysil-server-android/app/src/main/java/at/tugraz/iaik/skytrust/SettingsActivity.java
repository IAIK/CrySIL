package at.tugraz.iaik.skytrust;

import android.os.Bundle;

/**
 * Simple preference activity to show basic settings to the user
 */
public class SettingsActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
