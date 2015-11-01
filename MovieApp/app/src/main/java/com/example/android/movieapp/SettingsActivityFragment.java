package com.example.android.movieapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_main);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if(preference == null){
            return;
        }

        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference)preference;
            listPreference.setSummary(listPreference.getEntry().toString());
        }
    }

    private void triggerSharedPreferencesChange(){
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        Map<String, ?> keys = sharedPreferences.getAll();

        if(keys == null){
            return;
        }

        for(String key : keys.keySet()){
            onSharedPreferenceChanged(sharedPreferences, key);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        triggerSharedPreferencesChange();
    }

    @Override
    public void onPause(){
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
