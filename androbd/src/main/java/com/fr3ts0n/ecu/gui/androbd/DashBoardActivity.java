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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.obd.ObdProt;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.github.anastr.speedviewlib.Gauge;

import java.util.HashSet;
import java.util.Objects;


/**
 * Display selected data items as dashboard
 */
public class DashBoardActivity extends Activity
		implements PvChangeListener, AdapterView.OnItemLongClickListener
{
	/**
	 * For passing the index number of the <code>Sensor</code> in its
	 * <code>SensorManager</code>
	 */
	public static final String POSITIONS = "POSITIONS";
	/**
	 * For passing the resource id of the <code>dashboard display</code>
	 */
	public static final String RES_ID = "RES_ID";

	/**
	 * Minimum size for gauges to be displayed
	 */
	private static int MIN_GAUGE_SIZE = 300; /* dp */

	/**
	 * the wake lock to keep app communication alive
	 */
	private static PowerManager.WakeLock wakeLock;
	private transient ObdGaugeAdapter adapter;
	private transient GridView grid;

	/** Map to uniquely collect PID numbers */
	private final HashSet<Integer> pidNumbers = new HashSet<>();

	protected static final int MESSAGE_UPDATE_VIEW = 1;

	private static ListAdapter mAdapter = null;
	/** display metrics */
	private static final DisplayMetrics metrics = new DisplayMetrics();

	/** record positions to be charted */
	private transient int[] positions;

	/** data adapter as source of display data */
	public static ListAdapter getAdapter()
	{
		return mAdapter;
	}

	// screen distribution matrix
	private static final int[][] rowCols=
	{
		{1,1},{1,1},{2,1},{2,2},{2,2},{3,2},{3,2},{4,2},{4,2},{3,3},
		{4,3},{4,3},{4,3},{4,4},{4,4},{4,4},{4,4},{5,4},{5,4},{5,4},{5,4}
	};


	/**
	 * Set list adapter as data source of display
	 * @param Adapter List adapter
	 */
	public static void setAdapter(ListAdapter Adapter)
	{
		mAdapter = Adapter;
	}

	/**
	 * Handle message requests
	 */
	protected transient final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case MESSAGE_UPDATE_VIEW:
					EcuDataPv currPv = (EcuDataPv)msg.obj;
					View itemView = grid.getChildAt(msg.arg1);
					if(itemView != null)
					{
						Gauge gauge = itemView.findViewById(R.id.chart);
						if(gauge != null)
						{
							Number val = (Number)currPv.get(EcuDataPv.FID_VALUE);
							gauge.speedTo(val.floatValue());
						}
					}
					break;
			}
		}
	};

	/**
	 * Update scaling of dashboard items
	 *
	 * Used for:
	 * - start/resume activity
	 * - screen size / orientation change
	 */
	void updateDashboardScaling()
	{
		// calculate minimum gauge size (1.6 inch) based on screen density
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		MIN_GAUGE_SIZE = Math.min( metrics.densityDpi * 15 / 10,
								   Math.min(metrics.widthPixels, metrics.heightPixels));

		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		int numColumns = Math.max(1, Math.min(positions.length, width / MIN_GAUGE_SIZE));
		int numRows = Math.max(1, Math.min(positions.length, height / MIN_GAUGE_SIZE));

		// distribute gauges on screen
		if(positions.length < numColumns*numRows)
		{
			// read for corresponding number of gauges & orientation
			numColumns = rowCols[positions.length][(width>height)?0:1];
		}
		/* get grid object */
		grid.setNumColumns(numColumns);

		adapter.clear();
		pidNumbers.clear();
		for (int position : positions)
		{
			// get corresponding Process variable
			EcuDataPv currPv = (EcuDataPv) mAdapter.getItem(position);
			if (currPv != null)
			{
				pidNumbers.add(currPv.getAsInt(EcuDataPv.FID_PID));
				adapter.add(currPv);
				currPv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);
			}
		}
		grid.setAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTheme(MainActivity.nightMode ? R.style.AppTheme_Dark : R.style.AppTheme);
		// set to full screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// keep main display on?
		if(MainActivity.prefs.getBoolean("keep_screen_on", false))
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		// hide the action bar
		ActionBar actionBar = getActionBar();
		if (actionBar != null) actionBar.hide();

		// prevent activity from falling asleep
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
			getString(R.string.app_name));
		wakeLock.acquire();

		// set the desired content screen
		int resId = getIntent().getIntExtra(RES_ID, R.layout.dashboard);
		setContentView(resId);
		grid = findViewById(android.R.id.list);
		grid.setOnItemLongClickListener(this);

		// create data adapter
		adapter = new ObdGaugeAdapter( this,
									   R.layout.obd_gauge);

		/* get PIDs to be shown */
		positions = getIntent().getIntArrayExtra(POSITIONS);
	}

	/**
	 * Handle destroy of the Activity
	 */
	@Override
	protected void onDestroy()
	{
		// reset PID limiting
		ObdProt.resetFixedPid();
		adapter.clear();
		// allow sleeping again
		wakeLock.release();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		// set scaling of dashboard items
		updateDashboardScaling();
		// limit selected PIDs to selection
		MainActivity.setFixedPids(pidNumbers);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		updateDashboardScaling();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		adapter.clear();
		super.onPause();
	}

	@Override
	public void pvChanged(PvChangeEvent event)
	{
		if(event.getKey().equals(EcuDataPv.FIELDS[EcuDataPv.FID_VALUE])
				&& event.getValue() instanceof Number)
		{
			int pos = adapter.getPosition((EcuDataPv)event.getSource());
			Message msg = mHandler.obtainMessage(MESSAGE_UPDATE_VIEW, pos,0, event.getSource());
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Set data item to be customized
		EcuDataPv pv = adapter.getItem(position);
		EcuDataItem item = EcuDataItems.byMnemonic.get(pv.get(EcuDataPv.FID_MNEMONIC));
		PidCustomization.item = item;

		// start customization ...
		Intent intent = new Intent(this, PidCustomization.class);
		startActivity(intent);

		return true;
	}
}
