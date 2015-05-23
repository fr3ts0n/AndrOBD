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

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Diagnostic data conversions
 *
 * @author erwin
 */
public class EcuConversions extends HashMap<String, Conversion[]>
{
	/** SerialVersion UID */
	private static final long serialVersionUID = 273813879102783740L;
	/// conversion systems METRIC and IMPERIAL
	public static final int SYSTEM_METRIC = 0;
	public static final int SYSTEM_IMPERIAL = 1;
	public static final int SYSTEM_TYPES = 2;
	// names of conversion types
	public static final String[] cnvSystems =
		{
			"METRIC",
			"IMPERIAL",
		};

	/** CSV field positions */
	static final int FLD_NAME = 0;
	static final int FLD_TYPE = 1;
	static final int FLD_VARIANT = 2;
	static final int FLD_SYSTEM = 3;
	static final int FLD_FACTOR = 4;
	static final int FLD_DIVIDER = 5;
	static final int FLD_OFFSET = 6;
	static final int FLD_PHOFFSET = 7;
	static final int FLD_UNITS = 8;
	static final int FLD_DESCRIPTION = 9;

	// the data logger
	static Logger log = Logger.getLogger("data.cnv");

	// current conversion system
	public static int cnvSystem = SYSTEM_METRIC;

	// DEFAULT type conversion
	static final LinearConversion dfltCnv = new LinearConversion(1, 1, 0, 0, "DFLT");
	// codelist conversion
	public static final ObdCodeList obdCodeList = new ObdCodeList();
	// OBD type conversion
	static final HashConversion cnvObdType = new HashConversion(new String[]{
		"1=OBD II",
		"2=OBD Federal EPA",
		"3=OBD and OBD II",
		"4=OBD I",
		"5=Not OBD compliant",
		"6=EOBD",
		"7=EOBD and OBD II",
		"8=EOBD and OBD",
		"9=EOBD, OBD and OBD II",
		"10=JOBD",
		"11=JOBD and OBD II",
		"12=JOBD and EOBD",
		"13=JOBD, EOBD and OBD II"
	});


	public EcuConversions()
	{
		// add static conversions
		//   name                             METRIC       IMPERIAL
		put("DEFAULT", new Conversion[]{dfltCnv, dfltCnv});
		put("OBD_CODELIST", new Conversion[]{obdCodeList, obdCodeList});
		put("OBD_TYPE", new Conversion[]{cnvObdType, cnvObdType});
		// add dynamic entris from csv file(s)
		initFromRessource("res/conversions.csv");
	}

	/**
	 * initialize list from ressource file (tab delimited)
	 *
	 * @param ressource name of ressource to be loaded
	 */
	private void initFromRessource(String ressource)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;
		Conversion[] currCnvSet;
		Conversion newCnv;
		int line = 0;

		try
		{
			rdr = new BufferedReader(new InputStreamReader(getClass().getResource(ressource).openStream()));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// ignore line 1
				if (++line == 1)
				{
					continue;
				}

				// repalce all optional quotes from CSV code list
				currLine = currLine.replaceAll("\"", "");
				// split CSV line into parameters
				params = currLine.split("\t");
				if (params[FLD_TYPE].equals("LINEAR"))
				{
					// create linear conversion
					newCnv = new LinearConversion(Integer.parseInt(params[FLD_FACTOR]),
						Integer.parseInt(params[FLD_DIVIDER]),
						Integer.parseInt(params[FLD_OFFSET]),
						Integer.parseInt(params[FLD_PHOFFSET]),
						params[FLD_UNITS]);
				} else
				{
					// create VAG conversion
					newCnv = new VagConversion(Integer.parseInt(params[FLD_VARIANT]),
						Double.parseDouble(params[FLD_FACTOR]) / Integer.parseInt(params[FLD_DIVIDER]),
						Double.parseDouble(params[FLD_OFFSET]),
						params[FLD_UNITS]);
				}
				// insert fault code element
				currCnvSet = get(params[FLD_NAME]);
				// if this conversion does not exist yet ...
				if (currCnvSet == null)
				{
					// create new set for metric and imperial
					currCnvSet = new Conversion[SYSTEM_TYPES];
					// and initialize both systems with this data
					for (int i = 0; i < SYSTEM_TYPES; i++)
					{
						currCnvSet[i] = newCnv;
						log.debug("+" + params[FLD_NAME] + "/" + params[FLD_SYSTEM] + " - " + newCnv.toString());
					}
				} else
				{
					// if it is known already, then only update the matching system
					for (int i = 0; i < SYSTEM_TYPES; i++)
					{
						if (cnvSystems[i].equals(params[FLD_SYSTEM]))
						{
							currCnvSet[i] = newCnv;
							log.debug("+" + params[FLD_NAME] + "/" + params[FLD_SYSTEM] + " - " + newCnv.toString());
						}
					}
				}
				// (re-)enter the updated conversion set into map
				put(params[FLD_NAME], currCnvSet);
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
