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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Create a screenshot and save it to sd-card
 * Created by erwin on 10.11.14.
 */
public class Screenshot
{
	/**
	 * Take a screenhot of selected view in selected context and save on external
	 * storage as filename <AppName>_<TimeStamp>.jpeg
	 *
	 * @param context context of view
	 * @param view    view to be saved
	 */
	public static void takeScreenShot(Context context, View view)
	{
		// get Bitmap from the view
		Bitmap bitmap = loadBitmapFromView(context, view);
		// generate file name
		String mPath = Environment.getExternalStorageDirectory()
			+ File.separator
			+ context.getPackageName() + "."
			+ System.currentTimeMillis()
			+ ".png";
		File imageFile = new File(mPath);

		try
		{
			// compress the bitmap to PNG file
			OutputStream fout = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
			// show notification
			Toast.makeText(context, "Screenshot saved: " + mPath, Toast.LENGTH_SHORT).show();
			Log.i("Screenshot saved", mPath);

			fout.flush();
			fout.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * get a bitmap from selected view
	 *
	 * @param context context of View to be taken
	 * @param v       View to be taken
	 * @return Bitmap of selected view
	 */
	public static Bitmap loadBitmapFromView(Context context, View v)
	{
		Bitmap returnedBitmap = Bitmap.createBitmap(v.getWidth(),
			v.getHeight(),
			Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(returnedBitmap);
		v.draw(c);

		return returnedBitmap;
	}
}
