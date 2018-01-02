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

import com.fr3ts0n.pvs.PvLimits;

/**
 * Definition of a single OBD data conversion
 *
 * @author erwin
 */
public class LinearConversion extends NumericConversion
{

	/**
	 *
	 */
	private static final long serialVersionUID = 7409621816599441879L;
	int factor = 1;
	int divider = 1;
	int offset = 0;
	int offsetPhys = 0;
	PvLimits limits = null;

	/**
	 * Creates a new instance of Conversion
	 */
	public LinearConversion()
	{
	}

	/**
	 * Creates a new instance of Conversion
	 *
	 * @param factor     conversion factor (integer part)
	 * @param divider    conversion divider (integer part)
	 * @param offset     linear offset to be added to raw memory value before conversion
	 * @param offsetPhys physical offset to be added after converting raw value
	 * @param units      physical units for this conversion
	 */
	public LinearConversion(int factor, int divider, int offset, int offsetPhys,
	                        String units)
	{
		this.offset = offset;
		this.factor = factor;
		this.divider = divider;
		this.offsetPhys = offsetPhys;
		this.units = units;
	}

	/**
	 * Creates a new instance of Conversion
	 *
	 * @param factor     conversion factor (integer part)
	 * @param divider    conversion divider (integer part)
	 * @param offset     linear offset to be added to raw memory value before conversion
	 * @param offsetPhys physical offset to be added after converting raw value
	 * @param units      physical units for this conversion
	 */
	public LinearConversion(int factor, int divider, int offset, int offsetPhys,
	                        String units, PvLimits limits)
	{
		this(factor, divider, offset, offsetPhys, units);
		this.limits = limits;
	}

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value raw memory value to be converted
	 */
	public Number memToPhys(long value)
	{
		float result = ((float) (value + offset) * factor / divider + offsetPhys);
		if (limits != null)
		{
			result = (Float) limits.limitedValue(result);
		}
		return result;
	}

	/**
	 * convert measurement item from physical value to raw storage format
	 *
	 * @param value physical value to be converted
	 */
	public Number physToMem(Number value)
	{
		return ((long) java.lang.Math.round((value.floatValue() - offsetPhys)
			* divider / factor - offset));
	}
}
