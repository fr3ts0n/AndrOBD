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

import java.util.Arrays;
import java.util.Comparator;

/**
 * Collection of all known OBD data items (PIDs)
 *
 * @author erwin
 */
public class Pids
{

	// maximum values / bitmasks per affected byte
	public static final long[] valueMask =
		{
			0x00,
			0xFF,
			0xFFFF,
			0xFFFFFF,
			0xFFFFFFFF
		};

	/* PID definitions */
	public static final Pid[] PIDs =
		{
			//       pid,ofs,len, formula                         digits  label
			new Pid(0x01, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Number of Fault Codes"), // fuel_system1_status_formula // fuel_system2_status_formula
			new Pid(0x02, 0, 2, Conversions.CNV_ID_OBD_CODELIST, -1, "DTC-Fault location"), // fault location conversion
			new Pid(0x03, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "Fuel System Status"), // fuel_system1_status_formula // fuel_system2_status_formula
			new Pid(0x04, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Calculated Load Value"),
			new Pid(0x05, 0, 1, Conversions.CNV_ID_TEMPERATURE, 1, "Coolant Temperature"),
			new Pid(0x06, 0, 2, Conversions.CNV_ID_PERCENT7_REL, 1, "Short Term Fuel Trim (Bank 1)"), // short_term_fuel_trim
			new Pid(0x07, 0, 2, Conversions.CNV_ID_PERCENT7_REL, 1, "Long Term Fuel Trim (Bank 1)"), // long_term_fuel_trim
			new Pid(0x08, 0, 2, Conversions.CNV_ID_PERCENT7_REL, 1, "Short Term Fuel Trim (Bank 2)"), // short_term_fuel_trim
			new Pid(0x09, 0, 2, Conversions.CNV_ID_PERCENT7_REL, 1, "Long Term Fuel Trim (Bank 2)"), // long_term_fuel_trim
			new Pid(0x0A, 0, 1, Conversions.CNV_ID_PRESS, 1, "Fuel Pressure (gauge)"),
			new Pid(0x0B, 0, 1, Conversions.CNV_ID_PRESS_AIR, 1, "Intake Manifold Pressure"),
			new Pid(0x0C, 0, 2, Conversions.CNV_ID_RPM, 0, "Engine RPM"),
			new Pid(0x0D, 0, 1, Conversions.CNV_ID_VEHSPEED, 0, "Vehicle Speed"),
			new Pid(0x0E, 0, 1, Conversions.CNV_ID_ANGLE, 2, "Timing Advance (Cyl. #1)"),
			new Pid(0x0F, 0, 1, Conversions.CNV_ID_TEMPERATURE, 1, "Intake Air Temperature"),
			new Pid(0x10, 0, 2, Conversions.CNV_ID_AIRFLOW, 2, "Air Flow Rate (MAF sensor)"),
			new Pid(0x11, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Absolute Throttle Position"),
			new Pid(0x12, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Secondary air status"), // secondary_air_status_formula,
			new Pid(0x13, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Location of O2 Sensor(s)"), // o2_sensor_formula,
			new Pid(0x14, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 1, Bank 1"), // o2_sensor_formula,
			new Pid(0x15, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 2, Bank 1"), // o2_sensor_formula,
			new Pid(0x16, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 3, Bank 1"), // o2_sensor_formula,
			new Pid(0x17, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 4, Bank 1"), // o2_sensor_formula,
			new Pid(0x18, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 1, Bank 2"), // o2_sensor_formula,
			new Pid(0x19, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 2, Bank 2"), // o2_sensor_formula,
			new Pid(0x1A, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 3, Bank 2"), // o2_sensor_formula,
			new Pid(0x1B, 0, 2, Conversions.CNV_ID_ONETOONE, 0, "O2 Sensor 4, Bank 2"), // o2_sensor_formula,
			new Pid(0x1C, 0, 1, Conversions.CNV_ID_OBD_TYPE, -1, "OBD conforms to"), // obd_requirements_formula,
			new Pid(0x1D, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Location of O2 Sensor(s)"), // o2_sensor_formula,
			new Pid(0x1E, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Power Take-Off Status"), // pto_status_formula,
			new Pid(0x1F, 0, 2, Conversions.CNV_ID_HOURS, 2, "Time Since Engine Start"),
			new Pid(0x21, 0, 2, Conversions.CNV_ID_DISTANCE, 0, "Distance since MIL activated"),
			new Pid(0x22, 0, 2, Conversions.CNV_ID_PRESS_REL, 3, "FRP rel. to manifold vacuum"), // fuel rail pressure relative to manifold vacuum
			new Pid(0x23, 0, 2, Conversions.CNV_ID_PRESS_WIDERANGE, 1, "Fuel Pressure (gauge)"), // fuel rail pressure (gauge), wide range
			new Pid(0x24, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 1, Bank 1 (WR)"), // o2_sensor_wrv_formula,  // o2 sensors (wide range), voltage
			new Pid(0x25, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 2, Bank 1 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x26, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 3, Bank 1 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x27, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 4, Bank 1 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x28, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 1, Bank 2 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x29, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 2, Bank 2 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x2A, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 3, Bank 2 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x2B, 0, 2, Conversions.CNV_ID_VOLTAGE_HIGHRES, 3, "O2 Sensor 4, Bank 2 (WR)"), // o2_sensor_wrv_formula,
			new Pid(0x2C, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Commanded EGR"),
			new Pid(0x2D, 0, 1, Conversions.CNV_ID_PERCENT_REL, 2, "EGR Error"),
			new Pid(0x2E, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Commanded Evaporative Purge"),
			new Pid(0x2F, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Fuel Level Input"),
			new Pid(0x30, 0, 1, Conversions.CNV_ID_ONETOONE, 0, "Warm-ups since ECU reset"),
			new Pid(0x31, 0, 2, Conversions.CNV_ID_DISTANCE, 0, "Distance since ECU reset"),
			new Pid(0x32, 0, 2, Conversions.CNV_ID_PRESS_VAPOR, 2, "Evap System Vapor Pressure"),
			new Pid(0x33, 0, 1, Conversions.CNV_ID_PRESS_AIR, 1, "Barometric Pressure (absolute)"),
			new Pid(0x34, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 1, Bank 1 (WR)"), // o2_sensor_wrc_formula, // o2 sensors (wide range), current
			new Pid(0x35, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 2, Bank 1 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x36, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 3, Bank 1 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x37, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 4, Bank 1 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x38, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 1, Bank 2 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x39, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 2, Bank 2 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x3A, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 3, Bank 2 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x3B, 0, 2, Conversions.CNV_ID_RATIO_WIDERANGE, 1, "O2 Sensor 4, Bank 2 (WR)"), // o2_sensor_wrc_formula,
			new Pid(0x3C, 0, 2, Conversions.CNV_ID_TEMP_WIDERANGE, 1, "CAT Temperature, B1S1"),
			new Pid(0x3D, 0, 2, Conversions.CNV_ID_TEMP_WIDERANGE, 1, "CAT Temperature, B2S1"),
			new Pid(0x3E, 0, 2, Conversions.CNV_ID_TEMP_WIDERANGE, 1, "CAT Temperature, B1S2"),
			new Pid(0x3F, 0, 2, Conversions.CNV_ID_TEMP_WIDERANGE, 1, "CAT Temperature, B2S2"),
			new Pid(0x41, 0, 4, Conversions.CNV_ID_ONETOONE, 0, "Monitor status for current driving cycle"),
			new Pid(0x42, 0, 2, Conversions.CNV_ID_VOLTAGE, 3, "ECU voltage"),
			new Pid(0x43, 0, 2, Conversions.CNV_ID_PERCENT, 1, "Absolute Engine Load"),
			new Pid(0x44, 0, 2, Conversions.CNV_ID_RATIO, 3, "Commanded Equivalence Ratio"),
			new Pid(0x45, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Relative Throttle Position"),
			new Pid(0x46, 0, 1, Conversions.CNV_ID_TEMPERATURE, 1, "Ambient Air Temperature"), // same scaling as $0F
			new Pid(0x47, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Absolute Throttle Position B"),
			new Pid(0x48, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Absolute Throttle Position C"),
			new Pid(0x49, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Accelerator Pedal Position D"),
			new Pid(0x4A, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Accelerator Pedal Position E"),
			new Pid(0x4B, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Accelerator Pedal Position F"),
			new Pid(0x4C, 0, 1, Conversions.CNV_ID_PERCENT, 1, "Comm. Throttle Actuator Cntrl"), // commanded TAC
			new Pid(0x4D, 0, 2, Conversions.CNV_ID_HOURS, 2, "Engine running while MIL on"), // minutes run by the engine while MIL activated
			new Pid(0x4E, 0, 2, Conversions.CNV_ID_HOURS, 2, "Time since DTCs cleared"),
			new Pid(0x4F, 0, 4, Conversions.CNV_ID_ONETOONE, 0, "Maximum values for Equivalence Ratio, Oxygen Sensor Voltage, Oxygen Sensor Current and Intake Manifold Absolute Pressure"),
		};
	/**
	 * Comparator object to compare Pid objects
	 */
	@SuppressWarnings("rawtypes")
	static Comparator pidComparator = new Comparator()
	{

		public int compare(Object o1, Object o2)
		{
			int pid1 = o1 instanceof Pid ? ((Pid) o1).pid : Integer.valueOf(String.valueOf(o1));
			int pid2 = o2 instanceof Pid ? ((Pid) o2).pid : Integer.valueOf(String.valueOf(o2));
			return (pid1 - pid2);
		}
	};

	/**
	 * Creates a new instance of Pids
	 */
	@SuppressWarnings("unchecked")
	public Pids()
	{
		// ensure Array ist sorted
		Arrays.sort(PIDs, pidComparator);
	}

	/**
	 * get the Pid-Object to numeric PID
	 *
	 * @param pidNum numeric PID to get PID-Object for
	 * @return PID-Object or NULL if no object available for specified PID
	 */
	@SuppressWarnings("unchecked")
	public static Pid getPid(int pidNum)
	{
		// default result = NOT FOUND
		Pid currPid = null;
		// search list
		int i = Arrays.binarySearch(PIDs, pidNum, pidComparator);
		// if found, set result
		if (i >= 0)
		{
			currPid = PIDs[i];
		}
		// and return it
		return (currPid);
	}

	/**
	 * convert memory/protocol value to physical value
	 *
	 * @param memVal value in memory/protocol format
	 * @param pidNum PID to use for conversion
	 * @return physical value
	 */
	public static float memToPhys(long memVal, int pidNum)
	{
		float result = 0;
		Pid currPid = getPid(pidNum);
		if (currPid != null)
		{
			long mskdVal = memVal & valueMask[currPid.bytes];
			result = Conversions.memToPhys(mskdVal, currPid.cnv);
		}
		return (result);
	}

	/**
	 * convert physical value to memory/protocol value
	 *
	 * @param physVal physical value
	 * @param pidNum  PID to use for conversion
	 * @return value in memory/protocol layout
	 */
	public static long physToMem(float physVal, int pidNum)
	{
		long result = 0;
		Pid currPid = getPid(pidNum);
		if (currPid != null)
		{
			result = java.lang.Math.min(Conversions.physToMem(physVal, currPid.cnv),
				valueMask[currPid.bytes]);
		}
		return (result);
	}
}
