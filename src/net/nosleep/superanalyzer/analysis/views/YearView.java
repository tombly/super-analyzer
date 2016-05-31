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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Stat;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class YearView implements IStatisticView
{
	public static final int Id = 13;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private XYSeriesCollection _dataset;

	public YearView(Analysis analysis)
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

		_dataset = new XYSeriesCollection();

		createChart();
		_chartPanel = new ChartPanel(_chart);
		_chartPanel.setMouseWheelEnabled(true);

		refreshDataset();
	}

	public JPanel getPanel(HomePanel homePanel)
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(_comboBox, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	private class YearCountItem
	{
		public int Year;
		public int Count;

		public YearCountItem(int year, int count)
		{
			Year = year;
			Count = count;
		}
	}

	private class YearCountComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			YearCountItem s1 = (YearCountItem) o1;
			YearCountItem s2 = (YearCountItem) o2;

			return s1.Year - s2.Year;
		}

		public boolean equals(Object o1, Object o2)
		{
			YearCountItem s1 = (YearCountItem) o1;
			YearCountItem s2 = (YearCountItem) o2;

			if (s1.Year == s2.Year)
				return true;
			else
				return false;
		}
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return The dataset.
	 */
	private void refreshDataset()
	{

		// TimeSeries t1 = new TimeSeries("Songs you have", "Year", "Count");
		// TimeSeries t2 = new TimeSeries("Songs you played", "Year", "Count");

		XYSeries t1 = new XYSeries(Misc.getString("SONGS_YOU_HAVE"));
		XYSeries t2 = new XYSeries(Misc.getString("SONGS_YOU_PLAYED"));

		Stat itemStats = null;

		if (_comboBox == null)
		{
			itemStats = _analysis.getStats(Analysis.KIND_TRACK, null);
		}
		else
		{
			ComboItem item = (ComboItem) _comboBox.getSelectedItem();
			itemStats = _analysis.getStats(item.getKind(), item.getValue());
		}

		Vector<YearCountItem> yearCountListHave = new Vector<YearCountItem>();

		Hashtable<Integer, Integer> years = itemStats.getYears();
		Enumeration<Integer> e = years.keys();
		while (e.hasMoreElements())
		{
			Integer year = e.nextElement();
			Integer count = years.get(year);
			// if (count.intValue() > 0)
			yearCountListHave.addElement(new YearCountItem(year.intValue(), count.intValue()));
		}
		Collections.sort(yearCountListHave, new YearCountComparator());
		for (int i = 0; i < yearCountListHave.size(); i++)
		{
			YearCountItem item = (YearCountItem) yearCountListHave.elementAt(i);
			// _dataset.addValue(item.Count, "Songs you have", new
			// Integer(item.Year));
			t1.add(item.Year, new Integer(item.Count));
		}

		Vector<YearCountItem> yearCountListPlay = new Vector<YearCountItem>();

		Hashtable<Integer, Integer> yearPlays = itemStats.getPlayYears();
		e = yearPlays.keys();
		while (e.hasMoreElements())
		{
			Integer year = (Integer) e.nextElement();
			Integer count = (Integer) yearPlays.get(year);
			// if (count.intValue() > 0)
			yearCountListPlay.addElement(new YearCountItem(year.intValue(), count.intValue()));
		}
		Collections.sort(yearCountListPlay, new YearCountComparator());
		for (int i = 0; i < yearCountListPlay.size(); i++)
		{
			YearCountItem item = (YearCountItem) yearCountListPlay.elementAt(i);
			// _dataset.addValue(item.Count, "Songs you've played", new
			// Integer(item.Year));
			t2.add(item.Year, new Integer(item.Count));
		}

		_dataset.removeAllSeries();

		_dataset.addSeries(t1);
		_dataset.addSeries(t2);

		// TimeSeriesCollection tsc = new TimeSeriesCollection(t1);

		XYPlot plot = (XYPlot) _chart.getPlot();
		NumberAxis axis = (NumberAxis) plot.getRangeAxis();

		axis.setAutoRangeStickyZero(false);
		axis.setRange(-axis.getUpperBound()*0.015, axis.getUpperBound());

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		renderer.setSeriesPaint(0, Theme.getColorSet()[0]);
		renderer.setSeriesPaint(1, Theme.getColorSet()[6]);

	}

	private void createChart()
	{

		NumberAxis xAxis = new NumberAxis(Misc.getString("RELEASE_YEAR"));
		xAxis.setAutoRangeIncludesZero(false);

		NumberAxis yAxis = new NumberAxis(Misc.getString("SONG_COUNT"));
		yAxis.setAutoRangeIncludesZero(true);

		xAxis.setNumberFormatOverride(new DecimalFormat("0"));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		XYPlot plot = new XYPlot(_dataset, xAxis, yAxis, renderer);

		// create and return the chart panel...
		_chart = new JFreeChart(Misc.getString("RELEASE_YEAR"), JFreeChart.DEFAULT_TITLE_FONT, plot, true);

		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setAxisOffset(new RectangleInsets(4, 4, 4, 4));

		LegendTitle legend = _chart.getLegend();
		legend.setFrame(BlockBorder.NONE);

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("RELEASE_YEAR_TOOLTIP")));

		XYToolTipGenerator generator = new StandardXYToolTipGenerator("{2}", new DecimalFormat("0.00"),
				new DecimalFormat("0.00"));
		renderer.setBaseToolTipGenerator(generator);

		ChartUtilities.applyCurrentTheme(_chart);

		// format the lines after applying the theme
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
		renderer.setBaseFillPaint(Color.white);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(1, new BasicStroke(2.0f));
		renderer.setSeriesShape(1, new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0));

		_chart.setPadding(new RectangleInsets(10, 10, 10, 10));

		Misc.formatChart(plot);
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
