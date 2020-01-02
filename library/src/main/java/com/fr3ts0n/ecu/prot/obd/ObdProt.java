/**
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.fr3ts0n.ecu.prot.obd;

import com.fr3ts0n.ecu.Conversion;
import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.ecu.EcuCodeList;
import com.fr3ts0n.ecu.EcuConversions;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.ObdCodeItem;
import com.fr3ts0n.prot.ProtoHeader;
import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvList;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

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
    public static final int OBD_SVC_NONE = 0x00;
    public static final int OBD_SVC_DATA = 0x01;
    public static final int OBD_SVC_FREEZEFRAME = 0x02;
    public static final int OBD_SVC_READ_CODES = 0x03;
    public static final int OBD_SVC_CLEAR_CODES = 0x04;
    private static final int OBD_SVC_O2_RESULT = 0x05;
    private static final int OBD_SVC_MON_RESULT = 0x06;
    public static final int OBD_SVC_PENDINGCODES = 0x07;
    private static final int OBD_SVC_CTRL_MODE = 0x08;
    public static final int OBD_SVC_VEH_INFO = 0x09;
    public static final int OBD_SVC_PERMACODES = 0x0A;

    /** negative response ID */
    private static final int OBD_ID_NRC = 0x7F;

    /** perform immediate reset on NRC reception? */
    private boolean isResetOnNrc()
    {
        return resetOnNrc;
    }

    /** Set protocol parameter
     * @param resetOnNrc  perform immediate reset on NRC reception?
     */
    public void setResetOnNrc(boolean resetOnNrc)
    {
        log.info(String.format("Reset on NRC = %b", resetOnNrc));
        this.resetOnNrc = resetOnNrc;
    }

    /** negative response codes */
    public enum NRC
    {
        GR(0x10, "General reject",DISP.ERROR, REACT.RESET),
        SNS(0x11, "Service 0x%02X not supported", DISP.ERROR, REACT.CANCEL),
        SFNS(0x12, "Sub-Function not supported (SVC:0x%02X)", DISP.NOTIFY, REACT.SKIP),
        IMLOIF(0x13, "Incorrect message length or invalid format", DISP.NOTIFY, REACT.SKIP),
        RTL(0x14, "Response too long", DISP.NOTIFY, REACT.SKIP),
        BRR(0x21, "Busy repeat request", DISP.NOTIFY, REACT.REPEAT),
        CNC(0x22, "Conditions not correct", DISP.ERROR, REACT.SKIP),
        RSE(0x24, "Request sequence error", DISP.ERROR, REACT.CANCEL),
        NRFSC(0x25, "No response from sub-net component", DISP.ERROR, REACT.RESET),
        FPEORA(0x26, "Failure prevents execution of requested action", DISP.ERROR, REACT.CANCEL),
        ROOR(0x31, "Request out of range (SVC:0x%02X)", DISP.NOTIFY, REACT.SKIP),
        SAD(0x33, "Security access denied", DISP.ERROR, REACT.CANCEL),
        IK(0x35, "Invalid key", DISP.ERROR, REACT.RESET),
        ENOA(0x36, "Exceeded number of attempts", DISP.ERROR, REACT.RESET),
        RTDNE(0x37, "Required time delay not expired", DISP.NOTIFY, REACT.REPEAT),
        UDNA(0x70, "Upload/Download not accepted", DISP.ERROR, REACT.CANCEL),
        TDS(0x71, "Transfer data suspended", DISP.NOTIFY, REACT.REPEAT),
        GPF(0x72, "General programming failure", DISP.ERROR, REACT.CANCEL),
        WBSC(0x73, "Wrong Block Sequence Counter", DISP.ERROR, REACT.CANCEL),
        RCRRP(0x78, "Request correctly received  but response is pending", DISP.NOTIFY, REACT.IGNORE),
        SFNSIAS(0x7E, "Sub-Function not supported in active session (SVC:0x%02X)", DISP.NOTIFY, REACT.SKIP),
        SNSIAS(0x7F, "Service 0x%02X not supported in active session", DISP.ERROR, REACT.CANCEL);

        /** NRC display classifiers */
        public enum DISP
        {
            HIDE,
            NOTIFY,
            WARN,
            ERROR
        }

        /** NRC reaction classifiers */
        public enum REACT
        {
            IGNORE,
            SKIP,
            REPEAT,
            CANCEL,
            RESET
        }

        public final int code;
        public final String description;
        public final DISP disp;
        public final REACT react;

        NRC(int _code, String _description, DISP _DISPClass, REACT _REACTClass)
        {
            code = _code;
            description = _description;
            disp = _DISPClass;
            react = _REACTClass;
        }

        /**
         * Get NRC with specified ID (NRC-code)
         * @param id ID (NRC-code) to search
         * @return specified NRC, or null if not found
         */
        static NRC get(int id)
        {
            NRC result = null;
            for (NRC nrc : values())
            {
                if (nrc.code == id)
                {
                    result = nrc;
                    break;
                }
            }
            return result;
        }

        /** return String representative */
        public String toString(int service)
        {
            return String.format("(NRC:0x%02X) %s", code, String.format(description, service));
        }
    }

    /** property name "number of codes" */
    public static final String PROP_NUM_CODES = "numCodes";
    public static final String PROP_NRC = "NRC";

    // current supported PID
    private static int currSupportedPid = 0;
    static boolean pidsWrapped = false;

    /** content of last sent message */
    static String lastTxMsg = "";
    /** content of last received message */
    static String lastRxMsg = "";
    /** Holds value of property service. */
    int service = OBD_SVC_NONE;
    /** service of last incoming message */
    private int msgService = OBD_SVC_NONE;

    /** List of PIDs supported by the vehicle */
    private static final Vector<Integer> pidSupported = new Vector<Integer>();

    /** positive response fields */
    private static final int ID_OBD_SVC = 0;
    private static final int ID_OBD_PID = 1;
    private static final int ID_OBD_FRAMEID = 2;

    /** negative response fields */
    public static final int ID_NR_ID = 0;
    private static final int ID_NR_SVC = 1;
    private static final int ID_NR_CODE = 2;

    /**
     * Negative response parameters
     * List of telegram parameters in order of appearance
     */
    private static final int[][] NR_PARAMETERS =
            /*  START,  LEN,     PARAM-TYPE     // REMARKS */
            /* ------------------------------------------- */
            {{0, 2, PT_HEX},     // ID_NR_ID
                    {2, 2, PT_HEX},     // ID_NR_SVC
                    {4, 2, PT_HEX},     // ID_NR_CODE
            };

    /**
     * List of telegram parameters in order of appearance
     */
    private static final int[][] SVC_PARAMETERS =
            /*  START,  LEN,     PARAM-TYPE     // REMARKS */
            /* ------------------------------------------- */
            {{0, 2, PT_HEX},     // ID_OBD_SVC
            };

    /**
     * List of telegram parameters in order of appearance
     */
    private static final int[][] OBD_PARAMETERS =
            /*  START,  LEN,     PARAM-TYPE     // REMARKS */
            /* ------------------------------------------- */
            {{0, 2, PT_HEX},     // ID_OBD_SVC
                    {2, 2, PT_HEX},     // ID_OBD_PID
            };

    /**
     * List of telegram parameters in order of appearance
     */
    private static final int[][] FRZFRM_PARAMETERS =
            /*  START,  LEN,     PARAM-TYPE     // REMARKS */
            /* ------------------------------------------- */
            {{0, 2, PT_HEX},     // ID_OBD_SVC
                    {2, 2, PT_HEX},     // ID_OBD_PID
                    {2, 2, PT_HEX},     // ID_OBD_FRAMEID
            };

    private static final int ID_NUM_CODES = 0;
    public static final int ID_MSK_CODES = 1;
    /**
     * List of telegram parameters in order of appearance
     */
    private static final int[][] NUMCODE_PARAMETERS =
            /*  START,  LEN,     PARAM-TYPE     // REMARKS */
            /* ------------------------------------------- */
            {{4, 2, PT_HEX},     // ID_NUM_CODES
                    {6, 6, PT_HEX},     // ID_MSK_CODES
            };

    private static final String[] OBD_DESCRIPTORS =
            {
                    "OBD Service",
                    "OBD PID",
            };

    /** new style data items */
    public static final EcuDataItems dataItems = new EcuDataItems();

    /** OBD data items */
    public static PvList PidPvs = new PvList();
    /** OBD vehicle identification items */
    public static PvList VidPvs = new PvList();
    /** current fault codes */
    public static PvList tCodes = new PvList();
    /** list of known fault codes */
    private static final EcuCodeList knownCodes = EcuConversions.codeList;
    /** queue of ELM commands to be sent */
    static final Vector<String> cmdQueue = new Vector<String>();
    /** freeze frame ID to request */
    private int freezeFrame_Id = 0;
    /** perform reset on NRC reception */
    private boolean resetOnNrc = false;

    /** Creates a new instance of ObdProt */
    ObdProt()
    {
        paddingChr = '0';
        // prepare PID PV list
        PidPvs.put(0, new EcuDataPv());
        VidPvs.put(0, new EcuDataPv());
        tCodes.put(0, new ObdCodeItem(0, "No trouble codes set"));
    }

    /**
     * set Freeze frame id to be requested
     * @param freezeFrame_Id ID of freeze frame to be requested
     */
    public void setFreezeFrame_Id(int freezeFrame_Id)
    {
        log.info(String.format("FreezeFrame ID: %d", freezeFrame_Id));
        this.freezeFrame_Id = freezeFrame_Id;
        setService(OBD_SVC_FREEZEFRAME, true);
    }

    /**
     * list of parameters for specific protocol
     * @return complete set of protocol parameters
     */
    public int[][] getTelegramParams()
    {
        return (getTelegramParams(msgService));
    }

    /**
     * list of parameters for specific protocol
     * @param service Service which this header is requested for
     * @return complete set of protocol parameters
     */
    private int[][] getTelegramParams(int service)
    {
        int fldMap[][];
        switch (service)
        {
            // negative response
            case OBD_ID_NRC:
                fldMap = NR_PARAMETERS;
                break;

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
        return (fldMap);
    }


    /**
     * return message footer for protocol payload
     * @return buffer of message footer
     */
    public char[] getFooter()
    {
        return (emptyBuffer);
    }

    /**
     * create a new telegram header for selected payload data buffer
     * inclunding setting all ID's, sizes and validity issues
     * @param buffer buffer of payload data
     * @return buffer of new telegram header
     */
    protected char[] getNewHeader(char[] buffer)
    {
        return (getNewHeader(buffer, OBD_SVC_DATA, Integer.valueOf(0)));
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
        char[] header = createEmptyBuffer(fldMap, '0');
        setParamValue(ID_OBD_SVC, fldMap, header, Integer.valueOf(type));
        switch (type)
        {
            // these commands do not require parametrs
            case OBD_SVC_READ_CODES:
            case OBD_SVC_PENDINGCODES:
            case OBD_SVC_PERMACODES:
            case OBD_SVC_CLEAR_CODES:
                break;

            // freezeframes require additional frame id
            case OBD_SVC_FREEZEFRAME:
                setParamValue(ID_OBD_FRAMEID, fldMap, header, freezeFrame_Id);
                // NO break here

                // all other commands require PID to be set
            default:
                setParamValue(ID_OBD_PID, fldMap, header, id);
        }
        return (header);
    }

    /**
     * list of parameter descriptions for specific protocol
     * @return complete set of protocol parameter description strings
     */
    protected String[] getParamDescriptors()
    {
        return (OBD_DESCRIPTORS);
    }

    /**
     * prepare process variables for each PID
     * @param pvList list of process vars
     */
    private void preparePidPvs(int obdService, PvList pvList)
    {
        // reset fixed PIDs
        resetFixedPid();

        HashMap<String, EcuDataPv> newList = new HashMap<String, EcuDataPv>();
        for (Integer currPid : pidSupported)
        {
            Vector<EcuDataItem> items = dataItems.getPidDataItems(obdService, currPid);
            // if no items defined, create dummy item
            if (items == null)
            {
                log.warning(String.format("unknown PID %02X", currPid));

                // create new dummy item / OneToOne conversion
                Conversion[] dummyCnvs = {EcuConversions.dfltCnv, EcuConversions.dfltCnv};
                EcuDataItem newItem = new EcuDataItem(currPid, 0, 0, 0, 32, 0xFFFFFFFF, dummyCnvs,
                        "%#08x", null, null,
                        String.format("PID %02X", currPid),
                        String.format("PID_%02X", currPid)
                );
                dataItems.appendItemToService(obdService, newItem);

                // re-load data items for this PID
                items = dataItems.getPidDataItems(obdService, currPid);
            }
            // loop through all items found ...
            for (EcuDataItem pidPv : items)
            {
                if (pidPv != null)
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
    private synchronized void markSupportedPids(int obdService, int start, long bitmask,
                                                PvList pvList)
    {
        currSupportedPid = 0;

        // loop through bits and mark corresponding PIDs as supported
        for (int i = 0; i < 0x1F; i++)
        {
            if ((bitmask & (0x80000000L >> i)) != 0)
            {
                pidSupported.add(i + start + 1);
            }
        }
        log.fine(Long.toHexString(bitmask).toUpperCase() + "(" + Long.toHexString(
                start) + "):" + pidSupported);
        // if next block may be requested
        if ((bitmask & 1) != 0)
            // request next block
            cmdQueue.add(String.format("%02X%02X", obdService, start + 0x20));
        else
            // setup PID PVs
            preparePidPvs(obdService, pvList);
    }

    /** Holds value of property numCodes. */
    private int numCodes;

    /** fixed PIDs to limit PID loop to single access */
    private static final Vector<Integer> fixedPids = new Vector<Integer>();

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
    synchronized Integer getNextSupportedPid()
    {
        Vector<Integer> pidsToCheck = (fixedPids.size() > 0) ? fixedPids : pidSupported;
        Integer result = 0;

        if (pidsToCheck.size() > 0)
        {
            result = pidsToCheck.get(currSupportedPid);
            currSupportedPid++;
            currSupportedPid %= (pidsToCheck.size());
            pidsWrapped = (currSupportedPid == 0);
        }
        return (result);
    }

    /**
     * handle OBD response telegram
     * @param buffer - telegram buffer
     * @return number of listeners notified
     */
    @Override
    @SuppressWarnings("fallthrough")
    public synchronized int handleTelegram(char[] buffer)
    {
        int result = 0;
        int msgPid;

        if (checkTelegram(buffer))
        {
            try
            {
                msgService = (Integer) getParamValue(ID_OBD_SVC, buffer);
                // check for negative result
                if (msgService == OBD_ID_NRC)
                {
                    // get NR service
                    int svc = (Integer) getParamValue(ID_NR_SVC, buffer);
                    // get NRC code
                    int nrcCode = (Integer) getParamValue(ID_NR_CODE, buffer);
                    // get NRC object
                    NRC nrc = NRC.get(nrcCode);
                    // create NRC error message
                    String error = nrc.toString(svc);
                    // log error
                    log.severe(error);
                    // notify change listeners
                    firePropertyChange(new PropertyChangeEvent(this, PROP_NRC, nrc, error));
                    // handle NRC reaction
                    switch(nrc.react)
                    {
                        case RESET:
                            if (isResetOnNrc())
                            {
                                // perform immediate reset because NRC reception
                                reset();
                            } else
                            {
                                // otherwise just switch off any active service
                                setService(OBD_SVC_NONE, true);
                            }
                            break;

                        case CANCEL:
                            // switch off any active service
                            setService(OBD_SVC_NONE, true);
                            break;

                        case REPEAT:
                            // Repeat last TX message
                            sendTelegram(lastTxMsg.toCharArray());
                            break;

                        case SKIP:
                        case IGNORE:
                        default:
                            // Intentionally do noting
                    }
                    // handling finished
                    return result;
                }

                // positive response -> mask service ID
                msgService &= ~0x40;
                // check service of message
                switch (msgService)
                {
                    // OBD Data frame
                    case OBD_SVC_FREEZEFRAME:
                    case OBD_SVC_DATA:
                        msgPid = (Integer) getParamValue(ID_OBD_PID, buffer);
                        switch (msgPid)
                        {
                            case 0x00:
                            case 0x20:
                            case 0x40:
                            case 0x60:
                            case 0x80:
                            case 0xA0:
                            case 0xC0:
                            case 0xE0:
                                long msgPayload = Long.valueOf(getPayLoadString(buffer), 16);
                                markSupportedPids(msgService, msgPid, msgPayload, PidPvs);
                                break;

                            // OBD number of fault codes
                            case 1:
                                msgPayload = ((Integer) getParamValue(ID_NUM_CODES,
                                        NUMCODE_PARAMETERS,
                                        buffer)).longValue();
                                setNumCodes(Long.valueOf(msgPayload).intValue());
                                // no break here ...
                            default:
                                dataItems.updateDataItems(msgService,
                                        msgPid,
                                        hexToBytes(String.valueOf(
                                                getPayLoad(buffer))));
                                break;
                        }
                        break;

                    // get vehicle information (mode 9)
                    case OBD_SVC_VEH_INFO:
                        msgPid = (Integer) getParamValue(ID_OBD_PID, buffer);
                        switch (msgPid)
                        {
                            case 0x00:
                            case 0x20:
                            case 0x40:
                            case 0x60:
                            case 0x80:
                            case 0xA0:
                            case 0xC0:
                            case 0xE0:
                                long msgPayload = Long.valueOf(getPayLoadString(buffer), 16);
                                markSupportedPids(msgService, msgPid, msgPayload, VidPvs);
                                break;

                            default:
                                dataItems.updateDataItems(msgService,
                                        msgPid,
                                        hexToBytes(String.valueOf(
                                                getPayLoad(buffer))));
                                break;
                        }
                        break;


                    // fault code response
                    case OBD_SVC_READ_CODES:
                    case OBD_SVC_PENDINGCODES:
                    case OBD_SVC_PERMACODES:
                        int currCode;
                        Integer key;
                        EcuCodeItem code;
                        int nCodes = 0;
                        // default DTC data to start at offset 2 (Byte 1)
                        int DTCOffs = 2;

                        // If message contains optional number of codes (1 Byte) then set it ...
                        boolean hasNumCodes = ((buffer.length % 4) == 0);
                        if (hasNumCodes)
                        {
                            nCodes = Integer.valueOf(new String(buffer, 2, 2), 16);
                            setNumCodes(nCodes);
                            // DTC data starts at offset 4 (byte 2)
                            DTCOffs = 4;
                        }

                        // read in all trouble codes
                        for (int i = DTCOffs; i < buffer.length; i += 4)
                        {
                            key = Integer.valueOf(new String(buffer, i, 4), 16);
                            currCode = key.intValue();
                            if (currCode != 0)
                            {
                                if ((code = knownCodes.get(key)) == null)
                                {
                                    code = new ObdCodeItem(key.intValue(),
                                            Messages.getString(
                                                    "customer.specific.trouble.code.see.manual"));
                                }
                                log.fine(String.format("+DFC: %04x: %s", key, code.toString()));
                                tCodes.put(key, code);
                                // if number of codes hasn't been delivered yet ...
                                if(!hasNumCodes)
                                {
                                    // increment number of detected codes
                                    nCodes++;
                                }
                            }
                        }
                        if (nCodes == 0)
                        {
                            tCodes.put(0, new ObdCodeItem(0, Messages.getString(
                                    "no.trouble.codes.set")));
                        }
                        break;

                    // clear code response
                    case OBD_SVC_CLEAR_CODES:
                        break;

                    default:
                        log.warning("Service not (yet) supported: " + msgService);
                }
            } catch (Exception e)
            {
                log.warning("'" + Arrays.toString(buffer) + "':" + e.getMessage());
            }
        }
        return (result);
    }

    private String getPayLoadString(char[] buffer) {
        // Getting only first 8 chars because some OBD simulators sends
        // very long string such "BE1B30130088180010BC48" which can not be
        // converted to Long, somehow this way fixes it, related issue:
        // https://github.com/fr3ts0n/AndrOBD/issues/90
        String payLoadString = new String(getPayLoad(buffer));
        payLoadString = payLoadString.length() > 8 ?
                payLoadString.substring(0, 8) : payLoadString;
        return payLoadString;
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
    private void setNumCodes(int numCodes)
    {
        int old = this.numCodes;
        this.numCodes = numCodes;
        firePropertyChange(new PropertyChangeEvent(this,
                PROP_NUM_CODES,
                Integer.valueOf(old),
                Integer.valueOf(numCodes)));
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
     * reset all protocol settings
     */
    public void reset()
    {
        // switch off any active service
        setService(OBD_SVC_NONE, true);
        // clear command queue
        cmdQueue.clear();
        // clear supported PIDs
        pidSupported.clear();
        // reset fixed PIDs
        resetFixedPid();
        // Clear data items
        PidPvs.clear();
        tCodes.clear();
        VidPvs.clear();
    }

    /**
     * clear data lists for selected service
     * @param obdService OBD service to clear lists for
     */
    private void clearDataLists(int obdService)
    {
        // clean up data lists
        switch (obdService)
        {
            case OBD_SVC_DATA:
            case OBD_SVC_FREEZEFRAME:
                // Clear data items
                pidSupported.clear();
                PidPvs.clear();
                break;

            case OBD_SVC_READ_CODES:
            case OBD_SVC_PENDINGCODES:
            case OBD_SVC_PERMACODES:
                tCodes.clear();
                break;

            case OBD_SVC_VEH_INFO:
                // Clear data items
                pidSupported.clear();
                VidPvs.clear();
                break;
        }
    }

    /**
     * Setter for property service.
     *  This includes initialisation of the requested service to the vehicle
     * @param obdService New OBD service to be requested.
     * @param clearLists clear data list for this service
     */
    public void setService(int obdService, boolean clearLists)
    {
        this.service = obdService;
        pidsWrapped = false;
        // if lists shall be cleared
        if (clearLists)
        {
            // then do it
            clearDataLists(obdService);
        }

        // set specified OBD service
        switch (obdService)
        {
            case OBD_SVC_NONE:
                // sendCommand(CMD_RESET,0);
                break;

            case OBD_SVC_DATA:
            case OBD_SVC_FREEZEFRAME:
                // request for PID's supported
                writeTelegram(emptyBuffer, obdService, 0);
                break;

            case OBD_SVC_READ_CODES:
            case OBD_SVC_PENDINGCODES:
            case OBD_SVC_PERMACODES:
                numCodes = 0;
                // read PID number of codes ...
                cmdQueue.add("0101");
                // read trouble codes
                writeTelegram(emptyBuffer, obdService, 0);
                break;

            case OBD_SVC_CLEAR_CODES:
                // clear trouble codes
                writeTelegram(emptyBuffer, obdService, 0);
                // wait for codes to be cleared
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    // Intentionally do nothing
                }
                break;

            case OBD_SVC_VEH_INFO:
                // read vehicle information
                writeTelegram(emptyBuffer, obdService, 0);
                break;

            case OBD_SVC_CTRL_MODE:
            case OBD_SVC_O2_RESULT:
            case OBD_SVC_MON_RESULT:
            default:
                log.warning("Service not supported: " + obdService);
        }
    }
}
