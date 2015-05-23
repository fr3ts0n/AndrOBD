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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fr3ts0n.ecu.Conversions;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Adapter for OBD data gauge display
 *
 * @author erwin
 */
@SuppressLint("ViewHolder")
public class ObdGaugeAdapter extends ArrayAdapter<EcuDataPv> implements
	PvChangeListener
{
	transient public static final String FID_GAUGE_SERIES = "GAUGE_SERIES";
	transient protected LayoutInflater mInflater;
	private static int resourceId;
	private transient int minWidth;
	private transient int minHeight;
	/**
	 * List of colors to be used for series
	 */
	public static final int[] colors =
		{
			Color.RED,
			Color.YELLOW,
			Color.BLUE,
			Color.GREEN,
			Color.MAGENTA,
			Color.CYAN,
			Color.WHITE,
			Color.LTGRAY,
		};

	static class ViewHolder
	{
		TextView tvDescr;
		FrameLayout gauge;
		DialRenderer renderer;
	}

	public ObdGaugeAdapter(Context context, int resource, int minWidth, int minHeight)
	{
		super(context, resource);
		mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resourceId = resource;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#remove(java.lang.Object)
	 */
	@Override
	public void remove(EcuDataPv object)
	{
		object.remove(FID_GAUGE_SERIES);
		object.removePvChangeListener(this);
		super.remove(object);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#add(java.lang.Object)
	 */
	@Override
	public void add(EcuDataPv currPv)
	{
		CategorySeries category = (CategorySeries) currPv.get(FID_GAUGE_SERIES);
		if (category == null)
		{
			category = new CategorySeries(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));
			category.add(String.valueOf(currPv.get(EcuDataPv.FID_UNITS)),
				Double.valueOf(String.valueOf(currPv.get(EcuDataPv.FID_VALUE))));
			currPv.put(FID_GAUGE_SERIES, category);
		}
		// make this adapter to listen for PV data updates
		currPv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);

		super.add(currPv);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		EcuDataPv currPv = (EcuDataPv) getItem(position);
		CategorySeries category = (CategorySeries) currPv.get(FID_GAUGE_SERIES);

		// get data PV
		// if (convertView == null)
		{
			convertView = mInflater.inflate(resourceId, parent, false);
			convertView.getLayoutParams().width = minWidth;
			convertView.getLayoutParams().height = minHeight;

			holder = new ViewHolder();

			// description text
			holder.tvDescr = (TextView) convertView.findViewById(R.id.label);
			holder.gauge = (FrameLayout) convertView.findViewById(R.id.gauge);

			Float minValue = (Float) currPv.get(EcuDataPv.FID_MIN);
			Float maxValue = (Float) currPv.get(EcuDataPv.FID_MAX);
			if (minValue == null) minValue = 0f;
			if (maxValue == null) maxValue = 255f;

			DialRenderer renderer = new DialRenderer();
			renderer.setScale(1.2f);

			// dial background
			renderer.setPanEnabled(false);
			renderer.setShowLegend(false);

			renderer.setLabelsTextSize(16);
			renderer.setLabelsColor(Color.WHITE);
			renderer.setShowLabels(true);

			renderer.setVisualTypes(new DialRenderer.Type[]{DialRenderer.Type.NEEDLE});

			renderer.setMinValue(minValue);
			renderer.setMaxValue(maxValue);

			// renderer.setChartTitleTextSize(24);
			renderer.setChartTitle(String.valueOf(currPv.get(EcuDataPv.FID_UNITS)));
			renderer.setChartTitleTextSize(18);

			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[position % colors.length]);
			try
			{
				r.setChartValuesFormat(Conversions.formats[currPv.getAsInt(EcuDataPv.FID_DECIMALS)]);
			} catch (Exception e)
			{
				// ignore
			}
			renderer.addSeriesRenderer(0, r);

			holder.renderer = renderer;

			holder.gauge.addView(ChartFactory.getDialChartView(getContext(), category, renderer), 0);
			convertView.setTag(holder);
		}
		// else
		// {
		// 	holder = (ViewHolder)convertView.getTag();
		// }

		holder.tvDescr.setTextColor(colors[position % colors.length]);
		holder.tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));

		return convertView;
	}

	@Override
	public void pvChanged(PvChangeEvent event)
	{
		ProcessVar currPv = (ProcessVar) event.getSource();
		CategorySeries series = (CategorySeries) currPv.get(FID_GAUGE_SERIES);
		series.set(0,
			String.valueOf(currPv.get(EcuDataPv.FID_UNITS)),
			(Float) event.getValue());
	}
}
