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

import com.fr3ts0n.common.UTF8Bundle;
import com.fr3ts0n.ecu.prot.obd.Messages;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Vehicle fault code list
 *
 * @author erwin
 */
public class EcuCodeList
	implements Conversion
{
	private static final long serialVersionUID = 219865459629423028L;
	private final transient ResourceBundle codes;
	private transient int radix = 10;

	/**
	 * construct a new code list
	 */
	EcuCodeList()
	{
		this("com.fr3ts0n.ecu.prot.obd.res.codes");
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param resourceBundleName name of used resource bundle
	 */
	public EcuCodeList(String resourceBundleName)
	{
		codes = UTF8Bundle.getBundle(resourceBundleName);
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param resourceBundleName name of used resource bundle
	 * @param idRadix    radix of numeric code id
	 */
	public EcuCodeList(String resourceBundleName, int idRadix)
	{
		this(resourceBundleName);
		radix = idRadix;
	}

	String getCode(Number value)
	{
		return(Long.toString(value.longValue(),radix));
	}

	public EcuCodeItem get(Number value)
	{
		EcuCodeItem result = null;
		if (codes != null)
		{
			String key = getCode(value);
			try
			{
				result = new EcuCodeItem(key, codes.getString(key));
			} catch (MissingResourceException e)
			{
				result = new EcuCodeItem(key,
				                         Messages.getString(
					                         "customer.specific.trouble.code.see.manual"));
			}
		}
		return result;
	}

	/**
	 * return all known values
	 * @return all known ressource values
	 */
	public Set<String> values()
	{
		Set<String> values = new HashSet<String>();
		for( String key : codes.keySet())
		{
			values.add(codes.getString(key));
		}
		return values;
	}

	@Override
	public String getUnits()
	{
		return "";
	}

	@Override
	public Number memToPhys(long value)
	{
		return (float) value;
	}

	@Override
	public String memToString(Number value, int numDecimals)
	{
		String fmt = "%." + numDecimals + "f";
		return physToPhysFmtString(memToPhys(value.longValue()), fmt);
	}

	@Override
	public Number physToMem(Number value)
	{
		return value;
	}

	@Override
	public String physToPhysFmtString(Number value, String format)
	{
		return (get(value).toString());
	}
}
