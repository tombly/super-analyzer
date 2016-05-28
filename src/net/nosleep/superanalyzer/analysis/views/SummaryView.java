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
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.Stat;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.panels.TextPanel;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringTriple;
import net.nosleep.superanalyzer.util.Theme;

public class SummaryView implements IStatisticView
{

	public static final int Id = 8;

	private JComboBox<ComboItem> _comboBox;
	private Analysis _analysis;
	private JPanel _gridPanel;
	private Vector<StringTriple> _statPairs;

	public SummaryView(Analysis analysis)
	{

		_analysis = analysis;
		createPanel();
	}

	private void createPanel()
	{

		_gridPanel = new JPanel(new GridLayout(19, 2));
		_gridPanel.setOpaque(false);

		_statPairs = createStatPairs(_analysis);

		for (int i = 0; i < _statPairs.size(); i++)
			addStatTriple(_gridPanel, _statPairs.elementAt(i));
	}

	public JPanel getPanel(HomePanel homePanel)
	{

		TextPanel textPanel = new TextPanel(_gridPanel, Misc.getString("LIBRARY_SUMMARY"), _analysis.tagsAreIncomplete(), homePanel);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(textPanel, BorderLayout.CENTER);

		return panel;
	}

	private void addStatTriple(JPanel panel, StringTriple triple)
	{

		JLabel wordLabel = new JLabel(triple.Name + ": ");
		wordLabel.setOpaque(false);
		wordLabel.setFont(Theme.getFont(12));
		wordLabel.setHorizontalAlignment(JLabel.RIGHT);
		panel.add(wordLabel);

		JLabel wordField = new JLabel();
		wordField.setOpaque(false);
		wordField.setFont(Theme.getBoldFont(12));
		wordField.setBorder(new javax.swing.border.EmptyBorder(1, 1, 1, 1));
		wordField.setForeground(Theme.getColorSet()[0]);
		panel.add(wordField);

		wordField.setText(triple.Value);
		wordField.setToolTipText(Misc.getTooltip(triple.Info));
	}

	public static Vector createStatPairs(Analysis analysis)
	{

		Stat trackStats = analysis.getStats(Analysis.KIND_TRACK, null);

		String name;
		String value;
		String description;
		StringTriple triple;

		Vector statPairs = new Vector();

		name = Misc.getString("TRACK_COUNT");
		value = Misc.getFormattedCountAsInt(trackStats.getTrackCount(), Misc.getString("TRACK"), Misc.getString("TRACKS"));
		description = Misc.getString("TRACK_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("PLAY_COUNT");
		value = Misc.getFormattedCountAsInt(trackStats.getPlayCount(), Misc.getString("TRACK"), Misc.getString("TRACKS")) + " " + Misc.getString("PLAYED");
		description = Misc.getString("PLAY_COUNT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("TOTAL_TIME");
		value = Misc.getFormattedDuration(trackStats.getTotalTime());
		description = Misc.getString("TOTAL_TIME_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("TOTAL_PLAY_TIME");
		value = Misc.getFormattedDuration(trackStats.getTotalPlayTime());
		description = Misc.getString("TOTAL_PLAY_TIME_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("ARTIST_COUNT");
		value = Misc.getFormattedCountAsInt(analysis.getHash(Analysis.KIND_ARTIST).size(), Misc.getString("ARTIST"), Misc.getString("ARTISTS"));
		description = Misc.getString("ARTIST_COUNT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("ALBUM_COUNT");
		value = Misc.getFormattedCountAsInt(analysis.getHash(Analysis.KIND_ALBUM).size(), Misc.getString("ALBUM"), Misc.getString("ALBUMS"));
		description = Misc.getString("ALBUM_COUNT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("GENRE_COUNT");
		value = Misc.getFormattedCountAsInt(analysis.getHash(Analysis.KIND_GENRE).size(), Misc.getString("GENRE"), Misc.getString("GENRES"));
		description = Misc.getString("GENRE_COUNT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("MOST_SONGS_PLAYED_AT");
		int hour = trackStats.getPopularHour();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, 0);
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);
		value = formatter.format(c.getTime()) + " " + Misc.getString("HOUR_SUFFIX");
		description = Misc.getString("MOST_SONGS_PLAYED_AT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_TRACK_LENGTH");
		value = Misc.getFormattedDuration(trackStats.getAvgLength());
		description = Misc.getString("AVERAGE_TRACK_LENGTH_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_PLAY_COUNT");
		value = Misc.getFormattedCount(trackStats.getAvgPlayCount(), Misc.getString("PLAY"), Misc.getString("PLAYS")) + " " + Misc.getString("PER_SONG");
		description = Misc.getString("AVERAGE_PLAY_COUNT_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_ALBUM_COMPLETENESS");
		value = Misc.getFormattedPercentage(analysis.getAvgAlbumCompleteness()) + " " + Misc.getString("COMPLETE");
		description = Misc.getString("AVERAGE_ALBUM_COMPLETENESS_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("COMPLETE_ALBUMS");
		value = Misc.getFormattedPercentage(analysis.getAvgCompleteAlbums()) + " " + Misc.getString("ARE_COMPLETE");
		description = Misc.getString("COMPLETE_ALBUMS_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_BIT_RATE");
		value = Misc.getFormattedBitrate(analysis.getAvgBitRate());
		description = Misc.getString("AVERAGE_BIT_RATE_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_FILE_SIZE");
		value = Misc.getFormattedByteCount(analysis.getAvgFileSize());
		description = Misc.getString("AVERAGE_FILE_SIZE_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("TOTAL_LIBRARY_SIZE");
		value = Misc.getFormattedByteCount(analysis.getTotalLibrarySize());
		description = Misc.getString("TOTAL_LIBRARY_SIZE_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("ALL_SONGS_PLAYED");
		value = Misc.getFormattedPercentage(analysis.getAvgTracksPlayedAtLeastOnce()) + " " + Misc.getString("PLAYED_AT_LEAST_ONCE");
		description = Misc.getString("ALL_SONGS_PLAYED_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("COMPILATIONS");
		value = Misc.getFormattedPercentage(trackStats.getTrackCompilationPercentage());
		description = Misc.getString("COMPILATIONS_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("LIBRARY_AGE");
		value = Misc.getFormattedDuration(analysis.getLibraryAge());
		description = Misc.getString("LIBRARY_AGE_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		name = Misc.getString("AVERAGE_GROWTH_RATE");
		value = Misc.getFormattedCount(analysis.getAverageGrowthRate(), Misc.getString("SONG"), Misc.getString("SONGS")) + "/" + Misc.capitalizeByLocale(Misc.getString("WEEK"));
		description = Misc.getString("AVERAGE_GROWTH_RATE_TOOLTIP");
		triple = new StringTriple(name, value, description);
		statPairs.add(triple);

		return statPairs;
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
