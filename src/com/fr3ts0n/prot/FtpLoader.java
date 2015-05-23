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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Threaded FTP Loader class to be used for threaded reading of an FTP stream
 *
 * @author $Author: erwin $
 * @version $Id: FtpLoader.java,v 1.7 2009-11-21 21:33:15 erwin Exp $
 */
public class FtpLoader extends URLConnection
	implements Runnable
{
	/** reader object to read from stream */
	BufferedReader rdr = null;

	/** List of telegram listeners */
	@SuppressWarnings("rawtypes")
	protected Vector TelegramListeners = new Vector();

	/**
	 * class constructor
	 */
	public FtpLoader(String newUrl)
		throws MalformedURLException
	{
		super(new URL(newUrl));
	}

	/**
	 * connect to URL and start threaded loading
	 */
	public void connect()
		throws IOException
	{
		new Thread(this).start();
	}

	/**
	 * load data from specifired URL
	 */
	public void run()
	{
		String currLine = null;
		try
		{
			rdr = new BufferedReader(new InputStreamReader(url.openStream()));

			while ((currLine = rdr.readLine()) != null)
			{
				notifyTelegram(currLine.toCharArray());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
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
	protected boolean addTelegramListener(TelegramListener newListener)
	{
		return (TelegramListeners.add(newListener));
	}

	/**
	 * remove a listener to be notified about new telegrams
	 *
	 * @param remListener - TelegramListener to be removed
	 * @return true if adding was OK, otherwise false
	 */
	protected boolean removeTelegramListener(TelegramListener remListener)
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
		Iterator it = TelegramListeners.iterator();
		Object currListener;

		while (it.hasNext())
		{
			currListener = it.next();
			if (currListener != null
				&& currListener instanceof TelegramListener)
			{
				((TelegramListener) currListener).handleTelegram(buffer);
			}
		}
	}

	/**
	 * The Main routine for testing purpose
	 */
	public static void main(String args[])
	{
		try
		{
			FtpLoader loader = new FtpLoader(args[0]);
			loader.connect();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
