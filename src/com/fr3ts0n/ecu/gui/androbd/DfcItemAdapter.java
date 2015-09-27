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
import android.widget.TextView;

import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.pvs.IndexedProcessVar;
import com.fr3ts0n.pvs.PvList;

import java.util.Collection;

/**
 * Adapter to display OBD DFCs
 *
 * @author erwin
 */
public class DfcItemAdapter extends ObdItemAdapter
{
	public DfcItemAdapter(Context context, int resource, PvList pvs)
	{
		super(context, resource, pvs);
	}

	@Override
	public Collection<Object> getPreferredItems(PvList pvs, String preferenceKey)
	{
		return pvs.values();
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.ecu.gui.androbd.ObdItemAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View v, ViewGroup parent)
	{
		// get data PV
		IndexedProcessVar currPv = (IndexedProcessVar) getItem(position);

		if (v == null)
		{
			v = mInflater.inflate(R.layout.obd_item, parent, false);
		}
		TextView tvDescr = (TextView) v.findViewById(R.id.obd_label);
		TextView tvValue = (TextView) v.findViewById(R.id.obd_units);

		tvValue.setText(String.valueOf(currPv.get(EcuCodeItem.FID_CODE)));
		tvDescr.setText(String.valueOf(currPv.get(EcuCodeItem.FID_DESCRIPT)));

		return v;
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.ecu.gui.androbd.ObdItemAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View v, ViewGroup parent)
	{
		return getView(position, v, parent);
	}
}
