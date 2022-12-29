package com.example.android.pickanumber;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i<count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }

        Preference highPreference = findPreference(getString(R.string.high_key));
        Preference lowPreference = findPreference(getString(R.string.low_key));
        assert highPreference != null;
        highPreference.setOnPreferenceChangeListener(this);
        assert lowPreference != null;
        lowPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }

    }

    @Override
    public boolean onPreferenceChange(@NonNull @NotNull Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please insert a natural number", Toast.LENGTH_SHORT);

        String highKey = getString(R.string.high_key);
        String lowKey = getString(R.string.low_key);

        if (preference.getKey().equals(highKey) || preference.getKey().equals(lowKey)) {
            String stringParameter = (String) newValue;
            try {
                int parameter = Integer.parseInt(stringParameter);
                if (parameter < 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException numberFormatException) {
                error.show();
                return false;
            }
        }
        return true;
    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getPreferenceScreen().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }
}
