package com.elijahbosley.textclockwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ekbos on 7/17/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("extra_spaces") || key.equals("caps_mode")) {
                Intent updateWidgetIntent = new Intent(getActivity().getApplicationContext(), TextClockWidget.class);
                updateWidgetIntent.setAction(TextClockWidget.COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE);
                getActivity().getApplicationContext().sendBroadcast(updateWidgetIntent);
            }
        }
    }
}
