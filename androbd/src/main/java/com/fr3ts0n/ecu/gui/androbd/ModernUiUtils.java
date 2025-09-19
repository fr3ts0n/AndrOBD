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

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Utility class for resource optimization and modern UI practices
 * Compatible with Android 4.1+
 */
public class ModernUiUtils {

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
     * Recursively cleanup view hierarchy to prevent memory leaks
     * @param view Root view to cleanup
     */
    public static void cleanupViewHierarchy(View view) {
        if (view == null) return;
        try {
            if (view instanceof ViewGroup viewGroup) {
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    cleanupViewHierarchy(viewGroup.getChildAt(i));
                }
            }

            // Clear any background drawables to prevent memory leaks
            view.setBackground(null);

            // Clear any click listeners
            view.setOnClickListener(null);
            view.setOnTouchListener(null);
        } catch (Exception e) {
            // Ignore any errors
        }
    }

}