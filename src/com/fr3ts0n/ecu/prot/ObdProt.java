/**
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.fr3ts0n.ecu.prot;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Vector;

import com.fr3ts0n.ecu.Conversion;
import com.fr3ts0n.ecu.Conversions;
import com.fr3ts0n.ecu.EcuConversions;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.ObdCodeItem;
import com.fr3ts0n.ecu.ObdCodeList;
import com.fr3ts0n.prot.ProtoHeader;
import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvList;


/**
 * OBD communication protocol layer
 *
 * The OBD protocol supports services which serve multiple PID's
 * A request of PID which is a multiple of 0x20 (0x00,0x20...0xE0) for each
 * service returns a bitmask of the next 32 PIDs which are suppoted by the vehicle
 * @author erwin
 */
public class ObdProt extends ProtoHeader
  implements TelegramListener, TelegramWriter
{
  public static final int OBD_SVC_NONE        = 0x00;
  public static final int OBD_SVC_DATA        = 0x01;
  public static final int OBD_SVC_FREEZEFRAME = 0x02;
  public static final int OBD_SVC_READ_CODES  = 0x03;
  public static final int OBD_SVC_CLEAR_CODES = 0x04;
  public static final int OBD_SVC_O2_RESULT   = 0x05;
  public static final int OBD_SVC_MON_RESULT  = 0x06;
  public static final int OBD_SVC_PENDINGCODES= 0x07;
  public static final int OBD_SVC_CTRL_MODE   = 0x08;
  public static final int OBD_SVC_VEH_INFO    = 0x09;
  public static final int OBD_SVC_PERMACODES  = 0x0A;

  // current supported PID
  static int currSupportedPid = 0;

  // content of last sent message
  protected static String lastTxMsg = "";
  // content of last received message
  protected static String lastRxMsg = "";
  /**
   * Holds value of property service.
   */
  protected int service = OBD_SVC_NONE;

  // List of PIDs supported by the vehicle
  static Vector<Integer> pidSupported = new Vector<Integer>();

  public static final int ID_OBD_SVC          = 0;
  public static final int ID_OBD_PID          = 1;
  public static final int ID_OBD_FRAMEID      = 2;

  /**
   * List of telegram parameters in order of appearance
   */
  static final int SVC_PARAMETERS[][] =
    /*  START,  LEN,     PARAM-TYPE     // REMARKS */
    /* ------------------------------------------- */
  { {     0,    2,     PT_HEX},     // ID_OBD_SVC
  };

  /**
   * List of telegram parameters in order of appearance
   */
  static final int OBD_PARAMETERS[][] =
    /*  START,  LEN,     PARAM-TYPE     // REMARKS */
    /* ------------------------------------------- */
  { {     0,    2,     PT_HEX},     // ID_OBD_SVC
    {     2,    2,     PT_HEX},     // ID_OBD_PID
  };

  /**
   * List of telegram parameters in order of appearance
   */
  static final int FRZFRM_PARAMETERS[][] =
    /*  START,  LEN,     PARAM-TYPE     // REMARKS */
    /* ------------------------------------------- */
  { {     0,    2,     PT_HEX},     // ID_OBD_SVC
    {     2,    2,     PT_HEX},     // ID_OBD_PID
    {     2,    2,     PT_HEX},     // ID_OBD_FRAMEID
  };

  public static final int ID_NUM_CODES = 0;
  public static final int ID_MSK_CODES = 1;
  /**
   * List of telegram parameters in order of appearance
   */
  static final int NUMCODE_PARAMETERS[][] =
    /*  START,  LEN,     PARAM-TYPE     // REMARKS */
    /* ------------------------------------------- */
  { {     4,    2,     PT_HEX},     // ID_NUM_CODES
    {     6,    6,     PT_HEX},     // ID_MSK_CODES
  };

   static final String[] OBD_DESCRIPTORS =
  {
    "OBD Service",
    "OBD PID",
  };

  /** new style data items */
  static final EcuDataItems dataItems = new EcuDataItems();

  /** OBD data items */
  public static PvList PidPvs  = new PvList();
  /** OBD vehicle identification items */
  public static PvList VidPvs  = new PvList();
  /** current fault codes */
  public static PvList tCodes  = new PvList();
  /** list of known fault codes */
  public static ObdCodeList knownCodes = Conversions.obdCodeList;
  /** queue of ELM commands to be sent */
  static Vector<String> cmdQueue      = new Vector<String>();


  /** Creates a new instance of ObdProt */
  public ObdProt()
  {
    paddingChr = '0';
    // prepare PID PV list
    PidPvs.put(0, new EcuDataPv());
    VidPvs.put(0, new EcuDataPv());
    tCodes.put(0, new ObdCodeItem(0,"No trouble codes set"));
  }

  /**
   * list of parameters for specific protocol
   * @return complete set of protocol parameters
   */
  public int[][] getTelegramParams()
  {
    return(getTelegramParams(service));
  }

  /**
   * list of parameters for specific protocol
   * @param service Service which this header is requested for
   * @return complete set of protocol parameters
   */
  public int[][] getTelegramParams(int service)
  {
    int fldMap[][];
    switch(service)
    {
      case OBD_SVC_FREEZEFRAME:
        fldMap = FRZFRM_PARAMETERS;
        break;

      case OBD_SVC_READ_CODES:
      case OBD_SVC_PENDINGCODES:
      case OBD_SVC_PERMACODES:
      case OBD_SVC_CLEAR_CODES:
        fldMap = SVC_PARAMETERS;
        break;

      default:
        fldMap = OBD_PARAMETERS;
    }
    return(fldMap);
  }


  /**
   * return message footer for protocol payload
   * @param buffer buffer of payload data
   * @return buffer of message footer
   */
  public char[] getFooter(char[] buffer)
  {
    return(emptyBuffer);
  }

  /**
   * create a new telegram header for selected payload data buffer
   * inclunding setting all ID's, sizes and validity issues
   * @param buffer buffer of payload data
   * @return buffer of new telegram header
   */
  protected char[] getNewHeader(char[] buffer)
  {
    return(getNewHeader(buffer,OBD_SVC_DATA,new Integer(0)));
  }

  /**
   * create a new telegram header inclunding setting all ID's, sizes and
   * validity issues
   * @param buffer buffer of payload data
   * @param type type of telegram content
   * @param id identifier for telegram (may be null)
   * @return buffer of new telegram header
   */
  @SuppressWarnings("fallthrough")
  protected char[] getNewHeader(char[] buffer, int type, Object id)
  {
    int[][] fldMap = getTelegramParams(type);
    char[] header = createEmptyBuffer(fldMap,'0');
    setParamValue(ID_OBD_SVC,fldMap,header,new Integer(type));
    switch(type)
    {
      // these commands do not require parametrs
      case OBD_SVC_READ_CODES:
      case OBD_SVC_PENDINGCODES:
      case OBD_SVC_PERMACODES:
      case OBD_SVC_CLEAR_CODES:
        break;

      // freezeframes require additional frame id
      case OBD_SVC_FREEZEFRAME:
        setParamValue(ID_OBD_FRAMEID,fldMap,header,0);
        // NO break here

      // all other commands require PID to be set
      default:
        setParamValue(ID_OBD_PID,fldMap,header,id);
    }
    return(header);
  }

  /**
   * list of parameter descriptions for specific protocol
   * @return complete set of protocol parameter description strings
   */
  protected String[] getParamDescriptors()
  {
    return(OBD_DESCRIPTORS);
  }

  /**
   * prepare process variables for each PID
   * @param pvList list of process vars
   */
  public void preparePidPvs(PvList pvList)
  {
    PvList newList = new PvList();
    for(Integer currPid : pidSupported)
    {
      Vector<EcuDataItem> items = dataItems.getPidDataItems(service,currPid);
      // if no items defined, create dummy item
	    if(items == null)
      {
	      log.warn(String.format("unknown PID %02X",currPid));

	      // create new dummy item / OneToOne conversion
	      Conversion[] dummyCnvs = { EcuConversions.dfltCnv, EcuConversions.dfltCnv };
	      EcuDataItem newItem =
		      new EcuDataItem( currPid, 0, 2,
			      dummyCnvs,
			      0, String.format("PID %02X",currPid)
		      );
	      dataItems.appendItemToService(service, newItem);

	      // re-load data items for this PID
	      items = dataItems.getPidDataItems(service,currPid);
      }
	    // loop through all items found ...
	    for(EcuDataItem pidPv : items)
	    {
		    if(pidPv !=null)
		    {
			    newList.put(pidPv.toString(), pidPv.pv);
		    }
	    }
    }
    pvList.putAll(newList, PvChangeEvent.PV_ADDED, false);
  }

  /**
   * mark all PIDs supported by the vehicle
   * @param start Start PID (multiple of 0x20) to process bitmask for
   * @param bitmask 32-Bit bitmask which indicates support for the next 32 PIDs
   */
  protected void markSupportedPids(int start, long bitmask, PvList pvList)
  {
    currSupportedPid = 0;
    // loop through bits and mark corresponding PIDs as supported
    for(int i=0; i<0x1F; i++)
    {
      if((bitmask & (0x80000000L >> i))!=0)
      {
        pidSupported.add(i+start+1);
      }
    }
    log.debug(Long.toHexString(bitmask).toUpperCase()+"("+Long.toHexString(start)+"):"+pidSupported);
    // if next block may be requested
    if((bitmask & 1) != 0)
      // request next block
      cmdQueue.add(String.format("%02X%02X", this.service,start+0x20));
    else
      // setup PID PVs
      preparePidPvs(pvList);
  }

  /** Holds value of property numCodes. */
  private int numCodes;

  /** fixed PIDs to limit PID loop to single access */
  private static Vector<Integer> fixedPids = new Vector<Integer>();

	/**
	 * Set fixed PID for faster data update
	 * @param pidCodes the fixedPid to set
	 */
	public static synchronized void setFixedPid(int[] pidCodes)
	{
		int curr;
		currSupportedPid = 0;
    for (Integer aPidSupported : pidSupported)
    {
      curr = aPidSupported;
      if (Arrays.binarySearch(pidCodes, curr) >= 0)
      {
        fixedPids.add(curr);
      }
    }
	}

	public static synchronized void resetFixedPid()
	{
		fixedPids.clear();
	}

	/**
   * get the next available supported PID
   * @return next available supported PID
   */
  protected synchronized Integer getNextSupportedPid()
  {
    Vector<Integer> pidsToCheck = (fixedPids.size() > 0) ? fixedPids : pidSupported;
    Integer result = null;

    if(pidsToCheck.size() > 0)
    {
      result = pidsToCheck.get(currSupportedPid);
      currSupportedPid++;
      currSupportedPid %= (pidsToCheck.size());
    }
    return(result);
  }

/**
 * handle OBD response telegram
 * @param buffer - telegram buffer
 * @return number of listeners notified
 */
  @Override
  @SuppressWarnings("fallthrough")
  public int handleTelegram(char[] buffer)
  {
    int result = 0;
    int msgSvc = 0;
    int oldObdSvc = 0;
    int msgPid;

    if(checkTelegram(buffer))
    {
      try
      {
        msgSvc = (Integer) getParamValue(ID_OBD_SVC, buffer) & ~0x40;
        // remember last set OBD service
        oldObdSvc = service;
        // set OBD service to service of RX telegram
        service = msgSvc;
        // check service of message
        switch(msgSvc)
        {
          // OBD Data frame
          case OBD_SVC_FREEZEFRAME:
          case OBD_SVC_DATA:
            msgPid = (Integer) getParamValue(ID_OBD_PID, buffer);
            switch(msgPid)
            {
              case 0x00:
              case 0x20:
              case 0x40:
              case 0x60:
              case 0x80:
              case 0xA0:
              case 0xC0:
              case 0xE0:
	              long msgPayload = Long.valueOf(new String(getPayLoad(buffer)), 16);
                markSupportedPids(msgPid, msgPayload, PidPvs);
                break;

              // OBD number of fault codes
              case 1:
                msgPayload = ((Integer)getParamValue(ID_NUM_CODES,NUMCODE_PARAMETERS,buffer)).longValue();
                setNumCodes(new Long(msgPayload).intValue());
                // no break here ...
              default:
                dataItems.updateDataItems(msgSvc,msgPid, getPayLoad(buffer));
                break;
            }
            break;

          // get vehicle information (mode 9)
          case OBD_SVC_VEH_INFO:
            msgPid = (Integer) getParamValue(ID_OBD_PID, buffer);
            switch(msgPid)
            {
              case 0x00:
              case 0x20:
              case 0x40:
              case 0x60:
              case 0x80:
              case 0xA0:
              case 0xC0:
              case 0xE0:
	              long msgPayload = Long.valueOf(new String(getPayLoad(buffer)), 16);
                markSupportedPids(msgPid, msgPayload, VidPvs);
                break;

              default:
                dataItems.updateDataItems(msgSvc,msgPid, getPayLoad(buffer));
                break;
            }
            break;


            // fault code response
          case OBD_SVC_READ_CODES:
          case OBD_SVC_PENDINGCODES:
          case OBD_SVC_PERMACODES:
            int currCode;
            Integer key;
            ObdCodeItem code;

            int nCodes = Integer.valueOf(new String(buffer, 2, 2),16);
            setNumCodes(nCodes);

            // read in all trouble codes
            for(int i=4; i < buffer.length; i+=4)
            {
              key = Integer.valueOf(new String(buffer, i, 4),16);
              currCode = key.intValue();
              if(currCode != 0)
              {
                if((code = (ObdCodeItem)knownCodes.get(key))!=null)
                {
                  tCodes.put(key,code);
                }
                else
                {
                  tCodes.put(key,new ObdCodeItem(key.intValue(),"Customer specific trouble code. See manual ..."));
                }
                // only increment number of codes if it was not set already
                nCodes++;
              }
            }
            if(nCodes == 0)
            {
              tCodes.put(0,new ObdCodeItem(0,"No trouble codes set"));
            }
            break;

            // clear code response
          case OBD_SVC_CLEAR_CODES:
            break;

          default:
            log.warn("Service not (yet) supported: "+msgSvc);
        }
      }
      catch(NumberFormatException e)
      {
        log.warn("'"+buffer.toString()+"':"+e.getMessage());
      }
    }
    // restore last OBD service
    service = oldObdSvc;
    return(result);
  }

  /**
   * Notify all telegram Writers about new telegram
   * @param buffer - telegram buffer
   */
    @Override
  public void sendTelegram(char[] buffer)
  {
    // remember last sent message
    lastTxMsg = new String(buffer);
    super.sendTelegram(buffer);
  }

  /**
   * Getter for property numCodes.
   * @return Value of property numCodes.
   */
  public int getNumCodes()
  {
    return this.numCodes;
  }

  /**
   * Setter for property numCodes.
   * @param numCodes New value of property numCodes.
   */
  public void setNumCodes(int numCodes)
  {
    int old = this.numCodes;
    this.numCodes = numCodes;
    firePropertyChange(new PropertyChangeEvent(this,"numCodes",new Integer(old),new Integer(numCodes)));
  }

  /**
   * Getter for property service.
   * @return Value of property service.
   */
  public int getService()
  {
    return this.service;
  }

  /**
   * Setter for property service.
   *  This includes initialisation of the requested service to the vehicle
   * @param obdService New service to be requested.
   */
  public void setService(int obdService)
  {
    this.service = obdService;
    switch(obdService)
    {
      case OBD_SVC_NONE:
        // sendCommand(CMD_RESET,0);
        break;

      case OBD_SVC_DATA:
        // read data items
        // Clear data items
        pidSupported.clear();
        PidPvs.clear();
        // request for PID's supported
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_FREEZEFRAME:
        // read freeze frame data items
        // Clear data items
        pidSupported.clear();
        PidPvs.clear();
        // request for PID's supported
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_READ_CODES:
        // read trouble codes
        tCodes.clear();
        numCodes = 0;
        cmdQueue.add("0101");
        // read PID number of codes ...
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_CLEAR_CODES:
        // clear trouble codes
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_PENDINGCODES:
        // read pending trouble codes
        tCodes.clear();
        numCodes = 0;
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_PERMACODES:
        // read pending trouble codes
        tCodes.clear();
        numCodes = 0;
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_VEH_INFO:
        // Clear data items
        pidSupported.clear();
        VidPvs.clear();
        // read vehicle information
        writeTelegram(emptyBuffer,obdService,0);
        break;

      case OBD_SVC_CTRL_MODE:
      case OBD_SVC_O2_RESULT:
      case OBD_SVC_MON_RESULT:
      default:
        log.warn("Service not supported: "+obdService);
    }
  }
}
