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

package com.fr3ts0n.pvs.gui;

import com.fr3ts0n.pvs.ProcessVar;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author esh
 */
class ProcessVarBeanInfo extends SimpleBeanInfo
{

	// Bean descriptor//GEN-FIRST:BeanDescriptor
	/*lazy BeanDescriptor*/
	private static BeanDescriptor getBdescriptor()
	{
		
		// Here you can add code for customizing the BeanDescriptor.

		return new BeanDescriptor(ProcessVar.class, PvDetailPanel.class);
	}//GEN-LAST:BeanDescriptor

	// Property identifiers//GEN-FIRST:Properties
	private static final int PROPERTY_empty = 0;
	private static final int PROPERTY_keyAttribute = 1;
	private static final int PROPERTY_keyValue = 2;
	private static final int PROPERTY_valueMap = 3;

	// Property array
	/*lazy PropertyDescriptor*/
	private static PropertyDescriptor[] getPdescriptor()
	{
		PropertyDescriptor[] properties = new PropertyDescriptor[4];

		try
		{
			properties[PROPERTY_empty] = new PropertyDescriptor("empty", ProcessVar.class, "isEmpty", null);
			properties[PROPERTY_keyAttribute] = new PropertyDescriptor("keyAttribute", ProcessVar.class, "getKeyAttribute", "setKeyAttribute");
			properties[PROPERTY_keyValue] = new PropertyDescriptor("keyValue", ProcessVar.class, "getKeyValue", "setKeyValue");
			properties[PROPERTY_valueMap] = new PropertyDescriptor("valueMap", ProcessVar.class, "getValueMap", "setValueMap");
			properties[PROPERTY_valueMap].setPropertyEditorClass(PvDetailPanel.class);
		} catch (IntrospectionException ignored)
		{
		}//GEN-HEADEREND:Properties

		// Here you can add code for customizing the properties array.

		return properties;
	}//GEN-LAST:Properties

	// EventSet identifiers//GEN-FIRST:Events
	private static final int EVENT_pvChangeListener = 0;

	// EventSet array
    /*lazy EventSetDescriptor*/
	private static EventSetDescriptor[] getEdescriptor()
	{
		EventSetDescriptor[] eventSets = new EventSetDescriptor[1];

		try
		{
			eventSets[EVENT_pvChangeListener] = new EventSetDescriptor(com.fr3ts0n.pvs.ProcessVar.class, "pvChangeListener", com.fr3ts0n.pvs.PvChangeListener.class, new String[]{"pvChanged"}, "addPvChangeListener", "removePvChangeListener");
		} catch (IntrospectionException ignored)
		{
		}//GEN-HEADEREND:Events

		// Here you can add code for customizing the event sets array.

		return eventSets;
	}//GEN-LAST:Events

	// Method identifiers//GEN-FIRST:Methods
	private static final int METHOD_addPvChangeListener0 = 0;
	private static final int METHOD_clear1 = 1;
	private static final int METHOD_clone2 = 2;
	private static final int METHOD_containsKey3 = 3;
	private static final int METHOD_containsValue4 = 4;
	private static final int METHOD_entrySet5 = 5;
	private static final int METHOD_equals6 = 6;
	private static final int METHOD_get7 = 7;
	private static final int METHOD_hashCode8 = 8;
	private static final int METHOD_keySet9 = 9;
	private static final int METHOD_put10 = 10;
	private static final int METHOD_putAll11 = 11;
	private static final int METHOD_pvChanged12 = 12;
	private static final int METHOD_remove13 = 13;
	private static final int METHOD_size14 = 14;
	private static final int METHOD_toString15 = 15;
	private static final int METHOD_values16 = 16;

	// Method array
    /*lazy MethodDescriptor*/
	private static MethodDescriptor[] getMdescriptor()
	{
		MethodDescriptor[] methods = new MethodDescriptor[17];

		try
		{
			methods[METHOD_addPvChangeListener0] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("addPvChangeListener",
				com.fr3ts0n.pvs.PvChangeListener.class, Integer.TYPE));
			methods[METHOD_addPvChangeListener0].setDisplayName("");
			methods[METHOD_clear1] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("clear"));
			methods[METHOD_clear1].setDisplayName("");
			methods[METHOD_clone2] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("clone"));
			methods[METHOD_clone2].setDisplayName("");
			methods[METHOD_containsKey3] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("containsKey",
				Object.class));
			methods[METHOD_containsKey3].setDisplayName("");
			methods[METHOD_containsValue4] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("containsValue",
				Object.class));
			methods[METHOD_containsValue4].setDisplayName("");
			methods[METHOD_entrySet5] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("entrySet"));
			methods[METHOD_entrySet5].setDisplayName("");
			methods[METHOD_equals6] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("equals",
				Object.class));
			methods[METHOD_equals6].setDisplayName("");
			methods[METHOD_get7] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("get",
				Object.class));
			methods[METHOD_get7].setDisplayName("");
			methods[METHOD_hashCode8] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("hashCode"));
			methods[METHOD_hashCode8].setDisplayName("");
			methods[METHOD_keySet9] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("keySet"));
			methods[METHOD_keySet9].setDisplayName("");
			methods[METHOD_put10] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("put",
				Object.class, Object.class, Integer.TYPE));
			methods[METHOD_put10].setDisplayName("");
			methods[METHOD_putAll11] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("putAll",
				java.util.Map.class, Integer.TYPE, Boolean.TYPE));
			methods[METHOD_putAll11].setDisplayName("");
			methods[METHOD_pvChanged12] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("pvChanged",
				com.fr3ts0n.pvs.PvChangeEvent.class));
			methods[METHOD_pvChanged12].setDisplayName("");
			methods[METHOD_remove13] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("remove",
				Object.class));
			methods[METHOD_remove13].setDisplayName("");
			methods[METHOD_size14] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("size"));
			methods[METHOD_size14].setDisplayName("");
			methods[METHOD_toString15] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("toString"));
			methods[METHOD_toString15].setDisplayName("");
			methods[METHOD_values16] = new MethodDescriptor(com.fr3ts0n.pvs.ProcessVar.class.getMethod("values"));
			methods[METHOD_values16].setDisplayName("");
		} catch (Exception ignored)
		{
		}//GEN-HEADEREND:Methods

		// Here you can add code for customizing the methods array.

		return methods;
	}//GEN-LAST:Methods

	private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
	private static final int defaultEventIndex = -1;//GEN-END:Idx

	@SuppressWarnings("rawtypes")
	public BeanInfo[] getAdditionalBeanInfo()
	{//GEN-FIRST:Superclass
		Class superclass = ProcessVar.class.getSuperclass();
		BeanInfo sbi = null;
		try
		{
			sbi = Introspector.getBeanInfo(superclass);//GEN-HEADEREND:Superclass

			// Here you can add code for customizing the Superclass BeanInfo.

		} catch (IntrospectionException ignored)
		{
		}
		return new BeanInfo[]{sbi};
	}//GEN-LAST:Superclass

	/**
	 * Gets the bean's <code>BeanDescriptor</code>s.
	 *
	 * @return BeanDescriptor describing the editable
	 * properties of this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 */
	public BeanDescriptor getBeanDescriptor()
	{
		return getBdescriptor();
	}

	/**
	 * Gets the bean's <code>PropertyDescriptor</code>s.
	 *
	 * @return An array of PropertyDescriptors describing the editable
	 * properties supported by this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 * If a property is indexed, then its entry in the result array will
	 * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
	 * A client of getPropertyDescriptors can use "instanceof" to check
	 * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
	 */
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return getPdescriptor();
	}

	/**
	 * Gets the bean's <code>EventSetDescriptor</code>s.
	 *
	 * @return An array of EventSetDescriptors describing the kinds of
	 * events fired by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public EventSetDescriptor[] getEventSetDescriptors()
	{
		return getEdescriptor();
	}

	/**
	 * Gets the bean's <code>MethodDescriptor</code>s.
	 *
	 * @return An array of MethodDescriptors describing the methods
	 * implemented by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public MethodDescriptor[] getMethodDescriptors()
	{
		return getMdescriptor();
	}

	/**
	 * A bean may have a "default" property that is the property that will
	 * mostly commonly be initially chosen for update by human's who are
	 * customizing the bean.
	 *
	 * @return Index of default property in the PropertyDescriptor array
	 * returned by getPropertyDescriptors.
	 * <P>	Returns -1 if there is no default property.
	 */
	public int getDefaultPropertyIndex()
	{
		return defaultPropertyIndex;
	}

	/**
	 * A bean may have a "default" event that is the event that will
	 * mostly commonly be used by human's when using the bean.
	 *
	 * @return Index of default event in the EventSetDescriptor array
	 * returned by getEventSetDescriptors.
	 * <P>	Returns -1 if there is no default event.
	 */
	public int getDefaultEventIndex()
	{
		return defaultEventIndex;
	}
}

