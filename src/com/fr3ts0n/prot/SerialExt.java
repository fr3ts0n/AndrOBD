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

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Extension to native serial selectedPort to allow odd baud rates and 5-Baud operations
 * especially for automotive usage
 *
 * @author erwin
 */
public class SerialExt
{

	static Logger log = Logger.getLogger("com.fr3ts0n.prot.ext");


	/**
	 * Open serial device
	 *
	 * @param fileName file name of serial device (i.e.: \\.\COMx, /dev/ttySx ...)
	 * @return file descriptor of file device, or -1 if not available
	 */
	private static native int openSerial(String fileName);

	/**
	 * Close serial device
	 *
	 * @param fd file descriptor of selectedPort
	 * @return error status of system function (0=success)
	 */
	private static native int closeSerial(int fd);

	/**
	 * native interface to
	 * set status of BREAK signal
	 * This is used to create a 5-baud signal for ECU kick-off
	 *
	 * @param fd        file descriptor of selectedPort
	 * @param brkStatus desired status of BREAK signal
	 * @return error status of system function (0=success)
	 */
	private static native int setBreak(int fd, int brkStatus);

	/**
	 * native interface to
	 * set custom baud rate
	 * Custom baud rates are set by dividing the selectedPort baud base frequency with a
	 * divisor value. Purpose of this function is to allow odd ball ECU baud rates
	 * like 10400 baud etc., which are not supported by regular system calls
	 *
	 * @param fd       file descriptor of selectedPort
	 * @param baudRate cusom baud rate to be set
	 * @return error status of system function (0=success)
	 */
	private static native int setCustomBaudrate(int fd, int baudrate);

	/**
	 * native interface to
	 * return custom baud rate setting
	 * Custom baud rates are set by dividing the selectedPort baud base frequency with a
	 * divisor value. Purpose of this function is to allow odd ball ECU baud rates
	 * like 10400 baud etc., which are not supported by regular system calls
	 *
	 * @param fd file descriptor of selectedPort
	 * @return custom baud rate setting, or 0 if no custom baud rate was set
	 */
	private static native int getCustomBaudrate(int fd);

	/**
	 * receive a character from COM selectedPort
	 *
	 * @param fd file descriptor of selectedPort
	 * @return received character or -1 on receive error
	 */
	private static native int receiveChar(int fd);

	/**
	 * send a character from COM selectedPort
	 *
	 * @param fd     file descriptor of selectedPort
	 * @param txChar character to be sent
	 * @return error status of system function (0=success)
	 */
	private static native int sendChar(int fd, char txChar);

	/** file descriptor of serial selectedPort */
	public static int serialPortDescriptor = -1;

	static
	{
		System.loadLibrary("SerialExt");
	}

	/**
	 * Open serial device
	 *
	 * @param fileName file name of serial device (i.e.: \\.\COMx, /dev/ttySx ...)
	 * @return file descriptor of file device, or -1 if not available
	 * @throws IOException
	 */
	public static int open(String fileName)
		throws IOException
	{
		int result = openSerial(fileName);
		if (result < 0)
		{
			throw new IOException("open");
		}
		serialPortDescriptor = result;
		return result;
	}

	/**
	 * Close serial device
	 *
	 * @return error status of serial operation (0=SUCCESS)
	 * @throws IOException
	 */
	public static int close()
		throws IOException
	{
		int result = closeSerial(serialPortDescriptor);
		serialPortDescriptor = -1;
		if (result != 0)
		{
			throw new IOException("close");
		}
		return result;
	}

	/**
	 * Set line BREAK status to desired state
	 *
	 * @param newStatus desired BREAK status
	 * @return error status of selectedPort/line setting (0=SUCCESS)
	 * @throws IOException
	 */
	public static int setBreak(int newStatus)
		throws IOException
	{
		log.fine(String.format("BREAK %s", newStatus != 0 ? "ON" : "OFF"));
		int result = setBreak(serialPortDescriptor, newStatus);
		if (result != 0)
		{
			throw new IOException("setBreak");
		}
		return result;
	}

	/**
	 * Set custom baud rate on specified SerialPort
	 *
	 * @param baudrate new baud rate in bps
	 * @return error status of selectedPort/line setting (0=SUCCESS)
	 * @throws IOException
	 */
	public static int setCustomBaudrate(int baudrate)
		throws IOException
	{
		log.fine(String.format("Setting custom baudrate %d", baudrate));
		int result = setCustomBaudrate(serialPortDescriptor, baudrate);
		if (result != 0)
		{
			throw new IOException("setCustomBaudrate");
		}
		return result;
	}

	/**
	 * Set custom baud rate on specified SerialPort
	 *
	 * @return error status of selectedPort/line setting (0=SUCCESS)
	 * @throws IOException
	 */
	public static int getCustomBaudrate()
		throws IOException
	{
		int result = getCustomBaudrate(serialPortDescriptor);
		log.fine(String.format("getting custom baudrate %d", result));
		if (result <= 0)
		{
			throw new IOException("getCustomBaudrate");
		}
		return result;
	}

	/**
	 * receive a character from COM selectedPort
	 *
	 * @return received character or -1 on receive error
	 */
	public static int receiveChar()
		throws IOException
	{
		log.finer(String.format("waiting for RX char"));
		int result = receiveChar(serialPortDescriptor);
		if (result < 0)
		{
			throw new IOException(String.format("receiveChar:%02X", result));
		}
		return result;
	}

	/**
	 * send a character to COM selectedPort
	 *
	 * @param txChar character to be sent
	 * @return received character or -1 on send error
	 */
	public static int sendChar(byte txChar)
		throws IOException
	{
		log.finer(String.format("sending char:%02X", (byte) txChar));
		int result = sendChar(serialPortDescriptor, (char) txChar);
		if (result < 0)
		{
			throw new IOException(String.format("sendChar:%d", result));
		}
		return result;
	}

	/**
	 * send characters to COM selectedPort
	 *
	 * @param txBytes characters to be sent
	 * @return received character or -1 on send error
	 */
	public static int write(byte[] txBytes)
		throws IOException
	{
		int result = 0;
		for (int i = 0; i < txBytes.length; i++)
			result |= sendChar(txBytes[i]);
		return result;
	}

}
