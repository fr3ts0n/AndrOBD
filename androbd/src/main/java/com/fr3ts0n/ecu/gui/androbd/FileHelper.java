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

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to save measurements
 *
 * @author Erwin Scheuch-Heilig
 */
class FileHelper
{
	/** Date Formatter used to generate file name */
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
	private static ModernProgressDialog progress;
	
	private static final Logger log = Logger.getLogger(FileHelper.class.getName());
	
	/** ExecutorService for background operations */
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	/** Handler for UI thread operations */
	private static final Handler mainHandler = new Handler(Looper.getMainLooper());
	
	private final Context context;
	private final ElmProt elm;

	/**
	 * Initialize static data for static calls
	 *  @param context APP context
	 *
	 */
	FileHelper(Context context)
	{
		this.context = context;
		this.elm = CommService.elm;
	}

	/**
	 * get default path for load/store operation
	 * * path is based on configured <user data location>/<package name>
	 *
	 * @return default path for current app context
	 */
	static String getPath(Context context)
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
	static String getFileName()
	{
		return dateFmt.format(System.currentTimeMillis());
	}


	/**
	 * Save all data in a independent thread using modern ExecutorService
	 */
	void saveDataThreaded()
	{
		// generate file name
		final String mPath = getPath(context);
		final String mFileName = mPath
			+ File.separator
			+ getFileName()
			+ ".obd";

		// create progress dialog
		progress = ModernProgressDialog.show(context,
			context.getString(R.string.saving_data),
			mFileName,
			true);

		executor.execute(new Runnable() {
			@Override
			public void run() {
				saveData(mPath, mFileName);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						if (progress != null) {
							progress.dismiss();
						}
					}
				});
			}
		});
	}

	/**
	 * Save all data
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private synchronized void saveData(String mPath, String mFileName)
	{
		File outFile;

		// ensure the path is created
		//noinspection ResultOfMethodCallIgnored
		new File(mPath).mkdirs();
		outFile = new File(mFileName);

		// prevent data updates for saving period
		ObdItemAdapter.allowDataUpdates = false;

		try
		{
			Looper.prepare();
			outFile.createNewFile();
			FileOutputStream fStr = new FileOutputStream(outFile);
			ObjectOutputStream oStr = new ObjectOutputStream(fStr);
			oStr.writeInt(elm.getService());
			oStr.writeObject(ObdProt.PidPvs);
			oStr.writeObject(ObdProt.VidPvs);
			oStr.writeObject(ObdProt.tCodes);
			oStr.writeObject(MainActivity.mPluginPvs);

			oStr.close();
			fStr.close();

			@SuppressLint("DefaultLocale")
			String msg = String.format("%s %d Bytes to %s",
				context.getString(R.string.saved),
				outFile.length(),
				mPath);
			log.info(msg);
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
	 * Load all data in a independent thread using modern ExecutorService
	 * @param uri Uri of file to be loaded
	 */
	synchronized void loadDataThreaded(final Uri uri,
	                                   final Handler reportTo)
	{
		// create progress dialog
		progress = ModernProgressDialog.show(context,
		                               context.getString(R.string.loading_data),
		                               uri.getPath(),
		                               true);

		executor.execute(new Runnable() {
			@Override
			public void run() {
				loadData(uri);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						if (progress != null) {
							progress.dismiss();
						}
						reportTo.sendMessage(reportTo.obtainMessage(MainActivity.MESSAGE_FILE_READ));
					}
				});
			}
		});
	}

	/**
	 * Load data from file into data structures
	 *
	 * @param uri URI of file to be loaded
	 */
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("UnusedReturnValue")
	private synchronized int loadData(final Uri uri)
	{
		int numBytesLoaded = 0;
		String msg;
		InputStream inStr;

		try
		{
			Looper.prepare();
			inStr = context.getContentResolver().openInputStream(uri);
			numBytesLoaded = inStr != null ? inStr.available() : 0;
			msg = context.getString(R.string.loaded).concat(String.format(" %d Bytes", numBytesLoaded));
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
			MainActivity.mPluginPvs = (PvList) oIn.readObject();

			oIn.close();

			log.log(Level.INFO, msg);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception ex)
		{
			Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
			log.log(Level.SEVERE, uri.toString(), ex);
		}
		return numBytesLoaded;
	}
}
