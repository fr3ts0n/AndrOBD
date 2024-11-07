package com.fr3ts0n.ecu.gui.androbd;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

public class WorkerService extends Service {
    public static boolean isRunning = false;
    public static String ACTION_STOP_SERVICE = "com.fr3tsOn.ecu.gui.androbd.action.STOP_WORKER_SERVICE";
    private final WorkerServiceBinder binder = new WorkerServiceBinder();
    private final BroadcastReceiver receiver = new Receiver();
    private final String CHANNEL_ID = "com.fr3tsOn.ecu.gui.androbd.CHANNEL_ID";

    public class WorkerServiceBinder extends Binder {
        WorkerService getService() {
            return WorkerService.this;
        }
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
                WorkerService.this.stopForeground(true);
                WorkerService.this.stopSelf();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();

        IntentFilter intentFilter = new IntentFilter(ACTION_STOP_SERVICE);
        int receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED;

        ContextCompat.registerReceiver(this, receiver, intentFilter, receiverFlags);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();

        return Service.START_NOT_STICKY;
    }

    private void startForeground() {
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE | ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
        }

        ServiceCompat.startForeground(this, 9999, createNotification("Service started"), type);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(String value) {
        return (new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_coin)
                .setContentTitle("Androdb Backgroung Worker")
                .setContentText(value)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_headup, "Stop", makePendingIntent(ACTION_STOP_SERVICE))
                .build());
    }

    private PendingIntent makePendingIntent(String action) {
        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(action);

        return PendingIntent.getBroadcast(this, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}