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
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Stat;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class PlayCountView implements IStatisticView
{
	public static final int Id = 5;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private IntervalXYDataset _dataset;
	private XYSeriesCollection collection = new XYSeriesCollection();

	public PlayCountView(Analysis analysis)
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

		_dataset = new XYBarDataset(collection, 0.9);

		createChart();
		double factor = 1; //percentage of maximum available space in panel, numbers lower than 1 leads to upscaling
		Rectangle windowSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		_chartPanel = new ChartPanel(_chart, (int) windowSize.getWidth() / 2, (int) windowSize.getHeight() / 2,
				400, 300, (int) (windowSize.getWidth() * factor), (int) ((windowSize.getHeight() - 64) * factor),
				true, true, true, true, true, true);
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

	private void createChart()
	{
		_chart = ChartFactory.createXYBarChart(Misc.getString("PLAY_COUNT"), Misc.getString("PLAY_COUNT"), false, Misc.getString("NUMBER_OF_SONGS"), _dataset,
				PlotOrientation.VERTICAL, false, true, false);

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("PLAY_COUNT_SUBTITLE")));

		// then customise it a little...
		XYPlot plot = (XYPlot) _chart.getPlot();
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		plot.setForegroundAlpha(0.75f);

		ChartUtilities.applyCurrentTheme(_chart);

		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setShadowVisible(false);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);

		domainAxis.setLowerMargin(0);
		domainAxis.setUpperMargin(0);
		// domainAxis.setLowerBound(0);
		// domainAxis.setAutoRangeIncludesZero(true);
		// domainAxis.setAutoRangeStickyZero(true);

		Misc.formatChart(plot);
		renderer.setSeriesPaint(0, Theme.getColorSet()[1]);

	}

	public class PlayCountItem
	{
		public int PlayCount;
		public int Count;

		public PlayCountItem(int playCount, int count)
		{
			PlayCount = playCount;
			Count = count;
		}
	}

	private class PlayCountComparator implements Comparator
	{

		public int compare(Object o1, Object o2)
		{
			PlayCountItem s1 = (PlayCountItem) o1;
			PlayCountItem s2 = (PlayCountItem) o2;

			return s1.Count - s2.Count;
		}

		public boolean equals(Object o1, Object o2)
		{
			PlayCountItem s1 = (PlayCountItem) o1;
			PlayCountItem s2 = (PlayCountItem) o2;

			if (s1.Count == s2.Count)
				return true;
			else
				return false;
		}

	}

	private void refreshDataset()
	{

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

		Vector<PlayCountItem> playCountList = new Vector<PlayCountItem>();

		Hashtable<Integer, Integer> playCounts = itemStats.getPlayCounts();
		Enumeration<Integer> e = playCounts.keys();
		while (e.hasMoreElements())
		{
			Integer playCount = e.nextElement();
			Integer count = playCounts.get(playCount);
			if (count.intValue() > 0)
				playCountList.addElement(new PlayCountItem(playCount.intValue(), count.intValue()));
		}
		Collections.sort(playCountList, new PlayCountComparator());

		XYSeries series = new XYSeries(Misc.getString("PLAY_COUNT"));

		for (int i = 0; i < playCountList.size(); i++)
		{
			PlayCountItem item = (PlayCountItem) playCountList.elementAt(i);
			series.add(item.PlayCount, item.Count);
		}

		collection.removeAllSeries();

		collection.addSeries(series);

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