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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
			String message = "";

			@Override
			public void onRunError(Exception e)
			{
				Log.d(TAG, "Runner stopped.");
				connectionLost();
			}

			@Override
			public void onNewData(final byte[] data)
			{
				for(byte chr : data)
				{
					Log.d(TAG, String.format("RX: %02X : %1c", chr, chr < 32 ? '.' : chr));

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
							elm.handleTelegram(Arrays.toString(data).toCharArray());
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
	}

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

				// Send the name of the connected device back to the UI Activity
				Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
				Bundle bundle = new Bundle();
				bundle.putString(MainActivity.DEVICE_NAME, sPort.toString());
				msg.setData(bundle);
				mHandler.sendMessage(msg);

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
		String tgm = Arrays.toString(buffer) + "\n";
		write(tgm.getBytes());
		return buffer.length;
	}

	@Override
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		return writeTelegram(buffer);
	}
}
