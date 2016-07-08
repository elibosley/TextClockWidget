package com.elijahbosley.textclockwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by elibosley on 7/8/16.
 */
public final class UpdateTimeService extends Service {
    static final String UPDATE_TIME = "com.elijahbosley.textclock.action.UPDATE_TIME";

    private Calendar mCalendar;
    private final static IntentFilter mIntentFilter = new IntentFilter();

    static {
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

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

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTime();
        }
    };

    private void updateTime() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.text_clock_widget);
        TimeString timeString = new TimeString();
        mRemoteViews.setTextViewText(R.id.appwidget_text, timeString.timeAsString());
        ComponentName mComponentName = new ComponentName(this, TextClockWidget.class);
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);
    }
}
