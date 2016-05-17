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
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

public class EncodingKindView implements IStatisticView
{

	public static final int Id = 1;

	private JComboBox _comboBox;
	private Analysis _analysis;
	private JFreeChart _chart;
	private ChartPanel _chartPanel;
	private DefaultPieDataset _dataset;
	private PieRotator _rotator;

	public EncodingKindView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();

		_rotator = new PieRotator((PiePlot3D) _chart.getPlot());
	}

	private void createPanel()
	{

		_comboBox = new JComboBox(_analysis.getComboBoxItems());
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
		panel.add(homePanel.createRightButtonBarPanel(_rotator), BorderLayout.EAST);

		return panel;
	}

	private void createChart()
	{

		_chart = ChartFactory.createPieChart3D(Misc.getString("KINDS_OF_MUSIC_FILES"), _dataset, false, true, false);

		PiePlot3D plot = (PiePlot3D) _chart.getPlot();
		plot.setDarkerSides(true);
		plot.setStartAngle(PieRotator.angle);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		plot.setNoDataMessage("No data to display");
		plot.setInsets(new RectangleInsets(10, 10, 10, 10));
		plot.setOutlineVisible(false);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));

		_chart.addSubtitle(HomePanel.createSubtitle(Misc.getString("KINDS_OF_MUSIC_FILES_SUBTITLE")));

		ChartUtilities.applyCurrentTheme(_chart);
		plot.setBackgroundPaint(Color.white);
		_chart.setBorderVisible(false);

		plot.setIgnoreZeroValues(true);

		// plot.setAutoPopulateSectionPaint(false);

		// Misc.formatChart(plot);
	}

	private void refreshDataset()
	{
		PiePlot3D plot = (PiePlot3D) _chart.getPlot();

		_dataset.clear();
		// plot.clearSectionPaints(true);

		Hashtable kindHash = null;

		if (_comboBox == null)
		{
			kindHash = _analysis.getEncodingKinds(Analysis.KIND_TRACK, null);
		}
		else
		{
			ComboItem item = (ComboItem) _comboBox.getSelectedItem();
			kindHash = _analysis.getEncodingKinds(item.getKind(), item.getValue());
		}

		Color[] colors = Theme.getColorSet();

		Enumeration e = kindHash.keys();
		int i = 0;
		while (e.hasMoreElements())
		{
			String kindName = (String) e.nextElement();
			Integer count = (Integer) kindHash.get(kindName);
			// if (count.intValue() > 0) {
			_dataset.setValue(kindName, new Double(count));
			plot.setSectionPaint(kindName, colors[5 - ((i) % colors.length)]);	// mod it in case we don't have enough colors
			i++;
			if (i > colors.length)
				i = 0;
			// }
		}

		// plot.notifyListeners(new PlotChangeEvent(plot));

		// _chartPanel.invalidate();
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