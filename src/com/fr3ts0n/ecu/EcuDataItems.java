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

import com.fr3ts0n.ecu.prot.obd.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

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
	enum FLD
	{
		SVC,
		PID,
		OFS,
		LEN,
		BIT_OFS,
		BIT_LEN,
		BIT_MASK,
		FORMULA,
		FORMAT,
		MIN,
		MAX,
		MNEMONIC,
		LABEL,
		DESCRIPTION,
		NUMBEROFFIELDS
	}

	// set of all conversions
	public static EcuConversions cnv;
	// the data logger
	static Logger log = Logger.getLogger("data.items");

	/**
	 * Create data items from default CSV pidResource files
	 * (prot/obd/res/pids.csv, prot/obd/res/conversions.csv)
	 */
	public EcuDataItems()
	{
		this( "prot/obd/res/pids.csv",
              "prot/obd/res/conversions.csv",
              "com.fr3ts0n.ecu.prot.obd.res.messages");
	}

	/**
	 * Create data items from CSV pidResource file
	 * @param pidResource resource file for PIDs (csv)
	 * @param conversionResource resource file for conversions (csv)
	 */
	public EcuDataItems(String pidResource, String conversionResource, String resourceBundleName)
	{
		Messages.init(resourceBundleName);
		cnv = new EcuConversions(conversionResource);
		loadFromResource(pidResource);
	}

	/**
	 * read data from resource file into data structure
	 *
	 * @param resource the resource file (csv)
	 */
	private void loadFromResource(String resource)
	{
		try
		{
			loadFromStream(getClass().getResource(resource).openStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * read data from input stream (csv) into data structure
	 *
	 * @param inStr the csv input stream
	 */
	public void loadFromStream(InputStream inStr)
	{
		BufferedReader rdr;
		String currLine;
		String[] params;
		Conversion[] currCnvSet;
		EcuDataItem newItm;
		int line = 0;
		try
		{
			rdr = new BufferedReader(new InputStreamReader(inStr));
			// loop through all lines of the file ...
			while ((currLine = rdr.readLine()) != null)
			{
				// ignore first line
				if (++line == 1 || currLine.startsWith("#")) //$NON-NLS-1$
				{
					continue;
				}
				// repalce all optional quotes from CSV code list
				currLine = currLine.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
				// split CSV line into parameters
				params = currLine.split("\t"); //$NON-NLS-1$

				currCnvSet = cnv.get(params[FLD.FORMULA.ordinal()]);
				if (currCnvSet == null)
				{
					log.warning("Conversion not found: " + params[FLD.FORMULA.ordinal()] + " " + currLine); //$NON-NLS-1$ //$NON-NLS-2$
				}
				// try to use MIN/MAX values from CSV
				Float minVal = null;
				Float maxVal = null;
				try {	minVal = Float.parseFloat(params[FLD.MIN.ordinal()]);	}
				catch(NumberFormatException ex) {	/* ignore */ }
				try {	maxVal = Float.parseFloat(params[FLD.MAX.ordinal()]);	}
				catch(NumberFormatException e) { /* ignore */	}
				String label = Messages.getString(params[FLD.MNEMONIC.ordinal()],
				                                  params[FLD.LABEL.ordinal()]);
				// create linear conversion
				newItm = new EcuDataItem(Integer.decode(params[FLD.PID.ordinal()]).intValue(),
										 Integer.parseInt(params[FLD.OFS.ordinal()]),
										 Integer.parseInt(params[FLD.LEN.ordinal()]),
									     Integer.parseInt(params[FLD.BIT_OFS.ordinal()]),
									     Integer.parseInt(params[FLD.BIT_LEN.ordinal()]),
									     Long.decode(params[FLD.BIT_MASK.ordinal()]).longValue(),
									     currCnvSet,
										 params[FLD.FORMAT.ordinal()],
										 minVal,
										 maxVal,
										 label,
					                     params[FLD.MNEMONIC.ordinal()]);

				// enter data item for all specified services
				String[] services = params[FLD.SVC.ordinal()].split(","); //$NON-NLS-1$
				for (String service : services)
				{
					int svcId = Integer.decode(service);
					appendItemToService(svcId, newItm);
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
	 * get all data items for selected service
	 *
	 * @param service service to search data items for
	 * @return Vector to data items - or null if no data items exist
	 */
	public Vector<EcuDataItem> getSvcDataItems(int service)
	{
		Vector<EcuDataItem> result = new Vector<EcuDataItem>();
		HashMap<Integer, Vector<EcuDataItem>> currSvc = get(service);
		if (currSvc != null)
		{
			for(Vector<EcuDataItem> currVec : currSvc.values())
			{
				result.addAll(currVec);
			}
		}
		return (result);
	}

	/**
	 * append new data item to specified service
	 * @param service service to add item to
	 * @param newItem EcuDataItem to be added
	 */
	public void appendItemToService(int service, EcuDataItem newItem)
	{
		// check if service existes already
		HashMap<Integer, Vector<EcuDataItem>> currSvc = get(service);
		// if not - create it
		if (currSvc == null)
		{
			currSvc = new HashMap<Integer, Vector<EcuDataItem>>();
			log.finer("+SVC: " + service + " - " + currSvc); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// check if item list exists for current PID
		Vector<EcuDataItem> currVec = currSvc.get(newItem.pid);
		// if not -- create it
		if (currVec == null)
		{
			currVec = new Vector<EcuDataItem>();
			log.finer("+PID: " + newItem.pid + " - " + currVec); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// enter data item into list of items / PID
		currVec.add(newItem);
		// and update list in into the pid map for corresponding service
		currSvc.put(newItem.pid, currVec);
		// update map of services
		put(service, currSvc);
		// debug message of new enty
		log.finer("+" + service + "/" + String.format("0x%02X",newItem.pid) + " - " + currVec); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Update all EcuDataItems with new data from buffer
	 *
	 * @param service service of current data
	 * @param pid     pid of current data
	 * @param buffer  data buffer to do conversions on
	 */
	public void updateDataItems(int service, int pid, char[] buffer)
	{
		EcuDataItem currItm;
		Vector<EcuDataItem> currItms = getPidDataItems(service, pid);

		for (EcuDataItem currItm1 : currItms)
		{
			currItm = currItm1;
			currItm.updatePvFomBuffer(buffer);
		}
	}

}

