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

package com.fr3ts0n.ecu.prot.vag;

import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.ecu.EcuCodeList;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.VagConversion;
import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.ProtoHeader;
import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;
import com.fr3ts0n.prot.gui.KLHandler;
import com.fr3ts0n.pvs.PvList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Keyword 1281 protocol (VAG diagnostic protocol)
 *
 * @author erwin
 */
public class Kw1281Prot extends ProtoHeader
	implements TelegramListener,
	TelegramWriter
{
	// command for clearing fault codes

	static final char CMD_CLEAR_DFCs = 0x05;
	// command for END communication
	static final char CMD_END_COMM = 0x06;
	// command for reading fault codes
	static final char CMD_READ_DFCs = 0x07;
	// command for reading single data
	static final char CMD_READ_SINGLE = 0x08;
	// Acknowledge command
	static final char CMD_ACK = 0x09;
	// ID for responded NO DATA available
	static final char ID_NODATA = 0x0A;
	// command for group reading of data items
	static final char CMD_GROUP_READ = 0x29;
	// ID for responded ASCII data
	static final char ID_ASCII_DATA = 0xF6;
	// ID for responded ASCII data
	static final char ID_DFC_DATA = 0xFC;
	// ID for responded GROUP READING data
	static final char ID_GRPREAD_DATA = 0xE7;
	// ID for responded header for GROUP READING DATA
	static final char ID_GRPINFO_HEAD = 0x02;
	// ID for responded GROUP READING DATA
	static final char ID_GRPINFO_DATA = 0xF4;
	// block end marker
	static final char BLOCK_END = 0x03;

	/** service ID for idle opeation */
	public static final int SVC_NONE = 0;
	/** service ID for reading fault codes */
	public static final int SVC_READ_DFCS = 1;
	/** service ID for clearing fault codes */
	public static final int SVC_CLEAR_DFCS = 2;
	/** service ID for reading single data group */
	public static final int SVC_READ_DATA_GRP = 3;
	/** service ID for reading all data */
	public static final int SVC_READ_DATA_ALL = 4;
	/** service ID for shutting down communication */
	public static final int SVC_SHUTDOWN = 5;
	/** service ID for shutting down communication */
	public static final int SVC_FINISHED = 6;

	/** currently selected service */
	private int service = SVC_NONE;
	/** Number of codes set */
	private int numCodes = 0;
	// static telegram footer definition
	static final char[] tgmFooter =
		{
			BLOCK_END
		};
	// static telegram empty payload
	static final char[] tgmEmpty =
		{
		};
	// Telegram field id's
	static final int FLD_ID_LEN = 0;
	static final int FLD_ID_BLKCNT = 1;
	static final int FLD_ID_CMD = 2;
	// number of data items in BLKREAD data block
	static final int BLK_NUM_ITEMS = 4;
	/**
	 * List of telegram parameters in order of appearance
	 */
	static final int TGM_PARAMETERS[][] =
  /*  START,  LEN,     PARAM-TYPE     // REMARKS */
  /* ------------------------------------------- */
		{
			{
				0, 1, PT_NUMERIC
			}, // ID_LEN

			{
				1, 1, PT_NUMERIC
			}, // ID_BLKCNT

			{
				2, 1, PT_NUMERIC
			},     // ID_CMD
		};
	/**
	 * Descriptors of telegram parameters
	 */
	static final String FLD_DESCRIPTORS[] =
		{
			"Length  ",
			"BlockCnt",
			"Command ",
		};
	// data items to be used for data display
	EcuDataItems itms = new EcuDataItems( "prot/vag/res/vag_pids.csv",
			                              "prot/vag/res/vag_conversions.csv",
                                          "com.fr3ts0n.ecu.prot.vag.res.messages");
	/** List of ECU data items */
	public static PvList PidPvs = new PvList();
	/** ECU vehicle identification items */
	public static PvList VidPvs = new PvList();
	/** current fault codes */
	public static PvList tCodes = new PvList();
	/** list of known fault codes */
	public static EcuCodeList knownCodes = new EcuCodeList("com.fr3ts0n.ecu.prot.vag.res.codes");
	/** running telegram block counter */
	char blockCounter = 0;
	/** current data group which was requested */
	private char currDataGroup = 0;
	/** selected data group to be requested next time */
	private char selectedDataGroup = 1;
	/** Vector of all Items within current group data frame */
	private Vector<EcuDataItem> currGrpItems;
	/** Map of all known Group data items */
	public HashMap<Integer, Vector<EcuDataItem>> knownGrpItems =
		new HashMap<Integer, Vector<EcuDataItem>>();

	/** name of last preset saved/loaded */
	protected String lastPresetName = null;

	/**
	 * Default constructor
	 */
	public Kw1281Prot()
	{
		super();
		log = Logger.getLogger("com.fr3ts0n.prot.1281");
		initialize();
	}

	/**
	 * Initialize protocol
	 */
	public void initialize()
	{
		// init
		VidPvs.clear();
		PidPvs.clear();
		// init TC list with "NO codes set"
		tCodes.clear();
		tCodes.put(Integer.valueOf(0), knownCodes.get(Integer.valueOf(0)));
		setNumCodes(0);

		// initialize map of known data items
		knownGrpItems.clear();
		for (int i = 0; i <= 0xFF; i++)
		{
			knownGrpItems.put(Integer.valueOf(i), new Vector<EcuDataItem>());
		}
		currDataGroup = selectedDataGroup = 0;
		currGrpItems = knownGrpItems.get(Integer.valueOf(currDataGroup));
		// notify about property change
		firePropertyChange(new PropertyChangeEvent(this, "preset", null, knownGrpItems));
	}

	/**
	 * load preset data
	 */
	protected void loadPreset()
	{
		Thread loadThd = new Thread()
		{
			@Override
			public void run()
			{
				EcuDataPv vid = (EcuDataPv) VidPvs.get(0);
				String fileName = vid.get(EcuDataPv.FID_VALUE).toString().trim() + ".prs";
				if (fileName.equals(lastPresetName))
				{
					log.info("Preset already loaded:" + fileName);
				} else
				{
					log.info("Load Preset: " + fileName);
					try
					{
						BufferedReader rdr = new BufferedReader(new FileReader(fileName));
						knownGrpItems.clear();
						PidPvs.clear();
						while (rdr.ready())
						{
							String line = rdr.readLine();

							// forget about empty, or remarked lines
							if (line.trim().length() == 0
								|| line.startsWith("#")
								|| line.startsWith("//")) continue;

							String params[] = line.split("\t");
							Integer frmNum = Integer.valueOf(params[0], 0x10);

							// List of all Items within group
							Vector<EcuDataItem> grpVec = new Vector<EcuDataItem>();
							// loop through all items for this group
							for (int i = 1; i < params.length && params[i].length() > 0; i++)
							{
								Vector<EcuDataItem> currVec = itms.getPidDataItems(ID_GRPINFO_DATA,
									Integer.parseInt(params[i], 0x10));

								// add all items in list to corresponding group vector
								Iterator<EcuDataItem> it = currVec.iterator();
								while (it.hasNext())
								{
									EcuDataItem itm = (EcuDataItem) it.next().clone();
									itm.pid = frmNum;
									itm.ofs = i - 1;
									itm.pv.put(EcuDataPv.FID_PID, Integer.valueOf(itm.pid));
									itm.pv.put(EcuDataPv.FID_OFS, Integer.valueOf(itm.ofs));
									grpVec.add(itm);
								}
							}
							// make items known items
							knownGrpItems.put(frmNum, grpVec);
						}
						log.info("Preset loaded: " + fileName);
						// notify about property change
						firePropertyChange(new PropertyChangeEvent(this, "preset", lastPresetName, fileName));
						// remember name of preset
						lastPresetName = fileName;
						rdr.close();
					} catch (IOException ex)
					{
						log.warning("LoadPreset: " + ex.toString());
					}
				}
			}
		};
		// MIN priority to NOT disturb communication
		loadThd.setPriority(Thread.MIN_PRIORITY);
		loadThd.start();
	}

	/**
	 * save current preset data
	 */
	protected void savePreset()
	{
		Thread saveThd = new Thread()
		{
			@Override
			public void run()
			{

				EcuDataPv vid = (EcuDataPv) VidPvs.get(0);
				String fileName = vid.get(EcuDataPv.FID_VALUE).toString().trim() + ".prs";

				// notify about property change
				firePropertyChange(new PropertyChangeEvent(this, "preset", lastPresetName, fileName));
				// remember preset
				lastPresetName = fileName;

				// 1st VID is used as preset filename
				File pstFile = new File(fileName);
				// if file aleady exists, we don't save it
				if (!pstFile.exists())
				{
					log.info("Save Preset: " + fileName);
					FileWriter wtr = null;
					try
					{
						wtr = new FileWriter(pstFile);
						// Loop through all known data grous
						Iterator<Entry<Integer, Vector<EcuDataItem>>> it = knownGrpItems.entrySet().iterator();
						while (it.hasNext())
						{
							Entry<Integer, Vector<EcuDataItem>> itmSet = it.next();
							// write group number
							wtr.write(String.format("%02X\t", itmSet.getKey()));

							// loop through all items within data group
							Vector<EcuDataItem> itms = itmSet.getValue();
							Iterator<EcuDataItem> itItm = itms.iterator();
							while (itItm.hasNext())
							{
								EcuDataItem currItm = itItm.next();
								// write PID of item
								wtr.write(String.format("%02X\t", currItm.pid));
							}
							// finish data group entry
							wtr.write("\n");
						}
						log.info("Preset saved: " + fileName);
					} catch (IOException ex)
					{
						log.severe("SavePreset: " + ex.toString());
					} finally
					{
						try
						{
							wtr.close();
						} catch (IOException ex)
						{
							log.severe("SavePreset: " + ex.toString());
						}
					}
				} else
				{
					log.info("Preset saving skipped: " + fileName);
				}
			}
		};
		// MIN priority to NOT disturb communication
		saveThd.setPriority(Thread.MIN_PRIORITY);
		saveThd.start();
	}

	/* (non-Javadoc)
	   * @see com.fr3ts0n.prot.ProtoHeader#getFooter(char[])
	   */
	@Override
	public char[] getFooter(char[] buffer)
	{
		return tgmFooter;
	}

	/* (non-Javadoc)
	   * @see com.fr3ts0n.prot.ProtoHeader#getNewHeader(char[])
	   */
	@Override
	protected char[] getNewHeader(char[] buffer)
	{
		return getNewHeader(buffer, CMD_ACK, null);
	}

	/* (non-Javadoc)
	   * @see com.fr3ts0n.prot.ProtoHeader#getNewHeader(char[], int, java.lang.Object)
	   */
	@Override
	protected char[] getNewHeader(char[] buffer, int type, Object id)
	{
		char[] hdr = createEmptyHeader();
		// set parameters
		setParamInt(FLD_ID_LEN, hdr, buffer.length + 3);
		setParamInt(FLD_ID_BLKCNT, hdr, ++blockCounter);
		setParamInt(FLD_ID_CMD, hdr, type);

		return (hdr);
	}

	/* (non-Javadoc)
	   * @see com.fr3ts0n.prot.ProtoHeader#getParamDescriptors()
	   */
	@Override
	protected String[] getParamDescriptors()
	{
		return FLD_DESCRIPTORS;
	}

	/* (non-Javadoc)
	   * @see com.fr3ts0n.prot.ProtoHeader#getTelegramParams()
	   */
	@Override
	public int[][] getTelegramParams()
	{
		return TGM_PARAMETERS;
	}

	/**
	 * check telegram if it meets protocol requirements
	 * current implementation only performs a size check, any other check
	 * needs to be implemented in sub-class
	 *
	 * @param buffer = current telegram buffer
	 * @return true if telegram is OK, otherwise false
	 */
	@Override
	public boolean checkTelegram(char[] buffer)
	{
		return (buffer.length >= java.lang.Math.max(getHeaderLength() + getFooterLength(), getParamInt(FLD_ID_LEN, buffer)));
	}

	/**
	 * get next supported data group
	 *
	 * @return next supported data group
	 */
	char getNextDataGroup()
	{
		char grp = getCurrDataGroup();
		// search next item only makes sense if there is at least one more ...
		if (knownGrpItems.size() > 0)
		{
			// search next valid data group
			while (knownGrpItems.get(Integer.valueOf(++grp)) == null)
			{/* just search */}
		}
		if (grp == 0) grp++;
		// return new group number
		return (grp);
	}

	/**
	 * send request to read group data
	 *
	 * @param groupNum data group to be read
	 * @return result of writeTelegram @see writeTelegram
	 */
	int requestGroupData(char groupNum)
	{
		setCurrDataGroup(groupNum);
		// set list of group data items
		currGrpItems = knownGrpItems.get(Integer.valueOf(getCurrDataGroup()));
		char[] payLoad =
			{
				groupNum
			};
		return (writeTelegram(payLoad, CMD_GROUP_READ, null));
	}

	/**
	 * send request to read DFCs
	 *
	 * @return result of writeTelegram @see writeTelegram
	 */
	int requestDFCs()
	{
		return (writeTelegram(tgmEmpty, CMD_READ_DFCs, null));
	}

	/**
	 * send request to clear DFCs
	 *
	 * @return result of writeTelegram @see writeTelegram
	 */
	int requestClearDFCs()
	{
		return (writeTelegram(tgmEmpty, CMD_CLEAR_DFCs, null));
	}

	/**
	 * send ACK telegram
	 *
	 * @return result of writeTelegram @see writeTelegram
	 */
	int requestACK()
	{
		return (writeTelegram(tgmEmpty, CMD_ACK, null));
	}

	/**
	 * send request to end communication
	 *
	 * @return result of writeTelegram @see writeTelegram
	 */
	int requestEndComm()
	{
		return (writeTelegram(tgmEmpty, CMD_END_COMM, null));
	}

	/**
	 * @return the service
	 */
	public /** currently selected service */
	int getService()
	{
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(int service)
	{
		if (service != this.service)
		{
			this.service = service;
			// immediate dialog actions ...
			switch (service)
			{
				case SVC_CLEAR_DFCS:
				case SVC_READ_DFCS:
					tCodes.clear();
					setNumCodes(0);
					break;

				case SVC_READ_DATA_ALL:
					showAllGroupItems();
					break;

				case SVC_READ_DATA_GRP:
					break;
			}
		}
	}

	/**
	 * Getter for property numCodes.
	 *
	 * @return Value of property numCodes.
	 */
	public int getNumCodes()
	{
		return this.numCodes;
	}

	/**
	 * Setter for property numCodes.
	 *
	 * @param numCodes New value of property numCodes.
	 */
	public void setNumCodes(int numCodes)
	{
		firePropertyChange(new PropertyChangeEvent(this, "numCodes", this.numCodes, numCodes));
		this.numCodes = numCodes;
	}

	/**
	 * @return the selectedDataGroup
	 */
	public /** selected data group to be requested next time */
	char getSelectedDataGroup()
	{
		return selectedDataGroup;
	}

	/**
	 * @param selectedDataGroup the selectedDataGroup to set
	 */
	public void setSelectedDataGroup(char selectedDataGroup)
	{
		this.selectedDataGroup = selectedDataGroup;
	}

	/**
	 * @return the currDataGroup
	 */
	public /** current data group which was requested */
	char getCurrDataGroup()
	{
		return currDataGroup;
	}

	/**
	 * Show all known group items
	 */
	void showAllGroupItems()
	{
		Iterator<Integer> itKeys = knownGrpItems.keySet().iterator();
		while (itKeys.hasNext())
		{
			showGroupItems(itKeys.next());
		}
	}

	/**
	 * show all data items of selected group
	 * * if there are other items shown, it does not hide them.
	 *
	 * @param groupNum
	 */
	void showGroupItems(int groupNum)
	{
		// get items of goup
		Vector<EcuDataItem> grpItms = knownGrpItems.get(groupNum);
		if (grpItms != null)
		{
			// loop through items in group
			Iterator<EcuDataItem> itItm = grpItms.iterator();
			while (itItm.hasNext())
			{
				EcuDataItem itm = itItm.next();
				PidPvs.put(itm.pv.toString(), itm.pv);
			}
		}
	}

	/**
	 * @param currDataGroup the currDataGroup to set
	 */
	private void setCurrDataGroup(char currDataGroup)
	{
		// if data group changes in single group mode ...
		if (service == SVC_READ_DATA_GRP
			&& currDataGroup != this.currDataGroup)
		{
			// ... clear PID-PVs to create new data list at reception
			PidPvs.clear();
			showGroupItems(currDataGroup);
		}
		this.currDataGroup = currDataGroup;
	}

	/**
	 * handle incoming protocol telegram
	 * default implementaion only checks telegram and notifies listeners with
	 * protocol payload
	 *
	 * @param buffer - telegram buffer
	 * @return number of listeners notified
	 */
	@Override
	public int handleTelegram(char[] buffer)
	{
		int cnt = 0;

		log.fine("RX:" + ProtUtils.hexDumpBuffer(buffer));

		// if telegram is OK
		if (checkTelegram(buffer))
		{
			// get command
			int cmd = getParamInt(FLD_ID_CMD, buffer);
			// update block counter
			blockCounter = (char) getParamInt(FLD_ID_BLKCNT, buffer).intValue();
			// get payload
			char[] payLoad = getPayLoad(buffer);
			switch (cmd)
			{
				case ID_GRPINFO_HEAD:
					// field number within group
					int fldId = 0;
					// clear list of current group data items
					// currGrpItems.clear();
					// loop through all data items within telegram
					for (int i = 0; i < payLoad.length; )
					{
						// get data id from group reading
						int dId = (int) payLoad[i];
						// get the corresponding data items for svc/pid
						Vector<EcuDataItem> pidItems = itms.getPidDataItems(ID_GRPINFO_DATA, dId);
						// if item was found ...
						if (pidItems != null && pidItems.size() > 0)
						{
							EcuDataItem currItm;
							// if item already exists
							if (currGrpItems.size() > fldId)
							{
								// re-use existing item
								currItm = currGrpItems.get(fldId);
							} else
							{
								// otherwise clone a new one
								currItm = (EcuDataItem) pidItems.get(0).clone();
								// set specific values for this item
								currItm.pid = (int) getCurrDataGroup();
								currItm.ofs += fldId;
								currItm.pv.put(EcuDataPv.FID_PID, Integer.valueOf(currItm.pid));
								currItm.pv.put(EcuDataPv.FID_OFS, Integer.valueOf(currItm.ofs));
								// ensure there are enough elements in list
								while (currGrpItems.size() <= fldId)
									currGrpItems.add(null);

								// add item to list of group data items
								currGrpItems.set(fldId, currItm);
							}
							// update metadata
							int numTblChrs = payLoad[i + 2];
							// if conversion is a VagConversion, we may adjust meta parameters
							if (currItm.cnv[0] instanceof VagConversion)
							{
								// set meta parameter for conversion
								((VagConversion) currItm.cnv[0]).setMetaNw(payLoad[i + 1]);
								// any following table data available?
								if (numTblChrs != 0)
								{
									// set table values for conversion
									((VagConversion) currItm.cnv[0]).setMetaTblValues(String.valueOf(payLoad, i + 3, numTblChrs).toCharArray());
								}
							}
							// increment byte index to next entry
							i += 3 + numTblChrs;
							// increment field number
							fldId++;
						}
					}
					showGroupItems(getCurrDataGroup());
					// now request again to get real data
					requestGroupData(getCurrDataGroup());
					break;

				case ID_GRPINFO_DATA:
					if (currGrpItems == null || currGrpItems.size() < BLK_NUM_ITEMS)
					{
						log.severe(String.format("Missing/Incomplete Metadata for GRP:%d", (int) getCurrDataGroup()));
					}
				{
					for (int i = 0; i < Math.min(payLoad.length, currGrpItems.size()); i++)
					{
						EcuDataItem currItm = currGrpItems.get(i);
						if (currItm != null)
						{
							// and update corresponding process var
							currItm.updatePvFomBuffer(payLoad);
						} else
						{
							log.severe(String.format("Data w/o meta GRP:%d, ITM:%d", (int) getCurrDataGroup(), i));
						}
					}
				}
				// if we do group reading and desired group has not changed ...
				if (service == SVC_READ_DATA_GRP && getCurrDataGroup() == getSelectedDataGroup())
				{
					// request same group again
					requestGroupData(getCurrDataGroup());
				} else
				{
					// otherwise close this group by  requesting ACK
					requestACK();
				}
				break;

				case ID_GRPREAD_DATA:
					// currGrpItems.clear();
					// loop through all 4 entries of group
					for (int i = 0; i < payLoad.length; i += 3)
					{
						// get data it from group reading
						int dId = (int) payLoad[i];
						// get the corresponding data items for svc/pid
						Vector<EcuDataItem> pidItems = itms.getPidDataItems(cmd, dId);
						// if item was found ...
						if (pidItems != null)
						{
							// loop through all data items for this dId
							Iterator<EcuDataItem> it = pidItems.iterator();
							while (it.hasNext())
							{
								EcuDataItem currItm = (EcuDataItem) it.next().clone();
								// set specific values for this item
								currItm.pid = (int) getCurrDataGroup();
								currItm.ofs += i / 3;
								currItm.pv.put(EcuDataPv.FID_PID, Integer.valueOf(currItm.pid));
								currItm.pv.put(EcuDataPv.FID_OFS, Integer.valueOf(currItm.ofs));
								// ensure there are enough elements in list
								while (currGrpItems.size() < (i / 3))
									currGrpItems.add(null);

								// add item to list of group data items
								currGrpItems.set(i / 3, currItm);

								// and update corresponding process var
								currItm.updatePvFomBuffer(Arrays.copyOfRange(payLoad, i + 1, i + 3));
							}
						} else
						{
							log.warning("Unknown data ID:" + dId);
						}
					}
					// if we do group reading and desired group has not changed ...
					if (service == SVC_READ_DATA_GRP && getCurrDataGroup() == getSelectedDataGroup())
					{
						// request same group again
						requestGroupData(getCurrDataGroup());
					} else
					{
						// otherwise close this group by  requesting ACK
						requestACK();
					}
					break;

				case CMD_ACK:
					switch (service)
					{
						case SVC_READ_DATA_ALL:
							// request next available data group
							requestGroupData(getNextDataGroup());
							break;

						case SVC_READ_DATA_GRP:
							// ensure new selection of group is updated
							setCurrDataGroup(getSelectedDataGroup());
							// request same group again
							requestGroupData(getCurrDataGroup());
							break;

						case SVC_READ_DFCS:
							// clear fault code list
							tCodes.clear();
							setNumCodes(0);
							// request failure codes
							requestDFCs();
							break;

						case SVC_CLEAR_DFCS:
							// Request clearing DFCs ...
							requestClearDFCs();
							// ... and continue with Reading DFCc
							setService(SVC_READ_DFCS);
							break;

						case SVC_SHUTDOWN:
							requestEndComm();
							// now set to Service NONE
							setService(SVC_FINISHED);
							break;

						case SVC_NONE:
						default:
							requestACK();
							break;
					}
					break;

				case ID_NODATA:
					log.info(String.format("NODATA: Group:%d -> remove", (int) getCurrDataGroup()));
					// since data is not available, remove group from map ...
					knownGrpItems.remove(Integer.valueOf(getCurrDataGroup()));
					// if we finished the initial turaround, save preset data
					if (currDataGroup == 0)
					{
						savePreset();
					}
					// request ACK
					requestACK();
					break;

				case ID_DFC_DATA:
					int nCodes = numCodes;
					// loop through all codes reported
					for (int i = 0; i < payLoad.length; i += 3)
					{
						// get code number and status
						int dfcNum = getParamInt(i, 2, payLoad);
						int dfcStat = getParamInt(i + 2, 1, payLoad);
						// enter code into code list for visualisation
						EcuCodeItem code = knownCodes.get(dfcNum);
						// if code is not known ...
						if (code == null)
						{
							// create new one and add it to list of known codes
							code = new EcuCodeItem(dfcNum, "Unknown Fault code");
						}
						code.put(EcuCodeItem.FID_STATUS, Integer.valueOf(dfcStat));
						tCodes.put(dfcNum, code);
						if (dfcNum != 0xFFFF)
						{
							nCodes++;
							// set the MIL status based on code status
							nCodes |= dfcStat & 0x80;
						}
					}
					// set number of codes
					setNumCodes(nCodes);
					// finish service since we received corresponding answer
					setService(SVC_NONE);
					// further DFCs may still come after ACK ...
					requestACK();
					break;

				case ID_ASCII_DATA:
					// save vehicle ID data
					EcuDataPv pv = new EcuDataPv();
					pv.put(EcuDataPv.FID_DESCRIPT, "ID");
					pv.put(EcuDataPv.FID_VALUE, String.valueOf(payLoad));
					VidPvs.put(VidPvs.size(), pv);
					// if this is the first VID dataset, try to load corresponding preset
					if (VidPvs.size() == 1) loadPreset();
					// send ACK
					requestACK();
					break;

				default:
					requestACK();
					break;

			}
			cnt++;
		}
		return (cnt);
	}

	/**
	 * Protocol simulation thread
	 * This may be started to use the program in demonstration mode w/o any
	 * adaptor/vehicle connected
	 */
	public Thread simulation = new Thread()
	{
		@Override
		@SuppressWarnings("fallthrough")
		public void run()
		{
			char i = 0;
			// handle ID_ASCII_DATA
			handleTelegram(createTelegram("1234567890".toCharArray(), Kw1281Prot.ID_ASCII_DATA, null));
			// handle ID_ASCII_DATA
			handleTelegram(createTelegram("SIMULATION".toCharArray(), Kw1281Prot.ID_ASCII_DATA, null));
			// handle ID_ASCII_DATA
			handleTelegram(createTelegram("TEST".toCharArray(), Kw1281Prot.ID_ASCII_DATA, null));
			setCurrDataGroup((char) 1);
			setSelectedDataGroup((char) 1);
			handleTelegram(createTelegram("".toCharArray(), CMD_ACK, null));
			while (true)
			{
				switch (service)
				{
					case SVC_CLEAR_DFCS:
					case SVC_READ_DFCS:
						// handleTelegram(createTelegram(new char[]{0x02,0x0D,0x9A,0x02,0x0E,0x1B}, ID_DFC_DATA, null));
						handleTelegram(createTelegram(new char[]{0x46, 0x3A, 0xA3, 0x02, 0x01, 0x9B, 0x02, 0x0D, 0x9A}, ID_DFC_DATA, null));
						break;
					case SVC_READ_DATA_ALL:
					case SVC_READ_DATA_GRP:
						if (service == SVC_READ_DATA_ALL || getSelectedDataGroup() != getCurrDataGroup())
						{
							handleTelegram(createTelegram("".toCharArray(), CMD_ACK, null));
							// handle ID_GRPINFO_HEAD
							handleTelegram(createTelegram(new char[]
								{
									0x80, 0x20, 0x00,
									0x8C, 0x30, 0x11, 0x00, 0x0C, 0x18, 0x24, 0x30, 0x3C, 0x48, 0x54, 0x60, 0x6C, 0x78, 0x84, 0x90, 0x9C, 0xA8, 0xAC, 0xB8,
									0x85, 0x05, 0x00,
									0x88, 0xFF, 0x00
								}, ID_GRPINFO_HEAD, null));
						}
						handleTelegram(createTelegram(new char[]{i, i, i, i++}, ID_GRPINFO_DATA, null));
						break;
				}
				try
				{
					Thread.sleep(50);
				} catch (Exception e)
				{/* do nothing */}
			}
		}
	};

	/**
	 * main routine for testing modules
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		final Kw1281Prot prt = new Kw1281Prot();

		if (args.length > 0)
		{
			// set default parameters
			final int adress = args.length > 1 ? Integer.parseInt(args[1], 16) : 0x01;
			final String device = args.length > 0 ? args[0] : "/dev/ttyUSB0";

			final KLHandler adapt = new KLHandler(device);
			// connect telegram layers
			prt.addTelegramWriter(adapt);
			adapt.setMessageHandler(prt);
			/** Handler for communication Timeout */
			adapt.addTimeoutListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					log.warning("CommTimeout -> re-initializing");
					adapt.init5Baud(adress);
				}
			});

			prt.setService(SVC_READ_DATA_ALL);
			// initialize communication
			if (adapt.init5Baud(adress) != 0)
			{
				// wait until we are shutting down
				while (adapt.getProtStat() != KLHandler.ProtStatus.OFFLINE)
				{
					try
					{
						Thread.sleep(500);
					} catch (Exception e)
					{
						log.log(Level.SEVERE, null, e);
					}
				}
			}
			adapt.close();
		} else
		{
			// otherwise run simulation
			prt.setService(SVC_READ_DATA_ALL);
			prt.simulation.run();
		}
	}
}
