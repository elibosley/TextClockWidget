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

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TextClockWidgetConfigureActivity TextClockWidgetConfigureActivity}
 */
public class TextClockWidget extends AppWidgetProvider {
    private static String clockText = "hi";

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


        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Intent serviceIntent = new Intent(UpdateTimeService.UPDATE_TIME);
        serviceIntent.setPackage("com.elijahbosley.textclockwidget.UpdateTimeService");
        context.startService(serviceIntent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        TimeString timeString = new TimeString();
        clockText = timeString.timeAsString();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_clock_widget);
        views.setTextViewText(R.id.appwidget_text, clockText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}

