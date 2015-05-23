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

import java.util.HashMap;
import java.util.Map;

/**
 * conversion of numeric values based on a Bitmap
 *
 * @author erwin
 */
public class BitmapConversion extends NumericConversion
{
	/** SerialVersion UID */
	private static final long serialVersionUID = -8498739122873083420L;
	/* the HashMap Data */
	@SuppressWarnings("rawtypes")
	private HashMap hashData = new HashMap();
	String units = "-";

	/**
	 * create a new Instance
	 */
	public BitmapConversion()
	{
	}

	/**
	 * create a new hash converter which is initialized with values from map data
	 * The map data needs to contain Bit position and the meaning of it
	 *
	 * @param data map data for conversions
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BitmapConversion(Map data)
	{
		hashData.putAll(data);
	}

	/**
	 * create a new hash converter which is initialized with avlues from an array
	 * of strings in the format "BitPos=value"
	 *
	 * @param initData initializer strings for conversions in the format "BitPos=value[;BitPos=value[...]]"
	 */
	public BitmapConversion(String[] initData)
	{
		initFromStrings(initData);
	}

	/**
	 * initialize hash map with values from an array of strings in the format "BitPos=value"
	 *
	 * @param initData initializer strings for conversions in the format "BitPos=value[;BitPos=value[...]]"
	 */
	@SuppressWarnings("unchecked")
	public void initFromStrings(String[] initData)
	{
		Long key;
		String value;
		String[] data;
		// clear old hash data
		hashData.clear();

		// loop through all string entries ...
		for (int i = 0; i < initData.length; i++)
		{
			data = initData[i].split(";");
			for (int j = 0; j < data.length; j++)
			{
				// ... split key and value ...
				String[] words = data[j].split("=");
				key = Long.valueOf(words[0]);
				value = words[1];
				// TODO: if( key >= 64 )
				// ... and enter into hash map
				hashData.put(key, value);
			}
		}
	}

	public Number memToPhys(long value)
	{
		return (value);
	}

	public Number physToMem(Number value)
	{
		return (value);
	}

	/**
	 * convert a numerical physical value into a formatted string
	 *
	 * @param physVal  physical value
	 * @param decimals number of decimals for formatting
	 * @return formatted String
	 */
	@Override
	public String physToPhysFmtString(Number physVal, int decimals)
	{
		String result = null;
		long val = physVal.longValue();
		long currBitVal;
		// loop through all bits
		for (int i = 0; i < 64; i++)
		{
			currBitVal = 1 << i;
			if ((val & currBitVal) != 0)
			{
				// check if we have a meaning for this bit
				String bitRslt = (String) hashData.get(i);
				// yes, then add the meaning to result string
				if (bitRslt != null)
				{
					// if this is NOT the first entry, then add a new line
					if (result != null) result += "\n";
					// now add the result
					result += bitRslt;
				}
			}
		}
		// if we haven't found a string representation, return numeric value
		if (result == null) result = String.valueOf(physVal);
		return (result);
	}
}
