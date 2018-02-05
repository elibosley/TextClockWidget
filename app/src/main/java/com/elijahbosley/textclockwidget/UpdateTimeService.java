package com.elijahbosley.textclockwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Service to update the clock every minute
 */

public class UpdateTimeService extends Service {
    static final String UPDATE_TIME = "com.elijahbosley.widget.textclock.action.UPDATE_TIME";
    private final static IntentFilter mIntentFilter = new IntentFilter();
    private Calendar mCalendar;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Update Time Service", "onStartCommand");
        if (intent != null) {
            if (UPDATE_TIME.equals(intent.getAction())) {
                updateTime();
            }
        }
        return START_STICKY;
    }

    static {
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

    private BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Update Time Service", "Called updated time");
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
        Log.d("Update Time Service", "Stopped Service");
        unregisterReceiver(mTimeChangedReceiver);
    }

    private void updateTime() {
        Log.d("Update Time Service", "Call to update time");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int textColor = sharedPrefs.getInt("text_color", Color.WHITE);
        int textSelection = Integer.parseInt(sharedPrefs.getString("font_identifier", "0"));
        int backgroundColor = sharedPrefs.getInt("background_color", 1);

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        TimeString timeString = new TimeString();
        String[] time = timeString.getTimeArray();
        time = FormatTextString.formatStringArray(time, getApplicationContext());
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.text_clock_widget);

        // Create bitmap
        Bitmap textBitmap = BitmapCreator.getFontBitmap(getApplicationContext(), time, textColor, 50, textSelection, true, backgroundColor);
        mRemoteViews.setImageViewBitmap(R.id.appwidget_imageview, textBitmap);

        // Apply changes
        ComponentName mComponentName = new ComponentName(this, TextClockWidget.class);
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);
    }
}