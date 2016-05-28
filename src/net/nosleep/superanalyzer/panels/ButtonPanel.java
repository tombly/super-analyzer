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
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.nosleep.superanalyzer.analysis.views.EncodingKindView;
import net.nosleep.superanalyzer.analysis.views.GenreView;
import net.nosleep.superanalyzer.analysis.views.GrowthView;
import net.nosleep.superanalyzer.analysis.views.AlbumLikesView;
import net.nosleep.superanalyzer.analysis.views.ArtistLikesView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedAAView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedDGView;
import net.nosleep.superanalyzer.analysis.views.PlayCountView;
import net.nosleep.superanalyzer.analysis.views.QualityView;
import net.nosleep.superanalyzer.analysis.views.RatingView;
import net.nosleep.superanalyzer.analysis.views.SummaryView;
import net.nosleep.superanalyzer.analysis.views.TimeView;
import net.nosleep.superanalyzer.analysis.views.WordView;
import net.nosleep.superanalyzer.analysis.views.YearView;
import net.nosleep.superanalyzer.util.Theme;

public class ButtonPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JButton addButton(String filename)
	{
		URL url = this.getClass().getResource("/media/" + filename);
		JButton button = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(url)));

		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setPreferredSize(new Dimension(100, 60));

		button.setAlignmentX(0.5f);
		button.setAlignmentY(0.5f);
		button.setFont(Theme.getFont(12));

		button.setBorder(null);
		button.setBorderPainted(false);

		button.setText(null);

		return button;
	}

	public ButtonPanel(final HomePanel homePanel)
	{
		setLayout(new BorderLayout());

		JPanel linePanel = new JPanel();
		linePanel.setPreferredSize(new Dimension(450, 1));
		linePanel.setOpaque(true);
		linePanel.setBackground(Color.GRAY);
		add(linePanel, BorderLayout.NORTH);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		JButton buttonBasics = addButton("tab_basics.png");
		buttonBasics.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(SummaryView.Id);
			}
		});
		toolBar.add(buttonBasics);

		JButton buttonGenres = addButton("tab_genres.png");
		buttonGenres.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(GenreView.Id);
			}
		});
		toolBar.add(buttonGenres);

		JButton buttonAlbumLikes = addButton("tab_like.png");
		buttonAlbumLikes.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(AlbumLikesView.Id);
			}
		});
		toolBar.add(buttonAlbumLikes);
		
		JButton buttonArtistLikes = addButton("tab_like.png");
		buttonArtistLikes.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(ArtistLikesView.Id);
			}
		});
		toolBar.add(buttonArtistLikes);

		JButton buttonYear = addButton("tab_year.png");
		buttonYear.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(YearView.Id);
			}
		});
		toolBar.add(buttonYear);

		JButton buttonRating = addButton("tab_rating.png");
		buttonRating.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(RatingView.Id);
			}
		});
		toolBar.add(buttonRating);

		JButton buttonTime = addButton("tab_time.png");
		buttonTime.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(TimeView.Id);
			}
		});
		toolBar.add(buttonTime);

		JButton buttonBitrate = addButton("tab_bitrate.png");
		buttonBitrate.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(QualityView.Id);
			}
		});
		toolBar.add(buttonBitrate);

		JButton buttonTop = addButton("tab_top.png");
		buttonTop.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(MostPlayedAAView.Id);
			}
		});
		toolBar.add(buttonTop);

		JButton buttonMostDG = addButton("tab_top.png");
		buttonMostDG.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(MostPlayedDGView.Id);
			}
		});
		toolBar.add(buttonMostDG);

		JButton buttonPlayCount = addButton("tab_playcount.png");
		buttonPlayCount.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(PlayCountView.Id);
			}
		});
		toolBar.add(buttonPlayCount);

		JButton buttonEncodingKind = addButton("tab_kind.png");
		buttonEncodingKind.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(EncodingKindView.Id);
			}
		});
		toolBar.add(buttonEncodingKind);

		JButton buttonGrowth = addButton("tab_growth.png");
		buttonGrowth.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(GrowthView.Id);
			}
		});
		toolBar.add(buttonGrowth);

		JButton buttonWords = addButton("tab_words.png");
		buttonWords.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				homePanel.showView(WordView.Id);
			}
		});
		toolBar.add(buttonWords);

		add(toolBar, BorderLayout.CENTER);
		setMaximumSize(new Dimension(400, 56));
	}

}
