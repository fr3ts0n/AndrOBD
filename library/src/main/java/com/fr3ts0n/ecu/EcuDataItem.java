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

import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.ProtoHeader;

import java.util.logging.Logger;

/**
 * Definition of a single ECU Data item (EcuDataItem)
 *
 * @author erwin
 */
public class EcuDataItem
	implements Cloneable
{
	/** conversion systems METRIC and IMPERIAL */
	public static final int SYSTEM_METRIC = 0;
	public static final int SYSTEM_IMPERIAL = 1;
	public static final int SYSTEM_TYPES = 2;
	/** names of conversion system types */
	public static final String[] cnvSystems =
	{
		"METRIC",
		"IMPERIAL",
	};
	// current conversion system
	public static int cnvSystem = SYSTEM_METRIC;
	// maximum number of conversion errors before disabling data item
    public static int MAX_ERROR_COUNT = 3;

	public int pid;             ///< pid
	public int ofs;             ///< Offset within message
	public Conversion[] cnv;    ///< type of conversion
	int bytes;                  ///< number of data bytes expected from vehicle
	int bitOffset = 0;          ///< bit offset within extracted long
	int numBits = 32;           ///< number of relevant bits within extracted long
	long bitMask = 0xFFFFFFFF;  ///< mask for relevant bits within extracted long
	String fmt;                 ///< Format for text output
	public String label;        ///< text label
	public String mnemonic;     ///< unique textual mnemonic
	public EcuDataPv pv;        ///< the process variable for displaying
	int currErrorCount = 0;     ///< current number of consecutive conversion errors

	// Logger object
	public static final Logger log = Logger.getLogger("data.ecu");

	public static int[] byteValues =
	{
		    0xFFFF, // fake default max value for length 0
			  0xFF,
		    0xFFFF,
		  0xFFFFFF,
		0xFFFFFFFF
	};

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
	 * @param bitOfs Bit offset of measurement within numeric value
	 * @param numberOfBits Number of relevant bits within numeric value
	 * @param conversions data conversion to be used with this item
	 * @param format formatting string for text representation
	 * @param minValue minimum physical value to display/scale
	 * @param maxValue maximum physical value to display/scale
	 * @param labelText descriptive text label
	 */
	public EcuDataItem( int newPid,
	                    int offset,
	                    int numBytes,
	                    int bitOfs,
	                    int numberOfBits,
	                    long maskingBits,
	                    Conversion[] conversions,
	                    String format,
	                    Number minValue,
	                    Number maxValue,
	                    String labelText,
						String _mnemonic)
	{
		pid = newPid;
		ofs = offset;
		bytes = numBytes;
		bitOffset = bitOfs;
		numBits = numberOfBits;
		bitMask = maskingBits;
		cnv = conversions;
		fmt = format;
		label = labelText;
		mnemonic = _mnemonic;
		pv = new EcuDataPv();
		Number minVal = minValue;
		Number maxVal = maxValue;

		// initialize new PID with current data
		pv.put(EcuDataPv.FID_PID, Integer.valueOf(pid));
		pv.put(EcuDataPv.FID_OFS, Integer.valueOf(ofs));
		pv.put(EcuDataPv.FID_BIT_OFS, Integer.valueOf(bitOffset));
		pv.put(EcuDataPv.FID_DESCRIPT, label);
		pv.put(EcuDataPv.FID_MNEMONIC, mnemonic);
		pv.put(EcuDataPv.FID_UNITS,
		       (cnv != null && cnv[cnvSystem] != null)
		       ? cnv[cnvSystem].getUnits()
		       : "");
		pv.put(EcuDataPv.FID_VALUE, Float.valueOf(0));
		pv.put(EcuDataPv.FID_FORMAT, fmt);
		pv.put(EcuDataPv.FID_CNVID, cnv);
		if(cnv != null && cnv[cnvSystem] != null)
		{
			if(minVal == null) minVal = cnv[cnvSystem].memToPhys(0);
			if(maxVal == null) maxVal = cnv[cnvSystem].memToPhys((1L<<numBits)-1);
		}
		pv.put(EcuDataPv.FID_MIN, minVal);
		pv.put(EcuDataPv.FID_MAX, maxVal);
	}

	@Override
	public String toString()
	{
		return (String.format("%02X.%d.%d", pid, ofs, bitOffset));
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
		try
		{
			if (cnv != null && cnv[cnvSystem] != null)
			{
				// extract value from buffer
				long value = ProtoHeader.getParamInt(ofs, bytes, buffer).longValue();
				// calculate effective value ...
				// shift on bit offset
				value = (value >> bitOffset);
				// mask with bit lenth mask
				value = (value & ((1L << numBits)-1));
				// mask with specific bit mask
				value = (value & bitMask);

				// now run conversion to physical value on it ...
				result = cnv[cnvSystem].memToPhys(value);
			}
			else
			{
				result = String.copyValueOf(buffer, ofs, bytes);
			}
            // decrement error counter
            currErrorCount = Math.max(0, currErrorCount -1);
		} catch(Exception ex)
		{
			result = "n/a";
			log.warning(String.format("%s: %s - [%s]", toString(), ex.getMessage(), ProtUtils.hexDumpBuffer(buffer)));

            // increment error counter
            currErrorCount = Math.min(MAX_ERROR_COUNT, currErrorCount +1);
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
		// process data item
		try
		{
			// get physical value
			Object result = physFromBuffer(buffer);
			// if consecutive conversion error counter not exceeded
			if(currErrorCount < MAX_ERROR_COUNT)
			{
				pv.put(EcuDataPv.FID_VALUE, result);
				log.fine(String.format("%02X %-30s %16s %s",
										pid,
										label,
										pv.get(EcuDataPv.FID_VALUE),
										pv.get(EcuDataPv.FID_UNITS)));
			}
			else
			{
				log.warning(String.format("Item disabled: %s (%d/%d)",
										  toString(),
										  currErrorCount,
										  MAX_ERROR_COUNT));
			}
		}
		catch(Exception ex)
		{
			log.warning(ex.toString());
		}
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
