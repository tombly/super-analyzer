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

public class MostPlayedDGView implements IStatisticView
{

	public static final int Id = 14;

	private Analysis _analysis;
	private JFreeChart _decadeChart;
	private JFreeChart _genreChart;
	private ChartPanel _decadeChartPanel;
	private ChartPanel _genreChartPanel;
	private DefaultCategoryDataset _decadeDataset;
	private DefaultCategoryDataset _genreDataset;

	public MostPlayedDGView(Analysis analysis)
	{
		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{
		_decadeDataset = new DefaultCategoryDataset();
		_genreDataset = new DefaultCategoryDataset();

		_decadeChart = createChart(Misc.getString("MOST_PLAYED_DECADES"), "", _decadeDataset);
		_genreChart = createChart(Misc.getString("MOST_PLAYED_GENRES"), "", _genreDataset);

		refreshDataset();

		_decadeChartPanel = new ChartPanel(_decadeChart);
		_genreChartPanel = new ChartPanel(_genreChart);
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(null, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);

		JPanel gridPanel = new JPanel(new GridLayout(1, 2));
		gridPanel.add(_decadeChartPanel);
		gridPanel.add(_genreChartPanel);

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

		Vector<StringInt> decades = _analysis.getMostPlayedDecades();
		_decadeDataset.clear();
		for (int i = 0; i < decades.size(); i++)
		{
			StringInt decade = decades.elementAt(i);
			_decadeDataset.addValue(decade.IntVal, Misc.getString("DECADE"), decade.StringVal);
		}

		Vector<StringInt> genres = _analysis.getMostPlayedGenres();
		_genreDataset.clear();
		for (int i = 0; i < genres.size(); i++)
		{
			StringInt genre = genres.elementAt(i);
			_genreDataset.addValue(genre.IntVal, Misc.getString("ARTISTS"), genre.StringVal);
		}
	}

	public boolean canSaveImage()
	{
		return true;
	}

	public void saveImage(File file, Dimension d) throws IOException
	{
		if (d == null)
			d = new Dimension(_decadeChartPanel.getWidth(), _decadeChartPanel.getHeight());
		ChartUtilities.saveChartAsPNG(file, _decadeChart, d.width, d.height);
	}

	public void saveImageExtra(File file, Dimension d) throws IOException
	{
		if (d == null)
			d = new Dimension(_genreChartPanel.getWidth(), _genreChartPanel.getHeight());
		ChartUtilities.saveChartAsPNG(file, _genreChart, d.width, d.height);
	}

	public int getId()
	{
		return Id;
	}

	public void willDisappear()
	{
	}

}
