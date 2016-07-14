package com.elijahbosley.textclockwidget;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showColorSelector(View view) {
        new ColorOMaticDialog.Builder()
                .initialColor(Color.WHITE)
                .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int i) {
                        // do your stuff
                        String background = Integer.toHexString(i);
                        System.out.println(background);
                        //savePref(getApplicationContext(), mAppWidgetId, background, Resources.getSystem().getString(R.string.background_color));

                        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.text_clock_widget);
                        remoteViews.setInt(R.id.appwidget_text, "SetBackgroundColor", i);
                        remoteViews.setTextViewText(R.id.appwidget_text, "TestS");

                        System.out.println("Set background to " + background);
                    }
                })
                .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(getSupportFragmentManager(), "ColorOMaticDialog");
    }
}
