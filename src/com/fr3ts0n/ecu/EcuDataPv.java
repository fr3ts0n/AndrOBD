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

import com.fr3ts0n.pvs.IndexedProcessVar;

/**
 * Process variable which contains a single OBD data item
 *
 * @author erwin
 */
public class EcuDataPv extends IndexedProcessVar
{
	/** UID for serialisation */
	private static final long serialVersionUID = -7787217159439147214L;

	// Field IDs
	public static final int FID_PID = 0;
	public static final int FID_DESCRIPT = 1;
	public static final int FID_VALUE = 2;
	public static final int FID_UNITS = 3;
	// optional Field IDs which will be invisible for table display
	public static final String FID_FORMAT = "FMT";
	public static final String FID_CNVID = "CNV_ID";
	public static final String FID_MIN = "MIN";
	public static final String FID_MAX = "MAX";

	public static final String[] FIELDS =
		{
			"PID",
			"DESCRIPTION",
			"VALUE",
			"UNITS",
		};

	/**
	 * Creates a new instance of EcuDataPv
	 */
	public EcuDataPv()
	{
		super();
		this.setKeyAttribute(FIELDS[0]);
	}

	public String[] getFields()
	{
		return (FIELDS);
	}
}
