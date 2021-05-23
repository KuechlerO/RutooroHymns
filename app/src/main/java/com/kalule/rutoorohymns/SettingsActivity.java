package com.kalule.rutoorohymns;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;


import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for settings
 */
public class SettingsActivity extends AppCompatActivity {

    // inner class (public and static!)
    public static class SettingsFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            this.getActivity().getTheme().applyStyle(new Preferences(this.getActivity()).getFontStyle().getResId(), true);

            super.onCreate(savedInstanceState);
            // add preferences
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister listener
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Listener -> updates the appearance if changes occurred
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // update appearance of activity
            Preferences.updateSettings(this.getActivity());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Preferences.updateSettings(this);
    }
}
