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
import java.awt.Color;
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

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Genre;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class GenreView implements IStatisticView
{

	public static final int Id = 2;

	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private DefaultCategoryDataset _dataset;

	public GenreView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{
		_dataset = new DefaultCategoryDataset();

		createChart();

		refreshDataset();

		double factor = 1;
		Rectangle windowSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		_chartPanel = new ChartPanel(_chart, (int) windowSize.getWidth() / 2, (int) windowSize.getHeight() / 2,
				400, 300, (int) (windowSize.getWidth() * factor), (int) ((windowSize.getHeight() - 64) * factor),
				true, true, true, true, true, true);
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(null, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	private class GenreItem
	{
		public String GenreName;
		public int Count;

		public GenreItem(String genreName, int count)
		{
			GenreName = genreName;
			Count = count;
		}
	}

	private class GenreItemComparator implements Comparator
	{

		public int compare(Object o1, Object o2)
		{
			GenreItem s1 = (GenreItem) o1;
			GenreItem s2 = (GenreItem) o2;

			return s1.GenreName.compareTo(s2.GenreName);
		}

		public boolean equals(Object o1, Object o2)
		{
			GenreItem s1 = (GenreItem) o1;
			GenreItem s2 = (GenreItem) o2;

			if (s1.GenreName.equalsIgnoreCase(s2.GenreName))
				return true;
			else
				return false;
		}

	}

	public void createChart()
	{

		_chart = ChartFactory.createBarChart3D(Misc.getString("GENRES"), // chart
																			// title
				Misc.getString("GENRE"), // domain axis label
				Misc.getString("SONG_COUNT"), // range axis label
				_dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("GENRES_SUBTITLE")));

		CategoryPlot plot = (CategoryPlot) _chart.getPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		ChartUtilities.applyCurrentTheme(_chart);

		Misc.formatChart(plot);

		CategoryItemRenderer renderer = plot.getRenderer();
		BarRenderer3D barRenderer = (BarRenderer3D) renderer;
		barRenderer.setWallPaint(Color.white);
		barRenderer.setSeriesPaint(0, Theme.getColorSet()[1]);
	}

	private void refreshDataset()
	{
		Hashtable<String, ?> genres = _analysis.getHash(Analysis.KIND_GENRE);

		Vector<GenreItem> items = new Vector<GenreItem>();
		Enumeration<String> e = genres.keys();
		while (e.hasMoreElements())
		{
			String genreName = e.nextElement();
			Genre genre = (Genre) genres.get(genreName);
			items.add(new GenreItem(genreName, genre.getStats().getTrackCount()));
		}

		Collections.sort(items, new GenreItemComparator());

		for (int i = 0; i < items.size(); i++)
		{
			GenreItem item = (GenreItem) items.elementAt(i);

			_dataset.addValue(item.Count, Misc.getString("GENRE"), item.GenreName);
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
