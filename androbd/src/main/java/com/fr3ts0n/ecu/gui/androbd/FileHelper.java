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

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fr3ts0n.ecu.prot.obd.ElmProt;
import com.fr3ts0n.ecu.prot.obd.ObdProt;
import com.fr3ts0n.pvs.PvList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

/**
 * Task to save measurements
 *
 * @author Erwin Scheuch-Heilig
 */
class FileHelper
{
	/** Date Formatter used to generate file name */
	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
	private static ProgressDialog progress;

	private Context context;
	private ElmProt elm;

	/**
	 * Initialize static data for static calls
	 *
	 * @param context APP context
	 * @param elm     Elm protocol data to be stored
	 */
	public FileHelper(Context context, ElmProt elm)
	{
		this.context = context;
		this.elm = elm;
	}

	/**
	 * get default path for load/store operation
	 * * path is based on configured <user data location>/<package name>
	 *
	 * @return default path for current app context
	 */
	public static String getPath(Context context)
	{
		// generate file name
		return Environment.getExternalStorageDirectory()
			+ File.separator
			+ context.getPackageName();
	}

	/**
	 * get filename (w/o extension) based on current date & time
	 *
	 * @return file name
	 */
	public static String getFileName()
	{
		return dateFmt.format(System.currentTimeMillis());
	}


	/**
	 * Save all data in a independent thread
	 */
	public void saveDataThreaded()
	{
		// generate file name
		final String mPath = getPath(context);
		final String mFileName = mPath
			+ File.separator
			+ getFileName()
			+ ".obd";

		// create progress dialog
		progress = ProgressDialog.show(context,
			context.getString(R.string.saving_data),
			mFileName,
			true);

		Thread saveTask = new Thread()
		{
			public void run()
			{
				Looper.prepare();
				saveData(mPath, mFileName);
				progress.dismiss();
				Looper.loop();
			}
		};
		saveTask.start();
	}

	/**
	 * Save all data
	 */
	public synchronized void saveData(String mPath, String mFileName)
	{
		File outFile;

		// ensure the path is created
		new File(mPath).mkdirs();
		outFile = new File(mFileName);

		// prevent data updates for saving period
		ObdItemAdapter.allowDataUpdates = false;

		try
		{
			outFile.createNewFile();
			FileOutputStream fStr = new FileOutputStream(outFile);
			ObjectOutputStream oStr = new ObjectOutputStream(fStr);
			oStr.writeInt(elm.getService());
			oStr.writeObject(ObdProt.PidPvs);
			oStr.writeObject(ObdProt.VidPvs);
			oStr.writeObject(ObdProt.tCodes);

			oStr.close();
			fStr.close();

			String msg = String.format("%s %d Bytes to %s",
				context.getString(R.string.saved),
				outFile.length(),
				mPath);
			Log.i(context.getString(R.string.saved), msg);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e)
		{
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// we are done saving, allow data updates again
		ObdItemAdapter.allowDataUpdates = true;
	}

	/**
	 * Load all data in a independent thread
	 * @param uri Uri of ile to be loaded
	 */
	public synchronized void loadDataThreaded(final Uri uri,
	                                          final Handler reportTo,
																						final int reportMessage)
	{
		// create progress dialog
		progress = ProgressDialog.show(context,
		                               context.getString(R.string.loading_data),
		                               uri.getPath(),
		                               true);

		Thread loadTask = new Thread()
		{
			public void run()
			{
				Looper.prepare();
				loadData(uri);
				progress.dismiss();
				reportTo.sendMessage(reportTo.obtainMessage(reportMessage));
				Looper.loop();
			}
		};
		loadTask.start();
	}

	/**
	 * Load data from file into data sructures
	 *
	 * @param uri URI of file to be loaded
	 */
	public synchronized int loadData(final Uri uri)
	{
		int numBytesLoaded = 0;
		String msg;
		InputStream inStr;

		try
		{
			inStr = context.getContentResolver().openInputStream(uri);
			numBytesLoaded = inStr.available();
			msg = String.format("%d Bytes", numBytesLoaded);
			ObjectInputStream oIn = new ObjectInputStream(inStr);
			/* ensure that measurement page is activated
			   to avoid deletion of loaded data afterwards */
			int currService = oIn.readInt();
			/* if data was saved in mode 0, keep current mode */
			if(currService != 0) elm.setService(currService, false);
			/* read in the data */
			ObdProt.PidPvs = (PvList) oIn.readObject();
			ObdProt.VidPvs = (PvList) oIn.readObject();
			ObdProt.tCodes = (PvList) oIn.readObject();
			oIn.close();

			Log.i(context.getString(R.string.load), context.getString(R.string.loaded).concat(" ").concat(msg));
			Toast.makeText(context, context.getString(R.string.loaded).concat(" ").concat(msg), Toast.LENGTH_SHORT).show();
		} catch (Exception ex)
		{
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
			Log.e(context.getString(R.string.load), uri.toString(), ex);
		}
		return numBytesLoaded;
	}
}
