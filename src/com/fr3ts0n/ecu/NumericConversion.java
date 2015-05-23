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

import java.text.DecimalFormat;

/**
 * Base class for numeric diagnostic data conversions
 *
 * @author erwin
 */
public abstract class NumericConversion implements Conversion
{
	/** fixed serial version id  */
	private static final long serialVersionUID = 5506104864792893549L;
	/** Formatter to format numeric values to strings */
	protected static DecimalFormat decimalFormat = new DecimalFormat();
	/** format templates to format numeric values to strings */
	static String[] formats = {"0;-#", "0.0;-#",
		"0.00;-#", "0.000;-#", "0.0000;-#"};

	/** physical units of data item */
	String units = "";

	/**
	 * convert a numerical physical value into a formatted string
	 *
	 * @param physVal  physical value
	 * @param decimals number of decimals for formatting
	 * @return formatted String
	 */
	public String physToPhysFmtString(Number physVal, int decimals)
	{
		String result = "";
		if (decimals >= 0)
		{
			LinearConversion.decimalFormat
				.applyPattern(LinearConversion.formats[decimals]);
			result = LinearConversion.decimalFormat.format(physVal);
		} else
		{
			result = String.valueOf(physVal);
		}
		return result;
	}

	public NumericConversion()
	{
	}

	/**
	 * return physical units of measurement
	 *
	 * @return physical units
	 */
	public String getUnits()
	{
		return units;
	}

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value       memory value
	 * @param numDecimals number of decimal for string formatting of numbers
	 * @return string representation of numeric value
	 */
	public String memToString(Number value, int numDecimals)
	{
		return physToPhysFmtString(memToPhys(value.longValue()), numDecimals);
	}

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value raw memory value to be converted
	 */
	public abstract Number memToPhys(long value);

	/**
	 * convert measurement item from physical value to raw storage format
	 *
	 * @param value physical value to be converted
	 */
	public abstract Number physToMem(Number value);

}
