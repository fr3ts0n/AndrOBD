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

package com.fr3ts0n.ecu;

import com.fr3ts0n.prot.ProtoHeader;

import org.apache.log4j.Logger;

/**
 * Definition of a single ECU Data item (EcuDataItem)
 *
 * @author erwin
 */
public class EcuDataItem
	extends Object
	implements Cloneable
{
	public int pid;        // pid
	public int bytes;        // number of data bytes expected from vehicle
	public int ofs;        // Offset within message
	public Conversion[] cnv;        // type of conversion
	public int decimals;    // number of decimal digits
	public String label;        // text label
	public EcuDataPv pv;        // the process variable for displaying
	// Logger object
	public static final Logger log = Logger.getLogger("data.ecu");

	/**
	 * Creates a new instance of EcuDataItem
	 */
	public EcuDataItem()
	{
	}

	/**
	 * Creates a new instance of EcuDataItem
	 *
	 * @param newPid PID of data item
	 * @param offset offset within PID data (in bytes)
	 * @param numBytes length of parameter in bytes
	 * @param conversions data conversion to be used with this item
	 * @param numDecimals number of decimals to display
	 * @param labelText descriptive text label
	 */
	public EcuDataItem( int newPid,
	                    int offset,
	                    int numBytes,
	                    Conversion[] conversions,
	                    int numDecimals,
	                    String labelText)
	{
		pid = newPid;
		ofs = offset;
		bytes = numBytes;
		cnv = conversions;
		decimals = numDecimals;
		label = labelText;
		pv = new EcuDataPv();

		// initialize new PID with current data
		pv.put(EcuDataPv.FID_PID, Integer.valueOf(pid));
		pv.put(EcuDataPv.FID_DESCRIPT, label);
		pv.put(EcuDataPv.FID_UNITS, cnv != null ? cnv[EcuConversions.cnvSystem].getUnits() : "");
		pv.put(EcuDataPv.FID_VALUE, Float.valueOf(0));
		pv.put(EcuDataPv.FID_DECIMALS, decimals);
		pv.put(EcuDataPv.FID_CNVID, cnv);
		if(cnv != null)
		{
			pv.put(EcuDataPv.FID_MIN, cnv[EcuConversions.cnvSystem].memToPhys(0));
			pv.put(EcuDataPv.FID_MAX, cnv[EcuConversions.cnvSystem].memToPhys(0xFFFFFFFF));
		}
	}

	@Override
	public String toString()
	{
		return (String.format("%02d.%d.%s", pid, ofs, label));
	}

	/**
	 * get physical value from buffer
	 *
	 * @param buffer communication buffer content
	 * @return physical value
	 */
	Object physFromBuffer(char[] buffer)
	{
		Object result;
		if (cnv != null)
		{
			result = cnv[EcuConversions.cnvSystem].memToPhys(ProtoHeader.getParamInt(ofs, bytes, buffer).longValue());
		}
		else
		{
			result = ProtoHeader.hexStrToAlphaStr(new String(buffer));
		}
		return (result);
	}

	/**
	 * Update process var from Buffer value
	 *
	 * @param buffer communication buffer content
	 */
	public void updatePvFomBuffer(char[] buffer)
	{
		// get physical value
		Object result = physFromBuffer(buffer);
		pv.put(EcuDataPv.FID_VALUE, result);
		log.debug(String.format("%02X %-30s %16s %s",
			pid,
			label,
			pv.get(EcuDataPv.FID_VALUE),
			pv.get(EcuDataPv.FID_UNITS)));

	}

	@Override
	public Object clone()
	{
		EcuDataItem result = null;
		try
		{
			result = (EcuDataItem) super.clone();
			result.pv = (EcuDataPv) pv.clone();
		} catch (CloneNotSupportedException ex)
		{
			ex.printStackTrace();
		}

		return (result);
	}

}
