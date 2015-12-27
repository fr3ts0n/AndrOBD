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

/**
 * List of all known OBD failure codes
 * This list is initialized by reading data files 'res/pcodes' and 'res/ucodes'
 *
 * @author erwin
 */
public class ObdCodeList
	extends EcuCodeList
{

	/**
	 *
	 */
	private static final long serialVersionUID = 2198654596294230437L;

	/** Creates a new instance of ObdCodeList */
	public ObdCodeList()
	{
		super("com.fr3ts0n.ecu.prot.obd.res.codes");
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param resourceBundleName name of used resource bundle
	 */
	public ObdCodeList(String resourceBundleName)
	{
		super(resourceBundleName);
	}

	@Override
	protected String getCode(Number value)
	{
		return ObdCodeItem.getPCode(value.intValue());
	}
}
