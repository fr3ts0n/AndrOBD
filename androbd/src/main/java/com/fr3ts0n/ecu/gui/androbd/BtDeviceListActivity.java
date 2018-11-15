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

package com.fr3ts0n.ecu.gui.androbd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BtDeviceListActivity extends Activity
{
	// Debugging
	private static final String TAG = BtDeviceListActivity.class.getSimpleName();
	private static final Logger log = Logger.getLogger(TAG);
	
	// Return Intent extra
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	private BluetoothAdapter mBtAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);
		// Setup the window
		setContentView(R.layout.device_list);
		
		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		ArrayAdapter<String> mPairedDevicesArrayAdapter =
			new ArrayAdapter<>(this, R.layout.device_name);
		
		// Find and set up the ListView for paired devices
		ListView pairedListView = findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);

		if(mBtAdapter == null || !mBtAdapter.isEnabled())
		{
			String noDevices = getResources().getText(R.string.none_paired).toString();
			mPairedDevicesArrayAdapter.add(noDevices);
			
			return;
		}
		
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0)
		{
			// set up list selection handlers
			pairedListView.setOnItemClickListener(mDeviceClickListener);
			for (BluetoothDevice device : pairedDevices)
			{
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else
		{
			String noDevices = getResources().getText(R.string.none_paired).toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}
	}
	
	// The on-click listener for all devices in the ListViews
	private final OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
		{
			// Cancel discovery because it's costly and we're about to connect
			mBtAdapter.cancelDiscovery();

			// Get the device MAC address, which is the last 17 chars in the View
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);
			//String address = "00:0D:18:A0:4E:35"; //FORCE OBD MAC Address
			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
			log.log(Level.FINE, "Sending Result...");
			finish();
		}
	};
}
