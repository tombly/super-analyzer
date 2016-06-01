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
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Stat;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class TimeView implements IStatisticView
{
	public static final int Id = 10;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private DefaultCategoryDataset _dataset;

	public TimeView(Analysis analysis)
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

		JPanel headerPanel = homePanel.createHeaderPanel(_comboBox, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	private void refreshDataset()
	{
		_dataset.clear();

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

		int[] hours = itemStats.getHours();
		for (int i = 0; i < 24; i++)
		{

			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, i);
			c.set(Calendar.MINUTE, 0);

			SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);

			_dataset.addValue(hours[i], Misc.getString("LISTENING_TIMES"), formatter.format(c.getTime()));
		}

		CategoryPlot plot = (CategoryPlot) _chart.getPlot();
		NumberAxis axis = (NumberAxis) plot.getRangeAxis();

		axis.setAutoRangeStickyZero(false);

	}

	private void createChart()
	{
		// create the chart...
		_chart = ChartFactory.createLineChart(Misc.getString("LISTENING_TIMES"), // chart
				// title
				Misc.getString("HOUR_OF_DAY"), // domain axis label
				Misc.getString("SONG_COUNT"), // range axis label
				_dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips
				false // urls
				);

		CategoryPlot plot = (CategoryPlot) _chart.getPlot();
		plot.setRangePannable(true);
		plot.setRangeZeroBaselineVisible(false);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("LISTENING_TIMES_TOOLTIP")));

		ChartUtilities.applyCurrentTheme(_chart);

		// format the renderer after applying the theme
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
		renderer.setBaseFillPaint(Color.white);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

		// get rid of the little line above/next to the axis
		// plot.setRangeZeroBaselinePaint(Color.white);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		Misc.formatChart(plot);
		renderer.setSeriesPaint(0, Theme.getColorSet()[1]);

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
