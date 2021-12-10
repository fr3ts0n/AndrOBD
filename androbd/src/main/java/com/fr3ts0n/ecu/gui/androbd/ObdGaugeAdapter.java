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
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuDataPv;
import com.github.anastr.speedviewlib.AwesomeSpeedometer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * Adapter for OBD data gauge display
 *
 * @author erwin
 */
class ObdGaugeAdapter extends ArrayAdapter<EcuDataPv>
{
	private final transient LayoutInflater mInflater;
	private static int resourceId;

	/** format for numeric labels */
	private static final NumberFormat labelFormat = new DecimalFormat("0;-#");

	static class ViewHolder
	{
		AwesomeSpeedometer gauge;
		TextView tvDescr;
	}

	public ObdGaugeAdapter(Context context, int resource)
	{
		super(context, resource);
		mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resourceId = resource;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		EcuDataPv currPv = getItem(position);
		int pid = Objects.requireNonNull(currPv).getAsInt(EcuDataPv.FID_PID);

		// if no recycled convertView delivered, then create a new one
		if (convertView == null)
		{
			convertView = mInflater.inflate(resourceId, parent, false);

			holder = new ViewHolder();
			// get all views into view holder
			holder.gauge = convertView.findViewById(R.id.chart);
			holder.tvDescr = convertView.findViewById(R.id.label);

			// remember this view holder
			convertView.setTag(holder);
		}
		else
		{
			// recall previous holder
			holder = (ViewHolder)convertView.getTag();
		}
		// Get display color ...
		int pidColor = ColorAdapter.getItemColor(currPv);

		// Taint background with PID color
		convertView.setBackgroundColor(pidColor & 0x10FFFFFF);
		// set new values for display
		holder.tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));

		Number minValue = (Number) currPv.get(EcuDataPv.FID_MIN);
		Number maxValue = (Number) currPv.get(EcuDataPv.FID_MAX);
		Number value =    (Number) currPv.get(EcuDataPv.FID_VALUE);
		String format = (String) currPv.get(EcuDataPv.FID_FORMAT);

		if (minValue == null) minValue = 0f;
		if (maxValue == null) maxValue = 255f;

		// Tick triangles show in PID color
		holder.gauge.setTrianglesColor(pidColor);
		// Use PID specific units and value format
		holder.gauge.setUnit(currPv.getUnits());
		holder.gauge.setSpeedTextListener(aFloat -> String.format(format, aFloat));

		holder.gauge.setMinSpeed(minValue.floatValue());
		holder.gauge.setMaxSpeed(maxValue.floatValue());
		holder.gauge.speedTo(value.floatValue());

		return convertView;
	}
}
