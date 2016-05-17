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
import java.util.Vector;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ItemXYToolTipGenerator;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.SimpleXYDataSet;
import net.nosleep.superanalyzer.util.Theme;

public class LikesView implements IStatisticView
{

	public static final int Id = 4;

	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private XYDataset _dataset;

	public LikesView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{
		// TRIAL FEATURE
		Vector points = _analysis.getAlbumPlayCountVsRating();
		//Vector points = _analysis.getAlbumPlayCountVsAge();
		_dataset = new SimpleXYDataSet(points);

		createChart();
		_chartPanel = new ChartPanel(_chart);
		_chartPanel.setMouseWheelEnabled(true);
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel panel = new JPanel(new BorderLayout());

		JPanel headerPanel = homePanel.createHeaderPanel(null, _analysis.tagsAreIncomplete());
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(_chartPanel, BorderLayout.CENTER);

		return panel;
	}

	private void createChart()
	{
		_chart = ChartFactory.createScatterPlot(Misc.getString("LIKES_VS_PLAYS"), Misc.getString("ALBUM_PLAY_COUNT"), Misc.getString("ALBUM_RATING"), _dataset,
				PlotOrientation.VERTICAL, false, true, false);

		_chart.addSubtitle(HomePanel
				.createSubtitle(Misc.getString("LIKES_VS_PLAYS_SUBTITLE")));

		XYPlot plot = (XYPlot) _chart.getPlot();

		ChartUtilities.applyCurrentTheme(_chart);
		
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);

		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);

		// get rid of the little line above/next to the axis
		plot.setDomainZeroBaselinePaint(Color.white);
		plot.setRangeZeroBaselinePaint(Color.white);

		/*
		 * NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		 * domainAxis.setAutoRangeIncludesZero(false);
		 * 
		 * domainAxis.setTickMarkInsideLength(2.0f);
		 * domainAxis.setTickMarkOutsideLength(2.0f);
		 * 
		 * domainAxis.setMinorTickCount(2);
		 * domainAxis.setMinorTickMarksVisible(true);
		 */
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		/*
		 * rangeAxis.setTickMarkInsideLength(2.0f);
		 * rangeAxis.setTickMarkOutsideLength(2.0f);
		 * rangeAxis.setMinorTickCount(2);
		 * rangeAxis.setMinorTickMarksVisible(true);
		 */
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// format the line renderer after applying the theme
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		
		renderer.setBaseToolTipGenerator(new ItemXYToolTipGenerator());

		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
		renderer.setBaseFillPaint(Color.white);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));

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