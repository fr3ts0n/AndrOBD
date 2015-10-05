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
import android.util.Log;

import com.fr3ts0n.ecu.prot.ElmProt;
import com.fr3ts0n.prot.TelegramWriter;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * USB device communication service
 */
public class UsbCommService extends CommService
	implements TelegramWriter
{
	private static UsbSerialPort sPort = null;
	private SerialInputOutputManager mSerialIoManager;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private final SerialInputOutputManager.Listener mListener =
		new SerialInputOutputManager.Listener()
		{

			@Override
			public void onRunError(Exception e)
			{
				Log.d(TAG, "Runner stopped.");
				connectionLost();
			}

			@Override
			public void onNewData(final byte[] data)
			{
				elm.handleTelegram(Arrays.toString(data).toCharArray());
			}
		};

	public UsbCommService(Context context, Handler handler, UsbSerialPort port)
	{
		super(context, handler);
		elm.addTelegramWriter(this);
		setDevice(port);
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
		setDevice((UsbSerialPort)device);
		start();
	}

	@Override
	public void start()
	{
		if (sPort != null)
		{
			Log.i(TAG, "Starting io manager ..");
			mExecutor.submit(mSerialIoManager);

			final UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

			UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
			if (connection == null)
			{
				return;
			}

			try
			{
				sPort.open(connection);
				sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
				setState(STATE_CONNECTED);
				// send RESET to Elm adapter
				elm.sendCommand(ElmProt.CMD.RESET, 0);
			}
			catch (IOException e)
			{
				Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				try
				{
					sPort.close();
				}
				catch (IOException e2)
				{
					// Ignore.
				}
				connectionLost();
				sPort = null;
			}
		}
	}

	@Override
	public void stop()
	{
		if (mSerialIoManager != null)
		{
			Log.i(TAG, "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
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
		mSerialIoManager.writeAsync(out);
	}

	@Override
	public int writeTelegram(char[] buffer)
	{
		write(Arrays.toString(buffer).getBytes());
		return buffer.length;
	}

	@Override
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		return writeTelegram(buffer);
	}
}
