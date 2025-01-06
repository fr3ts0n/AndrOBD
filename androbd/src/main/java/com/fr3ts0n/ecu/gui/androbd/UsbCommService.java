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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.TelegramWriter;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * USB device communication service
 */
public class UsbCommService extends CommService
	implements TelegramWriter
{
	private static UsbSerialPort sPort = null;
	private SerialInputOutputManager mSerialIoManager;
	public static final String INTENT_ACTION_GRANT_USB = ".GRANT_USB";

	public static final String PREF_KEY_BAUDRATE = "comm_baudrate";
	public static final int DEFAULT_BAUDRATE = 38400;

	private final SerialInputOutputManager.Listener mListener =
		new SerialInputOutputManager.Listener()
		{
			String message = "";

			@Override
			public void onRunError(Exception e)
			{
				log.log(Level.SEVERE, "onRunError: ", e);
				stop();
			}

			@Override
			public void onNewData(final byte[] data)
			{
				log.finer("RX: " +ProtUtils.hexDumpBuffer(new String(data).toCharArray()));
				for(byte chr : data)
				{
					switch (chr)
					{
						// ignore special characters
						case 32:
							break;

						// trigger message handling for new request
						case '>':
							//noinspection StringConcatenationInLoop
							message += (char) chr;
							// trigger message handling
						case 10:
						case 13:
							try
							{
								if(!message.isEmpty())
								{ elm.handleTelegram(message.toCharArray()); }
							}
							catch (Exception ex)
							{
								log.log(Level.WARNING, "handleTelegram", ex);
							}
							message = "";
							break;

						default:
							//noinspection StringConcatenationInLoop
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
	private void setDevice(UsbSerialPort port)
	{
		sPort = port;
	}

	@Override
	public void connect(Object device, boolean secure)
	{
		setState(STATE.CONNECTING);
		setDevice((UsbSerialPort)device);
		start();
	}

	/**
	 * Get preference int value
	 *
	 * @param key          preference key name
	 * @param defaultValue numeric default value
	 * @return preference int value
	 */
	@SuppressLint("DefaultLocale")
	private int getPrefsInt(String key, int defaultValue)
	{
		int result = defaultValue;

		try
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			result = Integer.parseInt(Objects.requireNonNull(prefs.getString(key, String.valueOf(defaultValue))));
		}
		catch (Exception ex)
		{
			// log error message
			log.severe(String.format("Preference '%s'(%d): %s", key, result, ex.toString()));
		}

		return result;
	}

	private int getBaudRate()
	{
		return getPrefsInt(PREF_KEY_BAUDRATE, DEFAULT_BAUDRATE);
	}

	@Override
	public void start()
	{
		if (sPort != null)
		{
			// Ensure general USB access
			final UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
			if (usbManager == null)
			{
                connectionFailed();
                return;
            }
			
			// Request runtime permission to access USB serial port
			UsbDevice device = sPort.getDriver().getDevice();
			String intentStr = mContext.getPackageName() + INTENT_ACTION_GRANT_USB;
			PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(intentStr), android.app.PendingIntent.FLAG_IMMUTABLE);
			usbManager.requestPermission(device, usbPermissionIntent);
			// Ensure access toUSB port is granted ...
			if (!usbManager.hasPermission(device))
			{
				connectionFailed();
				return;
			}

			// Open USB device
            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
			if (connection == null)
			{
				connectionFailed();
				return;
			}


			try
			{
				// Open serial port
				sPort.open(connection);
				// set serial parameters
				sPort.setParameters(getBaudRate(), 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
				sPort.setDTR(true);
				sPort.setRTS(true);

				// start communication thread
				log.info("Starting io manager ..");
				// Initialize SerialIoManager AFTER opening sPort
				// workaround for AndrOBD#285 / https://github.com/mik3y/usb-serial-for-android/issues/611
				mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
				Executors.newSingleThreadExecutor().submit(mSerialIoManager);

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
		log.finer("TX: " +ProtUtils.hexDumpBuffer(new String(out).toCharArray()));
		try
		{
			sPort.write(out,0);
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
