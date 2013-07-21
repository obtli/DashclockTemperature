package com.andrewjsinclair.dashclocktemperature;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;


public class TemperatureExtensionSettings extends PreferenceActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.device_access_brightness_low);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }


    protected void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.temperature_preferences);
        bindPreferenceSummaryToValue(findPreference(TemperatureExtension.PREF_LABEL));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // TODO: if the previous activity on the stack isn't a ConfigurationActivity,
            // launch it.
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new
     * value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Boolean updatedValue = (Boolean) value;
            if(updatedValue) {
                preference.setSummary("Fahrenheit");
            } else {
                preference.setSummary("Celsius");
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is
     * changed, its summary (line of text below the preference title) is updated to reflect the
     * value. The summary is also immediately updated upon calling this method. The exact display
     * format is dependent on the type of preference.
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        setAndCallPreferenceChangeListener(preference, sBindPreferenceSummaryToValueListener);
    }

    /**
     * When the preference's value is changed, trigger the given listener. The listener is also
     * immediately called with the preference's current value upon calling this method.
     */
    public static void setAndCallPreferenceChangeListener(Preference preference,
                                                          Preference.OnPreferenceChangeListener listener) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        // Trigger the listener immediately with the preference's
        // current value.
        listener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
    }
}
