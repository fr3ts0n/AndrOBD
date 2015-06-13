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
		// load code list from text files
		loadFromResource("prot/res/pcodes");
		loadFromResource("prot/res/ucodes");
	}

	/**
	 * Construct a new code list and initialize it with ressources files
	 *
	 * @param ressources Array of ressources file names
	 */
	public ObdCodeList(String[] ressources)
	{
		// init from ressources list
		for (String ressource : ressources)
		{
			loadFromResource(ressource);
		}
	}

	/**
	 * initialize list from resource file (tab delimited)
	 *
	 * @param resource name of resource to be loaded
	 */
	@Override
	protected void loadFromResource(String resource)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;

		try
		{
			rdr = new BufferedReader(new InputStreamReader(getClass().getResource(resource).openStream()));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// replace all optional quotes from CSV code list
				currLine = currLine.replaceAll("\"", "");
				// split CSV line into parameters
				params = currLine.split("\t");
				// insert fault code element
				put(ObdCodeItem.getNumericCode(params[0]), new ObdCodeItem(params[0], params[1]));
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String physToPhysFmtString(Number value, String format)
	{
		String result = "Fault code unknown";
		EcuCodeItem code = get(value.intValue());
		if (code != null)
		{
			result = code.get(ObdCodeItem.FID_DESCRIPT).toString();
		}
		result = ObdCodeItem.getPCode(value.intValue()) + " - " + result;
		return (result);
	}
}
