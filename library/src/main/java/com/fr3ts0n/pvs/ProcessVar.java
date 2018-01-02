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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

// For logging ...

/**
 * single Process variable containing all attributes of a process object
 *
 * @author $Author: erwin $
 */
@SuppressWarnings("rawtypes")
public class ProcessVar
	extends HashMap
	implements PvChangeListener, Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 7072161686290674442L;
	/** name of key attribute */
	Object KeyAttribute = null;
	/** default key attribute name */
	static final String DEF_KEYNAME = "key";
	/** time of last change * */
	public long lastChange = 0;
	/** type of last change * */
	public int lastChangeType = PvChangeEvent.PV_ADDED;
	/** default change action */
	protected int defaultAction = PvChangeEvent.PV_NOACTION;
	/** flag if to allow ChangeEvents to be fired */
	protected boolean allowEvents = false;
	/** list of process var change listeners */
	private transient Map<PvChangeListener, Integer> PvChangeListeners =
		Collections.synchronizedMap(new HashMap<PvChangeListener, Integer>());
	/** Map of attribute changes */
	private Map<Object, PvChangeEvent> changes =
		Collections.synchronizedMap(new HashMap<Object, PvChangeEvent>());
	/** The logger object */
	public static Logger log = Logger.getLogger(ProcessVar.class.getPackage().getName());

	public ProcessVar()
	{
		clear();
	}

	public ProcessVar(int initialSize)
	{
		super(initialSize);
		clear();
	}

	/** construct with a existing Map */
	public ProcessVar(Map map)
	{
		if (map != null)
		{
			putAll(map);
		}
	}

	/**
	 * put all attributes from map into current ProcessVar
	 * - use action for all notifications
	 *
	 * @param map              data map to put into ProcessVar
	 * @param action           Action code to be used for notifications
	 * @param allowChildEvents are child events (for each attribute) allowed?
	 */
	@SuppressWarnings("unchecked")
	public synchronized void putAll(Map map, int action, boolean allowChildEvents)
	{
		// remember flag for event creation
		boolean oldAllowEvents = allowEvents;
		// disable event creation for each map field
		// (we want to send one single event after the full map is handled)
		allowEvents = allowChildEvents;
		// remember old default action
		int oldAction = defaultAction;
		// set new action as the default action for all fields
		defaultAction = action;
		// put all fields to hashmap (using default action)
		super.putAll(map);
		// restore old default action
		defaultAction = oldAction;
		// enable event creation again
		allowEvents = oldAllowEvents;
		// now fire the one and only event for this map change
		firePvChanged(new PvChangeEvent(this, getKeyAttribute(), map.values().toArray(), action));
	}

	/**
	 * put all attributes from map into current ProcessVar
	 * - use action for all notifications
	 *
	 * @param map    data map to put into ProcessVar
	 * @param action Action code to be used for notifications
	 */
	public synchronized void putAll(Map map, int action)
	{
		putAll(map, action, true);
	}

	/**
	 * put all attributes from map into current ProcessVar
	 * - use default action for all notifications
	 *
	 * @param map data map to put into ProcessVar
	 */
	@Override
	public synchronized void putAll(Map map)
	{
		putAll(map, defaultAction);
	}

	/**
	 * handler for process variable changes
	 * forwarding of child process variables to current handler
	 */
	public synchronized void pvChanged(PvChangeEvent event)
	{
		log.finer(toString() + ":Child PvChange:" + event.toString());
		firePvChanged(new PvChangeEvent(this,
			((ProcessVar) event.getSource()).getKeyValue(),
			event.getSource(),
			event.getType() | PvChangeEvent.PV_CHILDCHANGE));
	}

	/** return String representation */
	@Override
	public String toString()
	{
		return (getClass().getName() + "[" + getKeyValue() + "]");
	}

	/**
	 * set attribute of selected key to selected value
	 * overridden method to allow notification of process var changes
	 *
	 * @param key    key of attribute
	 * @param value  value of attribute
	 * @param action type of action event @see PvChangeEvent
	 * @return previous value of attribute
	 */
	@SuppressWarnings("unchecked")
	public synchronized Object put(Object key, Object value, int action)
	{
		Object oldvalue = null;

		// if new value is a child process variable, try to re-use previous one ...
		if (value instanceof ProcessVar)
		{
			// get previous PV
			oldvalue = get(key);
			if (oldvalue != null && oldvalue instanceof ProcessVar)
			{
				// PV is existing
				((HashMap) oldvalue).putAll((Map) value);
			} else
			{
				// this will be a new child PV
				oldvalue = super.put(key, value);
			}
		} else
		{
			// NON child PV
			oldvalue = super.put(key, value);
		}

		if (oldvalue == null)
		{
			// new attribute -> PV_ADDED
			if (value != null)
			{
				action |= PvChangeEvent.PV_ADDED;
				// if we add a new child process variable, add listener for child
				if (value instanceof ProcessVar)
				{
					((ProcessVar) value).addPvChangeListener(this);
				}
			}
		} else
		{
			// Attribute has changed -> PV_MODIFIED
			if (!oldvalue.equals(value))
			{
				action |= PvChangeEvent.PV_MODIFIED;
			} else
			{
				// Attribute MANUAL_MOD confirmed -> PV_CONFIRMED
				PvChangeEvent lstChange = (PvChangeEvent) changes.get(key);
				if (lstChange != null && (lstChange.getType() & PvChangeEvent.PV_MANUAL_MOD) != 0)
				{
					action |= PvChangeEvent.PV_CONFIRMED;
				}
			}
		}

		firePvChanged(new PvChangeEvent(this, key, value, action));

		// .. and return
		return (oldvalue);
	}

	/**
	 * set attribute of selected key to selected value
	 * overridden method to allow notification of process var changes
	 *
	 * @param key   key of attribute
	 * @param value value of attribute
	 * @return previous value of attribute
	 */
	@Override
	public synchronized Object put(Object key, Object value)
	{
		// find out the type of the action
		int action = containsKey(key) ? defaultAction : PvChangeEvent.PV_ADDED;
		// and perform the put operation
		return (put(key, value, action));
	}

	/**
	 * get attribute of selected key
	 * overridden method to allow synchronized access
	 *
	 * @param key key of attribute
	 * @return value of attribute
	 */
	@Override
	public synchronized Object get(Object key)
	{
		return (super.get(key));
	}

	/**
	 * get attribute of selected key as int value
	 * overridden method to allow synchronized access
	 *
	 * @param key key of attribute
	 * @return value of attribute
	 */
	public synchronized int getAsInt(Object key)
	{
		int result = 0;
		Object val = get(key);
		try
		{
			if (val != null)
			{
				result = Integer.valueOf(val.toString()).intValue();
			}
		} catch (NumberFormatException e)
		{
			// Intentionally do nothing
		}
		return (result);
	}

	/**
	 * set attribute of selected key to selected value
	 * overridden method to allow notification of process var changes
	 *
	 * @param key   key of attribute
	 * @param value value of attribute
	 */
	public synchronized void putAsInt(Object key, int value)
	{
		put(key, new Integer(value));
	}

	/**
	 * remove attribute with selected key
	 * overridden method to allow notification of process var changes
	 *
	 * @param key key of attribute
	 * @return previous value of attribute
	 */
	@Override
	public synchronized Object remove(Object key)
	{
		Object result = super.remove(key);

		if (result != null)
		{
			firePvChanged(new PvChangeEvent(this, key, null, PvChangeEvent.PV_DELETED));
		}

		// if old object was a process variable, we need to remove change listener
		if (result != null && result instanceof ProcessVar)
		{
			((ProcessVar) result).removePvChangeListener(this);
		}

		return (result);
	}

	/**
	 * remove all attributes from process var
	 * overridden clear method to allow notification of process var changes
	 */
	@Override
	public synchronized void clear()
	{
		// now really clear the hashmap
		super.clear();
		// notify listeners of removal
		firePvChanged(new PvChangeEvent(this, null, null, PvChangeEvent.PV_CLEARED));
	}

	/** get object/name of key attribute */
	public Object getKeyAttribute()
	{
		return (KeyAttribute != null ? KeyAttribute : DEF_KEYNAME);
	}

	/** set object/name of key attribute */
	public void setKeyAttribute(Object newKeyAttribute)
	{
		KeyAttribute = newKeyAttribute;
	}

	/** get value of key attribute */
	public Object getKeyValue()
	{
		return (get(getKeyAttribute()));
	}

	/** set value of key attribute */
	public void setKeyValue(Object newKeyValue)
	{
		put(getKeyAttribute(), newKeyValue);
	}

	/**
	 * ensure there is a list of PvChangeListeners
	 * * it may be null, if PV has been de-serialized
	 */
	private void ensurePvChangeListeners()
	{
		if (PvChangeListeners == null)
			PvChangeListeners = new HashMap<PvChangeListener, Integer>();
	}
	/**
	 * Handling for list of PvChangeListeners
	 */
	/** remove listener for Pv changes */
	public synchronized void removePvChangeListener(PvChangeListener l)
	{
		ensurePvChangeListeners();
		PvChangeListeners.remove(l);
		allowEvents = !PvChangeListeners.isEmpty();
		log.finer("-PvListener:" + toString() + "->" + String.valueOf(l));
	}

	/**
	 * add listener for Pv changes with specified change events
	 *
	 * @param l         event listener to be registered
	 * @param eventMask events the listener wants to be notified about
	 */
	public synchronized void addPvChangeListener(PvChangeListener l, int eventMask)
	{
		ensurePvChangeListeners();
		PvChangeListeners.put(l, new Integer(eventMask));
		allowEvents = true;
		log.finer("+PvListener:" + toString() + "->" + String.valueOf(l));
	}

	/**
	 * add listener for Pv changes
	 *
	 * @param l event listener to be registered
	 */
	public synchronized void addPvChangeListener(PvChangeListener l)
	{
		addPvChangeListener(l, PvChangeEvent.PV_ALLEVENTS);
	}

	/**
	 * fire a Pv Change event
	 *
	 * @param e the event to be fired
	 */
	public synchronized void firePvChanged(PvChangeEvent e)
	{
		if (allowEvents && e.getType() != PvChangeEvent.PV_NOACTION)
		{
			log.finer("PvChange:" + e.toString());

			Integer evtMask;
			Map.Entry curr;

			ensurePvChangeListeners();
			// loop through all registered listeners ...
			Set entries = PvChangeListeners.entrySet();
			Iterator it = entries.iterator();

			while (it.hasNext())
			{
				curr = (Map.Entry) it.next();

				if (curr.getKey() != null && curr.getKey() != this)
				{
					// check if listener wants to be notified by this event
					evtMask = (Integer) curr.getValue();

					if ((evtMask.intValue() & e.getType()) != 0)
					{
						log.finer("Notify:" + curr);
						((PvChangeListener) curr.getKey()).pvChanged(e);
					}
				}
			}
			// set time and type of last change
			lastChange = e.getTime();
			lastChangeType = e.getType();
			changes.put(e.getKey(), e);
		}
	}

	/**
	 * Getter for property values.
	 *
	 * @return Value of property values.
	 */
	public Map getValueMap()
	{

		return this;
	}

	/**
	 * Setter for property values.
	 *
	 * @param values New value of property values.
	 */
	public void setValueMap(Map values)
	{
		putAll(values);
	}
}
