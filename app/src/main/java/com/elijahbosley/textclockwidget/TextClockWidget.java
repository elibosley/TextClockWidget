package com.elijahbosley.textclockwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextClockWidgetConfigureActivity TextClockWidgetConfigureActivity}
 */
public class TextClockWidget extends AppWidgetProvider {

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
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        context.startService(new Intent(UpdateTimeService.UPDATE_TIME));
    }

    public static final class UpdateTimeService extends Service {
        static final String UPDATE_TIME = "org.coax.widget.digitalclock.action.UPDATE_TIME";

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
            TimeString timeString = new TimeString();
            String time = timeString.timeAsString();
            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.text_clock_widget);
            mRemoteViews.setTextViewText(R.id.appwidget_text, time);

            ComponentName mComponentName = new ComponentName(this, TextClockWidget.class);
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(this);
            mAppWidgetManager.updateAppWidget(mComponentName, mRemoteViews);
        }
    }


}

