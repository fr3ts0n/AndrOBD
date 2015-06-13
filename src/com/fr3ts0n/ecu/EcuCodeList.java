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
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Vehicle fault code list
 *
 * @author erwin
 */
public class EcuCodeList extends HashMap<Integer, EcuCodeItem>
	implements Conversion
{
	private static final long serialVersionUID = 219865459629423028L;

	/**
	 * construct a new code list
	 */
	public EcuCodeList()
	{
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param ressources Array of ressources file names
	 */
	public EcuCodeList(String[] ressources)
	{
		// init from ressources list
		for (String ressource : ressources)
		{
			loadFromResource(ressource);
		}
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param ressources Array of ressources file names
	 * @param idRadix    radix of numeric code id
	 */
	public EcuCodeList(String[] ressources, int idRadix)
	{
		// init from ressources list
		for (int i = 0; i < ressources.length; i++)
		{
			loadFromResource(ressources[i], idRadix);
		}
	}

	public String getUnits()
	{
		return "-";
	}

	/**
	 * initialize list from ressource file (tab delimited)
	 *
	 * @param ressource name of ressource to be loaded
	 */
	protected void loadFromResource(String ressource)
	{
		loadFromResource(ressource, 10);
	}

	/**
	 * initialize list from ressource file (tab delimited)
	 *
	 * @param ressource name of ressource to be loaded
	 * @param idRadix   radix of numeric code id
	 */
	protected void loadFromResource(String ressource, int idRadix)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;

		try
		{
			rdr = new BufferedReader(new InputStreamReader(getClass().getResource(ressource).openStream()));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// if line is not empty and is not a remark
				if (currLine.trim().length() > 0 && !currLine.startsWith("#") && !currLine.startsWith("//"))
				{
					// repalce all optional quotes from CSV code list
					currLine = currLine.replaceAll("\"", "");
					// split CSV line into parameters
					params = currLine.split("\t");
					// insert fault code element
					put(Integer.valueOf(params[0], idRadix), new EcuCodeItem(params[0], params[1]));
				}
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public Number memToPhys(long value)
	{
		return (float) value;
	}

	public String memToString(Number value, int numDecimals)
	{
		String fmt = "%." + numDecimals + "f";
		return physToPhysFmtString(memToPhys(value.longValue()), fmt);
	}

	public Number physToMem(Number value)
	{
		return value;
	}

	@Override
	public String physToPhysFmtString(Number value, String format)
	{
		String result;
		EcuCodeItem code = get(value.intValue());
		if (code != null)
		{
			result = code.get(ObdCodeItem.FID_CODE).toString()
				+ " - "
				+ code.get(ObdCodeItem.FID_DESCRIPT).toString();
		} else
		{
			result = value.toString() + " - Fault code unknown";
		}
		return (result);
	}

}
