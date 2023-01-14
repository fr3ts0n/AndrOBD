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
	private int bytes;                  ///< number of data bytes expected from vehicle
	private int bitOffset = 0;          ///< bit offset within extracted long
	private int numBits = 32;           ///< number of relevant bits within extracted long
	private long bitMask = 0xFFFFFFFF;  ///< mask for relevant bits within extracted long
	private String fmt;                 ///< Format for text output
	public String label;        ///< text label
	private String mnemonic;     ///< unique textual mnemonic
	public EcuDataPv pv;        ///< the process variable for displaying
	private int currErrorCount = 0;     ///< current number of consecutive conversion errors
	public long updatePeriod_ms = 0; ///< Minimum update period in ms

	// Logger object
	private static final Logger log = Logger.getLogger("data.ecu");

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
	 * @param minUpdatePeriod Minimum expected data update period in ms
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
						long minUpdatePeriod,
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
		updatePeriod_ms = minUpdatePeriod;
		label = labelText;
		mnemonic = _mnemonic;
		pv = new EcuDataPv();

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
		updateLimits(minValue, maxValue);
	}

	/**
	 * Update MIN/MAX limit values
	 *
	 * - if values are specified, this sets the MIN/MAX limits to specified values
	 * - if NOT specified the MIN/MAX range is calculated from the data range using the given conversion
	 *
	 * This update is required on:
	 * - Initialisation
	 * - Update of dynamic conversion factors
	 *
	 * @param minValue	Specific MIN value or NULL if not specified
	 * @param maxValue	Specific MAX value or NULL if not specified
	 */
	protected void updateLimits(Number minValue, Number maxValue)
	{
		// set specified values
		Number minVal = minValue;
		Number maxVal = maxValue;
		// Check conversion
		if(cnv != null && cnv[cnvSystem] != null)
		{
			// If MIN/MAX value un-specified. calculate from data range conversion
			if(minVal == null) minVal = physMin();
			if(maxVal == null) maxVal = physMax();
		}
		// Update limits ...
		pv.put(EcuDataPv.FID_MIN, minVal);
		pv.put(EcuDataPv.FID_MAX, maxVal);
	}

	/**
	 * Return minimum raw (integer) value before conversion
	 * - calculated based on bit width & mask
	 *
	 * @apiNote Just for completeness, This wil always return a 0
	 *
	 * @return MIM raw integer value
	 */
	public long rawMin()
	{
		return 0L;
	}

	/**
	 * Return maximum raw (integer) value before conversion
	 * - calculated based on bit width & mask
	 *
	 * @return MAX raw integer value
	 */
	public long rawMax()
	{
		return((((1L<<numBits)-1) & bitMask));
	}

	/**
	 * Return physical value from raw (integer) value
	 * @param rawVal RAW integer value
	 * @return Physical value
	 */
	public Number physVal(long rawVal)
	{
		return( cnv[cnvSystem].memToPhys( rawVal ));
	}

	/**
	 * Return raw (integer) value from physical value
	 * - calculation based on conversion
	 *
	 * @param physVal physical value
	 * @return raw integer value
	 */
	public long rawVal(Number physVal)
	{
		return cnv[cnvSystem].physToMem(physVal).longValue();
	}

	/**
	 * Return physically minimum value
	 * - Value is calculated from bit width and data conversion
	 *
	 * @return MIN value
	 */
	public Number physMin()
	{
		return physVal(rawMin());
	}

	/**
	 * Return physically maximum value
	 * - Value is calculated from bit width and data conversion
	 *
	 * @return MAX value
	 */
	public Number physMax()
	{
		return physVal(rawMax());
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
	private Object physFromBuffer(char[] buffer)
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
				result = physVal(value);
			}
			else
			{
				// get number of padding \0 characters
				int padChars = 0; while(buffer[ofs + padChars] == 0) padChars++;
				// copy string content after padding characters ...
				result = String.copyValueOf(buffer, ofs + padChars, bytes);
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
	 * @return Next expected update period
	 */
	@SuppressWarnings("DefaultLocale")
	public long updatePvFomBuffer(char[] buffer)
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
				pv.put(EcuDataPv.FID_UNITS, pv.getUnits());
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

		/* return next expected update period */
		return updatePeriod_ms;
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
