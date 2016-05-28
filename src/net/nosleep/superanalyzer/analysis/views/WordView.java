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
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComboBox;
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
import net.nosleep.superanalyzer.analysis.WordCounterMemory;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class WordView implements IStatisticView
{

	public static final int Id = 12;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private DefaultCategoryDataset _dataset;

	public WordView(Analysis analysis)
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

		_dataset = new DefaultCategoryDataset();

		createChart();
		_chartPanel = new ChartPanel(_chart);
		_chartPanel.setMouseWheelEnabled(true);

		refreshDataset();
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(null, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	public void createChart()
	{

		_chart = ChartFactory.createBarChart3D(Misc.getString("SONG_WORDS"), // chart title
				Misc.getString("WORD"), // domain axis label
				Misc.getString("SONG_COUNT"), // range axis label
				_dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("SONG_WORDS_TOOLTIP")));

		CategoryPlot plot = (CategoryPlot) _chart.getPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		ChartUtilities.applyCurrentTheme(_chart);
		Misc.formatChart(plot);

		CategoryItemRenderer renderer = plot.getRenderer();
		BarRenderer3D barRenderer = (BarRenderer3D) renderer;
		barRenderer.setWallPaint(Color.white);
		barRenderer.setSeriesPaint(0, Theme.getColorSet()[1]);
	}

	private void refreshDataset()
	{
		// get the most common wong words, up to 25 of them
		Vector pairs = _analysis.getMostCommonWords();

		// Collections.sort(pairs, new CommonWordComparator());

		for (int i = 0; i < pairs.size(); i++)
		{
			WordCounterMemory.CommonWord item = (WordCounterMemory.CommonWord) pairs.elementAt(i);

			_dataset.addValue(item.Count, Misc.getString("WORD"), item.Word);
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
