/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Background service for OBD communication
 * Provides continuous monitoring and data collection even when app is in background
 */
public class ObdBackgroundService extends Service {
    
    private static final String TAG = "ObdBackgroundService";
    private static final Logger log = Logger.getLogger(TAG);
    
    public static final String CHANNEL_ID = "obd_service_channel";
    private static final int NOTIFICATION_ID = 1001;
    
    // Service states
    public enum ServiceState {
        STOPPED, STARTING, RUNNING, STOPPING
    }
    
    private ServiceState currentState = ServiceState.STOPPED;
    private CommService commService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<ServiceStateListener> stateListeners = new ArrayList<>();
    
    // Binder for local service binding
    public class LocalBinder extends Binder {
        public ObdBackgroundService getService() {
            return ObdBackgroundService.this;
        }
    }
    
    private final IBinder binder = new LocalBinder();
    
    // Interface for service state callbacks
    public interface ServiceStateListener {
        void onServiceStateChanged(ServiceState newState);
        void onDataReceived(String data);
        void onConnectionStateChanged(CommService.STATE connectionState);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        log.info("ObdBackgroundService created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (currentState == ServiceState.STOPPED) {
            startForegroundService();
            initializeCommService();
        }
        // Return sticky to restart service if killed by system
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    @Override
    public void onDestroy() {
        stopCommService();
        currentState = ServiceState.STOPPED;
        notifyStateListeners();
        log.info("ObdBackgroundService destroyed");
        super.onDestroy();
    }
    
    private void startForegroundService() {
        Notification notification = createNotification("OBD Service Running", "Monitoring vehicle data...");
        startForeground(NOTIFICATION_ID, notification);
        currentState = ServiceState.RUNNING;
        notifyStateListeners();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "OBD Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background OBD data monitoring");
            channel.setShowBadge(false);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification(String title, String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }
    
    private void initializeCommService() {
        // Initialize communication service based on selected medium
        Handler serviceHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                handleCommServiceMessage(msg);
            }
        };
        
        switch (CommService.medium) {
            case BLUETOOTH:
                commService = new BtCommService(this, serviceHandler);
                break;
            case USB:
                commService = new UsbCommService(this, serviceHandler);
                break;
            case NETWORK:
                commService = new NetworkCommService(this, serviceHandler);
                break;
        }
        
        if (commService != null) {
            commService.start();
        }
    }
    
    private void stopCommService() {
        if (commService != null) {
            commService.stop();
            commService = null;
        }
    }
    
    private void handleCommServiceMessage(android.os.Message msg) {
        // Handle messages from communication service
        // This would process data and notify listeners
        switch (msg.what) {
            case MainActivity.MESSAGE_STATE_CHANGE:
                CommService.STATE state = (CommService.STATE) msg.obj;
                notifyConnectionStateChanged(state);
                
                // Update notification based on connection state
                String notificationText = getNotificationTextForState(state);
                updateNotification("OBD Service", notificationText);
                break;
                
            case MainActivity.MESSAGE_DATA_ITEMS_CHANGED:
                // Process received data
                String data = msg.obj != null ? msg.obj.toString() : "";
                notifyDataReceived(data);
                break;
        }
    }
    
    private String getNotificationTextForState(CommService.STATE state) {
        switch (state) {
            case CONNECTING:
                return "Connecting to OBD device...";
            case CONNECTED:
                return "Connected - Monitoring vehicle data";
            case LISTEN:
                return "Waiting for connection...";
            default:
                return "OBD service running";
        }
    }
    
    private void updateNotification(String title, String message) {
        Notification notification = createNotification(title, message);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }
    
    // Public methods for service control
    public void connectToDevice(Object device, boolean secure) {
        if (commService != null) {
            commService.connect(device, secure);
        }
    }
    
    public void disconnect() {
        if (commService != null) {
            commService.stop();
        }
    }
    
    public ServiceState getCurrentState() {
        return currentState;
    }
    
    public CommService.STATE getConnectionState() {
        return commService != null ? commService.getState() : CommService.STATE.NONE;
    }
    
    // Listener management
    public void addStateListener(ServiceStateListener listener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }
    
    public void removeStateListener(ServiceStateListener listener) {
        stateListeners.remove(listener);
    }
    
    private void notifyStateListeners() {
        mainHandler.post(() -> {
            for (ServiceStateListener listener : stateListeners) {
                listener.onServiceStateChanged(currentState);
            }
        });
    }
    
    private void notifyDataReceived(String data) {
        mainHandler.post(() -> {
            for (ServiceStateListener listener : stateListeners) {
                listener.onDataReceived(data);
            }
        });
    }
    
    private void notifyConnectionStateChanged(CommService.STATE state) {
        mainHandler.post(() -> {
            for (ServiceStateListener listener : stateListeners) {
                listener.onConnectionStateChanged(state);
            }
        });
    }
}