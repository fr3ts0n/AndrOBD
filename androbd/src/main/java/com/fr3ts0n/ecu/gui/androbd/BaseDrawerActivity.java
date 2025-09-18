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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fr3ts0n.ecu.prot.obd.ObdProt;

/**
 * Base Activity with Navigation Drawer support for AndrOBD app
 * 
 * This class provides a unified navigation drawer implementation that can be
 * inherited by other activities in the app, ensuring consistent navigation
 * behavior across all major screens while maintaining compatibility with
 * Android 4.1+ (API 16+).
 * 
 * Features:
 * - Unified navigation menu across all activities
 * - Preserves existing menu functionality and context menus
 * - Integrates with existing themes (AppTheme.Dark/AppTheme)
 * - Maintains backward compatibility with older Android versions
 * - Handles navigation item selections and routing back to MainActivity
 * 
 * Usage:
 * 1. Extend this class instead of Activity
 * 2. Call super.onCreate() before setting content
 * 3. Use setDrawerContentView() instead of setContentView()
 * 4. Existing context menus and activity-specific functionality is preserved
 */
public abstract class BaseDrawerActivity extends Activity 
    implements NavigationDrawerHelper.NavigationDrawerListener {
    
    /**
     * Navigation drawer helper for managing drawer functionality
     */
    protected NavigationDrawerHelper navigationDrawerHelper;
    
    /**
     * Flag to track if drawer is enabled for this activity
     */
    protected boolean drawerEnabled = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply the current theme based on night mode
        setTheme(MainActivity.nightMode ? R.style.AppTheme_Dark : R.style.AppTheme);
        
        if (drawerEnabled) {
            initializeNavigationDrawer();
        }
    }
    
    /**
     * Initialize the navigation drawer
     * 
     * Sets up the drawer layout and navigation view, integrating the drawer
     * with the existing action bar and maintaining compatibility.
     */
    protected void initializeNavigationDrawer() {
        // Set the drawer layout as the main content view
        super.setContentView(R.layout.activity_main_drawer);
        
        // Initialize the navigation drawer helper
        navigationDrawerHelper = new NavigationDrawerHelper(this, this);
        navigationDrawerHelper.setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);
        
        // Setup action bar with home button for drawer toggle
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
    
    /**
     * Set content in the drawer's content frame
     * 
     * This method should be used instead of setContentView() for activities
     * that use the navigation drawer.
     * 
     * @param layoutResID Resource ID of the layout to display
     */
    protected void setDrawerContentView(int layoutResID) {
        setDrawerContentView(getLayoutInflater().inflate(layoutResID, null));
    }
    
    /**
     * Set content view in the drawer's content frame
     * 
     * @param view The view to display in the content area
     */
    protected void setDrawerContentView(View view) {
        if (navigationDrawerHelper != null) {
            navigationDrawerHelper.wrapContentInDrawer(view);
        } else {
            // Fallback if drawer is not enabled
            super.setContentView(view);
        }
    }
    
    @Override
    public void setContentView(int layoutResID) {
        if (drawerEnabled && navigationDrawerHelper != null) {
            setDrawerContentView(layoutResID);
        } else {
            super.setContentView(layoutResID);
        }
    }
    
    @Override
    public void setContentView(View view) {
        if (drawerEnabled && navigationDrawerHelper != null) {
            setDrawerContentView(view);
        } else {
            super.setContentView(view);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation drawer toggle (hamburger menu)
        if (item.getItemId() == android.R.id.home && navigationDrawerHelper != null) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            }
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // First check if navigation drawer should handle the back press
        if (navigationDrawerHelper != null && navigationDrawerHelper.onBackPressed()) {
            return; // Drawer was closed, don't proceed with other back handling
        }
        
        super.onBackPressed();
    }
    
    // NavigationDrawerListener interface implementation
    
    /**
     * Handle navigation drawer item selection
     * 
     * Routes navigation selections back to MainActivity for consistent
     * handling of all navigation actions.
     * 
     * @param menuId the ID of the selected menu item
     * @return true if the selection was handled
     */
    @Override
    public boolean onNavigationItemSelected(int menuId) {
        // For non-MainActivity activities, route navigation back to MainActivity
        // except for certain actions that should be handled locally
        
        switch (menuId) {
            case R.id.settings:
                // Settings can be opened from any activity
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
                
            case R.id.day_night_mode:
                // Day/night mode toggle should affect current activity
                boolean newNightMode = !MainActivity.nightMode;
                MainActivity.prefs.edit().putBoolean("night_mode", newNightMode).apply();
                // Recreate activity to apply theme change
                recreate();
                return true;
                
            default:
                // For all other navigation items, go back to MainActivity
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                
                // Add the menu action as an extra so MainActivity can handle it
                mainIntent.putExtra("navigation_action", menuId);
                
                startActivity(mainIntent);
                return true;
        }
    }
    
    /**
     * Update navigation drawer state
     * 
     * This method is called to update the navigation drawer's menu items
     * based on the current application state. For non-MainActivity activities,
     * we use the current state from MainActivity.
     */
    @Override
    public void updateNavigationState() {
        if (navigationDrawerHelper != null) {
            // Use the current mode from MainActivity
            boolean isConnected = (MainActivity.getCurrentMode() == MainActivity.MODE.ONLINE || 
                                 MainActivity.getCurrentMode() == MainActivity.MODE.DEMO);
            boolean obdServicesEnabled = (CommService.elm.getService() != ObdProt.OBD_SVC_NONE);
            
            navigationDrawerHelper.updateNavigationVisibility(isConnected, obdServicesEnabled);
        }
    }
    
    /**
     * Disable the navigation drawer for this activity
     * 
     * Some activities may want to disable the drawer (e.g., during critical operations)
     */
    protected void disableDrawer() {
        drawerEnabled = false;
    }
    
    /**
     * Enable the navigation drawer for this activity
     */
    protected void enableDrawer() {
        drawerEnabled = true;
    }
}