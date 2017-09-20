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

import java.util.HashMap;
import java.util.Map;

/**
 * conversion of numeric values based on a hash map
 *
 * @author erwin
 */
public class HashConversion extends NumericConversion
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1077047688974749271L;
	/* the HashMap Data */
	private HashMap<Long, String> hashData = new HashMap<Long, String>();

	/**
	 * create a new hash converter which is initialized with values from map data
	 *
	 * @param data map data for conversions
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public HashConversion(Map data)
	{
		hashData.putAll(data);
	}

	/**
	 * create a new hash converter which is initialized with avlues from an array
	 * of strings in the format "key=value"
	 *
	 * @param initData initializer strings for conversions in the format "key=value[;key=value[...]]"
	 */
	public HashConversion(String[] initData)
	{
		initFromStrings(initData);
	}

	/**
	 * initialize hash map with values from an array of strings in the format "key=value"
	 *
	 * @param initData initializer strings for conversions in the format "key=value[;key=value[...]]"
	 */
	public void initFromStrings(String[] initData)
	{
		Long key;
		String value;
		String[] data;
		// clear old hash data
		hashData.clear();

		// loop through all entries ...
		for (String anInitData : initData)
		{
			data = anInitData.split(";");
			for (String aData : data)
			{
				// ... split key and value ...
				String[] words = aData.split("=");
				key = Long.valueOf(words[0]);
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
		String result = hashData.get(physVal.longValue());
		// if we haven't found a string representation, return numeric value
		if (result == null)
			result = "Unknown state: "+super.physToPhysFmtString(physVal, format);

		return (result);
	}

}
