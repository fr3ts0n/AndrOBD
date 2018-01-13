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

import java.util.logging.Logger;

/**
 * Base class for numeric diagnostic data conversions
 *
 * @author erwin
 */
public abstract class NumericConversion implements Conversion
{
	/** fixed serial version id  */
	private static final long serialVersionUID = 5506104864792893549L;
	/** Logger object */
	public static final Logger log = Logger.getLogger("data.ecu");

	/** physical units of data item */
	String units = "";

	@Override
	public String physToPhysFmtString(Number physVal, String format)
	{
		return String.format(format, physVal);
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
		String fmt = "%." + numDecimals + "d";
		return physToPhysFmtString(memToPhys(value.longValue()), fmt);
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
