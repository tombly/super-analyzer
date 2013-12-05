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

package net.nosleep.superanalyzer.analysis;

import javax.swing.*;

import net.nosleep.superanalyzer.analysis.views.EncodingKindView;
import net.nosleep.superanalyzer.analysis.views.GenreView;
import net.nosleep.superanalyzer.analysis.views.GrowthView;
import net.nosleep.superanalyzer.analysis.views.IStatisticView;
import net.nosleep.superanalyzer.analysis.views.LikesView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedAAView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedDGView;
import net.nosleep.superanalyzer.analysis.views.PlayCountView;
import net.nosleep.superanalyzer.analysis.views.QualityView;
import net.nosleep.superanalyzer.analysis.views.RatingView;
import net.nosleep.superanalyzer.analysis.views.SummaryView;
import net.nosleep.superanalyzer.analysis.views.TagView;
import net.nosleep.superanalyzer.analysis.views.TimeView;

import net.nosleep.superanalyzer.analysis.views.WordView;
import net.nosleep.superanalyzer.analysis.views.YearView;
import net.nosleep.superanalyzer.util.ComboItem;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringComparator;
import net.nosleep.superanalyzer.util.StringInt;

import java.io.File;
import java.util.*;

/**
 * This is the core of the application. When the user tells the UI object to
 * perform an analysis, it extracts all the track info from the library file and
 * then gives the track info to an Analysis object to analyze.
 * 
 * The Analysis object is just a container for collections of information about
 * the music library. There are 5 primary categories of information collected
 * about the library: artists, albums, genres, tracks, and decades.
 * 
 * All this class does is make sure that each collection of information gets a
 * chance to analyze every track in the library.
 */
public class Analysis
{
	/*
	 * There are five basic kinds of statistics
	 */
	public final static int KIND_TRACK = 1;
	public final static int KIND_ARTIST = 2;
	public final static int KIND_ALBUM = 3;
	public final static int KIND_DECADE = 4;
	public final static int KIND_GENRE = 5;

	// each of these analyzes tracks and stores statistics about them in each
	// of their respective interests
	private Artists artists;
	private Genres genres;
	private Albums albums;
	private Decades decades;
	private Tracks tracks;

	private Vector _comboBoxItems;

	/**
	 * Just sets some data members for use in the analyze() method.
	 */
	public Analysis()
	{

		tracks = new Tracks();
		artists = new Artists();
		genres = new Genres();
		albums = new Albums();
		decades = new Decades();
	}

	public void analyze(File file, JProgressBar progressBar)
	{
		Misc.printMemoryInfo("start");

		// parse the file
		FileParser fileParser = new FileParser(this, file, progressBar);
		fileParser.parse();

		Misc.printMemoryInfo("parse");
		
		// finalize the statistics and free any memory we can
		tracks.finish();
		artists.finish();
		albums.finish();
		decades.finish();
		genres.finish();

		Misc.printMemoryInfo("finish");

		// clean up any memory we can
		System.gc();
		Misc.printMemoryInfo("gc");

		// create the album/artist/decade combo boxes
		createComboBoxItems();

		Misc.printMemoryInfo("combo");
	}

	/**
	 * Creates a list of all decades, genres, artists, and albums as combo box
	 * items.
	 */
	private void createComboBoxItems()
	{
		_comboBoxItems = new Vector();

		ComboItem comboItem = new ComboItem(Misc.getString("SHOW_ALL"), Analysis.KIND_TRACK);
		_comboBoxItems.addElement(comboItem);

		addCollectionToShowVector(decades, Misc.getString("DECADE"), _comboBoxItems, Analysis.KIND_DECADE);
		addCollectionToShowVector(genres, Misc.getString("GENRE"), _comboBoxItems, Analysis.KIND_GENRE);
		addCollectionToShowVector(artists, Misc.getString("ARTIST"), _comboBoxItems, Analysis.KIND_ARTIST);
		addCollectionToShowVector(albums, Misc.getString("ALBUM"), _comboBoxItems, Analysis.KIND_ALBUM);
	}

	private void addCollectionToShowVector(Hashtable hash, String prefix, Vector vector, int kind)
	{
		String items[] = new String[hash.size()];
		int index = 0;

		Enumeration keys = hash.keys();
		while (keys.hasMoreElements())
		{

			String key = (String) keys.nextElement();
			key = key.replace(Album.Separator, " " + Misc.getString("BY") + " ");
			items[index] = key;
			index++;
		}

		Arrays.sort(items, new StringComparator());

		for (int i = 0; i < hash.size(); i++)
		{
			ComboItem item = new ComboItem(Misc.getString("SHOW") + " " + prefix + ": " + items[i], kind);
			vector.addElement(item);
		}
	}

	/**
	 * Creates new collections for each of the primary categories, then steps
	 * through every track and lets each collection analyze it.
	 */
	public void analyze(Track track)
	{

		// analyze the track
		tracks.analyze(track);

		// analyze the genre - populates the genres hash table
		genres.analyze(track);

		// analyze the artist - populates the artists hash table
		artists.analyze(track);

		// analyze the album - populates the album hash table
		albums.analyze(track);

		// analyze the decade - populates the decade hash table
		decades.analyze(track);
	}

	public IStatisticView getView(int id)
	{

		switch (id)
		{

		case EncodingKindView.Id:
			return new EncodingKindView(this);
		case GenreView.Id:
			return new GenreView(this);
		case GrowthView.Id:
			return new GrowthView(this);
		case LikesView.Id:
			return new LikesView(this);
		case PlayCountView.Id:
			return new PlayCountView(this);
		case QualityView.Id:
			return new QualityView(this);
		case RatingView.Id:
			return new RatingView(this);
		case SummaryView.Id:
			return new SummaryView(this);
		case TagView.Id:
			return new TagView(this);
		case TimeView.Id:
			return new TimeView(this);
		case MostPlayedAAView.Id:
			return new MostPlayedAAView(this);
		case MostPlayedDGView.Id:
			return new MostPlayedDGView(this);
		case WordView.Id:
			return new WordView(this);
		case YearView.Id:
			return new YearView(this);

		default:
			return null;
		}
	}

	public Vector getComboBoxItems()
	{
		return _comboBoxItems;
	}

	public boolean tagsAreIncomplete()
	{
		return tracks.getTagCheck().tagsAreIncomplete();
	}

	public Hashtable getEncodingKinds(int kind, String name)
	{
		Stat stats = getStats(kind, name);
		return stats.getEncodingKinds();
	}

	public Hashtable getDatesAdded(int kind, String name)
	{
		Stat stats = getStats(kind, name);
		return stats.getDatesAdded();
	}

	public Vector getAlbumPlayCountVsRating()
	{
		return albums.getAlbumPlayCountVsRating();
	}

	public Vector getAlbumPlayCountVsAge()
	{
		return albums.getAlbumPlayCountVsAge();
	}

	public double getAvgAlbumCompleteness()
	{
		return albums.getAvgAlbumCompleteness();
	}

	public double getAvgCompleteAlbums()
	{
		return albums.getAvgCompleteAlbums();
	}

	public double getAvgBitRate()
	{
		return tracks.getAvgBitRate();
	}

	public double getAvgFileSize()
	{
		return tracks.getAvgFileSize();
	}

	public double getTotalLibrarySize()
	{
		return tracks.getTotalLibrarySize();
	}

	public double getAvgTracksPlayedAtLeastOnce()
	{
		return tracks.getAvgTracksPlayedAtLeastOnce();
	}

	public double getLibraryAge()
	{
		return tracks.getLibraryAge();
	}

	public double getAverageGrowthRate()
	{
		return tracks.getAverageGrowthRate();
	}

	public Vector getMostCommonWords()
	{
		return tracks.getMostCommonWords();
	}

	public Vector getTagCheck()
	{
		return tracks.getTagCheck().getPairs();
	}

	public Hashtable getHash(int kind)
	{
		switch (kind)
		{
		case KIND_ALBUM:
			return albums;
		case KIND_DECADE:
			return decades;
		case KIND_ARTIST:
			return artists;
		case KIND_GENRE:
			return genres;
		default:
			return null;
		}
	}

	public Stat getStats(int kind, String name)
	{

		// tracks can't be filtered by a name, so we just return all of them
		if (kind == KIND_TRACK || name == null)
			return tracks.getStats();

		// get the hash table we're working with
		Hashtable hash = getHash(kind);

		// get the items from the collection that match our search name
		StatHolder item = (StatHolder) hash.get(name);
		if (item == null)
		{
			System.out.println("ERROR: unable to find selected item: " + name);
			return tracks.getStats();
		}
		else
		{
			return item.getStats();
		}
	}

	private static boolean vectorContainsString(Vector<StringInt> v, String s)
	{
		Iterator i = v.iterator();
		while (i.hasNext())
		{
			StringInt vs = (StringInt) i.next();
			if (vs.StringVal.compareTo(s) == 0)
				return true;
		}
		return false;
	}

	private static void topSearch(Vector<StringInt> chosenNames, boolean byRating, Hashtable hash)
	{
		int count = 0;
		int topValue = 0;
		String topString = "";

		Enumeration keys = hash.keys();
		while (keys.hasMoreElements())
		{

			String name = (String) keys.nextElement();
			StatHolder holder = (StatHolder) hash.get(name);

			int value;

			// skip any with no name
			if (name == null)
				continue;

			// skip any items that are from an unknown album
			if (name.indexOf("unknown album") != -1)
				continue;

			if (byRating)
				value = holder.getStats().getRating();
			else
				// (by play count)
				value = holder.getStats().getPlayCount();

			if (value > topValue && !vectorContainsString(chosenNames, name))
			{
				topValue = value;
				topString = name;
			}

			count++;
		}

		chosenNames.addElement(new StringInt(topString, topValue));
	}

	/**
	 * we just do topCount number of passes through the collection sure, it
	 * might take a sec, but it's soooo clean to program it
	 */
	public static Vector<StringInt> findMostPlayed(Hashtable hash)
	{

		Vector<StringInt> items = new Vector<StringInt>();

		// adds the highest rated item to the vector on each iteration
		for (int i = 0; i < 20; i++)
			topSearch(items, false, hash);

		return items;
	}

	public Vector<StringInt> getMostPlayedArtists()
	{
		return artists.getMostPlayed();
	}

	public Vector<StringInt> getMostPlayedAlbums()
	{
		return albums.getMostPlayed();
	}

	public Vector<StringInt> getMostPlayedDecades()
	{
		return decades.getMostPlayed();
	}

	public Vector<StringInt> getMostPlayedGenres()
	{
		return genres.getMostPlayed();
	}
}
