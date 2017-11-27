package org.crysil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Simple preference activity to show basic settings to the user
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getActivity().getApplicationContext().getString(R.string.pref_key_skytrust_dialog))) {
            Preference pref = findPreference(key);
            Boolean confirmationDialog = getPreferenceManager().getSharedPreferences().getBoolean(
                    getActivity().getApplicationContext().getString(R.string.pref_key_skytrust_dialog), true);
        }
    }
}
