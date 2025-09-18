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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for navigation drawer menu items
 * Provides sidebar-style menu functionality
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

    private final LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> items) {
        super(context, R.layout.nav_drawer_item, items);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_drawer_item, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.nav_item_icon);
            holder.text = convertView.findViewById(R.id.nav_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationDrawerItem item = getItem(position);
        if (item != null) {
            holder.icon.setImageResource(item.getIconResId());
            holder.text.setText(item.getTitle());
            
            // Set enabled/disabled state
            convertView.setEnabled(item.isEnabled());
            convertView.setAlpha(item.isEnabled() ? 1.0f : 0.5f);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView text;
    }
}

/**
 * Navigation drawer menu item
 */
class NavigationDrawerItem {
    private final int id;
    private final String title;
    private final int iconResId;
    private boolean enabled;

    public NavigationDrawerItem(int id, String title, int iconResId) {
        this(id, title, iconResId, true);
    }

    public NavigationDrawerItem(int id, String title, int iconResId, boolean enabled) {
        this.id = id;
        this.title = title;
        this.iconResId = iconResId;
        this.enabled = enabled;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getIconResId() { return iconResId; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}