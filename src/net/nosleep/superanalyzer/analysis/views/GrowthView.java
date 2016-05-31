/****************************************************************************
 The Super Analyzer
 Copyright (C) 2009 Tom Bulatewicz, Nosleep Software

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 ***************************************************************************/

package net.nosleep.superanalyzer.analysis.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class GrowthView implements IStatisticView
{

	public static final int Id = 3;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private TimeSeriesCollection _dataset;

	public GrowthView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{

		_comboBox = new JComboBox<ComboItem>(_analysis.getComboBoxItems());
		_comboBox.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				refreshDataset();
			}
		});

		_dataset = new TimeSeriesCollection();

		createChart();
		_chartPanel = new ChartPanel(_chart);
		_chartPanel.setMouseWheelEnabled(true);

		refreshDataset();

		_comboBox = new JComboBox<ComboItem>(_analysis.getComboBoxItems());
		_comboBox.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				refreshDataset();
			}
		});
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(_comboBox, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	private void createChart()
	{

		_chart = ChartFactory.createXYAreaChart(Misc.getString("LIBRARY_GROWTH"), Misc.getString("DATE"), Misc.getString("SONGS_IN_LIBRARY"), _dataset,
				PlotOrientation.VERTICAL, false, // legend
				true, // tool tips
				false // URLs
				);
		XYPlot plot = (XYPlot) _chart.getPlot();
		plot.setDomainPannable(true);
		ValueAxis domainAxis = new DateAxis(Misc.getString("TIME"));
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		plot.setDomainAxis(domainAxis);
		plot.setForegroundAlpha(0.75f);

		XYItemRenderer renderer = plot.getRenderer();
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("d-MMM-yyyy"),
				new DecimalFormat("#,##0.00")));

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("LIBRARY_GROWTH_SUBTITLE")));

		ChartUtilities.applyCurrentTheme(_chart);
		Misc.formatChart(plot);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		renderer.setSeriesPaint(0, Theme.getColorSet()[1]);

	}

	private void refreshDataset()
	{
		TimeSeries series1 = new TimeSeries(Misc.getString("SONGS_IN_LIBRARY"));

		Hashtable<Day, Integer> addedHash = null;

		if (_comboBox == null)
		{
			addedHash = _analysis.getDatesAdded(Analysis.KIND_TRACK, null);
		}
		else
		{
			ComboItem item = (ComboItem) _comboBox.getSelectedItem();
			addedHash = _analysis.getDatesAdded(item.getKind(), item.getValue());
		}

		Vector<GrowthItem> items = new Vector<GrowthItem>();

		Enumeration<Day> e = addedHash.keys();
		while (e.hasMoreElements())
		{

			Day day = e.nextElement();
			Integer count = addedHash.get(day);

			if (count.intValue() > 0)
			{
				items.addElement(new GrowthItem(day, count));
			}
		}

		Collections.sort(items, new GrowthComparator());

		double value = 0.0;
		for (int i = 0; i < items.size(); i++)
		{
			GrowthItem item = (GrowthItem) items.elementAt(i);

			value += item.Count;

			series1.add(item.Day, value);

		}

		_dataset.removeAllSeries();
		_dataset.addSeries(series1);
	}

	public class GrowthItem
	{
		public Day Day;
		public int Count;

		public GrowthItem(Day day, int count)
		{
			Day = day;
			Count = count;
		}
	}

	private class GrowthComparator implements Comparator
	{

		public int compare(Object o1, Object o2)
		{
			GrowthItem s1 = (GrowthItem) o1;
			GrowthItem s2 = (GrowthItem) o2;

			return s1.Day.compareTo(s2.Day);
		}

		@SuppressWarnings("unused")
		public boolean equals(Object o1, Object o2)
		{
			GrowthItem s1 = (GrowthItem) o1;
			GrowthItem s2 = (GrowthItem) o2;

			return (s1.Day == s2.Day);
		}

	}

	public boolean canSaveImage()
	{
		return true;
	}

	public void saveImage(File file, Dimension d) throws IOException
	{
		if (d == null)
			d = new Dimension(_chartPanel.getWidth(), _chartPanel.getHeight());
		ChartUtilities.saveChartAsPNG(file, _chart, d.width, d.height);
	}

	public int getId()
	{
		return Id;
	}

	public void saveImageExtra(File file, Dimension d) throws IOException
	{

	}

	public void willDisappear()
	{
	}

}
