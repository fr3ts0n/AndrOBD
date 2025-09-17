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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Enhanced error handling and recovery utility
 * Provides automatic retry mechanisms and graceful degradation
 */
public class ErrorRecoveryManager {
    
    private static final Logger log = Logger.getLogger(ErrorRecoveryManager.class.getName());
    
    // Connection retry settings
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds
    private static final long EXPONENTIAL_BACKOFF_MULTIPLIER = 2;
    
    // Error tracking
    private final AtomicInteger connectionRetryCount = new AtomicInteger(0);
    private final AtomicInteger communicationErrorCount = new AtomicInteger(0);
    
    // Context for preferences and toasts
    private final Context context;
    private final SharedPreferences prefs;
    
    public ErrorRecoveryManager(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    /**
     * Handle connection errors with automatic retry
     * @param error The error that occurred
     * @param retryCallback Callback to execute for retry
     * @return true if should retry, false if max attempts reached
     */
    public boolean handleConnectionError(Exception error, Runnable retryCallback) {
        int currentAttempts = connectionRetryCount.incrementAndGet();
        
        log.warning("Connection error (attempt " + currentAttempts + "/" + MAX_RETRY_ATTEMPTS + "): " + error.getMessage());
        
        if (currentAttempts >= MAX_RETRY_ATTEMPTS) {
            log.severe("Max connection retry attempts reached. Giving up.");
            resetConnectionRetryCount();
            return false;
        }
        
        // Calculate delay with exponential backoff
        long delay = RETRY_DELAY_MS * (long) Math.pow(EXPONENTIAL_BACKOFF_MULTIPLIER, currentAttempts - 1);
        
        // Retry after delay
        ModernUiUtils.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    log.info("Retrying connection (attempt " + currentAttempts + ")...");
                    retryCallback.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warning("Retry interrupted: " + e.getMessage());
                }
            }
        });
        
        return true;
    }
    
    /**
     * Handle communication errors during active connection
     * @param error The error that occurred
     * @return true if communication should continue, false if should disconnect
     */
    public boolean handleCommunicationError(Exception error) {
        int errorCount = communicationErrorCount.incrementAndGet();
        
        log.warning("Communication error #" + errorCount + ": " + error.getMessage());
        
        // Allow some communication errors but disconnect if too many
        if (errorCount > 10) {
            log.severe("Too many communication errors. Disconnecting.");
            resetCommunicationErrorCount();
            return false;
        }
        
        return true;
    }
    
    /**
     * Reset connection retry counter (call when connection succeeds)
     */
    public void resetConnectionRetryCount() {
        connectionRetryCount.set(0);
    }
    
    /**
     * Reset communication error counter (call when establishing new connection)
     */
    public void resetCommunicationErrorCount() {
        communicationErrorCount.set(0);
    }
    
    /**
     * Check if device supports certain features based on past errors
     * This allows graceful degradation of functionality
     */
    public boolean isFeatureSupported(String featureKey) {
        return prefs.getBoolean("feature_" + featureKey + "_supported", true);
    }
    
    /**
     * Mark a feature as unsupported to avoid future errors
     */
    public void markFeatureUnsupported(String featureKey) {
        prefs.edit().putBoolean("feature_" + featureKey + "_supported", false).apply();
        log.info("Feature marked as unsupported: " + featureKey);
    }
    
    /**
     * Reset feature support flags (useful when connecting to different device)
     */
    public void resetFeatureSupport() {
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith("feature_") && key.endsWith("_supported")) {
                editor.remove(key);
            }
        }
        editor.apply();
        log.info("Feature support flags reset");
    }
}