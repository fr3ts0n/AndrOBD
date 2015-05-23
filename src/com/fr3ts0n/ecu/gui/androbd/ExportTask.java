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
import android.os.AsyncTask;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

/**
 * Builds the CSV dump for sharing.
 *
 * @author Erwin Scheuch-Heilig
 */
class ExportTask extends AsyncTask<XYMultipleSeriesDataset, Integer, String>
{

	private Activity activity;
	private static DateFormat tagFormat = new SimpleDateFormat("yyyyMMddkkmmss");

	public static final String CSV_FIELD_DELIMITER = ",";
	public static final String CSV_LINE_DELIMITER = "\n";

	public ExportTask(Activity activity)
	{
		this.activity = activity;
	}

	@Override
	protected String doInBackground(XYMultipleSeriesDataset... params)
	{
		double currX;
		double currY;
		int maxCounts = 0;
		int highestResChannel = 0;	/* channel id with highest x-resolution */

		XYSeries series[] = params[0].getSeries();

		StringBuilder sb = new StringBuilder();
		// find channel with highest x-resolution
		for (int i = 0; i < series.length; i++)
		{
			if (maxCounts < series[i].getItemCount())
			{
				maxCounts = series[i].getItemCount();
				highestResChannel = i;
			}
		}
		// create measurement header (may be used as filename)
		long startTime = (long) series[highestResChannel].getX(0);
		sb.append(activity.getString(R.string.app_name)).append(".");
		sb.append(tagFormat.format(startTime)).append(".csv");
		sb.append(CSV_LINE_DELIMITER);

		// create header line
		sb.append(activity.getString(R.string.time));
		sb.append(CSV_FIELD_DELIMITER);
		for (XYSeries sery : series)
		{
			sb.append("\"").append(sery.getTitle()).append("\"");
			sb.append(CSV_FIELD_DELIMITER);
		}
		sb.append(CSV_LINE_DELIMITER);

		// generate data
		int samples = maxCounts;
		for (int i = 0; i < samples; i++)
		{
			currX = series[highestResChannel].getX(i);
			sb.append((currX - startTime) / 1000);
			sb.append(CSV_FIELD_DELIMITER);
			for (XYSeries sery : series)
			{
				try
				{
					SortedMap<Double, Double> map = sery.getRange(currX, currX, true);
					currY = map.get(map.firstKey());
					sb.append(currY);
					sb.append(CSV_FIELD_DELIMITER);
				} catch (Exception ex)
				{
					// do nothing, just catch the error
				}
			}
			sb.append(CSV_LINE_DELIMITER);
			publishProgress(10000 * i / samples);
		}
		return sb.toString();
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
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, result);
		sendIntent.setType("text/plain");
		activity.startActivity(Intent.createChooser(sendIntent, activity
			.getResources().getText(R.string.send_to)));
	}
}
