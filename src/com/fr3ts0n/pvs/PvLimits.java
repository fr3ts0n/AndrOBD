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

package com.fr3ts0n.pvs;

/**
 * Range check to allow checking a process var to be within certain range
 *
 * @author erwin
 */
public class PvLimits
{
	/** result codes */
	/** value is within specified range */
	public static final byte RC_WITHIN_RANGE = 0x00;
	/** value is above specified range */
	public static final byte RC_ABOVE_RANGE = 0x01;
	/** value is below specified range */
	public static final byte RC_BELOW_RANGE = 0x02;

	/** minimum limit for range check */
	@SuppressWarnings("rawtypes")
	protected Comparable minValue;
	/** maximum limit for range check */
	@SuppressWarnings("rawtypes")
	protected Comparable maxValue;

	/**
	 * Creates a new instance of PvLimits
	 */
	public PvLimits()
	{
	}

	/**
	 * Creates a new instance of PvLimits
	 *
	 * @param minVal minimum Limit for range check. Setting this value to NULL disables
	 *               the check for value above limit
	 * @param maxVal maximum Limit for range check. Setting this value to NULL disables
	 *               the check for value below limit
	 */
	@SuppressWarnings("rawtypes")
	public PvLimits(Comparable minVal, Comparable maxVal)
	{
		minValue = minVal;
		maxValue = maxVal;
	}

	/**
	 * Getter for property minValue.
	 *
	 * @return Value of property minValue.
	 */
	@SuppressWarnings("rawtypes")
	public Comparable getMinValue()
	{
		return this.minValue;
	}

	/**
	 * Setter for property minValue.
	 *
	 * @param minValue New value of property minValue.
	 */
	@SuppressWarnings("rawtypes")
	public void setMinValue(Comparable minValue)
	{
		this.minValue = minValue;
	}

	/**
	 * Getter for property maxValue.
	 *
	 * @return Value of property maxValue.
	 */
	@SuppressWarnings("rawtypes")
	public Comparable getMaxValue()
	{
		return this.maxValue;
	}

	/**
	 * Setter for property maxValue.
	 *
	 * @param maxValue New value of property maxValue.
	 */
	@SuppressWarnings("rawtypes")
	public void setMaxValue(Comparable maxValue)
	{
		this.maxValue = maxValue;
	}

	/**
	 * check a value against defined range This method may be called from a static
	 * context
	 *
	 * @param value    the value to be checked
	 * @param minLimit minimum Limit for range check.
	 * @param maxLimit maximum Limit for range check.
	 * @return bitmasked check result
	 * @see PvLimits#RC_WITHIN_RANGE
	 * @see PvLimits#RC_ABOVE_RANGE
	 * @see PvLimits#RC_BELOW_RANGE
	 */
	public static byte checkRange(Object value, Comparable<Object> minLimit,
	                              Comparable<Object> maxLimit)
	{
		byte retVal = RC_WITHIN_RANGE;
		// check Range Minimum
		if (minLimit != null && minLimit.compareTo(value) > 0)
			retVal |= RC_BELOW_RANGE;
		// check Range Maximum
		if (maxLimit != null && maxLimit.compareTo(value) < 0)
			retVal |= RC_ABOVE_RANGE;
		return (retVal);
	}

	/**
	 * check a value against range defined within class instance
	 *
	 * @param value the value to be checked
	 * @return bitmasked check result
	 * @see PvLimits#RC_WITHIN_RANGE
	 * @see PvLimits#RC_ABOVE_RANGE
	 * @see PvLimits#RC_BELOW_RANGE
	 */
	@SuppressWarnings("unchecked")
	public byte checkRange(Object value)
	{
		return (checkRange(value, minValue, maxValue));
	}

	/**
	 * return value limited against range defined within class instance
	 *
	 * @param value the value to be checked
	 * @return value limited to limits
	 */
	@SuppressWarnings("unchecked")
	public Object limitedValue(Object value)
	{
		Object result;
		switch (checkRange(value, minValue, maxValue))
		{
			case RC_BELOW_RANGE:
				result = minValue;
				break;

			case RC_ABOVE_RANGE:
				result = maxValue;
				break;

			default:
				result = value;
		}
		return result;
	}
}
