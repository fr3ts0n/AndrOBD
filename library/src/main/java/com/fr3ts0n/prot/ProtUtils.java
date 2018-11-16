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

package com.fr3ts0n.prot;

/**
 * Protocol utility tool box
 *
 * @author erwin
 */
public class ProtUtils
{

	private static String hexFmt;
	private static String asciiFmt;

	/**
	 * return HEX:ASCII dump of buffer
	 * in ASCII section all NON ASCII chars are '.'
	 * @param buffer buffer to be dumped
	 * @return String containing HEX:ASCII data of buffer
	 */
	public static String hexDumpBuffer(char[] buffer)
	{
		hexFmt = "";
		asciiFmt = "";

		for (int i = 0; i < buffer.length; i++)
		{
			hexFmt += String.format("%02X ", (byte) buffer[i]);
			asciiFmt += String.format("%1s", buffer[i] < 32 || buffer[i] > 127 ? '.' : buffer[i]);
		}
		return (hexFmt + " : " + asciiFmt);
	}

}
