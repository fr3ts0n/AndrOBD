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
import java.util.Iterator;
import java.util.Vector;

/**
 * Collection of all known data items:
 * <pre>
 * The data structure looks as follows:
 * Service -- PID -- EcuDataItem
 *         |      |- EcuDataItem
 *         |      |- ...
 * ...     |- PID -- EcuDataItem
 *         |      |- ...
 *         |- ... -- ...
 * Service ...
 * </pre>
 *
 * @author erwin
 */
public class EcuDataItems extends HashMap<Integer, HashMap<Integer, Vector<EcuDataItem>>>
{
	/** SerialVerion UID */
	private static final long serialVersionUID = 5525561909111851836L;
	/** CSV field positions */
	static final int FLD_SVC = 0;
	static final int FLD_PID = 1;
	static final int FLD_OFS = 2;
	static final int FLD_LEN = 3;
	static final int FLD_FORMULA = 4;
	static final int FLD_DIGITS = 5;
	static final int FLD_LABEL = 6;
	static final int FLD_DESCRIPTION = 7;

	// set of all conversions
	static EcuConversions cnv = new EcuConversions();
	// the data logger
	static Logger log = Logger.getLogger("data.items");

	public EcuDataItems()
	{
		initFromRessource("res/pids.csv");
	}

	/**
	 * read data from ressource file into data structure
	 *
	 * @param ressource the ressource file (csv)
	 */
	private void initFromRessource(String ressource)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;
		Conversion[] currCnvSet;
		EcuDataItem newItm;
		int line = 0;
		try
		{
			rdr = new BufferedReader(new InputStreamReader(getClass().getResource(ressource).openStream()));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// ignore first line
				if (++line == 1)
				{
					continue;
				}
				// repalce all optional quotes from CSV code list
				currLine = currLine.replaceAll("\"", "");
				// split CSV line into parameters
				params = currLine.split("\t");

				currCnvSet = cnv.get(params[FLD_FORMULA]);
				if (currCnvSet == null)
				{
					log.warn("Conversion not found: " + params[FLD_FORMULA] + " " + currLine);
				}
				// create linear conversion
				newItm = new EcuDataItem(Integer.decode(params[FLD_PID]).intValue(),
					Integer.parseInt(params[FLD_OFS]),
					Integer.parseInt(params[FLD_LEN]),
					currCnvSet,
					Integer.parseInt(params[FLD_DIGITS]),
					params[FLD_LABEL]);

				// enter data item for all specified services
				String[] services = params[FLD_SVC].split(",");
				for (int i = 0; i < services.length; i++)
				{
					int svcId = Integer.decode(services[i]).intValue();
					// check if service existes already
					HashMap<Integer, Vector<EcuDataItem>> currSvc = get(svcId);
					// if not - create it
					if (currSvc == null)
					{
						currSvc = new HashMap<Integer, Vector<EcuDataItem>>();
						log.debug("+SVC: " + services[i] + " - " + currSvc);
					}

					// check if item list exists for current PID
					Vector<EcuDataItem> currVec = currSvc.get(newItm.pid);
					// if not -- create it
					if (currVec == null)
					{
						currVec = new Vector<EcuDataItem>();
						log.debug("+PID: " + newItm.pid + " - " + currVec);
					}
					// enter data item into list of items / PID
					currVec.add(newItm);
					// and update list in into the pid map for corresponding service
					currSvc.put(newItm.pid, currVec);
					// update map of services
					put(svcId, currSvc);
					// debug message of new enty
					log.debug("+" + services[i] + "/" + params[FLD_PID] + " - " + currVec);
				}
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * get all data items for selected service and PID
	 *
	 * @param service service to search data items for
	 * @param pid     pid to search data items for
	 * @return Vector to data items - or null if no data items exist
	 */
	public Vector<EcuDataItem> getPidDataItems(int service, int pid)
	{
		Vector<EcuDataItem> currVec = null;
		HashMap<Integer, Vector<EcuDataItem>> currSvc = get(service);
		if (currSvc != null)
		{
			currVec = currSvc.get(pid);
		}
		return (currVec);
	}

	/**
	 * Update all EcuDataItems with new data from buffer
	 *
	 * @param service service of current data
	 * @param pid     pid of current data
	 * @param buffer  data buffer to do conversions on
	 */
	void updateDataItems(int service, int pid, char[] buffer)
	{
		EcuDataItem currItm;
		Vector<EcuDataItem> currItms = getPidDataItems(service, pid);

		Iterator<EcuDataItem> it = currItms.iterator();
		while (it.hasNext())
		{
			currItm = it.next();
			currItm.updatePvFomBuffer(buffer);
		}
	}

}

