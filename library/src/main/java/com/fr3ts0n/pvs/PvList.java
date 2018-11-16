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

import java.util.Map;

/**
 * List of multiple Process vars of the same type<br>
 * all the process variables are stored/identified by their key value
 *
 * @author $Author: erwin $
 */
public class PvList extends ProcessVar
	implements DataMapHandler
{

	/**
	 *
	 */
	private static final long serialVersionUID = -4024558082429586661L;

	public PvList()
	{
	}

	public PvList(Object key)
	{
		super.setKeyAttribute(key);
	}

	/**
	 * handle a set/map of data attributes with specified notification action
	 *
	 * @param data             Map of new data attributes to handle
	 * @param action           PvChangeEvent-Action code to be used for notifications
	 * @param allowChildEvents are child events (for each attribute) allowed?
	 * @return previous value of corresponding data item
	 */
	@SuppressWarnings("rawtypes")
	private synchronized Object handleData(Map data, int action, boolean allowChildEvents)
	{
		Object result = null;


		if (data.containsKey(getKeyAttribute()))
		{
			// remember flag for event creation
			boolean oldAllowEvents = allowEvents;
			// set flag for event creation
			allowEvents = allowChildEvents;
			ProcessVar dataset = (ProcessVar) get(data.get(getKeyAttribute()));
			if (dataset == null)
			{
				dataset = new ProcessVar();
				dataset.setKeyAttribute(getKeyAttribute());
			}
			dataset.putAll(data, action, allowChildEvents);
			// restore flag for event creation
			allowEvents = oldAllowEvents;
			result = put(dataset.getKeyValue(), dataset, action);
		}
		return (result);
	}

	/**
	 * handle a set/map of data attributes with specified notification action
	 *
	 * @param data   Map of new data attributes to handle
	 * @param action PvChangeEvent-Action code to be used for notifications
	 * @return previous value of corresponding data item
	 */
	@SuppressWarnings("rawtypes")
	private synchronized Object handleData(Map data, int action)
	{
		return (handleData(data, action, false));
	}

	/**
	 * handle a set/map of data attributes with default notification action
	 *
	 * @param data Map of new data attributes to handle
	 * @return previous value of corresponding data item
	 */
	@SuppressWarnings("rawtypes")
	public synchronized Object handleData(Map data)
	{
		return (handleData(data, defaultAction));
	}
}
