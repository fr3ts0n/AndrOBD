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

import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


/**
 * Communication protocol to talk to a ELM327 OBD interface
 *
 * @author erwin
 */
public class ElmProt
	extends ObdProt
	implements TelegramListener, TelegramWriter, Runnable
{
	/** virtual OBD service for CAN monitoring */
	public static final int OBD_SVC_CAN_MONITOR = 256;

	/** property name for ECU addresses */
	public static final String PROP_ECU_ADDRESS = "ecuaddr";
	/** property name for protocol status */
	public static final String PROP_STATUS      = "status";

	/** CAN protocol handler */
	public static CanProtFord canProt = new CanProtFord();
	/** Adaptive timing handler */
	public AdaptiveTiming mAdaptiveTiming = new AdaptiveTiming();

	/** number of bytes expected from opponent */
	private int charsExpected = 0;
	/** remember last command which was sent */
	private char[] lastCommand;

	/** preferred ELM protocol to be selected */
	static private PROT preferredProtocol = PROT.ELM_PROT_AUTO;

	/** list of identified ECU addresses */
	private TreeSet<Integer> ecuAddresses = new TreeSet<Integer>();
	/** selected ECU address */
	private int selectedEcuAddress = 0;
	/** custom ELM initialisation commands */
	Vector<String> customInitCommands = new Vector<String>();

	/**
	 * ELM protocol ID's
	 */
	public enum PROT
	{
		ELM_PROT_AUTO           ( "Automatic"                             ),
		ELM_PROT_J1850PWM       ( "SAE J1850 PWM (41.6 KBaud)"            ),
		ELM_PROT_J1850VPW       ( "SAE J1850 VPW (10.4 KBaud)"            ),
		ELM_PROT_9141_2         ( "ISO 9141-2 (5 Baud Init)"              ),
		ELM_PROT_14230_4        ( "ISO 14230-4 KWP (5 Baud Init)"         ),
		ELM_PROT_14230_4F       ( "ISO 14230-4 KWP (fast Init)"           ),
		ELM_PROT_15765_11_F     ( "ISO 15765-4 CAN (11 Bit ID, 500 KBit)" ),
		ELM_PROT_15765_29_F     ( "ISO 15765-4 CAN (29 Bit ID, 500 KBit)" ),
		ELM_PROT_15765_11_S     ( "ISO 15765-4 CAN (11 Bit ID, 250 KBit)" ),
		ELM_PROT_15765_29_S     ( "ISO 15765-4 CAN (29 Bit ID, 250 KBit)" ),
		ELM_PROT_J1939_29_S     ( "SAE J1939 CAN (29 bit ID, 250* kbaud)" ),
		ELM_PROT_USER1_CAN_11_S ( "User1 CAN (11* bit ID, 125* kbaud)"    ),
		ELM_PROT_USER2_CAN_11_S ( "User2 CAN (11* bit ID, 50* kbaud)"     );
		private String description;

		PROT(String _description)
		{
			description = _description;
		}

		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * possible ELM responses and ID's
	 */
	enum RSP_ID
	{
		PROMPT    ( ">"           ),
		OK        ( "OK"          ),
		MODEL     ( "ELM"         ),
		NODATA    ( "NODATA"      ),
		SEARCH    ( "SEARCHING"   ),
		ERROR     ( "ERROR"       ),
		NOCONN    ( "UNABLE"      ),
		NOCONN2   ( "NABLETO"     ),
		CANERROR  ( "CANERROR"    ),
		BUSBUSY   ( "BUSBUSY"     ),
		BUSERROR  ( "BUSERROR"    ),
		BUSINIERR ( "BUSINIT:ERR" ),
		BUSINIERR2( "BUSINIT:BUS" ),
		BUSINIERR3( "BUSINIT:...ERR" ),
		FBERROR   ( "FBERROR"     ),
		DATAERROR ( "DATAERROR"   ),
		BUFFERFULL( "BUFFERFULL"  ),
		STOPPED   ( "STOPPED"     ),
		RXERROR   ( "<"           ),
		QMARK     ( "?"           ),
		UNKNOWN   ( ""            );
		private String response;

		RSP_ID(String response)
		{
			this.response = response;
		}

		@Override
		public String toString()
		{
			return response;
		}
	}

	/**
	 * possible communication states
	 */
	public enum STAT
	{
		UNDEFINED     ( "Undefined"     ),
		INITIALIZING  ( "Initializing"  ),
		INITIALIZED   ( "Initialized"   ),
		ECU_DETECT    ( "ECU detect"    ),
		ECU_DETECTED  ( "ECU detected"  ),
		CONNECTING    ( "Connecting"    ),
		CONNECTED     ( "Connected"     ),
		NODATA        ( "No data"       ),
		STOPPED       ( "Stopped"       ),
		DISCONNECTED  ( "Disconnected"  ),
		BUSERROR      ( "BUS error"     ),
		DATAERROR     ( "DATA error"    ),
		RXERROR       ( "RX error"      ),
		ERROR         ( "Error"         );
		private String elmState;

		STAT(String state)
		{
			elmState = state;
		}

		@Override
		public String toString()
		{
			return elmState;
		}
	}

	/**
	 * numeric IDs for commands
	 */
	public enum CMD
	{
		RESET(        "Z"   , 0, true ), ///< reset adapter
		WARMSTART(    "WS"  , 0, true ), ///< warm start
		PROTOCLOSE(   "PC"  , 0, true ), ///< protocol close
		DEFAULTS(     "D"   , 0, true ), ///< set all to defaults
		INFO(         "I"   , 0, true ), ///< request adapter info
		LOWPOWER(     "LP"  , 0, true ), ///< switch to low power mode
		ECHO(         "E"   , 1, true ), ///< enable/disable echo
		SETLINEFEED(  "L"   , 1, true ), ///< enable/disable line feeds
		SETSPACES(    "S"   , 1, true ), ///< enable/disable spaces
		SETHEADER(    "H"   , 1, true ), ///< enable/disable header response
		GETPROT(      "DP"  , 0, true ), ///< get protocol
		SETPROT(      "SP"  , 1, true ), ///< set protocol
		CANMONITOR(   "MA"  , 0, true ), ///< monitor CAN messages
		SETPROTAUTO(  "SPA" , 1, true ), ///< set protocol auto
		ADAPTTIMING(  "AT"  , 1, true ), ///< Set ELM internal adaptive timing (0-2)
		SETTIMEOUT(   "ST"  , 2, true ), ///< set timeout (x*4ms)
		SETTXHDR(     "SH"  , 3, true ), ///< set TX header
		SETCANRXFLT(  "CRA" , 3, true ), ///< set CAN RX filter
		CLRCANRXFLT(  "CRA" , 0, true ); ///< clear CAN RX filter

		protected static final String CMD_HEADER = "AT";
		private String command;
		protected int paramDigits;
		private boolean disablingAllowed;
		private boolean enabled = true;

		CMD(String cmd, int numDigitsParameter, boolean allowAdaption)
		{
			command = cmd;
			paramDigits = numDigitsParameter;
			disablingAllowed = allowAdaption;
		}

		@Override
		public String toString()
		{
			return CMD_HEADER+command;
		}

		public boolean isEnabled()
		{
			return enabled;
		}

		public void setEnabled(boolean enabled)
		{
			if (disablingAllowed)
			{
				this.enabled = enabled;
			}
			// log current state
			log.fine(String.format("ELM command '%s' -> %s",
					toString(),
					this.enabled ? "enabled" : "disabled"));
		}

		public boolean isDisablingAllowed()
		{
			return disablingAllowed;
		}
	}

	/**
	 * Adaptive timing mode
	 */
	public enum AdaptTimingMode
	{
		OFF,
		ELM_AT1,
		ELM_AT2,
		SOFTWARE
	}

	/**
	 * Adaptive ELM timing handler
	 * * optimizes ELM message timeout at runtime
	 */
	public class AdaptiveTiming
	{
		/**
		 * for ELM message timeout handling
		 */
		/** max. ELM Message Timeout [ms] */
		private static final int ELM_TIMEOUT_MAX = 1000;
		/** default ELM message timeout */
		private static final int ELM_TIMEOUT_DEFAULT = 200;
		/** Learning resolution of ELM Message Timeout [ms] */
		private static final int ELM_TIMEOUT_RES = 4;
		/** minimum ELM timeout */
		protected int ELM_TIMEOUT_MIN = 12;
		/** minimum ELM timeout (learned from vehicle) */
		protected int ELM_TIMEOUT_LRN_LOW = 12;
		/** ELM message timeout: defaults to approx 200 [ms] */
		protected int elmMsgTimeout = ELM_TIMEOUT_MAX;

		/** adaptive timing handling enabled? */
		private AdaptTimingMode mode = AdaptTimingMode.OFF;

		public AdaptTimingMode getMode()
		{
			return mode;
		}

		public void setMode(AdaptTimingMode mode)
		{

			log.info(String.format("AdaptiveTiming mode: %s -> %s",
									this.mode.toString(),
									mode.toString()));
			this.mode = mode;
			// initialize with new mode
			initialize();
		}

		/**
		 * min. (configured) ELM Message Timeout
		 * @return minimum (configured) ELM timeout value [ms]
		 */
		public int getElmTimeoutMin()
		{
			return ELM_TIMEOUT_MIN;
		}

		/**
		 * Set min. (configured) ELM Message Timeout
		 * @param elmTimeoutMin minimum (configured) ELM timeout value [ms]
		 */
		public void setElmTimeoutMin(int elmTimeoutMin)
		{
			log.info(String.format("ELM min timeout: %d -> %d",
			                       ELM_TIMEOUT_MIN, elmTimeoutMin));
			ELM_TIMEOUT_MIN = elmTimeoutMin;
		}

		/**
		 * Initialize timing hadler
		 */
		public void initialize()
		{
			if(mode == AdaptTimingMode.SOFTWARE)
			{
				// ... reset learned minimum timeout ...
				setElmTimeoutLrnLow(getElmTimeoutMin());
				// set default timeout
				setElmMsgTimeout(ELM_TIMEOUT_DEFAULT);
				// switch OFF ELM internal adaptive timing
				pushCommand(CMD.ADAPTTIMING, 0);
			}
			else
			{
				pushCommand(CMD.ADAPTTIMING, mode.ordinal());
			}
		}

		/**
		 * Adapt ELM message timeout
		 * @param increaseTimeout increase/decrease timeout
		 */
		public void adapt(boolean increaseTimeout)
		{
			if(mode != AdaptTimingMode.SOFTWARE) return;
			if(increaseTimeout)
			{
				// increase OBD timeout since we may expect answers too fast
				if ((elmMsgTimeout + ELM_TIMEOUT_RES) < ELM_TIMEOUT_MAX)
				{
					// increase timeout, since we have just timed out
					setElmMsgTimeout(elmMsgTimeout + ELM_TIMEOUT_RES);
					// ... and limit MIN timeout for this session
					setElmTimeoutLrnLow(elmMsgTimeout);
				}
			}
			else
			{
				// reduce OBD timeout towards minimum limit
				if ((elmMsgTimeout - ELM_TIMEOUT_RES) >= getElmTimeoutLrnLow())
				{
					setElmMsgTimeout(elmMsgTimeout - ELM_TIMEOUT_RES);
				}

			}
		}

		/**
		 * LOW Learn value ELM Message Timeout
		 * @return currently learned timout value [ms]
		 */
		private int getElmTimeoutLrnLow()
		{
			return ELM_TIMEOUT_LRN_LOW;
		}

		/**
		 * set LOW Learn value ELM Message Timeout
		 * @param elmTimeoutLrnLow new learn value [ms]
		 */
		private void setElmTimeoutLrnLow(int elmTimeoutLrnLow)
		{
			log.info(String.format("ELM learn timeout: %d -> %d",
			                       ELM_TIMEOUT_LRN_LOW, elmTimeoutLrnLow));
			ELM_TIMEOUT_LRN_LOW = elmTimeoutLrnLow;
		}

		/**
		 * Set message timeout to ELM adapter to wait for valid response from vehicle
		 * If this timeout expires before a valid response is received from the
		 * vehicle, the ELM adapter will respond with "NO DATA"
		 *
		 * @param newTimeout desired timeout in milliseconds
		 */
		private void setElmMsgTimeout(int newTimeout)
		{
			if (newTimeout > 0 && newTimeout != elmMsgTimeout)
			{
				log.info("ELM Timeout: " + elmMsgTimeout + " -> " + newTimeout);
				// set the timeout variable
				elmMsgTimeout = newTimeout;
				// queue the new timeout message
				pushCommand(CMD.SETTIMEOUT, newTimeout / 4);
			}
		}
	}

	/**
	 * Creates a new instance of ElmProtocol
	 */
	public ElmProt()
	{
	}

	/**
	 * set preferred ELM protocol to be used
	 * @param protoIndex preferred ELM protocol index
	 */
	public static void setPreferredProtocol(int protoIndex)
	{
		preferredProtocol = PROT.values()[protoIndex];
		log.info("Preferred protocol: " + preferredProtocol);
	}

	/**
	 * set ECU address to be received
	 * @param ecuAddress ECU address to be filtered / 0 = clear address filter
	 */
	public void setEcuAddress(int ecuAddress)
	{
		log.info(String.format("Set ECU address: 0x%x", ecuAddress));
		selectedEcuAddress = ecuAddress;
		// ensure headers are off
		pushCommand(CMD.SETHEADER, 0);
		// set/clear RX filter
		pushCommand((selectedEcuAddress != 0) ? CMD.SETCANRXFLT : CMD.CLRCANRXFLT, selectedEcuAddress);
	}

	/**
	 * disable a set of ELM commands ELM commands from preference
	 * @param disabledCmds set of ELM commands (ATxx strings) to be disabled
	 */
	public static void disableCommands(Set<String> disabledCmds)
	{
		for (CMD cmd : CMD.values())
		{
			cmd.setEnabled(disabledCmds == null
				           || !disabledCmds.contains(cmd.toString()));
		}
	}

	/**
	 * create ELM command string from command id and paramter
	 *
	 * @param cmdID ID of ELM command
	 * @param param parameter for ELM command (0 if not required)
	 * @return command char sequence or NULL if command disabled/invalid
	 */
	public String createCommand(CMD cmdID, int param)
	{
		String cmd = null;
		if(cmdID.isEnabled())
		{
			cmd = cmdID.toString();
			// if parameter is required and provided, add parameter to command
			if (cmdID.paramDigits > 0)
			{
				String fmtString = "%0".concat(String.valueOf(cmdID.paramDigits)).concat("X");
				cmd += String.format(fmtString, param);
			}
		}
		// return command String
		return cmd;
	}

	/**
	 * send command to ELM adapter
	 *
	 * @param cmdID ID of ELM command
	 * @param param parameter for ELM command (0 if not required)
	 */
	public void sendCommand(CMD cmdID, int param)
	{
		// now send command
		String cmd = createCommand(cmdID, param);
		if(cmd != null)	sendTelegram(cmd.toCharArray());
	}

	/**
	 * queue command to ELM command queue
	 *
	 * @param cmdID ID of ELM command
	 * @param param parameter for ELM command (0 if not required)
	 */
	public void pushCommand(CMD cmdID, int param)
	{
		String cmd = createCommand(cmdID, param);
		if(cmd != null)	cmdQueue.add(cmd);
	}

	@Override
	public void sendTelegram(char[] buffer)
	{
		log.fine(this.toString() + " TX:'" + String.valueOf(buffer) + "'");
		lastCommand = buffer;
		super.sendTelegram(buffer);
	}

	/**
	 * return numeric ID to given response
	 *
	 * @param response clear text response from ELM adapter
	 */
	static RSP_ID getResponseId(String response)
	{
		RSP_ID result = RSP_ID.UNKNOWN;
		for (RSP_ID id : RSP_ID.values())
		{
			if (response.startsWith(id.toString()))
			{
				result = id;
				break;
			}
		}
		// return ID
		return (result);
	}

	/**
	 * send ELM adapter to sleep mode
	 */
	public void goToSleep()
	{
		sendCommand(CMD.LOWPOWER, 0);
	}

	/**
	 * reset ELM adapter
	 */
	public void reset()
	{
		// reset all learned protocol data
		super.reset();
		// either RESET or INFO command needs to be enabled
		if(CMD.RESET.isEnabled())
			sendCommand(CMD.RESET, 0);
		else
			sendCommand(CMD.INFO, 0);
	}

	/**
	 * request addresses of all connected ECUs
	 * (received IDs are evaluated in @ref:handleDataMessage)
	 */
	private void queryEcus()
	{
		// set status to ECU detection
		setStatus(STAT.ECU_DETECT);

		// clear all identified ECU addresses
		ecuAddresses.clear();
		// clear selected ECU
		selectedEcuAddress = 0;
		// remember to disable headers again
		pushCommand(CMD.SETHEADER, 0);
		// request PIDs (from all devices)
		cmdQueue.add("0100");
		// enable headers
		sendCommand(CMD.SETHEADER, 1);
	}

	private void initialize()
	{
		// set status to INITIALIZING
		setStatus(STAT.INITIALIZING);

		// push custom init commands
		cmdQueue.addAll(customInitCommands);

		// set to preferred protocol
		pushCommand(CMD.SETPROT, preferredProtocol.ordinal());

		// initialize adaptive timing handler
		mAdaptiveTiming.initialize();

		// speed up protocol by removing spaces and line feeds from output
		pushCommand(CMD.SETSPACES, 0);
		pushCommand(CMD.SETLINEFEED, 0);

		// immediate set echo off
		pushCommand(CMD.ECHO, 0);
	}

	/**
	 * Implementation of TelegramListener
	 */

	/** multiline response is pending, for responses w/o a length info */
	private boolean responsePending = false;
	/**
	 * handle incoming protocol telegram
	 *
	 * @param buffer - telegram buffer
	 * @return number of listeners notified
	 */
	@Override
	public synchronized int handleTelegram(char[] buffer)
	{
		int result = 0;
		String bufferStr = new String(buffer);

		log.fine(this.toString() + " RX:'" + bufferStr + "'");

		// empty result
		if (buffer.length == 0)
		{
			return result;
		}

		// if ths is echo of last command
		if (lastTxMsg.compareToIgnoreCase(bufferStr) == 0)
		{
			// ignore echoed command
			return result;
		}

		// log message reception as answer to last TX message
		log.fine("ELM rx:'" + bufferStr + "' ("+lastTxMsg+")");

		// handle response
		switch (getResponseId(bufferStr))
		{
			case SEARCH:
				setStatus(status != STAT.ECU_DETECT ? STAT.CONNECTING : status);
				// NO break here
			case QMARK:
			case NODATA:
			case OK:
			case ERROR:
			case NOCONN:
			case NOCONN2:
			case CANERROR:
			case BUSERROR:
			case BUSINIERR:
			case BUSINIERR2:
			case BUSINIERR3:
			case BUSBUSY:
			case FBERROR:
			case DATAERROR:
			case BUFFERFULL:
			case RXERROR:
			case STOPPED:
				// remember this as last received message
				// do NOT respond immediately
				lastRxMsg = bufferStr;
				break;

			case MODEL:
				initialize();
				break;

			// received a PROMPT, what was the last response?
			case PROMPT:
				// check for last received message
				switch (getResponseId(lastRxMsg))
				{
					case NOCONN:
					case NOCONN2:
					case CANERROR:
					case BUSERROR:
					case BUSINIERR:
					case BUSINIERR2:
					case BUSINIERR3:
					case BUSBUSY:
					case FBERROR:
						setStatus(STAT.DISCONNECTED);
						// re-queue last command
						cmdQueue.add(String.valueOf(lastCommand));
						// queue setting to preferred protocol
						pushCommand(CMD.SETPROT, preferredProtocol.ordinal());
						// Initialize adaptive timing
						mAdaptiveTiming.initialize();
						// immediately close current protocol
						sendCommand(CMD.PROTOCLOSE, 0);
						break;

					case DATAERROR:
						setStatus(STAT.DATAERROR);
						sendCommand(CMD.WARMSTART, 0);
						break;

					case BUFFERFULL:
					case RXERROR:
						setStatus(STAT.RXERROR);
						sendCommand(CMD.WARMSTART, 0);
						break;

					case ERROR:
						setStatus(STAT.ERROR);
						sendCommand(CMD.WARMSTART, 0);
						break;

					case NODATA:
						setStatus(STAT.NODATA);
						// re-queue next data item
						if(service != OBD_SVC_NONE)
							cmdQueue.add(
								String.valueOf(createTelegram(emptyBuffer, service, getNextSupportedPid()))
							);
						// increase OBD timeout since we may expect answers too fast
						mAdaptiveTiming.adapt(true);
                        // set to preferred protocol
                        pushCommand(CMD.SETPROT, preferredProtocol.ordinal());
						// NO break here since reaction is only quqeued

					case MODEL:
					case SEARCH:
					case STOPPED:
						// was already handled before prompt
					case QMARK:
						// last command stays ignored

					case OK:
					default:
						// if there is a pending data response, handle it now ...
						if(responsePending)
						{
							result = handleDataMessage(lastRxMsg);
						}

						// queued commands will be sent first
						if (cmdQueue.size() > 0)
						{
							// get last command
							String cmd = cmdQueue.lastElement();
							// and remove it from list
							cmdQueue.remove(cmd);
							// send the command
							sendTelegram(cmd.toCharArray());
						} else
						{
							// all queued commands are sent -> we are done initializing
							if(status == STAT.INITIALIZING)
							{
								// set status to initialized
								setStatus(STAT.INITIALIZED);
								// initiate query of connected ECUs
								queryEcus();
								break;
							}

							// all queued commands are sent -> we are done detecting ECUs
							setStatus(status == STAT.ECU_DETECT ? STAT.ECU_DETECTED : status);

							switch (service)
							{
								case OBD_SVC_VEH_INFO:
									// if all pid's have been read once ...
									if(pidsWrapped)
									{
										// ... terminate service loop
										break;
									}
									// no break here ...
								case OBD_SVC_DATA:
								case OBD_SVC_FREEZEFRAME:
								{
									// otherwise the next PID will be requested
									writeTelegram(emptyBuffer, service, getNextSupportedPid());
									// reduce OBD timeout towards minimum limit
									mAdaptiveTiming.adapt(false);
								}
								break;

								case OBD_SVC_NONE:
								default:
									// do nothing
							}
						}
				}
				break;

			// handle data response
			default:
				// if we are still initializing check for address entries
				switch(status)
				{
					case ECU_DETECT:
					{
						// start of 0100 response is end of address
						int adrEnd = bufferStr.indexOf("41");
						int adrStart = bufferStr.lastIndexOf(".")+1;
						if(adrEnd > adrStart)
						{
							int adrLen = adrEnd - adrStart;
							if((adrLen % 2) != 0)
							{
								/*
								 * odd address length
								 * -> CAN address length = 3 digits + 2 digits frame type
								 */
								adrLen = 3;
							}
							else if(adrLen == 6)
							{
								/*
								 * 6 char (3 byte) prefix -> ISO9141 address <FF><RR><SS>
								 *     FF - Frame type
								 *     RR - receiver address
								 *     SS - sender address
								 */
								adrLen = 2;
								adrStart = adrEnd - adrLen;
							}
							// extract address
							String address = bufferStr.substring(adrStart, adrStart + adrLen);
							
							log.fine(String.format("Found ECU address: 0x%s", address));
							// and add to list of addresses
							ecuAddresses.add(Integer.valueOf(address, 16));
						}
						return lastRxMsg.length();
					}
					default:
						break;
				}

				// we are connected ...
				setStatus(STAT.CONNECTED);

				// is this a length identifier?
				if (buffer[0] == '0' && buffer.length == 3)
				{
					// then remember the length to be expected
					charsExpected = Integer.valueOf(bufferStr, 16) * 2;
					lastRxMsg = "";
					return (result);
				}

				// is this a multy-line response
				int idx = bufferStr.indexOf(':');
				if (idx >= 0)
				{
					/* no length known, set marker for pending response
					   response will be finished on reception of prompt */
					responsePending = (charsExpected == 0);

					if(buffer[0] == '0')
					{
						// first line of a multiline message
						lastRxMsg = bufferStr.substring(idx + 1);
					}
					else
					{
						// continuation lines
						// concat response without line counter
						lastRxMsg += bufferStr.substring(idx + 1);
					}
				} else
				{
					// otherwise use this as last received message
					lastRxMsg = bufferStr;
					charsExpected = 0;
					responsePending = false;
				}

				// if we haven't received complete result yet, then wait for the rest
				if (lastRxMsg.length() < charsExpected)
				{
					return (result);
				}

				// if response is finished, handle it
				if(!responsePending)
				{
					result = handleDataMessage(lastRxMsg);
				}
		}
		return (result);
	}

	/**
	 * forward data message for further handling
	 * @param lastRxMsg received message to be forwarded
	 * @return number of bytes processed
	 */
	private int handleDataMessage(String lastRxMsg)
	{
		int result = 0;

		// otherwise process response
		switch (service)
		{
			case OBD_SVC_NONE:
				// ignore messages
				break;

			case OBD_SVC_CAN_MONITOR:
				result = canProt.handleTelegram(lastRxMsg.toCharArray());
				break;

			default:
				// Let the OBD protocol handle the telegram
				result = super.handleTelegram(lastRxMsg.toCharArray());
		}
		return result;
	}

	// switch to exit the demo thread
	public static boolean runDemo;

	/**
	 * run threaded loop to simulate incoming telegrams
	 */
	public void run()
	{
		int value = 0;
		Integer pid;
		runDemo = true;

		log.info("ELM DEMO thread started");
		while (runDemo)
		{
			try
			{
				handleTelegram(RSP_ID.MODEL.toString().toCharArray());
				setStatus(STAT.ECU_DETECT);
				handleTelegram("SEARCHING...".toCharArray());
				handleTelegram("7EA074100000000".toCharArray());
				handleTelegram("486B104100BF9FA8919B".toCharArray());
				handleTelegram("...486B104100BF9FA8919B".toCharArray());
				handleTelegram("7E8064100000000".toCharArray());
				handleTelegram("7E9074100000000".toCharArray());
				handleTelegram("7E9074100000000".toCharArray());
				setStatus(STAT.ECU_DETECTED);

				while (runDemo)
				{
					switch (service)
					{
						// read any kinds of trouble codes
						case OBD_SVC_READ_CODES:
							// simulate 12 TCs set as multy line response
							// send codes as multy line response
							handleTelegram("014".toCharArray());
							handleTelegram("0:438920B920BD".toCharArray());
							handleTelegram("1:C002242A246E02".toCharArray());
							handleTelegram("2:36010101162453".toCharArray());
							// number of codes = 12 + MIL ON
							handleTelegram("41018C000000".toCharArray());
							Thread.sleep(500);
							break;

						case OBD_SVC_PENDINGCODES:
							// simulate 12 TCs set as subsequent single line responses
							// send codes as subsequent single line responses
							handleTelegram("470920B920BD".toCharArray());
							handleTelegram("4709C002242A246E".toCharArray());
							handleTelegram("4709023601010116".toCharArray());
							handleTelegram("4709245300000000".toCharArray());
							// number of codes = 12 + MIL OFF
							handleTelegram("41010C000000".toCharArray());
							Thread.sleep(500);
							break;

						case OBD_SVC_PERMACODES:
							// simulate 12 TCs set as multy line response
							// send codes as multy line response
							handleTelegram("014".toCharArray());
							handleTelegram("0:4A8920B920BD".toCharArray());
							handleTelegram("1:C002242A246E02".toCharArray());
							handleTelegram("2:36010101162453".toCharArray());
							// number of codes = 12 + MIL ON
							handleTelegram("41018C000000".toCharArray());
							Thread.sleep(500);
							break;

						// otherwise send data ...
						case OBD_SVC_DATA:
						case OBD_SVC_FREEZEFRAME:
							pid = getNextSupportedPid();
							if (pid != 0)
							{
								value++;
								value &= 0xFF;
								// format new data message and handle it as new reception
								handleTelegram(String.format(
									service == OBD_SVC_DATA ? "4%X%02X%02X%02X%02X%02X" : "4%X%02X00%02X%02X%02X%02X",
									service, pid, value, value, value, value).toCharArray());
							} else
							{
								// simulate "ALL PIDs supported"
								int i;
								for (i = 0; i < 0xE0; i += 0x20)
									handleTelegram(String.format(
										service == OBD_SVC_DATA ? "4%X%02XFFFFFFFF"
											: "4%X%02X00FFFFFFFF", service, i).toCharArray());
								handleTelegram(String.format(
									service == OBD_SVC_DATA ? "4%X%02XFFFFFFFE"
										: "4%X%02X00FFFFFFFE", service, i).toCharArray());
							}
							break;

						case OBD_SVC_VEH_INFO:
							pid = getNextSupportedPid();
							if (pid == 0)
							{
								// simulate "ALL pids supported"
								handleTelegram("490054000000".toCharArray());
							}

							// send VIN "1234567890ABCDEFG"
							handleTelegram("013".toCharArray());
							handleTelegram("1:4902313233".toCharArray());
							handleTelegram("2:34353637383930".toCharArray());
							handleTelegram("3:41424344454647".toCharArray());

							// send CAL-ID "GSPA..." without length id
							handleTelegram("0:490401475350".toCharArray());
							handleTelegram("1:412D3132333435".toCharArray());
							handleTelegram("2:36373839303000".toCharArray());

							// CAL-ID 01234567
							handleTelegram("490601234567".toCharArray());
							break;

						case OBD_SVC_NONE:
							// just keep quiet until soneone requests something
							break;

						default:
							// respond "service not supported"
							handleTelegram(String.format("7F%02X11", service).toCharArray());
							Thread.sleep(500);
							break;

					}
					Thread.sleep(50);
				}
			} catch (Exception ex)
			{
				log.severe(ex.getLocalizedMessage());
			}
		}
		log.info("ELM DEMO thread finished");
	}

	/**
	 * set custom initialisation commands
	 * @param commands custom initialisation commands
	 */
	public void setCustomInitCommands(String[] commands)
	{
		List<String> cmds = Arrays.asList(commands);
		// reverse list, since all commands are pushed rather than queued
		Collections.reverse(cmds);
		// clear list
		customInitCommands.clear();
		// add all entries
		customInitCommands.addAll(cmds);
	}

	/**
	 * Setter for property service.
	 *
	 * @param service New value of property service.
	 * @param clearLists clear data list for this service
	 */
	@Override
	public void setService(int service, boolean clearLists)
	{
		// log the change in service
		if (service != this.service)
		{
			log.info("OBD Service: " + this.service + "->" + service);
			this.service = service;

			// send corresponding command(s)
			switch (service)
			{
				case OBD_SVC_CAN_MONITOR:
					sendCommand(CMD.CANMONITOR, 0);
					break;

				default:
					super.setService(service, clearLists);
			}
		}
	}

	/**
	 * set OBD service - compatibility function
	 * @param service New value of property service.
	 */
	public void setService(int service)
	{
		setService(service, true);
	}

	/**
	 * Holds value of property status.
	 */
	private STAT status;

	/**
	 * Getter for property status.
	 *
	 * @return Value of property status.
	 */
	public STAT getStatus()
	{
		return this.status;
	}

	/**
	 * Setter for property status.
	 *
	 * @param status New value of property status.
	 */
	public void setStatus(STAT status)
	{
		STAT oldStatus = this.status;
		this.status = status;
		if (status != oldStatus)
		{
			log.info("Status change: " + oldStatus + "->" + status);
			// ECUs detected -> send identified ECU addresses
			if(status == STAT.ECU_DETECTED)
			{
				firePropertyChange(new PropertyChangeEvent(this, PROP_ECU_ADDRESS, null, ecuAddresses));
			}

			// now fire regular status change
			firePropertyChange(new PropertyChangeEvent(this, PROP_STATUS, oldStatus, status));
		}
	}
}
