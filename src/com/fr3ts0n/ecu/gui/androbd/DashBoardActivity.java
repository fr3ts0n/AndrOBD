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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.ObdProt;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by erwin on 06.12.14.
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
	public static final int MIN_GAUGE_SIZE = 300; /* dp */

	/**
	 * the wake lock to keep app communication alive
	 */
	private static PowerManager.WakeLock wakeLock;
	ObdGaugeAdapter adapter;
	GridView grid;

	/** Map to uniquely collect PID numbers */
	private HashSet<Integer> pidNumbers = new HashSet<Integer>();

	public static final int MESSAGE_UPDATE_VIEW = 7;

	private static ListAdapter mAdapter = null;

	/** data adapter as source of display data */
	public static ListAdapter getAdapter()
	{
		return mAdapter;
	}

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
		EcuDataPv currPv;

		super.onCreate(savedInstanceState);
		// set to full screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// keep display on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// hide the action bar
		getActionBar().hide();

		// prevent activity from falling asleep
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
			getString(R.string.app_name));
		wakeLock.acquire();

		/* get PIDs to be shown */
		int positions[] = getIntent().getIntArrayExtra(POSITIONS);

		// set the desired content screen
		int resId = getIntent().getIntExtra(RES_ID, R.layout.dashboard);
		setContentView(resId);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		int numColumns = Math.min(positions.length, Math.max(1, width / MIN_GAUGE_SIZE));
		int numRows = Math.min(positions.length, Math.max(1, height / MIN_GAUGE_SIZE));

		int minWidth = width / numColumns;
		int minHeight = height / numRows;

		grid = (GridView) findViewById(android.R.id.list);
		grid.setColumnWidth(minWidth);

		// set data adapter
		adapter = new ObdGaugeAdapter(this, R.layout.obd_gauge, minWidth, minHeight);
		grid.setAdapter(adapter);

		pidNumbers.clear();
		for (int position : positions)
		{
			// get corresponding Process variable
			currPv = (EcuDataPv) mAdapter.getItem(position);
			if (currPv != null)
			{
				pidNumbers.add(currPv.getAsInt(EcuDataPv.FID_PID));
				adapter.add(currPv);
			}
		}

		// limit selected PIDs to selection
		MainActivity.setFixedPids(pidNumbers);
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
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
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
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		refreshTimer.purge();
		super.onStop();
	}

}
