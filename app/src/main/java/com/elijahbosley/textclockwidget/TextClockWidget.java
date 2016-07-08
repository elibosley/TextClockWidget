package com.elijahbosley.textclockwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

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

        Intent serviceIntent = new Intent(UpdateTimeService.UPDATE_TIME);
        serviceIntent.setPackage("com.elijahbosley.textclockwidget.UpdateTimeService");
        context.startService(serviceIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        context.stopService(new Intent(context, UpdateTimeService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent serviceIntent = new Intent(UpdateTimeService.UPDATE_TIME);
        serviceIntent.setPackage("com.elijahbosley.textclockwidget.UpdateTimeService");
        context.startService(serviceIntent);
    }

}

