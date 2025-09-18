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
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * Helper class for managing Navigation Drawer functionality across multiple activities.
 * 
 * This class provides a consistent navigation drawer implementation that can be
 * integrated into existing activities while maintaining compatibility with
 * Android 4.1+ (API 16+).
 * 
 * Features:
 * - Unified navigation menu across all major screens
 * - Preserves existing menu functionality and dynamic behavior
 * - Integrates with existing themes (AppTheme.Dark/AppTheme)
 * - Maintains backward compatibility with older Android versions
 * 
 * Usage:
 * 1. Include drawer layout XML in activity
 * 2. Initialize drawer helper in onCreate()
 * 3. Handle navigation selections via callback interface
 */
public class NavigationDrawerHelper implements NavigationView.OnNavigationItemSelectedListener {
    
    /**
     * Interface for activities that use the navigation drawer
     */
    public interface NavigationDrawerListener {
        /**
         * Called when a navigation item is selected
         * @param menuId the ID of the selected menu item
         * @return true if the selection was handled
         */
        boolean onNavigationItemSelected(int menuId);
        
        /**
         * Called to update the current navigation state (e.g., connection status)
         */
        void updateNavigationState();
    }
    
    private final Activity activity;
    private final NavigationDrawerListener listener;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    /**
     * Constructor for NavigationDrawerHelper
     * 
     * @param activity The activity that will host the navigation drawer
     * @param listener Callback interface for handling navigation events
     */
    public NavigationDrawerHelper(Activity activity, NavigationDrawerListener listener) {
        this.activity = activity;
        this.listener = listener;
    }
    
    /**
     * Initialize the navigation drawer components
     * 
     * This method should be called after setContentView() in the activity's onCreate()
     * method. It sets up the drawer layout, navigation view, and handles the hamburger
     * menu icon integration with the action bar.
     * 
     * @param drawerLayoutId Resource ID of the DrawerLayout
     * @param navigationViewId Resource ID of the NavigationView
     */
    public void setupNavigationDrawer(int drawerLayoutId, int navigationViewId) {
        drawerLayout = activity.findViewById(drawerLayoutId);
        navigationView = activity.findViewById(navigationViewId);
        
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }
    
    /**
     * Wraps existing content in the navigation drawer layout
     * 
     * This method takes the current activity content and places it inside
     * the drawer's content frame, allowing existing activities to gain
     * navigation drawer functionality with minimal changes.
     * 
     * @param existingContent The view that represents the existing activity content
     */
    public void wrapContentInDrawer(View existingContent) {
        if (drawerLayout != null) {
            View contentFrame = drawerLayout.findViewById(R.id.content_frame);
            if (contentFrame instanceof ViewGroup) {
                ViewGroup contentContainer = (ViewGroup) contentFrame;
                contentContainer.removeAllViews();
                
                // Remove the existing content from its parent if it has one
                if (existingContent.getParent() != null) {
                    ((ViewGroup) existingContent.getParent()).removeView(existingContent);
                }
                
                contentContainer.addView(existingContent);
            }
        }
    }
    
    /**
     * Handle navigation item selection
     * 
     * Maps navigation drawer menu items to their corresponding action IDs
     * and delegates to the activity's listener for actual handling.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Map navigation drawer items to original menu item IDs
        int actionId = mapNavigationToActionId(item.getItemId());
        
        if (actionId != -1) {
            // Close the drawer first
            closeDrawer();
            
            // Delegate to the activity listener
            return listener.onNavigationItemSelected(actionId);
        }
        
        return false;
    }
    
    /**
     * Map navigation drawer menu items to original action bar menu item IDs
     * 
     * This maintains compatibility with existing menu handling logic by
     * translating navigation drawer selections to the equivalent action bar
     * menu item IDs.
     */
    private int mapNavigationToActionId(int navigationId) {
        switch (navigationId) {
            // Connection Management
            case R.id.nav_connect:
                return R.id.secure_connect_scan;
            case R.id.nav_disconnect:
                return R.id.disconnect;
                
            // Data Management
            case R.id.nav_save:
                return R.id.save;
            case R.id.nav_load:
                return R.id.load;
                
            // Display Options
            case R.id.nav_day_night_mode:
                return R.id.day_night_mode;
                
            // Configuration
            case R.id.nav_settings:
                return R.id.settings;
            case R.id.nav_plugin_manager:
                return R.id.plugin_manager;
            case R.id.nav_reset_preselections:
                return R.id.reset_preselections;
                
            // OBD Services
            case R.id.nav_service_vid_data:
                return R.id.service_vid_data;
            case R.id.nav_service_data:
                return R.id.service_data;
            case R.id.nav_service_freezeframes:
                return R.id.service_freezeframes;
            case R.id.nav_service_testcontrol:
                return R.id.service_testcontrol;
            case R.id.nav_service_codes:
                return R.id.service_codes;
            case R.id.nav_service_clearcodes:
                return R.id.service_clearcodes;
                
            default:
                return -1;
        }
    }
    
    /**
     * Update navigation menu visibility based on current state
     * 
     * This method maintains the dynamic behavior of the original menu system,
     * showing/hiding and enabling/disabling menu items based on the current
     * application state (online/offline, connection status, etc.).
     */
    public void updateNavigationVisibility(boolean isConnected, boolean obdServicesEnabled) {
        if (navigationView == null) return;
        
        // Connection state items
        navigationView.getMenu().findItem(R.id.nav_connect).setVisible(!isConnected);
        navigationView.getMenu().findItem(R.id.nav_disconnect).setVisible(isConnected);
        
        // OBD services availability
        setNavigationGroupEnabled(R.id.group_obd_services, obdServicesEnabled);
    }
    
    /**
     * Enable/disable a navigation menu group
     */
    private void setNavigationGroupEnabled(int groupId, boolean enabled) {
        if (navigationView != null) {
            navigationView.getMenu().setGroupEnabled(groupId, enabled);
        }
    }
    
    /**
     * Close the navigation drawer
     */
    public void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    
    /**
     * Check if the navigation drawer is open
     */
    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }
    
    /**
     * Handle back button press - close drawer if open
     * 
     * @return true if the back press was handled (drawer was closed)
     */
    public boolean onBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
            return true;
        }
        return false;
    }
}