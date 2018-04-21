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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Mirrored relative layout for HUD displays
 *
 * @author erwin
 */
public class MirrorRelativeLayout extends RelativeLayout
{
	public MirrorRelativeLayout(Context context)
	{
		super(context);
	}
	
	public MirrorRelativeLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public MirrorRelativeLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		// Scale the canvas in reverse in the x-direction, pivoting on
		// the center of the view
		canvas.scale(-1f, 1f, getWidth() / 2f, getHeight() / 2f);
		super.dispatchDraw(canvas);
	}
}
