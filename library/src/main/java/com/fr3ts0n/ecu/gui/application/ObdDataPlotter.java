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

package com.fr3ts0n.ecu.gui.application;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * OBD data graphing panel
 */
public class ObdDataPlotter extends JPanel
{
	private static final long serialVersionUID = 9085602185360330792L;
	/* Font for all legent items */
	private static final Font legendFont = new Font("Dialog", 0, 7);
	/** generator for all tool tips */
	private static final XYToolTipGenerator toolTipGen =
		StandardXYToolTipGenerator.getTimeSeriesInstance();

	/** switch to use range series per series */
	private static final boolean oneRangePerSeries = true;


	private int raIndex = 0;
	final TimeSeriesCollection dataset = new TimeSeriesCollection();

	private JFreeChart chart;

	/**
	 * create the graphing panel
	 */
	public ObdDataPlotter()
	{
		setLayout(new BorderLayout());
		chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);
		add(chartPanel, BorderLayout.CENTER);
	}

	/**
	 * set a new graph title
	 *
	 * @param newTitle the frame title.
	 */
	public void setTitle(String newTitle)
	{
		chart.setTitle(newTitle);
	}

	/**
	 * Creates a chart.
	 *
	 * @param dataset a dataset.
	 * @return A chart.
	 */
	private JFreeChart createChart(XYDataset dataset)
	{

		chart = ChartFactory.createTimeSeriesChart(
			"OBD Data Graph", // title
			"Time", // x-axis label
			"Value", // y-axis label
			dataset, // data
			true, // create legend?
			true, // generate tooltips?
			false // generate URLs?
		);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.getDomainAxis().setTickLabelFont(legendFont);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		chart.getLegend().setItemFont(legendFont);

		return chart;
	}

	/**
	 * remove all data series from graph object
	 */
	public synchronized void removeAllSeries()
	{
		dataset.removeAllSeries();
		if (oneRangePerSeries)
		{
			XYPlot plot = (XYPlot) chart.getPlot();
			for (; raIndex > 0; raIndex--)
			{
				plot.setDataset(raIndex, null);
				plot.setRenderer(raIndex, null);
				plot.setRangeAxis(raIndex, null);
			}
			plot.setDataset(dataset);
		}
	}

	/**
	 * add a new series to the graph
	 *
	 * @param series The new series to be added
	 */
	public synchronized void addSeries(TimeSeries series)
	{

		if (oneRangePerSeries)
		{
			// get paint for current axis/range/...
			Paint currPaint =
				DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[
					raIndex % DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length];

			XYPlot plot = (XYPlot) chart.getPlot();
			// set dataset
			plot.setDataset(raIndex, new TimeSeriesCollection(series));
			// ** set axis
			NumberAxis axis = new NumberAxis();
			axis.setTickLabelFont(legendFont);
			axis.setAxisLinePaint(currPaint);
			axis.setTickLabelPaint(currPaint);
			axis.setTickMarkPaint(currPaint);
			// ** set axis in plot
			plot.setRangeAxis(raIndex, axis);
			plot.setRangeAxisLocation(raIndex, raIndex % 2 == 0 ? AxisLocation.TOP_OR_LEFT : AxisLocation.BOTTOM_OR_RIGHT);
			plot.mapDatasetToRangeAxis(raIndex, raIndex);
			// ** create renderer
			XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
			renderer.setBaseToolTipGenerator(toolTipGen);
			renderer.setSeriesPaint(0, currPaint);
			// ** set renderer in plot
			plot.setRenderer(raIndex, renderer);

			raIndex++;
		}
		dataset.addSeries(series);
	}

	/**
	 * Holds value of property graphTime.
	 */
	private int graphTime;

	/**
	 * Getter for property graphTime.
	 *
	 * @return Value of property graphTime.
	 */
	public int getGraphTime()
	{
		return this.graphTime;
	}

	/**
	 * Setter for property graphTime.
	 *
	 * @param graphTime New value of property graphTime.
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void setGraphTime(int graphTime)
	{
		TimeSeries currSer;
		TimeSeriesCollection currDs;
		XYPlot currPlot = (XYPlot) chart.getPlot();
		this.graphTime = graphTime;
		// lop through all datasets
		for (int i = currPlot.getDatasetCount(); i >= 0; --i)
		{
			currDs = (TimeSeriesCollection) currPlot.getDataset(i);
			// Update all series within dataset
			Iterator it = currDs.getSeries().iterator();
			while (it.hasNext())
			{
				currSer = (TimeSeries) it.next();
				currSer.setMaximumItemAge(graphTime);
			}
		}
	}
}
