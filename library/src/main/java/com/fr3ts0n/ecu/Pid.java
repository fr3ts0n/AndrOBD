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
 * Definition of a single OBD Data item (PID)
 *
 * @author erwin
 */
public class Pid
{
	public int pid;        // PID
	public int bytes;        // number of data bytes expected from vehicle
	public int ofs;        // Offset within message
	public int cnv;        // type of conversion
	public int decimals;    // number of decimal digits
	public String label;        // text label

	/**
	 * Creates a new instance of Pid
	 */
	public Pid()
	{
	}

	/**
	 * Creates a new instance of Pid
	 */
	public Pid(int pid, int offset, int bytes, int cnv, int decimals, String label)
	{
		this.pid = pid;
		this.ofs = offset;
		this.bytes = bytes;
		this.cnv = cnv;
		this.decimals = decimals;
		this.label = label;
	}

	@Override
	public String toString()
	{
		return (String.format("%02X", pid));
	}
}
