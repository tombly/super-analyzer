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
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Stat;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class RatingView implements IStatisticView
{

	public static final int Id = 7;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private DefaultPieDataset _dataset;
	private PieRotator _rotator;

	public RatingView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();

		_rotator = new PieRotator((PiePlot3D) _chart.getPlot());
		_rotator.start();
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

		_dataset = new DefaultPieDataset();

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

	private void createChart()
	{
		_chart = ChartFactory.createPieChart3D(Misc.getString("SONG_RATINGS"), _dataset, false, true, false);

		PiePlot3D plot = (PiePlot3D) _chart.getPlot();
		plot.setDarkerSides(true);
		plot.setStartAngle(PieRotator.angle);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		plot.setNoDataMessage(Misc.getString("NO_DATA_TO_DISPLAY"));
		plot.setInsets(new RectangleInsets(10, 10, 10, 10));
		plot.setOutlineVisible(false);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("SONG_RATINGS_SUBTITLE")));

		ChartUtilities.applyCurrentTheme(_chart);
		plot.setBackgroundPaint(Color.white);
		_chart.setBorderVisible(false);
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

		Vector<Double> counts = new Vector<Double>(11);
		double[] ratings = itemStats.getRatings();
		for (int i = 0; i < ratings.length; i++)
			counts.addElement(new Double(ratings[i]));

		Vector<String> labels = new Vector<String>(11);
		labels.add(new String(Misc.getString("NOT_RATED")));
		labels.add(new String("0.5 " + Misc.getString("STAR")));
		labels.add(new String("1 " + Misc.getString("STAR")));
		labels.add(new String("1.5 " + Misc.getString("STAR")));
		labels.add(new String("2 " + Misc.getString("STARS")));
		labels.add(new String("2.5 " + Misc.getString("STARS")));
		labels.add(new String("3 " + Misc.getString("STARS")));
		labels.add(new String("3.5 " + Misc.getString("STARS")));
		labels.add(new String("4 " + Misc.getString("STARS")));
		labels.add(new String("4.5 " + Misc.getString("STARS")));
		labels.add(new String("5 " + Misc.getString("STARS")));

		PiePlot3D plot = (PiePlot3D) _chart.getPlot();
		Color[] colors = Theme.getColorSet();
		plot.setIgnoreZeroValues(true);

		for (int i = 0; i < counts.size(); i++)
		{
			// if((Double)counts.elementAt(i) > 0)
			{
				_dataset.setValue((String) labels.elementAt(i), (Double) counts.elementAt(i));
				plot.setSectionPaint((String) labels.elementAt(i), colors[10 - i]);
			}
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
		_rotator.stop();
	}

}