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

package com.fr3ts0n.ecu.prot;

import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;

import java.beans.PropertyChangeEvent;


/**
 * Communication protocol to talk to a ELM327 OBD interface
 *
 * @author erwin
 */
public class ElmProt
	extends ObdProt
	implements TelegramListener, TelegramWriter, Runnable
{

	public static final int OBD_SVC_CAN_MONITOR = 256;

	/**
	 * ELM protocol ID's
	 */
	public static enum PROT
	{
		ELM_PROT_AUTO("Automatic"),
		ELM_PROT_J1850PWM("SAE J1850 PWM (41.6 KBaud)"),
		ELM_PROT_J1850VPW("SAE J1850 VPW (10.4 KBaud)"),
		ELM_PROT_9141_2("ISO 9141-2 (5 Baud Init)"),
		ELM_PROT_14230_4("ISO 14230-4 KWP (5 Baud Init)"),
		ELM_PROT_14230_4F("ISO 14230-4 KWP (fast Init)"),
		ELM_PROT_15765_11_F("ISO 15765-4 CAN (11 Bit ID, 500 KBit)"),
		ELM_PROT_15765_29_F("ISO 15765-4 CAN (29 Bit ID, 500 KBit)"),
		ELM_PROT_15765_11_S("ISO 15765-4 CAN (11 Bit ID, 250 KBit)"),
		ELM_PROT_15765_29_S("ISO 15765-4 CAN (29 Bit ID, 250 KBit)"),
		ELM_PROT_J1939_29_S("SAE J1939 CAN (29 bit ID, 250* kbaud)"),
		ELM_PROT_USER1_CAN_11_S("User1 CAN (11* bit ID, 125* kbaud)"),
		ELM_PROT_USER2_CAN_11_S("User2 CAN (11* bit ID, 50* kbaud)"),;
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
	static enum RSP_ID
	{
		PROMPT(">"),
		OK("OK"),
		MODEL("ELM"),
		NODATA("NODATA"),
		SEARCH("SEARCHING"),
		ERROR("ERROR"),
		NOCONN("UNABLE"),
		CANERROR("CANERROR"),
		BUSBUSY("BUSBUSY"),
		BUSERROR("BUSERROR"),
		FBERROR("FBERROR"),
		DATAERROR("DATAERROR"),
		BUFFERFULL("BUFFERFULL"),
		STOPPED("STOPPED"),
		RXERROR("<"),
		QMARK("?"),
		UNKNOWN("");
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
	public static enum STAT
	{
		UNDEFINED("Undefined"),
		INITIALIZING("Initializing"),
		CONNECTING("Connecting"),
		CONNECTED("Connected"),
		NODATA("No data"),
		DISCONNECTED("Disconnected"),
		BUSERROR("BUS error"),
		DATAERROR("DATA error"),
		RXERROR("RX error"),
		ERROR("Error");
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
	public static enum CMD
	{
		RESET("ATZ", 0),
		INFO("ATI", 0),
		GETPROT("ATDP", 0),
		CANMONITOR("ATMA", 0),
		ECHO("ATE", 1),
		SETPROT("ATSP", 1),
		SETPROTAUTO("ATSPA", 1),
		SETTIMEOUT("ATST", 2),
		SETLINEFEED("ATL", 1),
		SETSPACES("ATS", 1);
		private String command;
		protected int paramDigits;

		CMD(String cmd, int numDigitsParameter)
		{
			command = cmd;
			paramDigits = numDigitsParameter;
		}

		@Override
		public String toString()
		{
			return command;
		}
	}

	/** CAN protocol handler */
	public static CanProtFord canProt = new CanProtFord();

	/**
	 * Creates a new instance of ElmProtocol
	 */
	public ElmProt()
	{
	}

	/**
	 * for ELM message timeout handling
	 */
	/** LOW Learn value ELM Message Timeout [ms] */
	static int ELM_TIMEOUT_LRN_LOW = 10;
	/** min. ELM Message Timeout [ms] */
	static final int ELM_TIMEOUT_MIN = 10;
	/** max. ELM Message Timeout [ms] */
	static final int ELM_TIMEOUT_MAX = 200;
	/** Learning resolution of ELM Message Timeout [ms] */
	static final int ELM_TIMEOUT_RES = 4;
	/** ELM message timeout: defaults to approx 200 [ms] */
	protected int elmMsgTimeout = ELM_TIMEOUT_MAX;
	/** number of bytes expected from opponent */
	private int charsExpected = 0;
	/** remember last command which was sent */
	private char[] lastCommand;

	/**
	 * Set message timeout to ELM adapter to wait for valid response from vehicle
	 * If this timeout expires before a valid response is received from the
	 * vehicle, the ELM adapter will respond with "NO DATA"
	 *
	 * @param newTimeout desired timeout in milliseconds
	 */
	public void setElmMsgTimeout(int newTimeout)
	{
		if (newTimeout > 0 && newTimeout != elmMsgTimeout)
		{
			log.info("ELM Timeout: " + elmMsgTimeout + " -> " + newTimeout);
			// set the timeout variable
			elmMsgTimeout = newTimeout;
			// queue the new timeout message
			queueCommand(CMD.SETTIMEOUT, newTimeout / 4);
		}
	}

	/**
	 * create ELM command string from command id and paramter
	 *
	 * @param cmdID ID of ELM command
	 * @param param parameter for ELM command (0 if not required)
	 * @return command char sequence
	 */
	public String createCommand(CMD cmdID, int param)
	{
		String cmd = cmdID.toString();
		// if parameter is required and provided, add parameter to command
		if (cmdID.paramDigits > 0)
		{
			String fmtString = "%0".concat(String.valueOf(cmdID.paramDigits)).concat("X");
			cmd += String.format(fmtString, param);
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
		sendTelegram(createCommand(cmdID, param).toCharArray());
	}

	/**
	 * queue command to ELM command queue
	 *
	 * @param cmdID ID of ELM command
	 * @param param parameter for ELM command (0 if not required)
	 */
	public void queueCommand(CMD cmdID, int param)
	{
		cmdQueue.add(createCommand(cmdID, param));
	}

	@Override
	public void sendTelegram(char[] buffer)
	{
		log.debug(this.toString() + " TX:'" + String.valueOf(buffer) + "'");
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
	 * Implementation of TelegramListener
	 */
	/**
	 * handle incoming protocol telegram
	 *
	 * @param buffer - telegram buffer
	 * @return number of listeners notified
	 */
	@Override
	public int handleTelegram(char[] buffer)
	{
		int result = 0;
		String bufferStr = new String(buffer);

		log.debug(this.toString() + " RX:'" + bufferStr + "'");

		// empty result
		if (buffer.length == 0)
		{
			return result;
		}

		// if ths is echo of last command
		if (lastTxMsg.compareToIgnoreCase(bufferStr) == 0)
		{
			return result;
		}

		// handle response
		switch (getResponseId(bufferStr))
		{
			case SEARCH:
				setStatus(STAT.CONNECTING);
				// NO break here
			case QMARK:
			case NODATA:
			case OK:
			case ERROR:
			case MODEL:
			case NOCONN:
			case CANERROR:
			case BUSERROR:
			case BUSBUSY:
			case FBERROR:
			case DATAERROR:
			case BUFFERFULL:
			case RXERROR:
			case STOPPED:
				// remember this as last received message
				// do NOT respond immediately
				lastRxMsg = bufferStr;
				log.info("ELM rx:'" + bufferStr + "'");
				break;

			// received a PROMPT, what was the last response?
			case PROMPT:
				setStatus(STAT.CONNECTED);
				// check for last received message
				switch (getResponseId(lastRxMsg))
				{
					case MODEL:
						// since device just restarted, assume device timeout
						// to be set to default value ...
						elmMsgTimeout = ELM_TIMEOUT_MAX;
						// ... reset learned minimum timeout ...
						ELM_TIMEOUT_LRN_LOW = ELM_TIMEOUT_MIN;
						// ... and try to set it to optimum performance
						setElmMsgTimeout(ELM_TIMEOUT_LRN_LOW);
						// speed up protocol by removing spaces and line feeds from output
						queueCommand(CMD.SETSPACES, 0);
						queueCommand(CMD.SETLINEFEED, 0);
						// immediate set echo off
						sendCommand(CMD.ECHO, 0);
						setStatus(STAT.INITIALIZING);
						break;

					case NOCONN:
						// no immediate response
						setStatus(STAT.DISCONNECTED);
						sendCommand(CMD.RESET, 0);
						break;

					case CANERROR:
					case BUSERROR:
					case BUSBUSY:
					case FBERROR:
						setStatus(STAT.BUSERROR);
						sendCommand(CMD.RESET, 0);
						break;

					case DATAERROR:
						setStatus(STAT.DATAERROR);
						sendCommand(CMD.RESET, 0);
						break;

					case BUFFERFULL:
					case RXERROR:
						setStatus(STAT.RXERROR);
						sendCommand(CMD.RESET, 0);
						break;

					case ERROR:
						setStatus(STAT.ERROR);
						sendCommand(CMD.RESET, 0);
						break;

					case QMARK:
						// RESET device
						sendCommand(CMD.RESET, 0);
						setStatus(STAT.INITIALIZING);
						break;

					case SEARCH:
						setStatus(STAT.CONNECTING);
						break;

					case NODATA:
					case STOPPED:
						char[] veryLastCmd = lastCommand;
						setStatus(STAT.NODATA);
						// re-queue last command
						if(service != OBD_SVC_NONE)	cmdQueue.add(String.valueOf(veryLastCmd));
						// increase OBD timeout since we may expect answers too fast
						if ((elmMsgTimeout + ELM_TIMEOUT_RES) < ELM_TIMEOUT_MAX)
						{
							// increase timeout, since we have just timed out
							setElmMsgTimeout(elmMsgTimeout + ELM_TIMEOUT_RES);
							// ... and limit MIN timeout for this session
							ELM_TIMEOUT_LRN_LOW = elmMsgTimeout;
						}
						// NO break here since reaction is only quqeued

					case OK:
					default:
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
							switch (service)
							{
								case OBD_SVC_DATA:
								case OBD_SVC_FREEZEFRAME:
								case OBD_SVC_VEH_INFO:
								{
									// otherwise the next PID will be requested
									writeTelegram(emptyBuffer, service, getNextSupportedPid());
									// reduce OBD timeout towards minimum limit
									if ((elmMsgTimeout - ELM_TIMEOUT_RES) >= ELM_TIMEOUT_LRN_LOW)
									{
										setElmMsgTimeout(elmMsgTimeout - ELM_TIMEOUT_RES);
									}
								}
								break;

								default:
									// do nothing
							}
						}
				}
				break;

			// handle data response
			default:
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
					// concat response without line counter
					lastRxMsg += bufferStr.substring(idx + 1);
				} else
				{
					// otherwise use this as last received message
					lastRxMsg = bufferStr;
					charsExpected = 0;
				}

				// if we haven't received complete result yet, then wait for the rest
				if (lastRxMsg.length() < charsExpected)
				{
					return (result);
				}

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
		}
		return (result);
	}

	// switch to exit the demo thread
	public static boolean runDemo;

	/**
	 * run threaded loop to simulate incoming telegrams
	 */
	public void run()
	{
		int value = 0;
		Integer pid = null;
		runDemo = true;

		log.info("ELM DEMO thread started");
		while (runDemo)
		{
			try
			{
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
							// handleTelegram("41018C000000".toCharArray());
							break;

						case OBD_SVC_PENDINGCODES:
							// simulate 12 TCs set as subsequent single line responses
							// send codes as subsequent single line responses
							handleTelegram("470920B920BD".toCharArray());
							handleTelegram("4709C002242A246E".toCharArray());
							handleTelegram("4709023601010116".toCharArray());
							handleTelegram("4709245300000000".toCharArray());
							// number of codes = 12 + MIL OFF
							// handleTelegram("41010C000000".toCharArray());
							break;

						case OBD_SVC_PERMACODES:
							// simulate 12 TCs set as multy line response
							// send codes as multy line response
							handleTelegram("014".toCharArray());
							handleTelegram("0:4A8920B920BD".toCharArray());
							handleTelegram("1:C002242A246E02".toCharArray());
							handleTelegram("2:36010101162453".toCharArray());
							// number of codes = 12 + MIL ON
							// handleTelegram("41018C000000".toCharArray());
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
								handleTelegram("490040000000".toCharArray());
							}

							// send VIN "1234567890ABCDEFG"
							handleTelegram("013".toCharArray());
							handleTelegram("1:4902313233".toCharArray());
							handleTelegram("2:34353637383930".toCharArray());
							handleTelegram("3:41424344454647".toCharArray());
							break;

						case OBD_SVC_NONE:
						default:
							// just keep quiet until soneone requests something
							break;

					}
					Thread.sleep(50);
				}
			} catch (Exception ex)
			{
				log.error(ex.getLocalizedMessage());
			}
		}
		log.info("ELM DEMO thread finished");
	}

	/**
	 * Setter for property service.
	 *
	 * @param service New value of property service.
	 */
	@Override
	public void setService(int service)
	{
		// log the change in service
		if (service != this.service)
		{
			log.info("OBD Service: " + this.service + "->" + service);
			this.service = service;
			// reset timeout to optimum performance
			setElmMsgTimeout(ELM_TIMEOUT_MIN);

			// clean up data lists
			switch (service)
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
			}

			// send corresponding command(s)
			switch (service)
			{
				case OBD_SVC_CAN_MONITOR:
					sendCommand(CMD.CANMONITOR, 0);
					break;

				default:
					super.setService(service);
			}
		}

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
			firePropertyChange(new PropertyChangeEvent(this, "status", oldStatus, status));
		}
	}
}
