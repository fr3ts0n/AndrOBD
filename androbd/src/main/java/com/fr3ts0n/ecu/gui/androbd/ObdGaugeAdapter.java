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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fr3ts0n.ecu.EcuDataPv;
import com.github.anastr.speedviewlib.AwesomeSpeedometer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for OBD data gauge display
 *
 * @author erwin
 */
class ObdGaugeAdapter extends RecyclerView.Adapter<ObdGaugeAdapter.GaugeViewHolder>
{
	private final Context context;
	private final int resourceId;
	private final List<EcuDataPv> items = new ArrayList<>();
	private OnItemLongClickListener longClickListener;

	/** format for numeric labels */
	private static final NumberFormat labelFormat = new DecimalFormat("0;-#");

	/** Callback for a long-press on a gauge item. */
	interface OnItemLongClickListener
	{
		boolean onItemLongClick(int position);
	}

	static class GaugeViewHolder extends RecyclerView.ViewHolder
	{
		AwesomeSpeedometer gauge;
		TextView tvDescr;

		GaugeViewHolder(View itemView)
		{
			super(itemView);
			gauge = itemView.findViewById(R.id.chart);
			tvDescr = itemView.findViewById(R.id.label);
		}
	}

	public ObdGaugeAdapter(Context context, int resource)
	{
		this.context = context;
		this.resourceId = resource;
	}

	/** Sets the listener invoked on a long-press of a gauge item. */
	void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		longClickListener = listener;
	}

	/** Returns the item at {@code position}, or {@code null} if out of range. */
	EcuDataPv getItem(int position)
	{
		return (position >= 0 && position < items.size()) ? items.get(position) : null;
	}

	/** Returns the position of {@code pv} in the current item list, or -1 if not present. */
	int getPosition(EcuDataPv pv)
	{
		return items.indexOf(pv);
	}

	/** Appends {@code pv} to the item list and notifies the RecyclerView. */
	void add(EcuDataPv pv)
	{
		items.add(pv);
		notifyItemInserted(items.size() - 1);
	}

	/** Removes all items and notifies the RecyclerView. */
	void clear()
	{
		int size = items.size();
		items.clear();
		notifyItemRangeRemoved(0, size);
	}

	@Override
	public int getItemCount()
	{
		return items.size();
	}

	@NonNull
	@Override
	public GaugeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(context).inflate(resourceId, parent, false);
		return new GaugeViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull GaugeViewHolder holder, int position)
	{
		EcuDataPv currPv = getItem(position);
		if (currPv == null) return;

		holder.itemView.setOnLongClickListener(v ->
			longClickListener != null
				&& longClickListener.onItemLongClick(holder.getBindingAdapterPosition()));

		// Get display color ...
		int pidColor = ColorAdapter.getItemColor(currPv);

		// Taint background with PID color
		holder.itemView.setBackgroundColor(pidColor & 0x10FFFFFF);
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
	}
}
