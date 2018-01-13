/*
 * (C) Copyright 2016 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
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
 *
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.content.Context;
import android.os.Handler;

import com.fr3ts0n.prot.StreamHandler;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Network communication service to allow connection to WIFI OBD adapters
 * Created by fr3ts0n on 17.04.16.
 */
public class NetworkCommService
	extends CommService
	implements Runnable
{
	public Socket mSocket;
	/** communication stream handler */
	public StreamHandler ser = new StreamHandler();;
	public Thread serThread;

	/** default constructor */
	public NetworkCommService()
	{
		super();
	}

	/**
	 * Constructor. Prepares a new Network Communication session.
	 *
	 * @param context The UI Activity Context
	 * @param handler A Handler to send messages back to the UI Activity
	 */
	public NetworkCommService(Context context, Handler handler)
	{
		super(context, handler);
		ser.setMessageHandler(elm);
	}

	@Override
	public void start()
	{
		log.fine("start");
		// set up protocol handlers
		elm.addTelegramWriter(ser);
		// create communication thread
		serThread = new Thread(this);
		serThread.start();
	}

	@Override
	public void stop()
	{
		log.fine("stop");
		elm.removeTelegramWriter(ser);
		// close socket
		try
		{
			mSocket.close();
		} catch (Exception e)
		{
			log.severe(e.getMessage());
		}
		setState(STATE.OFFLINE);
	}

	@Override
	public void write(byte[] out)
	{
		// forward message to stream handler
		ser.writeTelegram(new String(out).toCharArray());
	}

	@Override
	public void run()
	{
		// run communication loop
		ser.run();
		// loop was finished -> we have lost connection
		connectionLost();
	}

	/**
	 * Thread for connecting network device
	 * * required to eliminate android.os.NetworkOnMainThreadException
	 */
	protected class ConnectThread extends Thread
	{
		CommService svc;
		String device;
		int portNum = 23;

		public ConnectThread(CommService svc, String device, int portNum)
		{
			this.svc = svc;
			this.device = device;
			this.portNum  = portNum;
		}

		@Override
		public void run()
		{
			log.info(String.format("Connecting to %s port %d", device, portNum));
			setState(STATE.CONNECTING);
			try
			{
				// create socket connection
				mSocket = new Socket();
				InetSocketAddress addr = new InetSocketAddress(device, portNum);
				mSocket.connect(addr);
				// set streams for stream handler
				ser.setStreams(mSocket.getInputStream(), mSocket.getOutputStream());
				// we are connected -> signal connection established
				connectionEstablished(device);

				// start communication service thread
				svc.start();
			} catch (Exception e)
			{
				log.severe(e.getMessage());
				connectionFailed();
			}
		}
	}

	/**
	 * Open network connection to device using specified port
	 * @param device address of device
	 * @param portNum port to connect to
	 */
	public void connect(Object device, int portNum)
	{
		new ConnectThread(this, String.valueOf(device), portNum).start();
	}

	@Override
	public void connect(Object device, boolean secure)
	{
		connect(device, secure ? 23 : 22);
	}
}
