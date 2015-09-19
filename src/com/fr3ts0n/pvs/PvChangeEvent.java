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

import java.util.EventObject;

/**
 * Event to notify about changes to a process variable
 *
 * @author $Author: erwin $
 */
public class PvChangeEvent extends EventObject
{

	/**
	 *
	 */
	private static final long serialVersionUID = 4378855847270229897L;
	public static final int PV_NOACTION = 0x00; 							/**< NO specified PV action */
	public static final int PV_ADDED = 0x01; 									/**< new Process var was added */
	public static final int PV_DELETED = 0x02; 								/**< process var was deleted */
	public static final int PV_MODIFIED = 0x04; 							/**< process var was modified */
	public static final int PV_CONFIRMED = 0x08; 							/**< process var was confirmed */
	public static final int PV_MANUAL_MOD = 0x10; 						/**< process var was modified manually */
	public static final int PV_CLEARED = 0x20; 								/**< process var was cleared */
	public static final int PV_ERROR = 0x40; 									/**< process var has an error */
	public static final int PV_ELIMINATED = 0x80; 						/**< process var got eliminated */
	public static final int PV_CHILDCHANGE = 0x8000000; 			/**< child process var change */
	public static final int PV_ALLACTIONS = ~PV_CHILDCHANGE;	/**< mask for all actions */
	public static final int PV_ALLEVENTS = 0xFFFFFFFF; 				/**< mask for all change types */
	private int type = PV_MODIFIED;
	private Object key = ProcessVar.DEF_KEYNAME;
	private Object value = ProcessVar.DEF_KEYNAME;
	private long time = System.currentTimeMillis();

	public PvChangeEvent(Object source, Object Key, Object Value, int Type)
	{
		super(source);
		setType(Type);
		setKey(Key);
		setValue(Value);
	}

	public int getType()
	{
		return (type & PV_ALLACTIONS);
	}

	public boolean isChildEvent()
	{
		return (type & PV_CHILDCHANGE) != 0;
	}

	public void setType(int newType)
	{
		type = newType;
	}

	public void setKey(Object newKey)
	{
		key = newKey;
	}

	public Object getKey()
	{
		return (key);
	}

	public void setValue(Object newValue)
	{
		value = newValue;
	}

	public Object getValue()
	{
		return (value);
	}

	/** return String Representation of Event */
	@Override
	public String toString()
	{
		return (String.valueOf(getType()) + ":" + getKey() + "=" + getValue());
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}
}
