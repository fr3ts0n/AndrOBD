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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

/**
 * Builds the CSV dump for sharing.
 *
 * @author Erwin Scheuch-Heilig
 */
class ExportTask extends AsyncTask<XYMultipleSeriesDataset, Integer, String>
{

	private Activity activity;
	private static final DateFormat tagFormat  = new SimpleDateFormat("yyyyMMddkkmmss");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static final String OPT_FIELD_DELIM		= "csv_field_delimiter";
	private static final String OPT_RECORD_DELIM	= "csv_record_delimiter";
	private static final String OPT_TEXT_QUOTED 	= "csv_text_quoted";
	private static final String OPT_SEND_EXPORT 	= "send_after_export";

	private static String CSV_FIELD_DELIMITER = ",";
	private static String CSV_LINE_DELIMITER = "\n";
	private static boolean CSV_TEXT_QUOTED = false;

	SharedPreferences prefs;

	// file name to be saved
    private String path;
	private String fileName;

	public ExportTask(Activity activity)
	{
		this.activity = activity;
        path = FileHelper.getPath(activity).concat(File.separator+"csv");
		fileName = path.concat(File.separator+FileHelper.getFileName()
                       .concat(".csv"));

		// get preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		CSV_FIELD_DELIMITER = prefs.getString(OPT_FIELD_DELIM,",");
		CSV_LINE_DELIMITER  = prefs.getString(OPT_RECORD_DELIM,"\n");
		CSV_TEXT_QUOTED     = prefs.getBoolean(OPT_TEXT_QUOTED,false);
	}

	private static String quoteStringIfNeeded(String string)
	{
		return String.format( CSV_TEXT_QUOTED ? "\"%s\"" : "%s", string);
	}

	@Override
	protected String doInBackground(XYMultipleSeriesDataset... params)
	{
		double currX;
		double currY;
		int maxCounts = 0;
		int highestResChannel = 0;	/* channel id with highest x-resolution */

		XYSeries series[] = params[0].getSeries();

		// find channel with highest x-resolution
		for (int i = 0; i < series.length; i++)
		{
			if (maxCounts < series[i].getItemCount())
			{
				maxCounts = series[i].getItemCount();
				highestResChannel = i;
			}
		}

        new File(path).mkdirs();
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(new File(fileName));

			// create header line
			writer.append(quoteStringIfNeeded(activity.getString(R.string.time)));
			writer.append(CSV_FIELD_DELIMITER);
			for (XYSeries sery : series)
			{
				writer.append(quoteStringIfNeeded(sery.getTitle()));
				writer.append(CSV_FIELD_DELIMITER);
			}
			writer.append(CSV_LINE_DELIMITER);

			// generate data
			for (int i = 0; i < maxCounts; i++)
			{
				currX = series[highestResChannel].getX(i);
				writer.append(dateFormat.format(new Date((long)currX)));
				writer.append(CSV_FIELD_DELIMITER);
				for (XYSeries sery : series)
				{
					try
					{
						SortedMap<Double, Double> map = sery.getRange(currX, currX, true);
						currY = map.get(map.firstKey());
						writer.append(String.valueOf(currY));
						writer.append(CSV_FIELD_DELIMITER);
					} catch (Exception ex)
					{
						// do nothing, just catch the error
					}
				}
				writer.append(CSV_LINE_DELIMITER);
				publishProgress(10000 * i / maxCounts);
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return fileName;
	}

	@Override
	public void onPreExecute()
	{
		activity.setProgressBarVisibility(true);
	}

	@Override
	public void onProgressUpdate(Integer... values)
	{
		activity.setProgress(values[0]);
	}

	@Override
	public void onPostExecute(String result)
	{
		activity.setProgressBarVisibility(false);

		// show saved message
		String msg = String.format("CSV %s to %s",
								   activity.getString(R.string.saved),
								   fileName);
		Log.i(activity.getString(R.string.saved), msg);
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

		// if export file should be sent immediately ...
		if(prefs.getBoolean(OPT_SEND_EXPORT, false))
		{
			// allow sending the generated file ...
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("*/*");
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
			activity.startActivity(
					Intent.createChooser(sendIntent,
										 activity.getResources().getText(R.string.send_to)));
		}
	}
}
