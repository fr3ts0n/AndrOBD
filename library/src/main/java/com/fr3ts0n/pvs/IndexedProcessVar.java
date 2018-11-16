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
 * Process variable @see ProcessVar which allows indexed access
 * to attribute fields
 *
 * @author $Author: erwin $
 */
public abstract class IndexedProcessVar extends ProcessVar
{

	/**
	 *
	 */
	private static final long serialVersionUID = 8478458496218575203L;

	/** return all available field names */
	public abstract String[] getFields();

	protected IndexedProcessVar()
	{
		String flds[] = getFields();
		for (int i = 0; i < flds.length; i++)
		{
			put(flds[i], null);
		}
	}

	/** indexed get for specified field id */
	public Object get(int fieldID)
	{
		return (get(getFields()[fieldID]));
	}

	/** indexed put for specified field id */
	public void put(int fieldID, Object newValue)
	{
		put(getFields()[fieldID], newValue);
	}

	/**
	 * get attribute of selected key
	 * overridden method to allow synchronized access
	 *
	 * @param fieldIndex index to key of attribute
	 * @return value of attribute
	 */
	public int getAsInt(int fieldIndex)
	{
		return (getAsInt(getFields()[fieldIndex]));
	}

	/**
	 * set attribute of selected key to selected value
	 * overridden method to allow notification of process var changes
	 *
	 * @param fieldIndex index to key of attribute
	 * @param value      value of attribute
	 */
	public void putAsInt(int fieldIndex, int value)
	{
		putAsInt(getFields()[fieldIndex], value);
	}

	/** indexed put for specified field id */
	public Object remove(int fieldID)
	{
		return (remove(getFields()[fieldID]));
	}
}
