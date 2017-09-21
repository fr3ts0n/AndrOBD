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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Diagnostic data conversions
 *
 * @author erwin
 */
public class EcuConversions extends HashMap<String, Conversion[]>
{
	/** SerialVersion UID */
	private static final long serialVersionUID = 273813879102783740L;

	/** conversion type IDs from CSV file */
	public static final String CNV_TYPE_LINEAR      = "LINEAR";
	public static final String CNV_TYPE_HASH        = "HASH";
	public static final String CNV_TYPE_BITMAP      = "BITMAP";
	public static final String CNV_TYPE_CODELIST    = "CODELIST";
	public static final String CNV_TYPE_PCODELIST   = "PCODELIST";
	public static final String CNV_TYPE_VAG         = "VAG";
	public static final String CNV_TYPE_INT         = "INTEGER";
	public static final String CNV_TYPE_ASCII       = "ASCII";

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
	static final int FLD_PARAMETERS = 10;

	// the data logger
	static Logger log = Logger.getLogger("data.cnv");

	/** DEFAULT type conversion */
	public static final NumericConversion dfltCnv = new IntConversion();

	/** code list conversion */
	public static EcuCodeList codeList = null;
	/**
	 * Create conversion list from default resource file (tab delimited csv)
	 * (prot/res/conversion.csv)
	 */
	public EcuConversions()
	{
		// add dynamic entris from csv file(s)
		this("prot/res/obd/conversions.csv");
	}

	/**
	 * Create conversion list from resource file (tab delimited csv)
	 *
	 * @param resource name of resource to be loaded
	 */
	public EcuConversions(String resource)
	{
		// add static conversions
		put("DEFAULT", new Conversion[]{dfltCnv, dfltCnv});
		// add dynamic entris from csv file(s)
		loadFromResource(resource);
	}

	public void loadFromStream(InputStream inStr)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;
		Conversion[] currCnvSet;
		Conversion newCnv;
		int line = 0;

		try
		{
			rdr = new BufferedReader(new InputStreamReader(inStr));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// ignore line 1
				if (++line == 1)
				{
					continue;
				}

				// replace all optional quotes from CSV code list
				currLine = currLine.replaceAll("\"", "");
				// split CSV line into parameters
				params = currLine.split("\t");
				if (params[FLD_TYPE].equals(CNV_TYPE_LINEAR))
				{
					// create linear conversion
					newCnv = new LinearConversion(Integer.parseInt(params[FLD_FACTOR]),
						Integer.parseInt(params[FLD_DIVIDER]),
						Integer.parseInt(params[FLD_OFFSET]),
						Integer.parseInt(params[FLD_PHOFFSET]),
						params[FLD_UNITS]);
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_HASH))
				{
					// create HashConversion based on CSV data
					newCnv = new HashConversion( String.valueOf(params[FLD_PARAMETERS]).split(";") );
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_BITMAP))
				{
					// create BitmapConversion based on CSV parameters
					newCnv = new BitmapConversion( String.valueOf(params[FLD_PARAMETERS]).split(";") );
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_CODELIST))
				{
					// create ECU code list based on ResourceBundle
					codeList = new EcuCodeList( String.valueOf(params[FLD_PARAMETERS]));
					newCnv = codeList;
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_PCODELIST))
				{
					// create OBD code list based on ResourceBundle
					codeList = new ObdCodeList( String.valueOf(params[FLD_PARAMETERS]));
					newCnv = codeList;
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_VAG))
				{
					// create VAG conversion
					newCnv = new VagConversion(Integer.parseInt(params[FLD_VARIANT]),
						Double.parseDouble(params[FLD_FACTOR]) / Integer.parseInt(params[FLD_DIVIDER]),
						Double.parseDouble(params[FLD_OFFSET]),
						params[FLD_UNITS]);
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_ASCII))
				{
					newCnv = null;
				}
				else if (params[FLD_TYPE].equals(CNV_TYPE_INT))
				{
					newCnv = dfltCnv;
				}
				else
				{
					newCnv = dfltCnv;
				}

				// insert fault code element
				currCnvSet = get(params[FLD_NAME]);
				// if this conversion does not exist yet ...
				if (currCnvSet == null)
				{
					// create new set for metric and imperial
					currCnvSet = new Conversion[EcuDataItem.SYSTEM_TYPES];
					// and initialize both systems with this data
					for (int i = 0; i < EcuDataItem.SYSTEM_TYPES; i++)
					{
						currCnvSet[i] = newCnv;
						log.finer("+" + params[FLD_NAME] + "/" + params[FLD_SYSTEM] + " - " + String.valueOf(newCnv));
					}
				} else
				{
					// if it is known already, then only update the matching system
					for (int i = 0; i < EcuDataItem.SYSTEM_TYPES; i++)
					{
						if (EcuDataItem.cnvSystems[i].equals(params[FLD_SYSTEM]))
						{
							currCnvSet[i] = newCnv;
							log.finer("+" + params[FLD_NAME] + "/" + params[FLD_SYSTEM] + " - " + newCnv.toString());
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

	/**
	 * load conversion list from resource file (tab delimited)
	 *
	 * @param resource name of resource to be loaded
	 */
	private void loadFromResource(String resource)
	{
		try
		{
			loadFromStream(getClass().getResource(resource).openStream());
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

}
