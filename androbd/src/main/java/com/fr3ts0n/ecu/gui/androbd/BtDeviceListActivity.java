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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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
	static final String TAG = BtDeviceListActivity.class.getSimpleName();
	protected static final Logger log = Logger.getLogger(TAG);
	
	// Return Intent extra
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	protected BluetoothAdapter mBtAdapter;
	protected ArrayAdapter<BluetoothDevice> mBtDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
			}
		}

		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);
		// Setup the window
		setContentView(R.layout.device_list);
		
		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		stopDeviceScan();

		mBtDevices =
			new ArrayAdapter<BluetoothDevice>(this, R.layout.device_name){
				final LayoutInflater mInflater =
						(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				@SuppressLint("MissingPermission")
                @NonNull
				@Override
				public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
					TextView tv;
					BluetoothDevice dev = getItem(position);
					if (convertView != null) {
						tv = (TextView) convertView;
					} else {
						tv = (TextView)mInflater.inflate(R.layout.device_name,
														 parent,
											 false);
					}
					tv.setText(String.format("%s\n%s", dev.getName(), dev.getAddress()));
					return tv;
				}
			};
		
		// Find and set up the ListView for paired devices
		ListView pairedListView = findViewById(R.id.list);
		pairedListView.setAdapter(mBtDevices);
		// set up list selection handlers
		pairedListView.setOnItemClickListener(mDeviceClickListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startDeviceScan();
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopDeviceScan();
	}

	protected void addDevice(BluetoothDevice device) {
		if (mBtDevices.getPosition(device) < 0) {
			mBtDevices.add(device);
		}
	}

	@SuppressLint("MissingPermission") // permission is checked before
	protected void startDeviceScan() {
		if(mBtAdapter != null && mBtAdapter.isEnabled())
		{
			// Get a set of currently paired devices
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

			for (BluetoothDevice device : pairedDevices)
			{
				addDevice(device);
			}
		}
	}

	@SuppressLint("MissingPermission") // permission is checked before
	protected void stopDeviceScan() {
			// Cancel discovery because it's costly and we're about to connect
			mBtAdapter.cancelDiscovery();
	}

	// The on-click listener for all devices in the ListViews
	private final OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> av, View v, int position, long id)
		{
			// Get the device MAC address, which is the last 17 chars in the View
			//String address = "00:0D:18:A0:4E:35"; //FORCE OBD MAC Address
			BluetoothDevice currDev = (BluetoothDevice)av.getItemAtPosition(position);
			String address = currDev.getAddress();

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
