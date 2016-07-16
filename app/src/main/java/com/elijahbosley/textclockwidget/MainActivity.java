package com.elijahbosley.textclockwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

public class MainActivity extends AppCompatActivity {
    private boolean background = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showColorSelector(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Processing to remove the leading alpha values - ColorOMatic does not support this
        int i = prefs.getInt("background_color", 1);
        String hexColor = String.format("#%06X", (0xFFFFFF & i));
        int colorInt;
        System.out.println(hexColor);
        try {
            colorInt = Color.parseColor(hexColor);
        } catch (Exception ex) {
            colorInt = 0;
        }
        new ColorOMaticDialog.Builder()
                .initialColor(colorInt)
                .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                .indicatorMode(IndicatorMode.DECIMAL) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int i) {
                        // do your stuff
                        String background = Integer.toHexString(i);
                        System.out.println(background);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor settingsEditor = prefs.edit();
                        settingsEditor.putInt("background_color", i);
                        settingsEditor.apply();


                        Intent updateWidgetIntent = new Intent(getApplicationContext(), TextClockWidget.class);
                        updateWidgetIntent.setAction(TextClockWidget.COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE);
                        getApplicationContext().sendBroadcast(updateWidgetIntent);
                        System.out.println("Set background to " + background);
                    }
                })
                .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(getSupportFragmentManager(), "ColorOMaticDialog");
    }

    public void toggleBackground(View view) {
        background = !background;
    }
}
