package com.elijahbosley.textclockwidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by elibosley on 7/8/16
 */
public final class UpdateTimeService extends Service {
    static final String UPDATE_TIME = "com.elijahbosley.textclock.action.UPDATE_TIME";

    BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");
    private CharSequence _tvTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    _tvTime = (_sdfWatchTime.format(new Date()));
                    RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.text_clock_widget);
                    views.setTextViewText(R.id.appwidget_text, _tvTime);
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        return 1;
    }

}