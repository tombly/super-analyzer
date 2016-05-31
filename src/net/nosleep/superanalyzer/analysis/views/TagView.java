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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.TagCheck.TagCheckItem;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.panels.TextPanel;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class TagView implements IStatisticView
{
	public static final int Id = 9;

	private Analysis _analysis;
	private JPanel _gridPanel;
	private String _introString = Misc.getString("TAG_INSTRUCTIONS");

	public TagView(Analysis analysis)
	{
		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{
		_gridPanel = new JPanel(new GridLayout(10, 2));
		_gridPanel.setOpaque(false);

		Vector<TagCheckItem> tagPairs = _analysis.getTagCheck();

		for (int i = 0; i < tagPairs.size(); i++)
			addTagPair(_gridPanel, tagPairs.elementAt(i));
	}

	private void addTagPair(JPanel panel, TagCheckItem pair)
	{

		JLabel wordLabel = new JLabel(pair.Name + ": ");
		wordLabel.setOpaque(false);
		wordLabel.setFont(Theme.getFont(12));
		wordLabel.setHorizontalAlignment(JLabel.RIGHT);
		panel.add(wordLabel);

		JLabel wordField = new JLabel();
		wordField.setOpaque(false);
		wordField.setFont(Theme.getBoldFont(12));
		wordField.setBorder(new javax.swing.border.EmptyBorder(1, 1, 1, 1));
		wordField.setText(pair.getValue());
		wordField.setForeground(pair.getColor());
		panel.add(wordField);
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		JPanel subtitlePanel = new JPanel(new BorderLayout());
		JTextArea subtitleLabel = new JTextArea(_introString);
		subtitleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		subtitleLabel.setFont(Theme.getFont(12));
		subtitleLabel.setLineWrap(true);
		subtitleLabel.setEditable(false);
		subtitleLabel.setWrapStyleWord(true);
		subtitlePanel.setOpaque(false);
		subtitlePanel.add(subtitleLabel, BorderLayout.CENTER);

		JPanel fieldsPanel = new JPanel(new FlowLayout());
		fieldsPanel.setOpaque(false);
		fieldsPanel.add(_gridPanel);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(subtitlePanel, BorderLayout.NORTH);
		panel.add(fieldsPanel, BorderLayout.CENTER);

		TextPanel textPanel = new TextPanel(panel, Misc.getString("TAG_SUMMARY"), false, homePanel);

		return textPanel;
	}

	public boolean canSaveImage()
	{
		return false;
	}

	public void saveImage(File file, Dimension d) throws IOException
	{
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
