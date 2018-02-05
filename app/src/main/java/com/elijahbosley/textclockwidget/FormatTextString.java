package com.elijahbosley.textclockwidget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ekbos on 12/19/2017.
 */

public class FormatTextString extends Activity {

    public static String formatString(String textString, Context context) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean capsMode = sharedPrefs.getBoolean("caps_mode", false);
        boolean extraSpacing = sharedPrefs.getBoolean("extra_spaces", false);
        if (capsMode) {
            textString = textString.toUpperCase();
        }
        if (extraSpacing) {
            textString = textString.replace("", " ").trim();
            textString = textString.replace(" \n", "\n");
            textString = textString.replace("\n ", "\n");
        }
        return textString;
    }

    public static String[] formatStringArray(String[] timeArray, Context context) {
        for (int i = 0; i < timeArray.length; i++) {
            timeArray[i] = formatString(timeArray[i], context);
        }
        return timeArray;
    }


}