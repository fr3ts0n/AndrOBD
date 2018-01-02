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

package com.fr3ts0n.prot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic Base Class of Protocol header hadling
 *
 * @author $Author: erwin $
 */
public abstract class ProtoHeader
	extends TelegramSender
	implements TelegramListener, TelegramWriter
{

	/** Logging object */
	protected static Logger log = Logger.getLogger("com.fr3ts0n.prot");
	/** List of telegram listeners */
	@SuppressWarnings("rawtypes")
	protected Vector TelegramListeners = new Vector();
	/** object to be used for parsing and formatting timestamps */
	protected static SimpleDateFormat TimeStampFormat =
		new SimpleDateFormat("yyyyMMddHHmmssSS");
	/** object to be used for parsing and formatting datestamps */
	protected static SimpleDateFormat DateStampFormat =
		new SimpleDateFormat("yyyyMMdd");
	/** object to be used for parsing and formatting decimal numbers */
	protected static DecimalFormat decimalFormat =
		new DecimalFormat("0000000000");
	/** empty buffer definition for further usage * */
	protected static final char emptyBuffer[] = {};

	/**
	 * Parameter type definitions
	 */
	/** alphanumeric parameter (String) */
	public static final int PT_ALPHA = 0;
	/** numeric integer parameter */
	public static final int PT_NUMERIC = 1;
	/** numeric float parameter */
	public static final int PT_FLOAT = 2;
	/** TimeStamp parameter */
	public static final int PT_TIMESTAMP = 3;
	/** DateStamp parameter */
	public static final int PT_DATESTAMP = 4;
	/** Integer parameter */
	public static final int PT_INTEGER = 5;
	/** BCD parameter */
	public static final int PT_BCD = 6;
	/** HEX parameter */
	public static final int PT_HEX = 7;
	/** HEX parameter signed 8 Bit */
	public static final int PT_HEX_S8 = 8;
	/** HEX parameter signed 16 Bit */
	public static final int PT_HEX_S16 = 9;
	/** String Value within hex parameter (HEXDUMP) */
	public static final int PT_HEX_ALPHA = 10;
	/**
	 * Indices of Parameter definitions (int[index])
	 */
	/** start position within telegram (staring with 1) */
	static final int ID_START = 0;
	/** length of parameter within telegram (staring with 1) */
	static final int ID_LEN = 1;
	/** type of parameter see type PT_xxxx */
	static final int ID_TYPE = 2;
	/** string padding character */
	protected static char paddingChr = ' ';
	/** check if telegram listeners have to be notified on empty payload */
	protected boolean isNotificationOnEmptyPayloadAllowed = false;

	/**
	 * list of parameters for specific protocol
	 *
	 * @return complete set of protocol parameters
	 */
	public abstract int[][] getTelegramParams();

	/**
	 * list of parameter descriptions for specific protocol
	 *
	 * @return complete set of protocol parameter description strings
	 */
	protected abstract String[] getParamDescriptors();

	/**
	 * return message footer for protocol payload
	 *
	 * @param buffer buffer of payload data
	 * @return buffer of message footer
	 */
	public abstract char[] getFooter(char[] buffer);

	/**
	 * create a new telegram header for selected payload data buffer
	 * inclunding setting all ID's, sizes and validity issues
	 *
	 * @param buffer buffer of payload data
	 * @return buffer of new telegram header
	 */
	protected abstract char[] getNewHeader(char[] buffer);

	/**
	 * create a new telegram header inclunding setting all ID's, sizes and
	 * validity issues
	 *
	 * @return buffer of new telegram header
	 */
	protected abstract char[] getNewHeader(char[] buffer, int type, Object id);

	/**
	 * create a new telegram header inclunding setting all ID's, sizes and
	 * validity issues (empty payload)
	 *
	 * @return buffer of new telegram header
	 */
	protected char[] getNewHeader()
	{
		return getNewHeader(emptyBuffer);
	}

	/**
	 * return message footer for protocol
	 *
	 * @return length of message footer
	 */
	public int getFooterLength()
	{
		return (getFooter(emptyBuffer).length);
	}

	/**
	 * return specific record for telegram parameter
	 *
	 * @return one specific record for telegram parameter
	 */
	public int[] getParamEntry(int index, int[][] paramMap)
	{
		return (paramMap[index]);
	}

	/**
	 * return specific record for telegram parameter
	 *
	 * @return one specific record for telegram parameter
	 */
	public int[] getTelegramParam(int index)
	{
		return (getTelegramParams()[index]);
	}

	/**
	 * return length of one message header
	 *
	 * @return length of message header
	 */
	public static int getBufferLength(int[][] fieldMap)
	{
		int len = 0;

		for (int cnt = 0; cnt < fieldMap.length; cnt++)
		{
			len += fieldMap[cnt][ID_LEN];
		}

		return (len);
	}

	/**
	 * return length of one message header
	 *
	 * @return length of message header
	 */
	public int getHeaderLength()
	{
		return (getBufferLength(getTelegramParams()));
	}

	/**
	 * return message parameter as String
	 *
	 * @param start  start offset in buffer
	 * @param len    length in bytes
	 * @param buffer telegram buffer to be read
	 * @return parameter value as String
	 */
	public static String getParamString(int start, int len, char[] buffer)
	{
		String result = new String(buffer,
			start,
			len);
		return (result);
	}

	/**
	 * return message parameter as String
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   telegram buffer to be read
	 * @return parameter value as String
	 */
	public static String getParamString(int id, int[][] fieldMap, char[] buffer)
	{
		int currParam[] = fieldMap[id];
		return (getParamString(currParam[ID_START],
			currParam[ID_LEN],
			buffer));
	}

	/**
	 * return message parameter as String
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer telegram buffer to be read
	 * @return parameter value as String
	 */
	public String getParamString(int id, char[] buffer)
	{
		return (getParamString(id, getTelegramParams(), buffer));
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * Object with corresponding data type
	 *
	 * @param start  start offset in buffer
	 * @param len    length in bytes
	 * @param buffer telegram buffer to be read
	 * @return parameter value as Object
	 */
	public static Integer getParamInt(int start, int len, char[] buffer)
	{
		int ofs;
		int value = 0;
		// if specified length is 0, take all the rest
		if(len == 0) len = buffer.length - start;
		for (ofs = start; ofs < start + len; ofs++)
		{
			value <<= 8;
			value |= buffer[ofs];
		}
		return Integer.valueOf(value);
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * Object with corresponding data type
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   telegram buffer to be read
	 * @return parameter value as Object
	 */
	public static Integer getParamInt(int id, int[][] fieldMap, char[] buffer)
	{
		int currParam[] = fieldMap[id];
		return (getParamInt(currParam[ID_START], currParam[ID_LEN], buffer));
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * Object with corresponding data type
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer telegram buffer to be read
	 * @return parameter value as Object
	 */
	public Integer getParamInt(int id, char[] buffer)
	{
		return (getParamInt(id, getTelegramParams(), buffer));
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * from BCD Data type
	 *
	 * @param start  start offset in buffer
	 * @param len    length in bytes
	 * @param buffer telegram buffer to be read
	 * @return parameter value as Object
	 */
	public static Integer getParamBCD(int start, int len, char[] buffer)
	{
		int ofs;
		int value = 0;
		int factor = 1;

		for (ofs = 0;
		     ofs < len;
		     ofs++)
		{
			value += Integer.valueOf(Integer.toHexString(buffer[start + len - ofs]), 16).intValue() * factor;
			factor *= 0x100;
		}
		return Integer.valueOf(value);
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * from BCD Data type
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @return parameter value as Object
	 */
	public static Integer getParamBCD(int id, int[][] fieldMap, char[] buffer)
	{
		int currParam[] = fieldMap[id];
		return (getParamBCD(currParam[ID_START], currParam[ID_LEN], buffer));
	}

	/**
	 * return value of message parameter as Integer (binary)
	 * from BCD Data type
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer message buffer
	 * @return parameter value as Object
	 */
	public Integer getParamBCD(int id, char[] buffer)
	{
		return (getParamBCD(id, getTelegramParams(), buffer));
	}

	/**
	 * return value of message parameter as String from HEX-Dump
	 *
	 * @param start  start offset in buffer
	 * @param len    length in bytes
	 * @param buffer telegram buffer to be read
	 * @return parameter value as Object
	 */
	public static String getParamHexAlpha(int start, int len, char[] buffer)
	{
		int ofs;
		String msgStr = new String(buffer);
		StringBuffer result = new StringBuffer();

		for (ofs = 0;
		     ofs < len;
		     ofs += 2)
		{
			result.append(Integer.valueOf(msgStr.substring(start + ofs, start + ofs + 2), 16).intValue());
		}
		return (result.toString());
	}

	/**
	 * return value of message parameter as String from HEX-Dump
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   message buffer
	 * @return parameter value as Object
	 */
	public static String getParamHexAlpha(int id, int[][] fieldMap, char[] buffer)
	{
		int currParam[] = fieldMap[id];
		return (getParamHexAlpha(currParam[ID_START], currParam[ID_LEN], buffer));
	}

	/**
	 * return value of message parameter as String from HEX-Dump
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer message buffer
	 * @return parameter value as Object
	 */
	public String getParamHexAlpha(int id, char[] buffer)
	{
		return (getParamHexAlpha(id, getTelegramParams(), buffer));
	}


	/**
	 * return value of message parameter as
	 * Object with corresponding data type
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   message buffer
	 * @return parameter value as Object
	 */
	public static Object getParamValue(int id, int[][] fieldMap, char[] buffer)
	{
		Object result = null;
		int currParam[] = fieldMap[id];
		int tmp;

		switch (currParam[ID_TYPE])
		{
			case PT_ALPHA:
				result = getParamString(id, fieldMap, buffer);
				if (result == null)
				{
					result = new String();
				}
				break;

			case PT_NUMERIC:
				result = Long.valueOf(getParamString(id, fieldMap, buffer).trim());
				break;

			case PT_FLOAT:
				result = Double.valueOf(getParamString(id, fieldMap, buffer).trim());
				break;

			case PT_TIMESTAMP:
				try
				{
					result = TimeStampFormat.parse(getParamString(id, fieldMap, buffer));
				} catch (ParseException ex)
				{
					ex.printStackTrace();
				}
				break;

			case PT_DATESTAMP:
				try
				{
					result = DateStampFormat.parse(getParamString(id, fieldMap, buffer));
				} catch (ParseException ex)
				{
					ex.printStackTrace();
				}
				break;

			case PT_INTEGER:
				result = getParamInt(id, fieldMap, buffer);
				break;

			case PT_BCD:
				result = getParamBCD(id, fieldMap, buffer);
				break;

			case PT_HEX:
				result = Integer.valueOf(getParamString(id, fieldMap, buffer), 16);
				break;

			case PT_HEX_S8:
				tmp = Integer.valueOf(getParamString(id, fieldMap, buffer), 16).intValue();
				result = Integer.valueOf((tmp & 0x80) == 0 ? tmp : tmp - 0x100);
				break;

			case PT_HEX_S16:
				tmp = Integer.valueOf(getParamString(id, fieldMap, buffer), 16).intValue();
				result = Integer.valueOf((tmp & 0x8000) == 0 ? tmp : tmp - 0x10000);
				break;

			case PT_HEX_ALPHA:
				result = getParamHexAlpha(id, fieldMap, buffer);
				break;

			default:
				log.severe("Invalid Parameter Type" + String.valueOf(currParam[ID_TYPE]));
				break;
		}
		return (result);
	}

	/**
	 * return value of message parameter as
	 * Object with corresponding data type
	 *
	 * @return parameter value as Object
	 */
	public Object getParamValue(int id, char[] buffer)
	{
		return (getParamValue(id, getTelegramParams(), buffer));
	}

	/**
	 * return type of telegram parameter
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @return Class type of message parameter
	 */
	@SuppressWarnings("rawtypes")
	public Class getParamType(int id, int[][] fieldMap)
	{
		Class result = null;
		int currParam[] = fieldMap[id];

		switch (currParam[ID_TYPE])
		{
			case PT_ALPHA:
			case PT_HEX_ALPHA:
				result = String.class;
				break;

			case PT_NUMERIC:
				result = Long.class;
				break;

			case PT_FLOAT:
				result = Double.class;
				break;

			case PT_TIMESTAMP:
			case PT_DATESTAMP:
				result = Date.class;
				break;

			case PT_INTEGER:
			case PT_BCD:
			case PT_HEX:
			case PT_HEX_S8:
			case PT_HEX_S16:
				result = Integer.class;
				break;

			default:
				log.severe("Invalid Parameter Type" + String.valueOf(currParam[ID_TYPE]));
				break;
		}
		return (result);
	}

	/**
	 * return type of telegram parameter
	 *
	 * @param id = ID of telegram parameter
	 * @return Class type of message parameter
	 */
	@SuppressWarnings("rawtypes")
	public Class getParamType(int id)
	{
		return (getParamType(id, getTelegramParams()));
	}

	/**
	 * set telegram parameter <pre>id</pre> to content of string
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   = current telegram buffer
	 * @param start    = start offset
	 * @param value    = String value of new parameter
	 * @return curent telegram buffer
	 */
	public static char[] setParamString(int id, int[][] fieldMap, char[] buffer, int start, String value)
	{
		// find out desired parameter
		int currParam[] = fieldMap[id];
		char valueChars[] = value.toCharArray();

		// set parameter within buffer
		for (int cnt = 0; cnt < currParam[ID_LEN]; cnt++)
		{
			// depending on position within parameter ...
			if (cnt < start)
			// ... pad parameter with leading spaces
			{
				buffer[currParam[ID_START] + cnt] = (char) paddingChr;
			} else if (cnt - start < valueChars.length)
			// ... set parameter char
			{
				buffer[currParam[ID_START] + cnt] = (char) valueChars[cnt - start];
			} else
			// ... set terinating 0's
			{
				buffer[currParam[ID_START] + cnt] = (char) paddingChr;
			}
		}

		// and return the buffer again
		return (buffer);
	}

	/**
	 * set telegram parameter <pre>id</pre> to content of string
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer = current telegram buffer
	 * @param start  = start offset
	 * @param value  = String value of new parameter
	 * @return curent telegram buffer
	 */
	public char[] setParamString(int id, char[] buffer, int start, String value)
	{
		return (setParamString(id, getTelegramParams(), buffer, start, value));
	}

	/**
	 * set telegram parameter <pre>id</pre> to content of string
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer = current telegram buffer
	 * @param value  = String value of new parameter
	 * @return curent telegram buffer
	 */
	public char[] setParamString(int id, char[] buffer, String value)
	{
		return (setParamString(id, buffer, 0, value));
	}

	/**
	 * set telegram parameter <pre>id</pre> as HexDump to content of string
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   = current telegram buffer
	 * @param start    = start offset
	 * @param value    = String value of new parameter
	 * @return curent telegram buffer
	 */
	public static char[] setParamHexAlpha(int id, int[][] fieldMap, char[] buffer, int start, String value)
	{
		String hexStr = "";
		char valChars[] = value.toCharArray();
		// create hex format of string
		for (int i = 0; i < value.length(); i++)
		{
			hexStr += String.format("%02X", valChars[i]);
		}
		// .. and set it as parameter
		return (setParamString(id, fieldMap, buffer, start, hexStr));
	}

	/**
	 * set telegram parameter <pre>id</pre> to BCD content of int
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   = current telegram buffer
	 * @param value    = String value of new parameter
	 * @return curent telegram buffer
	 */
	public static char[] setParamBCD(int id, int[][] fieldMap, char[] buffer, int value)
	{
		int currParam[] = fieldMap[id];
		int ofs;
		int rest = value;

		for (ofs = currParam[ID_LEN] - 1;
		     ofs >= 0;
		     ofs--)
		{
			buffer[currParam[ID_START] + ofs] = (char) Integer.valueOf(String.valueOf(rest % 100), 16).intValue();
			rest = rest / 100;
		}
		return (buffer);
	}

	/**
	 * set telegram parameter <pre>id</pre> to BCD content of int
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer = current telegram buffer
	 * @param value  = String value of new parameter
	 * @return curent telegram buffer
	 */
	public char[] setParamBCD(int id, char[] buffer, int value)
	{
		return (setParamBCD(id, getTelegramParams(), buffer, value));
	}

	/**
	 * set telegram parameter <pre>id</pre> to Integer content of int
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   = current telegram buffer
	 * @param value    = value of new parameter
	 * @return curent telegram buffer
	 */
	public static char[] setParamInt(int id, int[][] fieldMap, char[] buffer, int value)
	{
		int currParam[] = fieldMap[id];
		int ofs;
		int rest = value;

		for (ofs = currParam[ID_LEN] - 1; ofs >= 0; ofs--)
		{
			buffer[currParam[ID_START] + ofs] = (char) (rest % 0x100);
			rest = rest / 0x100;
		}
		return (buffer);
	}

	/**
	 * set telegram parameter <pre>id</pre> to Integer content of int
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer = current telegram buffer
	 * @param value  = value of new parameter
	 * @return curent telegram buffer
	 */
	public char[] setParamInt(int id, char[] buffer, int value)
	{
		return (setParamInt(id, getTelegramParams(), buffer, value));
	}

	/**
	 * set telegram parameter <pre>id</pre> to content of object
	 *
	 * @param id       = ID of telegram parameter
	 * @param fieldMap map of field parameters
	 * @param buffer   = current telegram buffer
	 * @param value    = String value of new parameter
	 * @return curent telegram buffer
	 */
	public static char[] setParamValue(int id, int[][] fieldMap, char[] buffer, Object value)
	{
		String parStr;
		char result[] = {};
		int currParam[] = fieldMap[id];

		// set parameter format based on it's type
		switch (currParam[ID_TYPE])
		{
			case PT_ALPHA:
				// string parameters are set left justified with trailing zeros
				result = setParamString(id, fieldMap, buffer, 0, value.toString());
				break;
			case PT_HEX_ALPHA:
				// string parameters are set left justified with trailing zeros
				result = setParamHexAlpha(id, fieldMap, buffer, 0, value.toString());
				break;
			case PT_NUMERIC:
			case PT_FLOAT:
				// numeric parameters are right justified padded with leadingspaces
				parStr = decimalFormat.format(value);
				result = setParamString(id, fieldMap, buffer, currParam[ID_LEN] - parStr.length(), parStr);
				break;
			case PT_HEX:
			case PT_HEX_S16:
			case PT_HEX_S8:
				// numeric parameters are right justified padded with leading zeros
				parStr = Integer.toHexString(((Integer) value).intValue()).toUpperCase();
				result = setParamString(id, fieldMap, buffer, currParam[ID_LEN] - parStr.length(), parStr);
				break;
			case PT_TIMESTAMP:
				// timestamp parameters are set left justified with trailing zeros
				result = setParamString(id, fieldMap, buffer, 0, TimeStampFormat.format((Date) value));
				break;
			case PT_DATESTAMP:
				// datestamp parameters are set left justified with trailing zeros
				result = setParamString(id, fieldMap, buffer, 0, DateStampFormat.format((Date) value));
				break;
			case PT_INTEGER:
				result = setParamInt(id, fieldMap, buffer, ((Integer) value).intValue());
				break;
			case PT_BCD:
				result = setParamBCD(id, fieldMap, buffer, ((Integer) value).intValue());
				break;

			default:
				log.severe("Invalid Parameter Type" + String.valueOf(currParam[ID_TYPE]));
				break;
		}
		return (result);
	}

	/**
	 * set telegram parameter <pre>id</pre> to content of object
	 *
	 * @param id     = ID of telegram parameter
	 * @param buffer = current telegram buffer
	 * @param value  = String value of new parameter
	 * @return curent telegram buffer
	 */
	public char[] setParamValue(int id, char[] buffer, Object value)
	{
		return (setParamValue(id, getTelegramParams(), buffer, value));
	}

	/**
	 * check telegram if it meets protocol requirements
	 * current implementation only performs a size check, any other check
	 * needs to be implemented in sub-class
	 *
	 * @param buffer = current telegram buffer
	 * @return true if telegram is OK, otherwise false
	 */
	public boolean checkTelegram(char[] buffer)
	{
		return (buffer.length >= (getHeaderLength() + getFooterLength()));
	}

	/**
	 * return the payload (user data) of telegram
	 *
	 * @param buffer = current telegram buffer
	 * @return buffer of payload data
	 */
	public char[] getPayLoad(char[] buffer)
	{
		String result = "";
		if (checkTelegram(buffer))
		{
			result = new String(buffer,
				getHeaderLength(),
				buffer.length - getHeaderLength() - getFooterLength());
		}
		return (result.toCharArray());
	}

	/**
	 * return String from HEX-Dump
	 *
	 * @param msgStr HEX-Dump of string
	 * @return ASCII string of hex dump
	 */
	public static String hexStrToAlphaStr(String msgStr)
	{
		StringBuffer result = new StringBuffer();

		for (int ofs = 0;
		     ofs < msgStr.length();
		     ofs += 2)
		{
			result.append(String.format("%c", Integer.parseInt(msgStr.substring(ofs, ofs + 2), 16)));
		}
		return (result.toString());
	}

	/**
	 * return HEX-Dump from String
	 *
	 * @param msgStr ASCII string to hex dump
	 * @return HEX-Dump of string
	 */
	public static String alphaStrToHexStr(String msgStr)
	{
		StringBuffer result = new StringBuffer();

		for (int ofs = 0;
		     ofs < msgStr.length();
		     ofs++)
		{
			result.append(String.format("%02x", msgStr.charAt(ofs)));
		}
		return (result.toString());
	}

	/**
	 * dump message parameters of message buffer
	 *
	 * @param buffer = current telegram buffer
	 */
	public void dumpParameters(char[] buffer)
	{
		if (checkTelegram(buffer))
		{
			for (int cnt = 0; cnt < getTelegramParams().length; cnt++)
			{
				try
				{
					log.fine(this.toString() + " " + getParamDescriptors()[cnt] + ": " + getParamInt(cnt, buffer));
				} catch (Exception e)
				{
					// we don't want to do anything here ...
				}
			}
			log.fine(this.toString() + " Payload : " + ProtUtils.hexDumpBuffer(getPayLoad(buffer)));
		} else
		{
			log.severe(this.toString() + " Invalid Telegram: '" + new String(buffer) + "' " + String.valueOf(buffer.length - getHeaderLength()));
		}
	}

	/**
	 * Implementation of TelegramListener
	 */
	/**
	 * handle incoming protocol telegram
	 * default implementaion only checks telegram and notifies listeners with
	 * protocol payload
	 *
	 * @param buffer - telegram buffer
	 * @return number of listeners notified
	 */
	public int handleTelegram(char[] buffer)
	{
		int cnt = 0;

		log.fine(this.toString() + " RX:" + ProtUtils.hexDumpBuffer(buffer));
		if (log.isLoggable(Level.FINE))
		{
			dumpParameters(buffer);
		}

		// if telegram is OK
		if (checkTelegram(buffer))
		{
			// then notify all listeners with telegram payload
			notifyTelegram(getPayLoad(buffer));
			cnt++;
		}
		return (cnt);
	}

	/**
	 * create an empy telegram header
	 *
	 * @return buffer of empty telegram header
	 */
	public static char[] createEmptyBuffer(int[][] fieldMap, char fillChar)
	{
		// create buffer
		char buffer[] = new char[getBufferLength(fieldMap)];
		// fill buffer with blanks
		java.util.Arrays.fill(buffer, (char) fillChar);
		// and return it
		return (buffer);
	}

	/**
	 * create an empy telegram header
	 *
	 * @return buffer of empty telegram header
	 */
	protected char[] createEmptyHeader()
	{
		// return newly created buffer
		return (createEmptyBuffer(getTelegramParams(), ' '));
	}

	/**
	 * create a new protocol telegram containing specified payload
	 *
	 * @param payLoad user data buffer to be packed into protocol telegram
	 * @return buffer of packet telegram
	 */
	public char[] createTelegram(char[] payLoad)
	{
		String tgmStr = new String(getNewHeader(payLoad)) + new String(payLoad) + new String(getFooter(payLoad));

		return (tgmStr.toCharArray());
	}

	/**
	 * create a new protocol telegram containing specified payload
	 *
	 * @param payLoad user data buffer to be packed into protocol telegram
	 * @return buffer of packet telegram
	 */
	public char[] createTelegram(char[] payLoad, int type, Object id)
	{
		char[] result = {};

		String tgmStr = new String(getNewHeader(payLoad, type, id)) + new String(payLoad) + new String(getFooter(payLoad));
		result = tgmStr.toCharArray();

		return result;
	}

	/**
	 * Handlers for TelegramListener List
	 */
	/**
	 * add a new listener to be notified about new telegrams
	 *
	 * @param newListener - TelegramListener to be added
	 * @return true if adding was OK, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean addTelegramListener(TelegramListener newListener)
	{
		return (TelegramListeners.add(newListener));
	}

	/**
	 * remove a listener to be notified about new telegrams
	 *
	 * @param remListener - TelegramListener to be removed
	 * @return true if adding was OK, otherwise false
	 */
	public boolean removeTelegramListener(TelegramListener remListener)
	{
		return (TelegramListeners.remove(remListener));
	}

	/**
	 * Notify all telegram listeners about new telegram
	 *
	 * @param buffer - telegram buffer
	 */
	@SuppressWarnings("rawtypes")
	protected void notifyTelegram(char[] buffer)
	{
		// if there is anything to be notified about
		if (buffer.length > 0 || isNotificationOnEmptyPayloadAllowed)
		{
			// loop through all listeners ...
			Iterator it = TelegramListeners.iterator();
			Object currListener;

			while (it.hasNext())
			{
				currListener = it.next();
				if (currListener != null && currListener instanceof TelegramListener)
				{
					// ... and tell them about it
					((TelegramListener) currListener).handleTelegram(buffer);
				}
			}
		}
	}

	/**
	 * convert hexadecimal string to array of bytes
	 * each byte is represented by 2 characters in hex string
	 *
	 * @param hexString hexadecomal string as responded by ELM adapter
	 * @return byte array representing the content of hex string
	 */
	public static char[] hexToBytes(String hexString)
	{
		char[] result = new char[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i += 2)
		{
			result[i / 2] = (char) Integer.parseInt(hexString.substring(i, i + 2), 16);
		}
		return (result);
	}

	/**
	 *
	 * Implementation of Interface TelegramWriter
	 *
	 */
	/**
	 * handle outgoing protocol telegram
	 *
	 * @param buffer - telegram buffer
	 * @return number of bytes sent
	 */
	public int writeTelegram(char[] buffer)
	{
		sendTelegram(createTelegram(buffer));
		return (buffer.length);
	}

	/**
	 * handle outgoing protocol telegram
	 *
	 * @param buffer - telegram buffer
	 * @return number of bytes sent
	 */
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		sendTelegram(createTelegram(buffer, type, id));
		return (buffer.length);
	}


	/** Utility field used by event firing mechanism. */
	private Vector<PropertyChangeListener> listenerList = null;

	/**
	 * Registers PropertyChangeListener to receive events.
	 *
	 * @param listener The listener to register.
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (listenerList == null)
		{
			listenerList = new Vector<PropertyChangeListener>();
		}
		listenerList.add(listener);
	}

	/**
	 * Removes PropertyChangeListener from the list of listeners.
	 *
	 * @param listener The listener to remove.
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
	{

		listenerList.remove(listener);
	}

	/**
	 * Notifies all registered listeners about the event.
	 *
	 * @param event The event to be fired
	 */
	protected void firePropertyChange(PropertyChangeEvent event)
	{

		if (listenerList == null) return;
		PropertyChangeListener listener;
		Iterator<PropertyChangeListener> it = listenerList.iterator();
		while (it.hasNext())
		{
			listener = it.next();
			listener.propertyChange(event);
		}
	}

}
