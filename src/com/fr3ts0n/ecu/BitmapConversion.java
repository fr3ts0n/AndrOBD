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

import com.fr3ts0n.ecu.prot.obd.Messages;

import java.util.Map;
import java.util.TreeMap;

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
	private TreeMap<Long,String> hashData = new TreeMap<Long,String>();

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
	public void initFromStrings(String[] initData)
	{
		Long key;
		String value;
		String[] data;
		// clear old hash data
		hashData.clear();

		// loop through all string entries ...
		for (String anInitData : initData)
		{
			data = anInitData.split(";");
			for (String aData : data)
			{
				// ... split key and value ...
				String[] words = aData.split("=");
				key = (long) (1 << Long.valueOf(words[0]));
				value = words[1];
				
				// attempt to translate ...
				String xlatKey = value;
				xlatKey = xlatKey.replaceAll("[ -]", "_").toLowerCase();
				value = Messages.getString(xlatKey, value);
				// debug log translated message
				log.finer(String.format("%s=%s", xlatKey, value));
				
				// ... and enter into hash map
				hashData.put(key, value);
			}
		}
	}

	public Number memToPhys(long value)
	{
		return value;
	}

	public Number physToMem(Number value)
	{
		return value;
	}

	@Override
	public String physToPhysFmtString(Number physVal, String format)
	{
		String result = null;
		long val = physVal.longValue();

		for(Map.Entry<Long,String> item : hashData.entrySet())
		{
			// if this is NOT the first entry, then add a new line
			if (result == null)
				result = "";
			else
				result += System.lineSeparator();
			// now add the result
			result += String.format("%s  %s",
									((val & item.getKey()) != 0) ? "(*)" : "(  )",
									item.getValue() );
		}
		// if we haven't found a string representation, return numeric value
		if (result == null) result = super.physToPhysFmtString(physVal,format);
		return (result);
	}
}
