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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.obd.ObdProt;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Display selected data items as dashboard
 */
public class DashBoardActivity extends Activity
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
	public static int MIN_GAUGE_SIZE = 300; /* dp */

	/**
	 * the wake lock to keep app communication alive
	 */
	private static PowerManager.WakeLock wakeLock;
	private transient ObdGaugeAdapter adapter;
	private transient GridView grid;

	/** Map to uniquely collect PID numbers */
	private HashSet<Integer> pidNumbers = new HashSet<Integer>();

	public static final int MESSAGE_UPDATE_VIEW = 7;

	private static ListAdapter mAdapter = null;
	/** display metrics */
	private static DisplayMetrics metrics = new DisplayMetrics();

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
	 * @param Adapter
	 */
	public static void setAdapter(ListAdapter Adapter)
	{
		mAdapter = Adapter;
	}

	/**
	 * Handle message requests
	 */
	private transient final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case MESSAGE_UPDATE_VIEW:
					grid.invalidateViews();
					break;
			}
		}
	};

	Timer refreshTimer = new Timer();

	/**
	 * Timer Task to cyclically update data screen
	 */
	private TimerTask updateTask = new TimerTask()
	{
		@Override
		public void run()
		{
			/* forward message to update the view */
			Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_UPDATE_VIEW);
			mHandler.sendMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
			getString(R.string.app_name));
		wakeLock.acquire();

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
		EcuDataPv currPv;

		super.onResume();
		// set the desired content screen
		int resId = getIntent().getIntExtra(RES_ID, R.layout.dashboard);
		setContentView(resId);

		// calculate minimum gauge size (1.6 inch) based on screen density
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		MIN_GAUGE_SIZE = Math.min( metrics.densityDpi * 16 / 10,
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
			numRows    = rowCols[positions.length][(width>height)?1:0];
		}
		// calc max gauge size
		int minWidth = width / numColumns;
		int minHeight = height / numRows;

		/* get grid object */
		grid = (GridView) findViewById(android.R.id.list);
		grid.setNumColumns(numColumns);

		// set data adapter
		adapter = new ObdGaugeAdapter( this,
		                               R.layout.obd_gauge,
		                               minWidth,
		                               minHeight,
		                               metrics);

		pidNumbers.clear();
		for (int position : positions)
		{
			// get corresponding Process variable
			currPv = (EcuDataPv) mAdapter.getItem(position);
			if (currPv != null)
			{
				currPv.setRenderingComponent(null);
				pidNumbers.add(currPv.getAsInt(EcuDataPv.FID_PID));
				adapter.add(currPv);
			}
		}
		grid.setAdapter(adapter);
		// limit selected PIDs to selection
		MainActivity.setFixedPids(pidNumbers);

		// start display update task
		try
		{
			refreshTimer.schedule(updateTask, 0, 100);
		} catch (Exception e)
		{
			// exception ignored here ...
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		refreshTimer.purge();
		adapter.clear();
		super.onPause();
	}

}
