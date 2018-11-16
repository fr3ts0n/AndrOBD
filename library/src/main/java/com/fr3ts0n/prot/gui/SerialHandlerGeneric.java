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

package com.fr3ts0n.prot.gui;

import com.fr3ts0n.prot.SerialExt;
import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author erwin
 */
public class SerialHandlerGeneric extends Thread
	implements TelegramWriter
{
	/** the serial device */
	String deviceName = "/dev/ttyS0";
	/** current protocol status */
	private ProtStatus protStat = ProtStatus.UNKNOWN;
	/** file descriptor of serial device */
	private long serialDeviceDescriptor = -1;
	// the logger object
	static Logger log = Logger.getLogger("com.fr3ts0n.prot.ser");

	/**
	 * Status of protocol handler - to determine if we currently send / receive
	 */
	public enum ProtStatus
	{
		UNKNOWN,
		SETUP,
		INITIALIZED,
		CONNECTING,
		SENDING,
		RECEIVING,
		OFFLINE,
		TIMEOUT,
		ERROR,
	}

	/** Colors for each protocol status */
	public static final Color[] statColor =
		{
			null,
			Color.YELLOW,
			null,
			Color.MAGENTA,
			Color.GREEN,
			Color.GREEN,
			null,
			null,
			Color.RED,
		};

	/*
	   FileInputStream rdr;
	   FileOutputStream wrtr;
	   */
	private RandomAccessFile rdr;
	private RandomAccessFile wrtr;
	TelegramListener messageHandler;

	// current receive message
	String message = "";

	/**
	 * Creates a new instance of SerialHandler
	 */
	SerialHandlerGeneric()
	{
		// setDeviceName(deviceName);
	}

	/**
	 * Creates a new instance of SerialHandler
	 *
	 * @param device device name
	 */
	public SerialHandlerGeneric(String device)
	{
		try
		{
			setDeviceName(device);
		} catch (IOException ex)
		{
			log.log(Level.SEVERE,"SerialHandlerGeneric", ex);
		}
	}

	/**
	 * @return the protStat
	 */
	ProtStatus getProtStat()
	{
		return protStat;
	}

	/**
	 * @param protStat the protStat to set
	 */
	void setProtStat(ProtStatus protStat)
	{
		firePropertyChange(new PropertyChangeEvent(this,
			"status",
			this.protStat,
			protStat));
		this.protStat = protStat;
	}

	/**
	 * return device/file name
	 *
	 * @return device name
	 */
	public String getDeviceName()
	{
		return (deviceName);
	}

	/**
	 * close current connections
	 */
	void close()
	{
		try
		{
			if (wrtr != null)
			{
				wrtr.close();
				wrtr = null;
			}
			if (rdr != null)
			{
				rdr.close();
				rdr = null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Setter for property deviceName.
	 *
	 * @param deviceName New value of property deviceName.
	 * @throws IOException
	 */
	public void setDeviceName(java.lang.String deviceName)
		throws IOException
	{
		// set up serial line ...
		close();
		// set new device name
		this.deviceName = deviceName;
		// get system file descriptor
		// serialDeviceDescriptor = SerialExt.setPortName(deviceName);
		// open java side file
		wrtr = new RandomAccessFile(deviceName, "rw");
		rdr = wrtr;
		// get system file descriptor
		serialDeviceDescriptor = getSystemFD();
		// and set it in serial extender
		SerialExt.serialPortDescriptor = (int) serialDeviceDescriptor;
	}

	/**
	 * get the numeric system file descriptor/handle
	 *
	 * @return numeric system file descriptor/handle
	 */
	private long getSystemFD()
	{
		long result = -1;
		Field dscrField;
		FileDescriptor dscr;
		try
		{
			// get java file descriptor
			dscr = wrtr.getFD();
			try
			{
				// try to get windows-specific field "handle"
				dscrField = FileDescriptor.class.getDeclaredField("handle");
			} catch (NoSuchFieldException ex)
			{
				// we are not windows... get field "fd"
				dscrField = FileDescriptor.class.getDeclaredField("fd");
			}
			// make field accessible
			dscrField.setAccessible(true);
			// and read it's value
			result = dscrField.getLong(dscr);
		} catch (Exception ex)
		{
			log.log(Level.SEVERE,"getFD", ex);
		}
		return result;
	}

	/**
	 * start the thread
	 */
	@Override
	@SuppressWarnings("fallthrough")
	public void run()
	{
		int chr;
		try
		{
			while ((chr = rdr.read()) >= 0)
			{
				switch (chr)
				{
					// ignore special characters
					case 13:
					case 32:
						break;

					// trigger message handling for new request
					case '>':
						message += (char) chr;
						// trigger message handling
					case 10:
						if (messageHandler != null)
							messageHandler.handleTelegram(message.toCharArray());
						message = "";
						break;

					default:
						message += (char) chr;
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * write a telegram to serial port
	 *
	 * @param buffer
	 * @return result of write operation
	 */
	public int writeTelegram(char[] buffer)
	{
		return (writeTelegram(buffer, 0, null));
	}

	/**
	 * write a telegram to serial port
	 *
	 * @param buffer message buffer to write
	 * @param type   message type
	 * @param id     message id
	 * @return result of write operation
	 */
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		int result = 0;
		try
		{
			String msg = new String(buffer);
			msg += "\n";
			wrtr.write(msg.getBytes());
			result = buffer.length;
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return (result);
	}

	/**
	 * configure serial handler
	 * this method informs about generic serial ports need to be
	 * configured by the operating system (via startup script)
	 */
	public void configure()
	{
		log.info("Generic Serial ports have to be configured by the\n"
			+ "operating system (via startup script)");
	}

	/**
	 * Getter for property messageHandler.
	 *
	 * @return Value of property messageHandler.
	 */
	public TelegramListener getMessageHandler()
	{
		return messageHandler;
	}

	/**
	 * Setter for property messageHandler.
	 *
	 * @param messageHandler New value of property messageHandler.
	 */
	public void setMessageHandler(TelegramListener messageHandler)
	{
		this.messageHandler = messageHandler;
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
	void firePropertyChange(PropertyChangeEvent event)
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
