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
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.prot.ProtoHeader;
import com.fr3ts0n.pvs.PvList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;


/**
 * Base class for all CAN protocol implementations
 *
 * @author erwin
 */
public abstract class CanProt extends ProtoHeader
{

	public static final int ID_CAN_SVC = 0;
	/**
	 * additional field indices (extending message parameters)
	 * to table below
	 */
	public static final int FLD_ID_CONV = 3;
	public static final int FLD_ID_DECIMALS = 4;
	public static final int FLD_ID_CANID = 5;

	/**
	 * List of telegram parameters in order of appearance
	 */
	static final int CAN_PARAMETERS[][] =
	/*  START,  LEN,     PARAM-TYPE     // REMARKS */
    /* ------------------------------------------- */
		{{0, 2, PT_HEX},     // ID_CAN_SVC
		};

	static final String[] CAN_DESCRIPTORS =
		{
			"CAN Service",
		};

	/** process variable list which holds all parameters */
	public PvList CanPvs = new PvList();

	/** internal Map for CAN messages to parameters */
	HashMap<Integer, Vector<Integer>> canMsgMap = new HashMap<Integer, Vector<Integer>>();

	/** Creates a new instance of CanProt */
	public CanProt()
	{
		for (int i = 0; i < getMsgParameters().length; i++)
		{
			int convId = getMsgParameters()[i][FLD_ID_CONV];
			Integer paramId = Integer.valueOf(i);
			Integer canId = getMsgParameters()[i][FLD_ID_CANID];

			// enter all parameters per CAN message
			Vector<Integer> paramList = canMsgMap.get(canId);
			if (paramList == null)
				paramList = new Vector<Integer>();
			paramList.add(paramId);
			canMsgMap.put(canId, paramList);

			/** enter process variables for each parameter */
			EcuDataPv pidData = new EcuDataPv();

			pidData.put(EcuDataPv.FID_PID, paramId);
			pidData.put(EcuDataPv.FID_DESCRIPT, getMsgDescriptors()[i]);
			pidData.put(EcuDataPv.FID_UNITS, Conversions.getUnits(convId));
			pidData.put(EcuDataPv.FID_VALUE, Float.valueOf(0));
			pidData.put(EcuDataPv.FID_FORMAT, "%."+getMsgParameters()[i][FLD_ID_DECIMALS]+"d");
			pidData.put(EcuDataPv.FID_CNVID, Integer.valueOf(getMsgParameters()[i][FLD_ID_CONV]));

			CanPvs.put(paramId, pidData);
		}
	}

	/**
	 * prepare process variables for each PID
	 *
	 * @param checkIfSupported if true, only supported PIDs will be processed
	 */
	public void preparePidPvs(boolean checkIfSupported)
	{
		for (int i = 0; i < getMsgParameters().length; i++)
		{
			Integer paramId = i;
			/** enter process variables for each parameter */
			EcuDataPv pidData = (EcuDataPv) CanPvs.get(paramId);
			if (pidData != null)
			{
				int convId = getMsgParameters()[i][FLD_ID_CONV];
				pidData.put(EcuDataPv.FID_UNITS, Conversions.getUnits(convId));
			}
		}
	}

	/**
	 * get List of message dependent telegram parameters in order of appearance
	 * Each parameter set contains following elements<br>
	 * <pre>START,  LEN,   TYPE,   CONVERSION_ID,                   , ID          // REMARKS </pre>
	 * <pre>---------------------------------------------------------------------------------</pre>
	 */
	public abstract int[][] getMsgParameters();

	/**
	 * get list of message parameter descriptions
	 *
	 * @return list of messag parameter descriptors
	 */
	public abstract String[] getMsgDescriptors();

	/**
	 * get physical parameter value from message buffer
	 */
	float getMsgValue(int ID, char[] buffer)
	{
		int memVal = ((Integer) getParamValue(ID, getMsgParameters(), buffer)).intValue();
		return (Conversions.memToPhys(memVal, getMsgParameters()[ID][FLD_ID_CONV]));
	}

	/**
	 * create a new telegram header for selected payload data buffer
	 * inclunding setting all ID's, sizes and validity issues
	 *
	 * @param buffer buffer of payload data
	 * @return buffer of new telegram header
	 */
	protected char[] getNewHeader(char[] buffer)
	{
		return (emptyBuffer);
	}

	/**
	 * return message footer for protocol payload
	 *
	 * @param buffer buffer of payload data
	 * @return buffer of message footer
	 */
	public char[] getFooter(char[] buffer)
	{
		return (emptyBuffer);
	}

	/**
	 * create a new telegram header inclunding setting all ID's, sizes and
	 * validity issues
	 *
	 * @return buffer of new telegram header
	 */
	protected char[] getNewHeader(char[] buffer, int type, Object id)
	{
		return (emptyBuffer);
	}

	/**
	 * list of parameters for specific protocol
	 *
	 * @return complete set of protocol parameters
	 */
	public int[][] getTelegramParams()
	{
		return (CAN_PARAMETERS);
	}

	/**
	 * list of parameter descriptions for specific protocol
	 *
	 * @return complete set of protocol parameter description strings
	 */
	protected String[] getParamDescriptors()
	{
		return (CAN_DESCRIPTORS);
	}

	/**
	 * handle incoming protocol telegram
	 * default implementaion only checks telegram and notifies listeners with
	 * protocol payload
	 *
	 * @param buffer - telegram buffer
	 * @return number of listeners notified
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int handleTelegram(char[] buffer)
	{
		int retValue = 0;
		try
		{
			Integer msgId = (Integer) getParamValue(ID_CAN_SVC, buffer);
			Vector params = canMsgMap.get(msgId);
			if (params != null)
			{
				Iterator it = params.iterator();
				while (it.hasNext())
				{
					Integer parId = (Integer) it.next();
					float value = getMsgValue(parId.intValue(), buffer);
					EcuDataPv pv = (EcuDataPv) CanPvs.get(parId);
					if (pv != null)
					{
						// now store all changes to PV
						pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_VALUE], new Float(value));
					}
				}
				retValue = params.size();
			}
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString(), e);
		}
		return retValue;
	}

}
