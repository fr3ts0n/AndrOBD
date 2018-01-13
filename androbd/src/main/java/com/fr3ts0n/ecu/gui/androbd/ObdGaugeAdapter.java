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
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import org.achartengine.GraphicalView;
import org.achartengine.chart.DialChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Adapter for OBD data gauge display
 *
 * @author erwin
 */
public class ObdGaugeAdapter extends ArrayAdapter<EcuDataPv> implements
	PvChangeListener
{
	transient public static final String FID_GAUGE_SERIES = "GAUGE_SERIES";
	transient protected LayoutInflater mInflater;
	private static int resourceId;
	private transient int minWidth;
	private transient int minHeight;
	private DisplayMetrics mDisplayMetrics;

	/** format for numeric labels */
	protected static final NumberFormat labelFormat = new DecimalFormat("0;-#");

	static class ViewHolder
	{
		FrameLayout gauge;
		TextView tvDescr;
	}

	public ObdGaugeAdapter(Context context, int resource, int minWidth, int minHeight, DisplayMetrics metrics)
	{
		super(context, resource);
		mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resourceId = resource;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		mDisplayMetrics = metrics;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#remove(java.lang.Object)
	 */
	@Override
	public void remove(EcuDataPv object)
	{
		object.remove(FID_GAUGE_SERIES);
		object.removePvChangeListener(this);
		object.setRenderingComponent(null);
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
					((Number)currPv.get(EcuDataPv.FID_VALUE)).doubleValue());
			currPv.put(FID_GAUGE_SERIES, category);
			currPv.setRenderingComponent(null);
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
		EcuDataPv currPv = getItem(position);
		int pid = currPv.getAsInt(EcuDataPv.FID_PID);

		// if no recycled convertView delivered, then create a new one
		if (convertView == null)
		{
			convertView = mInflater.inflate(resourceId, parent, false);

			holder = new ViewHolder();
			// get all views into view holder
			holder.gauge = (FrameLayout) convertView.findViewById(R.id.gauge);
			holder.tvDescr = (TextView) convertView.findViewById(R.id.label);

			// remember this view holder
			convertView.setTag(holder);
		}
		else
		{
			// recall previous holder
			holder = (ViewHolder)convertView.getTag();
		}

		convertView.getLayoutParams().width = minWidth;
		convertView.getLayoutParams().height = minHeight;

		// if no rendering component is registered with PV, then create and register new one
		DialChart chartView = (DialChart)currPv.getRenderingComponent();
		if(chartView == null)
		{
			CategorySeries category = (CategorySeries) currPv.get(FID_GAUGE_SERIES);

			Number minValue = (Number) currPv.get(EcuDataPv.FID_MIN);
			Number maxValue = (Number) currPv.get(EcuDataPv.FID_MAX);
			if (minValue == null) minValue = 0f;
			if (maxValue == null) maxValue = 255f;

			DialRenderer renderer = new DialRenderer();
			renderer.setScale(1.25f);

			// dial background
			renderer.setPanEnabled(false);
			renderer.setShowLegend(false);
			renderer.setShowLabels(true);

			renderer.setLabelsTextSize(mDisplayMetrics.densityDpi / 10);
			renderer.setLabelsColor(Color.WHITE);
			renderer.setShowLabels(true);

			renderer.setVisualTypes(new DialRenderer.Type[]{DialRenderer.Type.NEEDLE});

			renderer.setMinValue(minValue.doubleValue());
			renderer.setMaxValue(maxValue.doubleValue());

			renderer.setChartTitle(currPv.getUnits());
			renderer.setChartTitleTextSize(mDisplayMetrics.densityDpi/10);

			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(ChartActivity.getItemColor(pid));
			try
			{
				r.setChartValuesFormat(labelFormat);
			}
			catch (Exception e)
			{
				// ignore
			}
			renderer.addSeriesRenderer(0, r);


			// create chart view and register with PV
			chartView = new DialChart(category, renderer);
			currPv.setRenderingComponent(chartView);
		}
		convertView.setBackgroundColor(ChartActivity.getItemColor(pid) & 0x08FFFFFF);

		// set new values for display
		holder.tvDescr.setTextColor(ChartActivity.getItemColor(pid));
		holder.tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));
		// replace DialChart if needed
		holder.gauge.removeViewAt(0);
		holder.gauge.addView(new GraphicalView(getContext(), chartView), 0);

		return convertView;
	}

	@Override
	public void pvChanged(PvChangeEvent event)
	{
		ProcessVar currPv = (ProcessVar) event.getSource();
		CategorySeries series = (CategorySeries) currPv.get(FID_GAUGE_SERIES);
		series.set(0,
			String.valueOf(currPv.get(EcuDataPv.FID_UNITS)),
				((Number)event.getValue()).doubleValue());
	}
}
