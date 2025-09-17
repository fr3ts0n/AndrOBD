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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Utility class for resource optimization and modern UI practices
 * Compatible with Android 4.1+
 */
public class ModernUiUtils {
    
    private static final Logger log = Logger.getLogger(ModernUiUtils.class.getName());
    private static ExecutorService backgroundExecutor;
    
    /**
     * Get a shared background executor for non-UI operations
     * This reduces thread creation overhead
     */
    public static synchronized ExecutorService getBackgroundExecutor() {
        if (backgroundExecutor == null || backgroundExecutor.isShutdown()) {
            backgroundExecutor = Executors.newFixedThreadPool(
                Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
            );
        }
        return backgroundExecutor;
    }
    
    /**
     * Safely shutdown background executor
     */
    public static synchronized void shutdownBackgroundExecutor() {
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
            try {
                if (!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    backgroundExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                backgroundExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            backgroundExecutor = null;
        }
    }
    
    /**
     * Optimize ListView performance with modern techniques
     * @param listView ListView to optimize
     */
    public static void optimizeListView(ListView listView) {
        if (listView == null) return;
        
        // Enable smooth scrolling
        listView.setSmoothScrollbarEnabled(true);
        
        // Optimize scrolling performance  
        listView.setScrollingCacheEnabled(true);
        
        // Enable fast scroll for large lists
        listView.setFastScrollEnabled(true);
        
        // Optimize drawing cache
        listView.setDrawingCacheEnabled(true);
        
        // Set optimal scrollbar properties
        listView.setScrollbarFadingEnabled(true);
    }
    
    /**
     * Check if device is in landscape mode
     * @param context Application context
     * @return true if in landscape mode
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    /**
     * Check if device is a tablet
     * @param context Application context
     * @return true if device is likely a tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
    /**
     * Get optimal column count for grid layouts based on screen size
     * @param context Application context
     * @param itemWidthDp Minimum item width in dp
     * @return optimal column count
     */
    public static int getOptimalColumnCount(Context context, int itemWidthDp) {
        float screenWidthDp = context.getResources().getDisplayMetrics().widthPixels 
            / context.getResources().getDisplayMetrics().density;
        return Math.max(1, (int) (screenWidthDp / itemWidthDp));
    }
    
    /**
     * Recursively cleanup view hierarchy to prevent memory leaks
     * @param view Root view to cleanup
     */
    public static void cleanupViewHierarchy(View view) {
        if (view == null) return;
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                cleanupViewHierarchy(viewGroup.getChildAt(i));
            }
        }
        
        // Clear any background drawables to prevent memory leaks
        view.setBackground(null);
        
        // Clear any click listeners
        view.setOnClickListener(null);
        view.setOnTouchListener(null);
    }
    
    /**
     * Safely finish activity with proper cleanup
     * @param activity Activity to finish
     */
    public static void safeFinishActivity(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        
        try {
            // Cleanup view hierarchy
            if (activity.findViewById(android.R.id.content) != null) {
                cleanupViewHierarchy(activity.findViewById(android.R.id.content));
            }
            
            activity.finish();
        } catch (Exception e) {
            log.warning("Error finishing activity: " + e.getMessage());
        }
    }
    
    /**
     * Convert dp to pixels
     * @param context Application context
     * @param dp Value in dp
     * @return Value in pixels
     */
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    
    /**
     * Convert pixels to dp
     * @param context Application context
     * @param px Value in pixels
     * @return Value in dp
     */
    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }
}