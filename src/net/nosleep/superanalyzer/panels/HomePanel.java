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

package net.nosleep.superanalyzer.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import net.nosleep.superanalyzer.HomeWindow;
import net.nosleep.superanalyzer.Share;
import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.views.IStatisticView;
import net.nosleep.superanalyzer.analysis.views.PieRotator;
import net.nosleep.superanalyzer.analysis.views.SummaryView;
import net.nosleep.superanalyzer.analysis.views.TagView;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;

public class HomePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final int PANEL_SUMMARY = 1;
	public static final int PANEL_GENRE = 2;
	public static final int PANEL_LIKES = 3;
	public static final int PANEL_YEAR = 4;
	public static final int PANEL_RATING = 5;
	public static final int PANEL_TIME = 6;
	public static final int PANEL_QUALITY = 7;
	public static final int PANEL_MOSTAA = 8;
	public static final int PANEL_PLAYCOUNT = 9;
	public static final int PANEL_ENCODINGKIND = 10;
	public static final int PANEL_GROWTH = 11;
	public static final int PANEL_TAG = 12;
	public static final int PANEL_WORDS = 13;
	public static final int PANEL_MOSTDG = 14;

	private Analysis _analysis;
	private IStatisticView _currentView;
	private JPanel _containerPanel;
	private HomeWindow _homeWindow;

	public HomePanel(HomeWindow homeWindow, Analysis analysis)
	{
		_homeWindow = homeWindow;
		_analysis = analysis;

		setLayout(new BorderLayout());

		_containerPanel = new JPanel(new BorderLayout());
		_containerPanel.setOpaque(false);
		_containerPanel.setPreferredSize(new Dimension(850, 500));

		add(_containerPanel, BorderLayout.CENTER);
		add(new ButtonPanel(this), BorderLayout.SOUTH);

		showView(SummaryView.Id);
	}

	public void refresh()
	{
		showView(_currentView.getId());
	}

	public static TextTitle createSubtitle(String s)
	{
		TextTitle subtitle = new TextTitle(s);
		subtitle.setPosition(RectangleEdge.TOP);
		subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05, 0.05, 0.05));
		subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		return subtitle;
	}

	public void saveChartAsImage(JFrame window)
	{
		if (_currentView != null)
			Share.saveChartImage(window, _currentView);
	}

	public void showView(int viewId)
	{
		// tell the current view that it's going away
		if (_currentView != null)
			_currentView.willDisappear();

		// get the object of the requested view
		_currentView = _analysis.getView(viewId);

		// update the menu
		_homeWindow.setSaveImageMenuItemEnabled(_currentView.canSaveImage());

		// clear out the container panel and add the view's panel to it
		_containerPanel.removeAll();
		_containerPanel.add(_currentView.getPanel(this));
		_containerPanel.updateUI();
	}

	public JPanel createHeaderPanel(JComboBox<ComboItem> comboBox, boolean showTagWarning)
	{

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		if (comboBox != null)
		{
			comboBox.setOpaque(false);
			panel.add(comboBox, BorderLayout.WEST);
		}

		if (showTagWarning == true)
			panel.add(createWarningPanel(true), BorderLayout.EAST);

		return panel;
	}

	public JPanel createWarningPanel(boolean showText)
	{

		JPanel panel = new JPanel(new FlowLayout());
		panel.setOpaque(false);

		String label = null;
		if (showText == true)
			label = Misc.getString("SOME_TAGS_MISSING") + "...";

		URL url = this.getClass().getResource("/media/warning.png");
		JButton button = new JButton(label, new ImageIcon(Toolkit.getDefaultToolkit().getImage(url)));

		button.setBorder(new EmptyBorder(0, 0, 0, 0));

		button.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				showView(TagView.Id);
			}
		});

		panel.add(button);
		return panel;
	}
	
	public JPanel createRightButtonBarPanel(final PieRotator rotator)
	{
		URL url = this.getClass().getResource("/media/" + "ButtonPlay.png");
		final ImageIcon playIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		url = this.getClass().getResource("/media/" + "ButtonPause.png");
		final ImageIcon pauseIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		final JButton button = new JButton(playIcon);
		button.setBorder(null);
		button.setBorderPainted(false);
		button.setText(null);
		button.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if(rotator.isRunning() == false)
				{
					rotator.start();
					button.setIcon(pauseIcon);
				}
				else
				{
					rotator.stop();
					button.setIcon(playIcon);
				}
			}
		});
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBackground(Color.white);
		toolBar.add(button);

		JPanel barPanel = new JPanel(new BorderLayout());
		barPanel.setBackground(Color.white);
		barPanel.add(toolBar, BorderLayout.SOUTH);
		
		return barPanel;
	}
}
