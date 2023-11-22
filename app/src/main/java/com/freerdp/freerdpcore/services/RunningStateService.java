package com.freerdp.freerdpcore.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class RunningStateService extends Service {
    private static final String CHANNEL_ID = "com.xiaomi.mslgrdp";
    private static final int NOTIFICATION_ID = 999;
    private static final String NOTIFICATION_NAME = "RunningStateService";
    private static final String TAG = "RunningStateService";
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RunningStateService", "onCreate ...");
        this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RunningStateService", "startForeground ...");
        startForeground(NOTIFICATION_ID, configureNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RunningStateService", "onDestroy ...");
    }

    private Notification configureNotification() {
        this.mNotificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "RunningStateService", NotificationManager.IMPORTANCE_HIGH));
        Notification notification = new Notification.Builder(this, CHANNEL_ID).setOngoing(true).setSmallIcon(R.drawable.ic_launcher_background).build();
        return notification;
    }
}