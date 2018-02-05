package com.elijahbosley.textclockwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of the widget
 */
public class TextClockWidget extends AppWidgetProvider {
    public static String COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE = "TEXTCLOCK_UPDATE_STRING";

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        // Setup font size in order to fit in the box
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);
        updateWidget(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * Get the font size using calculate text size
     *
     * @param context          app context
     * @param appWidgetManager an initiated appWidgetManager
     * @param appWidgetId      the id for the widget
     * @return the correct textSize
     */
    private int fontSizeToFit(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int textSize;
        // get MaxWidth
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        textSize = (int) Math.round(calculateTextSize(context, maxWidth));

        return textSize;
    }

    /**
     * Calculate the textSize given the box width and a context
     *
     * @param context  the app's context
     * @param boxWidth width of the widget
     * @return Font size calculated
     */
    private double calculateTextSize(Context context, float boxWidth) {
        String longestWord = getLongestWord(context);
        int maxFontSize = 200;

        Paint paint = new Paint();
        paint.setTypeface(Typeface.MONOSPACE); //Todo Move typeface into class-level variable
        paint.setTextSize(maxFontSize);
        float textWidth = paint.measureText(longestWord);

        while (textWidth > boxWidth) {
            maxFontSize -= 2;
            paint.setTextSize(maxFontSize);
            textWidth = paint.measureText(longestWord);
        }
        return maxFontSize * 0.6;
    }

    /**
     * Get the longest word in the current time and return it given a context
     *
     * @param context the context used to format the text string
     * @return a string, longest word in the current time
     */
    private String getLongestWord(Context context) {
        TimeString timeString = new TimeString();
        String timeAsString = timeString.timeAsString();
        timeAsString = FormatTextString.formatString(timeAsString, context);

        String lines[] = timeAsString.split("\\r?\\n");
        String longestWord = lines[0];
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].length() > longestWord.length()) {
                longestWord = lines[i];
            }
        }
        return longestWord;
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent updateTimeIntent = new Intent(COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE);
        updateTimeIntent.setPackage("com.elijahbosley.textclockwidget");
        Log.d("TextClockWidget", "Service Started");
        context.startService(updateTimeIntent);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, UpdateTimeService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidget(context);
        Intent updateTimeIntent = new Intent(COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE);
        updateTimeIntent.setPackage("com.elijahbosley.textclockwidget");
        context.startService(updateTimeIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE.equals(intent.getAction())) {
            updateWidget(context);
        }
    }


    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ComponentName mComponentName = new ComponentName(context, TextClockWidget.class);

        // Update app widget with newly added changes
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        int textSize = fontSizeToFit(context, appWidgetManager, appWidgetIds[0]);
        int textColor = sharedPreferences.getInt("text_color", Color.WHITE);
        int textSelection = Integer.parseInt(sharedPreferences.getString("font_identifier", "0"));
        int backgroundColor = sharedPreferences.getInt("background_color", 1);
        TimeString timeString = new TimeString();
        String[] time = timeString.getTimeArray();
        time = FormatTextString.formatStringArray(time, context);
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.text_clock_widget);

        Bitmap textBitmap = BitmapCreator.getFontBitmap(context, time, textColor, textSize, textSelection, true, backgroundColor);
        mRemoteViews.setImageViewBitmap(R.id.appwidget_imageview, textBitmap);


        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);

    }


}

