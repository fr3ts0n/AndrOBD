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

import java.util.Iterator;
import java.util.Vector;

/**
 * TelegramSender
 * list of telegram writers
 * (handlers for outgoing telegrams)
 *
 * @author $Author: erwin $
 * @version $Id: TelegramSender.java,v 1.12 2010-02-23 21:36:27 erwin Exp $
 */
public class TelegramSender
{
	/**
	 * Handlers for TelegramWriter List
	 */

	@SuppressWarnings("rawtypes")
	private final
	Vector telegramWriters = new Vector();

	/**
	 * add a new Writer to be notified about new telegrams
	 *
	 * @param newWriter - TelegramWriter to be added
	 * @return true if adding was OK, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean addTelegramWriter(TelegramWriter newWriter)
	{
		return (telegramWriters.add(newWriter));
	}

	/**
	 * remove a Writer to be notified about new telegrams
	 *
	 * @param remWriter - TelegramWriter to be removed
	 * @return true if adding was OK, otherwise false
	 */
	public boolean removeTelegramWriter(TelegramWriter remWriter)
	{
		return (telegramWriters.remove(remWriter));
	}

	/**
	 * Notify all telegram Writers about new telegram
	 *
	 * @param buffer - telegram buffer
	 */
	@SuppressWarnings("rawtypes")
	private void sendTelegram(char[] buffer, int type, Object id)
	{
		Iterator it = telegramWriters.iterator();
		Object currWriter;

		ProtoHeader.log.finer(this.toString() + " TX:" + ProtUtils.hexDumpBuffer(buffer));

		while (it.hasNext())
		{
			currWriter = it.next();
			if (currWriter instanceof TelegramWriter)
			{
				((TelegramWriter) currWriter).writeTelegram(buffer, type, id);
			}
		}
	}

	/**
	 * Notify all telegram Writers about new telegram
	 *
	 * @param buffer - telegram buffer
	 */
	public void sendTelegram(char[] buffer)
	{
		sendTelegram(buffer, 0, null);
	}

}
