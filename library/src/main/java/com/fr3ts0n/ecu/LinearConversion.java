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
	private int factor = 1;
	private int divider = 1;
	private int offset = 0;
	private int offsetPhys = 0;
	private PvLimits limits = null;
	// mnemonic of dynamic factor
	private String factMnemonic = null;

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
	 * Creates a new instance of Conversion with usage of a optional dynamic conversion factor
	 *
	 * @param factor     conversion factor (integer part)
	 * @param divider    conversion divider (integer part)
	 * @param offset     linear offset to be added to raw memory value before conversion
	 * @param offsetPhys physical offset to be added after converting raw value
	 * @param units      physical units for this conversion
	 * @param factMnemonic mnemonic of dynamic conversion factor value
	 */
	public LinearConversion(int factor, int divider, int offset, int offsetPhys,
	                        String units, String factMnemonic)
	{
		this(factor, divider, offset, offsetPhys, units);
		if (factMnemonic != null && !factMnemonic.isEmpty())
		{
			this.factMnemonic = factMnemonic;
		}
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
	 * Dynamic update of conversion factor from other measurement value
	 *
	 * The dynamic conversion factor overrides the initial, static factor if:
	 * - Factor is reported by protocol
	 * - Value > 0
	 */
	private void updateCnvFromDynamicFactor()
	{
		if (factMnemonic != null)
		{
			// Get data item of dynamic conversion factor
			EcuDataItem newFactItm = EcuDataItems.byMnemonic.get(factMnemonic);
			if (newFactItm != null)
			{
				// Get value of dynamic conversion factor
				Number factVal = (Number)newFactItm.pv.get(EcuDataPv.FID_VALUE);
				// If there is a valid value, update factor with dynamic factor
				if (    factVal != null                 // Factor defined
                     && factVal.intValue() > 0          // and specified ...
                     && factVal.intValue() != factor    // and changed
                   )
				{
					// update conversion factor from dynamic value
					factor = factVal.intValue();
					// Notify all users of this conversion to update the data ranges
					EcuDataItems.notifyConversionChange(this);
				}
			}
		}
	}

	/**
	 * convert measurement item from storage format to physical value
	 *
	 * @param value raw memory value to be converted
	 */
	public Number memToPhys(long value)
	{
		updateCnvFromDynamicFactor();
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
