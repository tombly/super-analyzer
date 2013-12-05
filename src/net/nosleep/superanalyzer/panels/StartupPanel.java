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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;

import net.nosleep.superanalyzer.HomeWindow;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class StartupPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public StartupPanel(final HomeWindow parent)
	{
		setOpaque(false);

		JPanel panel = new JPanel();
		LayoutManager overlay = new OverlayLayout(panel);
		panel.setLayout(overlay);
		panel.setOpaque(false);

		URL url = this.getClass().getResource("/media/chart.png");
		JButton button = new JButton(Misc.getString("ANALYZE"), new ImageIcon(Toolkit.getDefaultToolkit().getImage(url)));
		button.setOpaque(false);
		button.setMargin(new Insets(5,15,5,15));
		button.setAlignmentX(0.5f);
		button.setAlignmentY(0.5f);
		button.setFont(Theme.getFont(12));
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);

		button.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				parent.performAnalysis();
			}
		});

		panel.add(button);

		panel.setPreferredSize(new Dimension(560, 425));
		panel.setOpaque(false);

		add(panel);
	}

}
