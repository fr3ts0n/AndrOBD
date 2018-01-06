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

package com.fr3ts0n.ecu.prot.obd;

import com.fr3ts0n.ecu.Conversions;

/**
 * CAN protocol definition for Ford Focus 1.8 TDI (Experimental)
 *
 * @author erwin
 */
public class CanProtFord extends CanProt
{

	/**
	 * List of message dependent telegram parameters in order of appearance
	 */
	static final int MSG_PARAMETERS[][] =
	/*  START,  LEN,   PARAM-TYPE   CONVERSION                       , DEC, MSG_ID    // REMARKS                   */
    /* ----------------------------------------------------------------------------------------------------------- */
		{
    /* MSG 25 (Engine) */
			{2, 4, PT_HEX, Conversions.CNV_ID_RPM, 0, 0x25},   // Engine RPM [/min]
			{6, 4, PT_HEX_S16, Conversions.CNV_ID_RATIO, 1, 0x25},   // Engine speed regulator [%]
			{10, 2, PT_HEX, Conversions.CNV_ID_PERCENT, 1, 0x25},   // AccPed Position [%]
    /* MSG 2 (Speed) */
			{2, 4, PT_HEX, Conversions.CNV_ID_SPEED_HIGHRES, 1, 0x02},   // Vehicle speed [km/h]
    /* MSG 10 (ECT) */
			{2, 2, PT_HEX, Conversions.CNV_ID_TEMPERATURE, 1, 0x10},   // Engine coolant temp [°C]
    /* MSG 14 (ASR/ESP?) */
			{2, 2, PT_HEX, Conversions.CNV_ID_ONETOONE, 0, 0x14},   // ESP-Status [-]
			{4, 2, PT_HEX, Conversions.CNV_ID_TORQUE, 1, 0x14},   // ESP ratio2? [%]
    /* MSG 12 (Engine Warmup?) */
			{2, 2, PT_HEX, Conversions.CNV_ID_TORQUE, 1, 0x12},   // Warmup counter [-]
    /* MSG 30 (T15?) */
			{2, 2, PT_HEX, Conversions.CNV_ID_ONETOONE, 0, 0x30},   // T15 Status? [-]
		};

	/**
	 * list of message parameter descriptions
	 */
	static final String[] MSG_DESCRIPTORS =
		{
			"Engine RPM",
			"Engine regulator",
			"Accelerator pedal",
			"Vehicle speed",
			"Engine coolant temparature",
			"ASR/ESP Status",
			"ASR/ESP Torque Limit",
			"Max. Engine torque",
			"T15 status",
		};

	/**
	 * get List of message dependent telegram parameters in order of appearance
	 * Each parameter set contains following elements<br>
	 * <pre>START,  LEN,   TYPE,   CONVERSION_ID,                   , ID          // REMARKS </pre>
	 * <pre>---------------------------------------------------------------------------------</pre>
	 */
	public int[][] getMsgParameters()
	{
		return (MSG_PARAMETERS);
	}

	/**
	 * get list of message parameter descriptions
	 *
	 * @return list of messag parameter descriptors
	 */
	public String[] getMsgDescriptors()
	{
		return (MSG_DESCRIPTORS);
	}
}
