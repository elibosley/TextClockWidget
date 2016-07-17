package com.elijahbosley.textclockwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextClockWidgetConfigureActivity TextClockWidgetConfigureActivity}
 */
public class TextClockWidget extends AppWidgetProvider {
    public static String COM_ELIJAHBOSLEY_TEXTCLOCK_UPDATE = "TEXTCLOCK_UPDATE_STRING";
    public TextClockWidget() {

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        // See the dimensions and
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get min width and height.
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        int textSize = minWidth < minHeight ? minWidth / 4 : minHeight / 2;

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);

        views.setFloat(R.id.appwidget_text, "setTextSize", textSize);
        ComponentName cn = new ComponentName(context, TextClockWidget.class);
        appWidgetManager.updateAppWidget(cn, views);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        context.startService(new Intent(UpdateTimeService.UPDATE_TIME));
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
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_clock_widget);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Load and set background color preference
        int bg = prefs.getInt("background_color", 1);
        int alpha = (bg >> 24) & 0xFF;
        int tc = prefs.getInt("text_color", 1);
        views.setInt(R.id.appwidget_background, "setColorFilter", bg);
        views.setInt(R.id.appwidget_background, "setAlpha", alpha);
        views.setInt(R.id.appwidget_text, "setTextColor", tc);


        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    /**
     * Service for updating the clock every minute
     */
    public static final class UpdateTimeService extends Service {
        static final String UPDATE_TIME = "org.coax.widget.digitalclock.action.UPDATE_TIME";
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

