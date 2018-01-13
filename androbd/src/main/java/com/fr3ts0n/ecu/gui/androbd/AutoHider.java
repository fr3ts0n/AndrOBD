/*
 * (C) Copyright 2016 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
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

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Automatically hide components after timeout and show again on touch action
 */
public class AutoHider
	extends TimerTask
	implements OnTouchListener
{
	/** parent activity */
	private Activity mActivity;
	/** activities message handler */
	private Handler mHandler;
	/** timestamp when component was hidden */
	private long componentHideTime;
	/** message ID to be sent for hiding component */
	private int mMessageId;
	/** current visibility state of component */
	private boolean visible = true;
	/** delay time[ms] before component gets hidden */
	long TB_HIDE_DELAY = 15000;

	/**
	 * Constructor
	 * @param activity parent activity
	 * @param handler activity's message handler
	 * @param hideMessageId message ID of hide/sho message
	 * @param hideDelayTime delay time[ms] before component gets hidden
	 */
	public AutoHider(Activity activity,
	                 Handler handler,
	                 int hideMessageId,
	                 long hideDelayTime )
	{
		TB_HIDE_DELAY = hideDelayTime;
		mMessageId = hideMessageId;
		mActivity = activity;
		mHandler  = handler;
		activity.getWindow().getDecorView().setOnTouchListener(this);
	}

	@Override
	public boolean cancel()
	{
		// make component visible
		showComponent();
		return super.cancel();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		showComponent();
		return false;
	}

	@Override
	public void run()
	{
		// update component visibility based on hide timer
		setComponentVisibility(System.currentTimeMillis() < componentHideTime);
	}

	/**
	 * Start AutoHider loop
	 * @param checkLoopInterval interval to update visibility
	 */
	public void start(int checkLoopInterval)
	{
		showComponent();
		new Timer().schedule(this, 0, checkLoopInterval);
	}

	/**
	 * set Visibility of component
	 * @param visible visible/invisible?
	 */
	private void setComponentVisibility(boolean visible)
	{
		// if visibility changed ...
		if(this.visible != visible)
		{
			/* forward message to update the view */
			Message msg = mHandler.obtainMessage(mMessageId);
			msg.obj = visible;
			mHandler.sendMessage(msg);
			// update visibility state
			this.visible = visible;
		}
	}

	/**
	 * Show tool bar again
	 */
	public void showComponent()
	{
		// set next hiding time
		componentHideTime = System.currentTimeMillis() + TB_HIDE_DELAY;
		// now show the component
		setComponentVisibility(true);
	}
}
