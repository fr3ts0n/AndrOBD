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
import android.widget.CheckBox;
import android.widget.TextView;

import com.fr3ts0n.ecu.Conversion;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.pvs.IndexedProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.fr3ts0n.pvs.PvList;

import org.achartengine.model.XYSeries;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Adapter for OBD data items (PVs)
 *
 * @author erwin
 */
public class ObdItemAdapter extends ArrayAdapter<Object>
	implements PvChangeListener
{
	transient protected PvList pvs;
	transient protected LayoutInflater mInflater;
	transient public static final String FID_DATA_SERIES = "SERIES";
	/** allow data updates to be handled */
	public static boolean allowDataUpdates = true;

	public ObdItemAdapter(Context context, int resource, PvList pvs)
	{
		super(context, resource);
		mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setPvList(pvs);
	}

	/**
	 * set / update PV list
	 *
	 * @param pvs process variable list
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setPvList(PvList pvs)
	{
		this.pvs = pvs;
		clear();
		Object[] pidPvs = pvs.values().toArray();
		Arrays.sort(pidPvs, pidSorter);
		addAll(pidPvs);
		if (this.getClass() == ObdItemAdapter.class)
			addAllDataSeries();
	}

	@SuppressWarnings("rawtypes")
	static Comparator pidSorter = new Comparator()
	{
		public int compare(Object lhs, Object rhs)
		{
			return   ((IndexedProcessVar)lhs).getAsInt(EcuDataPv.FID_PID)
				     - ((IndexedProcessVar)rhs).getAsInt(EcuDataPv.FID_PID);
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// get data PV
		EcuDataPv currPv = (EcuDataPv) getItem(position);

		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.obd_item, parent, false);
		}
		// set alternating background color
		// convertView.setBackgroundColor((position % 2)==0 ? Color.LTGRAY : Color.WHITE);

		// fill view fields with data

		// description text
		TextView tvDescr = (TextView) convertView.findViewById(R.id.obd_label);
		tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));
		CheckBox cbChecked = (CheckBox) convertView.findViewById(R.id.check);
		cbChecked.setVisibility(View.VISIBLE);

		// format value string
		String fmtText;
		Object colVal = currPv.get(EcuDataPv.FID_VALUE);
		Object cnvObj = currPv.get(EcuDataPv.FID_CNVID);
		try
		{
			if (cnvObj != null && cnvObj instanceof Conversion[])
			{
				Conversion[] cnv = (Conversion[]) cnvObj;
				fmtText = cnv[EcuDataItem.cnvSystem].physToPhysFmtString(
					(Float) colVal,
					String.valueOf(currPv.get(EcuDataPv.FID_FORMAT)));
			} else
			{
				fmtText = String.valueOf(colVal);
			}
		} catch (Exception ex)
		{
			fmtText = String.valueOf(colVal);
		}
		// set value
		TextView tvValue = (TextView) convertView.findViewById(R.id.obd_value);
		tvValue.setText(fmtText);
		TextView tvUnits = (TextView) convertView.findViewById(R.id.obd_units);
		tvUnits.setText(currPv.getUnits());

		return convertView;
	}

	/**
	 * Add data series to all process variables
	 */
	protected synchronized void addAllDataSeries()
	{
		IndexedProcessVar pv;
		@SuppressWarnings("unchecked")
		Iterator<IndexedProcessVar> it = pvs.values().iterator();
		while (it.hasNext())
		{
			pv = it.next();
			XYSeries series = (XYSeries) pv.get(FID_DATA_SERIES);
			if (series == null)
			{
				series = new XYSeries(String.valueOf(pv.get(EcuDataPv.FID_DESCRIPT)));
				pv.put(FID_DATA_SERIES, series);
				pv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);
			}
		}
	}

	@Override
	public void pvChanged(PvChangeEvent event)
	{
		if (allowDataUpdates)
		{
			IndexedProcessVar pv = (IndexedProcessVar) event.getSource();
			XYSeries series = (XYSeries) pv.get(FID_DATA_SERIES);
			if (series != null)
			{
				series.add(event.getTime(),
					Double.valueOf(String.valueOf(event.getValue())));
			}
		}
	}
}
