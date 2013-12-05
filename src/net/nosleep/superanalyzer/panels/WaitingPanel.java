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

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class WaitingPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JProgressBar _progressBar;

	public WaitingPanel()
	{
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());
		_progressBar = new JProgressBar();
		_progressBar.setOpaque(false);
		panel.add(_progressBar, BorderLayout.CENTER);

		int h = (getHeight() - 20) / 2;
		panel.setBorder(new javax.swing.border.MatteBorder(h, 70, h, 70, Color.white));
		panel.setBackground(Color.white);

		add(panel, BorderLayout.CENTER);
		updateUI();
	}

	public JProgressBar getProgressBar()
	{
		return _progressBar;
	}
}
