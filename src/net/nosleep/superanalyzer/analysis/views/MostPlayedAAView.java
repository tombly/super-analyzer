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
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JPanel;

import net.nosleep.superanalyzer.analysis.Album;
import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringInt;
import net.nosleep.superanalyzer.util.Theme;

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

public class MostPlayedAAView implements IStatisticView
{

	public static final int Id = 11;

	private Analysis _analysis;
	private JFreeChart _artistChart;
	private JFreeChart _albumChart;
	private ChartPanel _artistChartPanel;
	private ChartPanel _albumChartPanel;
	private DefaultCategoryDataset _artistDataset;
	private DefaultCategoryDataset _albumDataset;

	public MostPlayedAAView(Analysis analysis)
	{
		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{
		_artistDataset = new DefaultCategoryDataset();
		_albumDataset = new DefaultCategoryDataset();

		_artistChart = createChart(Misc.getString("MOST_PLAYED_ARTISTS"), "", _artistDataset);
		_albumChart = createChart(Misc.getString("MOST_PLAYED_ALBUMS"), "", _albumDataset);

		refreshDataset();

		_artistChartPanel = new ChartPanel(_artistChart);
		_albumChartPanel = new ChartPanel(_albumChart);
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(null, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);

		JPanel gridPanel = new JPanel(new GridLayout(1, 2));
		gridPanel.add(_artistChartPanel);
		gridPanel.add(_albumChartPanel);

		panel.add(gridPanel, BorderLayout.CENTER);

		return panel;
	}

	public JFreeChart createChart(String title, String domainLabel, DefaultCategoryDataset dataset)
	{
		JFreeChart chart = ChartFactory.createBarChart3D(title, // chart title
				domainLabel, // domain axis label
				Misc.getString("SONG_COUNT"), // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		// _artistChart.addSubtitle(HomePanel
		// .createSubtitle("How many songs you have in each genre"));

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		ChartUtilities.applyCurrentTheme(chart);

		Misc.formatChart(plot);
		plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.5f);

		CategoryItemRenderer renderer = plot.getRenderer();
		BarRenderer3D barRenderer = (BarRenderer3D) renderer;
		barRenderer.setWallPaint(Color.white);
		barRenderer.setSeriesPaint(0, Theme.getColorSet()[1]);

		return chart;
	}

	private void refreshDataset()
	{
		Vector<StringInt> artists = _analysis.getMostPlayedArtists();
		_artistDataset.clear();
		for (int i = 0; i < artists.size(); i++)
		{
			StringInt artist = artists.elementAt(i);
			_artistDataset.addValue(artist.IntVal, Misc.getString("ARTISTS"), artist.StringVal);
		}

		Vector<StringInt> albums = _analysis.getMostPlayedAlbums();
		_albumDataset.clear();
		for (int i = 0; i < albums.size(); i++)
		{
			StringInt album = albums.elementAt(i);
			_albumDataset.addValue(album.IntVal, Misc.getString("ALBUMS"), album.StringVal.replace(Album.Separator, " " + Misc.getString("BY") + " "));
		}
	}

	public boolean canSaveImage()
	{
		return true;
	}

	public void saveImage(File file, Dimension d) throws IOException
	{
		if (d == null)
			d = new Dimension(_albumChartPanel.getWidth(), _albumChartPanel.getHeight());
		ChartUtilities.saveChartAsPNG(file, _albumChart, d.width, d.height);
	}

	public void saveImageExtra(File file, Dimension d) throws IOException
	{
		if (d == null)
			d = new Dimension(_artistChartPanel.getWidth(), _artistChartPanel.getHeight());
		ChartUtilities.saveChartAsPNG(file, _artistChart, d.width, d.height);
	}

	public int getId()
	{
		return Id;
	}

	public void willDisappear()
	{
	}

}
