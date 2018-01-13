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
 *
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.TelegramWriter;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.logging.Level;

/**
 * USB device communication service
 */
public class UsbCommService extends CommService
	implements TelegramWriter
{
	private static UsbSerialPort sPort = null;
	private SerialInputOutputManager mSerialIoManager;

	private final SerialInputOutputManager.Listener mListener =
		new SerialInputOutputManager.Listener()
		{
			String message = "";

			@Override
			public void onRunError(Exception e)
			{
				log.log(Level.SEVERE, "onRunError: ", e);
				connectionLost();
			}

			@Override
			public void onNewData(final byte[] data)
			{
				log.finer("RX: " +
					          ProtUtils.hexDumpBuffer(new String(data).toCharArray()));
				for(byte chr : data)
				{
					switch (chr)
					{
						// ignore special characters
						case 0:	case 1:	case 2: case 3: case 4:
						case 5:	case 6: case 7: case 8: case 9:
						case 32:
							break;

						// trigger message handling for new request
						case '>':
							message += (char) chr;
							// trigger message handling
						case 10:
						case 13:
							if(message.length() > 0)
								elm.handleTelegram(message.toCharArray());
							message = "";
							break;

						default:
							message += (char) chr;
					}
				}
			}
		};

	public UsbCommService(Context context, Handler handler)
	{
		super(context, handler);
		elm.addTelegramWriter(this);
	}

	/**
	 * Set USB serial port device
	 * @param port serial port to be set
	 */
	protected void setDevice(UsbSerialPort port)
	{
		sPort = port;
		mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
	}

	@Override
	public void connect(Object device, boolean secure)
	{
		setState(STATE.CONNECTING);
		setDevice((UsbSerialPort)device);
		start();
	}

	@Override
	public void start()
	{
		if (sPort != null)
		{
			final UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
			UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
			if (connection == null)
			{
				connectionFailed();
				return;
			}

			try
			{
				sPort.open(connection);
				sPort.setDTR(true);
				sPort.setParameters(38400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

				log.info("Starting io manager ..");
				Thread runner = new Thread(mSerialIoManager);
				runner.setPriority(Thread.MAX_PRIORITY);
				runner.start();

				// we are connected -> signal connectionEstablished
				connectionEstablished(sPort.toString());
			}
			catch (IOException e)
			{
				log.log(Level.SEVERE, "Error setting up device: " + e.getMessage(), e);
				try
				{
					sPort.close();
				}
				catch (IOException e2)
				{
					// Ignore.
				}
				connectionFailed();
				sPort = null;
			}
		}
	}

	@Override
	public void stop()
	{
		// remove this as valid telegram writer for elm protocol
		elm.removeTelegramWriter(this);

		if (mSerialIoManager != null)
		{
			log.info( "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
			connectionLost();
		}
	}

	/**
	 * TelegramWriter interface methods
	 *
	 * @param out The bytes to write
	 */
	@Override
	public void write(byte[] out)
	{
		log.finer("RX: " +
			          ProtUtils.hexDumpBuffer(new String(out).toCharArray()));
		try
		{
			mSerialIoManager.writeAsync(out);
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "TX error", ex);
			connectionLost();
		}
	}

	@Override
	public int writeTelegram(char[] buffer)
	{
		String tgm = String.valueOf(buffer) + "\r";
		write(tgm.getBytes());
		return buffer.length;
	}

	@Override
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		return writeTelegram(buffer);
	}
}
