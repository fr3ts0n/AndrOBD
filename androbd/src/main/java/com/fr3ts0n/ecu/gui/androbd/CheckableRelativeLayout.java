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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of a relative layout to provide a checkable behaviour
 *
 * @author erwin
 */
public class CheckableRelativeLayout extends RelativeLayout implements
	Checkable
{

	private boolean isChecked;
	private List<Checkable> checkableViews;

	public CheckableRelativeLayout(Context context, AttributeSet attrs,
	                               int defStyle)
	{
		super(context, attrs, defStyle);
		initialise(attrs);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialise(attrs);
	}

	public CheckableRelativeLayout(Context context, int checkableId)
	{
		super(context);
		initialise(null);
	}

	/*
	 * @see android.widget.Checkable#isChecked()
	 */
	public boolean isChecked()
	{
		return isChecked;
	}

	/*
	 * @see android.widget.Checkable#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
		for (Checkable c : checkableViews)
		{
			c.setChecked(isChecked);
		}
	}

	/*
	 * @see android.widget.Checkable#toggle()
	 */
	public void toggle()
	{
		this.isChecked = !this.isChecked;
		for (Checkable c : checkableViews)
		{
			c.toggle();
		}
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		final int childCount = this.getChildCount();
		for (int i = 0; i < childCount; ++i)
		{
			findCheckableChildren(this.getChildAt(i));
		}
	}

	/**
	 * Read the custom XML attributes
	 */
	private void initialise(AttributeSet attrs)
	{
		this.isChecked = false;
		this.checkableViews = new ArrayList<Checkable>(5);
	}

	/**
	 * Add to our checkable list all the children of the view that implement the
	 * interface Checkable
	 */
	private void findCheckableChildren(View v)
	{
		if (v instanceof Checkable)
		{
			this.checkableViews.add((Checkable) v);
		}

		if (v instanceof ViewGroup)
		{
			final ViewGroup vg = (ViewGroup) v;
			final int childCount = vg.getChildCount();
			for (int i = 0; i < childCount; ++i)
			{
				findCheckableChildren(vg.getChildAt(i));
			}
		}
	}
}
