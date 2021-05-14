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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.pvs.PvList;

import java.util.Collection;

/**
 * Adapter to display OBD TID items from a process variable list
 *
 * @author erwin
 */
public class TidItemAdapter extends ObdItemAdapter
{
    public TidItemAdapter(Context context, int resource, PvList pvs)
    {
        super(context, resource, pvs);
    }

    @Override
    public Collection getPreferredItems(PvList pvs)
    {
        return pvs.values();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // get data PV
        EcuDataPv currPv = (EcuDataPv) getItem(position);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.obd_item, parent, false);
        }

        // Show icon
        ImageView ivIcon = convertView.findViewById(R.id.obd_icon);
        ivIcon.setVisibility(View.VISIBLE);

        // Hide value
        TextView tvValue = convertView.findViewById(R.id.obd_value);
        tvValue.setVisibility(View.GONE);

        // set description
        TextView tvUnits = convertView.findViewById(R.id.obd_units);
        tvUnits.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));

        return convertView;
    }
}
