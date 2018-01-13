/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundationpe; either version 2 of
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
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Protocol I/O stream handler
 *
 * @author erwin
 */
public class StreamHandler implements TelegramWriter, Runnable
{
	private static final Logger log = Logger.getLogger("stream");
	private InputStream    in;
	private BufferedWriter out;

	private TelegramListener messageHandler;
	// current receive message
	private String message = "";

	public StreamHandler()
	{
	}

	/**
	 * Construct new StreamHandler using specified input and output stream
	 *
	 * @param inStream  stream for incoming messages
	 * @param outStream stream for outgoing messages
	 */
	public StreamHandler(InputStream inStream, OutputStream outStream)
	{
		setStreams(inStream, outStream);
	}

	/**
	 * Set the input and output stream
	 *
	 * @param inStream  stream for incoming messages
	 * @param outStream stream for outgoing messages
	 */
	public void setStreams(InputStream inStream, OutputStream outStream)
	{
		in = inStream;
		/* Output uses BufferedWriter with buffer size 1 byte to trigger
		   flushing on every outgoing byte [$Fix #AndrOBD-27] */
		out = new BufferedWriter(new OutputStreamWriter(outStream), 1);
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.prot.TelegramWriter#writeTelegram(char[])
	 */
	@Override
	public int writeTelegram(char[] buffer)
	{
		return (writeTelegram(buffer, 0, null));
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.prot.TelegramWriter#writeTelegram(char[], int, java.lang.Object)
	 */
	@Override
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		int result = 0;
		try
		{
			String msg = new String(buffer);
			msg += "\r";
			log.finer(this.toString() + " TX:" + ProtUtils.hexDumpBuffer(msg.toCharArray()));
			out.write(msg.toCharArray());
			out.flush();
			result = buffer.length;
		} catch (Exception ex)
		{
			log.warning("TX error:'" + ProtUtils.hexDumpBuffer(buffer) + "':" + ex);
		}
		return (result);
	}

	/**
	 * process incoming character
	 * @param chr the received char
	 */
	private void processRxChar(int chr)
	{
		// process incoming data
		log.finer(this.toString() + " RX: '"
			          + String.format("%02X : %1c", (byte) chr, chr < 32 ? '.' : chr)
			          + "'");

		switch (chr)
		{
			// ignore special characters
			case 32:
				break;

			// trigger message handling for new request
			case '>':
				message += (char) chr;
				// trigger message handling
			case 10:
			case 13:
				if (messageHandler != null && !message.isEmpty())
					messageHandler.handleTelegram(message.toCharArray());
				message = "";
				break;

			default:
				message += (char) chr;
		}
	}

	/**
	 * start the thread
	 */
	@Override
	@SuppressWarnings("fallthrough")
	public void run()
	{
		int chr;
		log.info("RX Thread started");
		try
		{
			// loop until stream closed / invalid
			while (true)
			{
				// if no data available, then wait for it
				if (in.available() > 0)
				{
					// otherwise read- and process ...

					// Is end of stream reached?
					if ((chr = in.read()) > 0)
					{
						// process incoming data
						processRxChar(chr);
					}
					else
					{
						log.warning(this.toString() + " RX: End of stream!");
						// stream finished - break loop
						break;
					}
				}
				else
				{
					// wait 1 ms for incoming data
					Thread.sleep(1);
				}

			}
		}
		catch (	Exception ex )
		{
			log.log(Level.WARNING, "RX error", ex);
		}

		log.info("RX Thread stopped");
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
	protected void firePropertyChange(PropertyChangeEvent event)
	{

		if (listenerList == null) return;
		PropertyChangeListener listener;
		for (PropertyChangeListener aListenerList : listenerList)
		{
			listener = aListenerList;
			listener.propertyChange(event);
		}
	}

}
