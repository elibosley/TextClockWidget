package com.elijahbosley.textclockwidget;

import android.app.Activity;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class TextClockWidget extends AppWidgetProvider {
    public static String COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE = "TEXTCLOCK_UPDATE_STRING";


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        /** Setup font size in order to fit in the box */
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);
        updateWidget(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    /**
     * Get the font size using calculate text size
     * @param context app context
     * @param appWidgetManager an initiated appWidgetManager
     * @param appWidgetId the id for the widget
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
     * @param context the app's context
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
        System.out.println("Textwidth is:" + textWidth + " for word: " + longestWord);
        System.out.println("max size found:" + maxFontSize);
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
        int longest = 0;
        int current = 0;
        String currentWord = "";
        String longestWord = "";
        for (int i = 0; i < timeAsString.length(); i++) {
            if (timeAsString.charAt(i) != '\n') {
                current++;
                currentWord += timeAsString.charAt(i);
            }
            else {
                if (current > longest) {
                    longest = current;
                    System.out.println(currentWord);
                    longestWord = currentWord;
                }
                current = 0;
                currentWord = "";
            }
        }
        if (current > longest) {
            longest = current;
            System.out.println(currentWord);
            longestWord = currentWord;
        }
        return longestWord;
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(UpdateTimeService.UPDATE_TIME));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, UpdateTimeService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("called onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidget(context);

        context.startService(new Intent(UpdateTimeService.UPDATE_TIME));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE.equals(intent.getAction())) {

            System.out.println("received update intent");
            updateWidget(context);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ComponentName mComponentName = new ComponentName(context, TextClockWidget.class);
        // Load and set background color preference
        updateColors(remoteViews, sharedPreferences);
        // Update app widget with newly added changes
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        int textSize = fontSizeToFit(context, appWidgetManager, appWidgetIds[0]);
        int textColor = sharedPreferences.getInt("text_color", Color.WHITE);
        int textSelection = Integer.parseInt(sharedPreferences.getString("font_identifier", "0"));

        TimeString timeString = new TimeString();
        String[] time = timeString.getTimeArray();
        time = FormatTextString.formatStringArray(time, context);
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.text_clock_widget);

        Bitmap textBitmap = BitmapCreator.getFontBitmap(context, time, textColor, textSize, textSelection);
        System.out.println(textBitmap);
        mRemoteViews.setImageViewBitmap(R.id.appwidget_imageview, textBitmap);



        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);

    }

    private void updateColors(RemoteViews remoteViews, SharedPreferences sharedPreferences) {

        int bg = sharedPreferences.getInt("background_color", 1);
        System.out.println(bg);
        int alpha = (bg >> 24) & 0xFF;
        int tc = sharedPreferences.getInt("text_color", 1);
        remoteViews.setInt(R.id.appwidget_background, "setColorFilter", bg);
        remoteViews.setInt(R.id.appwidget_background, "setAlpha", alpha);
    }

    /**
     * Service for updating the clock every minute
     */
    public static final class UpdateTimeService extends Service {
        static final String UPDATE_TIME = "com.elijahbosley.widget.textclock.action.UPDATE_TIME";
        private final static IntentFilter mIntentFilter = new IntentFilter();

        static {
            mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
            mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        }

        private Calendar mCalendar;
        private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTime();
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();

            mCalendar = Calendar.getInstance();
            registerReceiver(mTimeChangedReceiver, mIntentFilter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            unregisterReceiver(mTimeChangedReceiver);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);

            if (intent != null) {
                if (UPDATE_TIME.equals(intent.getAction())) {
                    updateTime();
                }
            }
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void updateTime() {
            System.out.println("called updateTime");
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int textColor = sharedPrefs.getInt("text_color", Color.WHITE);
            System.out.println(sharedPrefs.getString("font_identifier", "0"));
            int textSelection = Integer.parseInt(sharedPrefs.getString("font_identifier", "0"));


            mCalendar.setTimeInMillis(System.currentTimeMillis());
            TimeString timeString = new TimeString();
            String[] time = timeString.getTimeArray();
            time = FormatTextString.formatStringArray(time, getApplicationContext());
            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.text_clock_widget);

            // Create bitmap
            Bitmap textBitmap = BitmapCreator.getFontBitmap(getApplicationContext(), time, textColor, 50, textSelection);
            System.out.println(textBitmap);
            mRemoteViews.setImageViewBitmap(R.id.appwidget_imageview, textBitmap);

            // Apply changes
            ComponentName mComponentName = new ComponentName(this, TextClockWidget.class);
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
            mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);
        }
    }

    public static class FormatTextString extends Activity {

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


}

