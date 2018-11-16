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

import java.io.Serializable;

/**
 * interface for various Data conversion types
 *
 * @author erwin
 */
public interface Conversion extends Serializable
{
	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value memory value
	 * @return physical value
	 */
	Number memToPhys(long value);

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value physical value
	 * @return memory value
	 */
	Number physToMem(Number value);

	/**
	 * convert a numerical physical value into a formatted string
	 *
	 * @param physVal  physical value
	 * @param format formatting pattern for text display
	 * @return formatted String
	 */
	String physToPhysFmtString(Number physVal, String format);

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value       memory value
	 * @param numDecimals number of decimal for string formatting of numbers
	 * @return string representation of numeric value
	 */
	String memToString(Number value, int numDecimals);

	/**
	 * return physical units of this conversion
	 *
	 * @return physical units of this conversion
	 */
	String getUnits();
}
