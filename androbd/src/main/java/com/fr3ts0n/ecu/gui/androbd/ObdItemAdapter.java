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
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fr3ts0n.androbd.plugin.mgr.PluginManager;
import com.fr3ts0n.ecu.Conversion;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.NumericConversion;
import com.fr3ts0n.ecu.prot.obd.ObdProt;
import com.fr3ts0n.pvs.IndexedProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.fr3ts0n.pvs.PvList;

import org.achartengine.model.XYSeries;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapter for OBD data items (PVs)
 *
 * @author erwin
 */
public class ObdItemAdapter extends ArrayAdapter<Object>
	implements PvChangeListener
{
	transient protected PvList pvs;
	transient protected boolean isPidList = false;
	transient protected LayoutInflater mInflater;
	transient public static final String FID_DATA_SERIES = "SERIES";
	/** allow data updates to be handled */
	public static boolean allowDataUpdates = true;
	transient SharedPreferences prefs;


	public ObdItemAdapter(Context context, int resource, PvList pvs)
	{
		super(context, resource);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
		isPidList = (pvs == ObdProt.PidPvs);
		// get set to be displayed (filtered with preferences */
		Collection<Object> filtered = getPreferredItems(pvs, SettingsActivity.KEY_DATA_ITEMS);
		// make it a sorted array
		Object[] pidPvs = filtered.toArray();
		Arrays.sort(pidPvs, pidSorter);

		clear();
		// add all elements
		addAll(pidPvs);

		if (this.getClass() == ObdItemAdapter.class)
			addAllDataSeries();
	}

	@SuppressWarnings("rawtypes")
	static Comparator pidSorter = new Comparator()
	{
		public int compare(Object lhs, Object rhs)
		{
			// criteria 1: ID string
			int result =  lhs.toString().compareTo(rhs.toString());

			// criteria 2: description
			if(result == 0)
			{
				result = String.valueOf(((IndexedProcessVar)lhs).get(EcuDataPv.FID_DESCRIPT))
             .compareTo(String.valueOf(((IndexedProcessVar) rhs).get(EcuDataPv.FID_DESCRIPT)));
			}
			// return compare result
			return result;
		}
	};

	/**
	 * get set of data items filtered with set of preferred items to be displayed
	 * @param pvs list of PVs to be handled
	 * @param preferenceKey key of preference to be used as filter
	 * @return Set of filtered data items
	 */
	public Collection getPreferredItems(PvList pvs, String preferenceKey)
	{
		// filter PVs with preference selections
		Set<String> pidsToShow = prefs.getStringSet( SettingsActivity.KEY_DATA_ITEMS,
		                                             (Set<String>)pvs.keySet());
		return getMatchingItems(pvs, pidsToShow);
	}

	/**
	 * get set of data items filtered with set of preferred items to be displayed
	 * @param pvs list of PVs to be handled
	 * @param pidsToShow Set of keys to be used as filter
	 * @return Set of filtered data items
	 */
	public Collection<Object> getMatchingItems(PvList pvs, Set<String> pidsToShow)
	{
		HashSet<Object> filtered = new HashSet<Object>();
		for(Object key : pidsToShow)
		{
			IndexedProcessVar pv = (IndexedProcessVar) pvs.get(key);
			if(pv != null)
				filtered.add(pv);
		}
		return(filtered);
	}

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

		// fill view fields with data

		// description text
		TextView tvDescr = (TextView) convertView.findViewById(R.id.obd_label);
		tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));
		TextView tvValue = (TextView) convertView.findViewById(R.id.obd_value);
		TextView tvUnits = (TextView) convertView.findViewById(R.id.obd_units);
		ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.bar);

		// format value string
		String fmtText;
		Object colVal = currPv.get(EcuDataPv.FID_VALUE);
		Object cnvObj = currPv.get(EcuDataPv.FID_CNVID);
		Number min = (Number) currPv.get(EcuDataPv.FID_MIN);
		Number max = (Number) currPv.get(EcuDataPv.FID_MAX);
		int pid = currPv.getAsInt(EcuDataPv.FID_PID);

		try
		{
			if ( cnvObj != null
				   && cnvObj instanceof Conversion[]
					 && ((Conversion[])cnvObj)[EcuDataItem.cnvSystem] != null
				 )
			{
				Conversion cnv;
				cnv = ((Conversion[])cnvObj)[EcuDataItem.cnvSystem];
				// set formatted text
				fmtText = cnv.physToPhysFmtString((Number)colVal,
						(String)currPv.get(EcuDataPv.FID_FORMAT));
				// set progress bar only on LinearConversion
				if(    min != null
					  && max != null
					  && cnv instanceof NumericConversion)
				{
					pb.setVisibility(ProgressBar.VISIBLE);
					pb.getProgressDrawable().setColorFilter(ChartActivity.getItemColor(pid), PorterDuff.Mode.SRC_IN);
					pb.setProgress((int)(100 * ((((Number)colVal).doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue()))));
				}
				else
				{
					pb.setVisibility(ProgressBar.GONE);
				}
			} else
			{
				fmtText = String.valueOf(colVal);
			}
		} catch (Exception ex)
		{
			fmtText = String.valueOf(colVal);
		}
		// set value
		tvValue.setText(fmtText);
		tvUnits.setText(currPv.getUnits());

		return convertView;
	}

	/**
	 * Add data series to all process variables
	 */
	protected synchronized void addAllDataSeries()
	{
		String pluginStr = "";
		for (IndexedProcessVar pv : (Iterable<IndexedProcessVar>) pvs.values())
		{
			XYSeries series = (XYSeries) pv.get(FID_DATA_SERIES);
			if (series == null) {
				series = new XYSeries(String.valueOf(pv.get(EcuDataPv.FID_DESCRIPT)));
				pv.put(FID_DATA_SERIES, series);
				pv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);
			}

			// assemble data items for plugin notification
			pluginStr += String.format( "%s;%s;%s;%s\n",
				                        pv.get(EcuDataPv.FID_MNEMONIC),
										pv.get(EcuDataPv.FID_DESCRIPT),
										String.valueOf(pv.get(EcuDataPv.FID_VALUE)),
				                        pv.get(EcuDataPv.FID_UNITS)
			                          );
		}

		// notify plugins
		if(PluginManager.pluginHandler != null)
		{
			PluginManager.pluginHandler.sendDataList(pluginStr);
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
				if(event.getValue() instanceof Number)
				{
					series.add(event.getTime(),
						((Number)event.getValue()).doubleValue());

				}
			}

			// send update to plugin handler
			if(PluginManager.pluginHandler != null)
			{
				PluginManager.pluginHandler.sendDataUpdate(
					pv.get(EcuDataPv.FID_MNEMONIC).toString(),
					event.getValue().toString());
			}
		}
	}
}
