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
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Shows a {@link ListView} of available USB devices.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public final class UsbDeviceListActivity extends AppCompatActivity
{
	private static final String TAG = UsbDeviceListActivity.class.getSimpleName();
	private static final Logger log = Logger.getLogger(TAG);
	
	/** selected USB port */
	public static UsbSerialPort selectedPort = null;

	private UsbManager mUsbManager;
	private static final int MESSAGE_REFRESH = 101;
	private static final long REFRESH_TIMEOUT_MILLIS = 5000;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private final Handler mHandler = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MESSAGE_REFRESH:
					refreshDeviceList();
					mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
					break;
				default:
					super.handleMessage(msg);
					break;
			}
		}

	};

	private final List<UsbSerialPort> mEntries = new ArrayList<>();
	private UsbAdapter mAdapter;
	private TextView mEmptyStateView;
	private LinearProgressIndicator mScanProgress;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usb_list);

		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		RecyclerView mListView = findViewById(R.id.deviceList);
		mListView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new UsbAdapter();
		mListView.setAdapter(mAdapter);

		mEmptyStateView = findViewById(R.id.empty_state);
		mEmptyStateView.setText(R.string.no_usb_devices_found);
		mScanProgress = findViewById(R.id.scan_progress);
	}

	private void onPortSelected(UsbSerialPort port) {
		selectedPort = port;

		// Create the result Intent and include the MAC address
		Intent intent = new Intent();
		// Set result and finish this Activity
		setResult(RESULT_OK, intent);
		log.fine("Sending Result...");
		finish();
	}

	private static class DeviceViewHolder extends RecyclerView.ViewHolder {
		final TextView text;
		DeviceViewHolder(@NonNull View itemView) {
			super(itemView);
			text = (TextView) itemView;
		}
	}

	private class UsbAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
		@NonNull
		@Override
		public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.device_name, parent, false);
			return new DeviceViewHolder(v);
		}

		@Override
		public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
			final UsbSerialPort port = mEntries.get(position);
			final UsbSerialDriver driver = port.getDriver();
			final UsbDevice device = driver.getDevice();

			final String title = String.format("USB: 0x%04x/0x%04x",
			                                   device.getVendorId(),
			                                   device.getProductId());
			final String subtitle = driver.getClass().getSimpleName();

			holder.text.setText(String.format("%s\n%s", title, subtitle));
			holder.itemView.setOnClickListener(v -> onPortSelected(port));
		}

		@Override
		public int getItemCount() {
			return mEntries.size();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mHandler.sendEmptyMessage(MESSAGE_REFRESH);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mHandler.removeMessages(MESSAGE_REFRESH);
	}

	// The device list is small and rebuilt wholesale every poll (no drivers persist across a
	// refresh), so a full notifyDataSetChanged is genuinely simplest here — not worth a DiffUtil
	// for a handful of items on a 5-second timer.
	@SuppressLint({"StringFormatInvalid", "NotifyDataSetChanged"})
	private void refreshDeviceList()
	{
		mScanProgress.setVisibility(View.VISIBLE);
		mExecutor.submit(() -> {
			log.fine("Refreshing device list ...");
			final List<UsbSerialDriver> drivers =
				UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
			final List<UsbSerialPort> result = new ArrayList<>();
			for (final UsbSerialDriver driver : drivers)
			{
				final List<UsbSerialPort> ports = driver.getPorts();
				log.fine(String.format("+ %s: %s selectedPort%s",
				                       driver, ports.size(),
				                       ports.size() == 1 ? "" : "s"));
				result.addAll(ports);
			}
			mHandler.post(() -> {
				mEntries.clear();
				mEntries.addAll(result);
				TextView numFound = findViewById(R.id.num_found);
				numFound.setText(getString(R.string.devices_found, result.size()));
				mAdapter.notifyDataSetChanged();
				mEmptyStateView.setVisibility(mEntries.isEmpty() ? View.VISIBLE : View.GONE);
				mScanProgress.setVisibility(View.GONE);
				log.fine("Done refreshing, " + mEntries.size() + " entries found.");
			});
		});
	}
}
