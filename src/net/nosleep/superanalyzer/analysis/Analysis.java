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

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JProgressBar;

import net.nosleep.superanalyzer.analysis.views.AgePlaycountView;
import net.nosleep.superanalyzer.analysis.views.AlbumLikesView;
import net.nosleep.superanalyzer.analysis.views.ArtistLikesView;
import net.nosleep.superanalyzer.analysis.views.ArtistLikesView2;
import net.nosleep.superanalyzer.analysis.views.EncodingKindView;
import net.nosleep.superanalyzer.analysis.views.GenreView;
import net.nosleep.superanalyzer.analysis.views.GrowthView;
import net.nosleep.superanalyzer.analysis.views.IStatisticView;
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
import net.nosleep.superanalyzer.util.DPoint;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringComparator;
import net.nosleep.superanalyzer.util.StringInt;

/**
 * This is the core of the application. When the user tells the UI object to
 * perform an analysis, it extracts all the track info from the library file and
 * then gives the track info to an Analysis object to analyze.<br><br>
 * 
 * The Analysis object is just a container for collections of information about
 * the music library. There are 5 primary categories of information collected
 * about the library: artists, albums, genres, tracks, and decades.<br><br>
 * 
 * All this class does is make sure that each collection of information gets a
 * chance to analyze every track in the library.
 */
public class Analysis
{
	/**
	 * There are five basic kinds of statistics:<br>
	 * TRACK = 1<br>
	 * ARTIST = 2<br>
	 * ALBUM = 3<br>
	 * DECADE = 4<br>
	 * GENRE = 5<br>

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

	private Vector<ComboItem> _comboBoxItems;
	
	private static Hashtable<String, Color> genreColors = new Hashtable<>(15);

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
		_comboBoxItems = new Vector<ComboItem>();

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
		case AlbumLikesView.Id:
			return new AlbumLikesView(this);
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
		case ArtistLikesView.Id:
			return new ArtistLikesView(this);
		case AgePlaycountView.Id:
			return new AgePlaycountView(this);
		case ArtistLikesView2.Id:
			return new ArtistLikesView2(this);

		default:
			return null;
		}
	}

	public Vector<ComboItem> getComboBoxItems()
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
	
	/**
	 * Returns a new color for a data point (which means string which is the genre)
	 */
	public static Color getColor(String genre)
	{
		Color color = null;

		if (genre == null)
			return Color.black;

		color = genreColors.get(genre);
		if (color == null)
		{
			// it's not in there, so get a new color and add it
			color = getNextColor(genreColors.size());
			genreColors.put(genre, color);
		}

		return color;
	}

	/**
	 * Returns a new color. Colors are meant to be as different from each other
	 * as possible.
	 */
	private static Color getNextColor(int i)
	{
		switch (i)
		{
		case 0:
			return new Color(255, 0, 0);
		case 1:
			return new Color(0, 255, 0);
		case 2:
			return new Color(0, 0, 255);
		case 3:
			return new Color(255, 255, 0);
		case 4:
			return new Color(255, 0, 255);
		case 5:
			return new Color(0, 255, 255);
		case 6:
			return new Color(155, 0, 0);
		case 7:
			return new Color(0, 155, 0);
		case 8:
			return new Color(0, 0, 155);
		case 9:
			return new Color(155, 155, 0);
		case 10:
			return new Color(155, 0, 155);
		case 11:
			return new Color(0, 155, 155);
		case 12:
			return new Color(255, 155, 0);
		case 13:
			return new Color(155, 255, 0);
		case 14:
			return new Color(255, 0, 155);
		case 15:
			return new Color(155, 0, 255);
		case 16:
			return new Color(0, 155, 255);
		case 17:
			return new Color(0, 255, 155);
		case 18:
			return new Color(255, 127, 127);
		case 19:
			return new Color(127, 255, 127);
		case 20:
			return new Color(127, 127, 255);
		case 21:
			return new Color(255, 255, 127);
		case 22:
			return new Color(255, 127, 255);
		case 23:
			return new Color(127, 255, 255);
		case 24:
			return new Color(155, 127, 127);
		case 25:
			return new Color(127, 155, 127);
		case 26:
			return new Color(127, 127, 155);
		case 27:
			return new Color(155, 155, 127);
		case 28:
			return new Color(155, 127, 155);
		case 29:
			return new Color(127, 155, 155);
		case 30:
			return new Color(255, 155, 127);
		case 31:
			return new Color(155, 255, 127);
		case 32:
			return new Color(255, 127, 155);
		case 33:
			return new Color(155, 127, 255);
		case 34:
			return new Color(127, 155, 255);
		case 35:
			return new Color(127, 255, 155);
		}

		return Color.black;
	}
	
	
	
	public Vector getAlbumPlayCountVsRating()
	{
		return albums.getAlbumPlayCountVsRating();
	}
	
	public Vector<DPoint> getArtistPlayCountVsRating(boolean splitByGenre)
	{
		if (Analysis.genreColors.isEmpty()){
			getAlbumPlayCountVsRating(); //make sure the genre colors are the same no matter what chart is viewed first
		}
		
		return artists.getArtistPlayCountVsRating(splitByGenre);
	}

	public Vector getAlbumPlayCountVsAge()
	{
		if (Analysis.genreColors.isEmpty()){
			getAlbumPlayCountVsRating(); //make sure the genre colors are the same no matter what chart is viewed first
		}
		
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

	/**
	 * 
	 * @param the kind as specified with the constants
	 * @return the hashtable of this type
	 */
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
