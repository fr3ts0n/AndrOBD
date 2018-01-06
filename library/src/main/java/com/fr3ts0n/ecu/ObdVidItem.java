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
 * OBD Vehicle identification Item
 * The VID item does not contain any units, since it is all alphanumeric IDs
 *
 * @author root
 */
public class ObdVidItem
	extends IndexedProcessVar
{
	/**
	 *
	 */
	private static final long serialVersionUID = 955050909875054165L;
	public static final int FID_DESCRIPT = 0;
	public static final int FID_VALUE = 1;

	/**
	 * description of all relevant fields for VID item
	 */
	static final String[] fields =
		{
			"Description",
			"Value"
		};

	@Override
	public String[] getFields()
	{
		return (fields);
	}

	/**
	 * Descriptions of mode 9 PIDs
	 */
	private static String descriptions[] =
		{
	/* PID 0 */ "Supported PIDs",
    /* PID 1 */ "VIN Count",
    /* PID 2 */ "Vehicle ID Number",
    /* PID 3 */ "Cal ID Count",
    /* PID 4 */ "Calibration ID",
    /* PID 5 */ "Cal Version Count",
    /* PID 6 */ "Cal Version",
    /* PID 7 */ "IPT Count",
    /* PID 8 */ "IPT",
    /* PID 9 */ "Control System count",
    /* PID A */ "Control System ID",
		};

	/**
	 * Return description for Mode $09 PID
	 *
	 * @param pid
	 * @return description for selected PID
	 */
	public static String getPidDescription(int pid)
	{
		String result;
		try
		{
			result = descriptions[pid];
		} catch (Exception e)
		{
			// PID is not defined -> create dummy
			result = String.format("PID %02X", pid);
		}
		return (result);
	}
}
