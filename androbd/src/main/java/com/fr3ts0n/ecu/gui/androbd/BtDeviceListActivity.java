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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BtDeviceListActivity extends AppCompatActivity
{
	// Debugging
	static final String TAG = BtDeviceListActivity.class.getSimpleName();
	protected static final Logger log = Logger.getLogger(TAG);
	
	// Return Intent extra
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	protected BluetoothAdapter mBtAdapter;
	protected final List<BluetoothDevice> mDevices = new ArrayList<>();
	protected DeviceAdapter mDeviceAdapter;
	protected TextView mEmptyStateView;
	protected LinearProgressIndicator mScanProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			ArrayList<String> missingPermissions = new ArrayList<>();
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
				missingPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
			}
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				missingPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
			}
			if (!missingPermissions.isEmpty()) {
				ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), 1);
			}
		}

		// Set result CANCELED in case the user backs out
		setResult(RESULT_CANCELED);
		// Setup the window
		setContentView(R.layout.device_list);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		stopDeviceScan();

		// Find and set up the RecyclerView for paired devices
		RecyclerView pairedListView = findViewById(R.id.list);
		pairedListView.setLayoutManager(new LinearLayoutManager(this));
		mDeviceAdapter = new DeviceAdapter();
		pairedListView.setAdapter(mDeviceAdapter);

		mEmptyStateView = findViewById(R.id.empty_state);
		mEmptyStateView.setText(R.string.no_paired_devices);
		mScanProgress = findViewById(R.id.scan_progress);

		updateEmptyState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mScanProgress.setVisibility(View.VISIBLE);
		startDeviceScan();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mScanProgress.setVisibility(View.GONE);
		stopDeviceScan();
	}

	protected void addDevice(BluetoothDevice device) {
		if (!mDevices.contains(device)) {
			mDevices.add(device);
			mDeviceAdapter.notifyItemInserted(mDevices.size() - 1);
			updateEmptyState();
		}
	}

	protected void updateEmptyState() {
		mEmptyStateView.setVisibility(mDevices.isEmpty() ? View.VISIBLE : View.GONE);
	}

	/** Called when the user picks a device from the list. */
	protected void onDeviceSelected(BluetoothDevice device) {
		// Create the result Intent and include the MAC address
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());

		// Set result and finish this Activity
		setResult(RESULT_OK, intent);
		log.log(Level.FINE, "Sending Result...");
		finish();
	}

	protected static class DeviceViewHolder extends RecyclerView.ViewHolder {
		final TextView text;
		DeviceViewHolder(@NonNull View itemView) {
			super(itemView);
			text = (TextView) itemView;
		}
	}

	protected class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
		@NonNull
		@Override
		public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.device_name, parent, false);
			return new DeviceViewHolder(v);
		}

		@SuppressLint("MissingPermission") // permission is checked before
		@Override
		public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
			BluetoothDevice dev = mDevices.get(position);
			String displayName;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				String alias = dev.getAlias();
				displayName = (alias != null && !alias.isEmpty()) ? alias : dev.getName();
			} else {
				displayName = dev.getName();
			}
			holder.text.setText(String.format("%s\n%s", displayName, dev.getAddress()));
			holder.itemView.setOnClickListener(v -> onDeviceSelected(dev));
		}

		@Override
		public int getItemCount() {
			return mDevices.size();
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
}
