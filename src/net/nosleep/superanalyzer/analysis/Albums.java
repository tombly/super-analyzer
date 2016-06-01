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

import java.awt.*;
import java.util.*;

import net.nosleep.superanalyzer.util.Constants;
import net.nosleep.superanalyzer.util.DPoint;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringInt;

/**
 * Describes a collection of albums. The keys are album names and the values are
 * Album objects.
 */
class Albums extends Hashtable
{

	Vector<StringInt> _mostPlayed;

	public void finish()
	{
		_mostPlayed = Analysis.findMostPlayed(this);
	}

	public Vector<StringInt> getMostPlayed()
	{
		return _mostPlayed;
	}

	/**
	 * Calculates the average completeness of all the albums in the library
	 * (i.e. what percentage of each album is complete?).
	 */
	@SuppressWarnings("unused")
	public double getAvgAlbumCompleteness()
	{
		long outOfSum = 0, trackCountSum = 0;

		// go through each album
		Enumeration keys = keys();
		while (keys.hasMoreElements())
		{

			String name = (String) keys.nextElement();
			Album album = (Album) get(name);

			int tracksInAlbum = 0;

			// go through each disc of each album and sum up the number of
			// tracks on each disc.
			Iterator d = album.getDiscs().iterator();
			while (d.hasNext())
			{
				Disc disc = (Disc) d.next();

				Integer tracksOnDisc = disc.getTracksOnDisc();
				if (tracksOnDisc == null)
					continue;

				tracksInAlbum += tracksOnDisc;
			}

			if (Constants.writeMissingToStdOut == true) //for debugging
			{
				if (tracksInAlbum != album.getStats().getTrackCount())
				{
					System.out.println("Album " + name + " is incomplete (" + album.getStats().getTrackCount() + "/"
							+ tracksInAlbum + ")");
				}

				if (album.getYear() == "")
				{
					System.out.println("Album " + name + " is missing the release year.");
				}

				if (album.getStats().getTrackCount() == 0)
				{
					System.out.println("Album " + name + " has a track count of 0.");
				}
			}

			outOfSum += tracksInAlbum;
			trackCountSum += album.getStats().getTrackCount();
		}

		return ((double) trackCountSum / (double) outOfSum) * 100.0;
	}

	/**
	 * Calculates the percentage of albums in the library that are complete
	 * (i.e. albums where all the tracks are present in the library).
	 */
	public double getAvgCompleteAlbums()
	{
		long completeAlbums = 0;
		long albumCount = 0;

		Enumeration e = elements();
		while (e.hasMoreElements())
		{
			int tracksInAlbum = 0;
			Album album = (Album) e.nextElement();

			// go through each disc of each album and sum up the number of
			// tracks on each disc.
			Iterator d = album.getDiscs().iterator();
			while (d.hasNext())
			{
				Disc disc = (Disc) d.next();

				Integer tracksOnDisc = disc.getTracksOnDisc();
				if (tracksOnDisc == null)
					continue;

				tracksInAlbum += tracksOnDisc;
			}

			long trackCount = album.getStats().getTrackCount();

			if (tracksInAlbum == trackCount)
				completeAlbums++;

			albumCount++;
		}

		return (double) completeAlbums / (double) albumCount * 100.0;
	}

	/**
	 * Creates a set of data points where each data point represents the average
	 * song play count and the average song rating for each album.
	 */
	@SuppressWarnings("rawtypes")
	public Vector getAlbumPlayCountVsRating()
	{
		Vector<DPoint> points = new Vector(1000);

		Enumeration keysEnumeration = keys();
		while (keysEnumeration.hasMoreElements())
		{
			String currentKey = (String) keysEnumeration.nextElement(); //the key is always the album name as a String
			Album a = (Album) (get(currentKey));
			Stat s = a.getStats();

			if (s.getAvgPlayCount() < 0.5 || s.getTrackCount() < 3) //if the average play count is less than 0.5 or the album has less than 3 tracks, it gets skipped
				continue;

			currentKey = currentKey.replace(Album.Separator, " " + Misc.getString("BY") + " "); //replace internal album separator by a readable separator
			points.add(new DPoint(s.getAvgPlayCount(), s.getAvgRating()/2, Analysis.getColor(a.getGenre()), currentKey, a.getGenre())); //dividing by two is easier than changing getAvgRating to double
			
		}

		return points;
	}

	public Vector getAlbumPlayCountVsAge()
	{
		Vector<DPoint> points = new Vector(1000);

		Enumeration keysEnumeration = keys();
		while (keysEnumeration.hasMoreElements())
		{
			String currentKey = (String) keysEnumeration.nextElement(); //the key is always the album name as a String
			Album a = (Album) get(currentKey);
			Stat s = a.getStats();

			// if (s.getAvgPlayCount() < 0.5)
			// continue;

			currentKey = currentKey.replace(Album.Separator, " " + Misc.getString("BY") + " "); //replace internal album separator by a readable separator
			points.add(new DPoint(s.getAvgPlayCount(), s.getAvgAge(), Analysis.getColor(a.getGenre()), currentKey, a.getGenre()));
		}

		return points;
	}

	/**
	 * Update the Stat object with the given track's info. Album objects are
	 * hashed by the artist+album names, unless the album is marked as a
	 * compilation, in which case the album is only hashed by the album name.
	 */
	public void analyze(Track track)
	{

		// get the artist and album name
		String albumName = track.getAlbum();
		String artistName = track.getArtist();

		// if it's a compilation, use "Various Artists"
		if (track.getCompilation() == true)
			artistName = "Various Artists";

		// use the album artist if there is one
		if (track.getAlbumArtist() != null)
			artistName = track.getAlbumArtist();

		// if there isn't an album or artist name, then we can't do anything
		// here
		if (albumName == null || artistName == null)
			return;

		// now we have to decide what to name this album. if it is not a
		// compilation, then we can just use the artist name.
		/*
		 * String key = null; if(track.getCompilation() == false) key =
		 * albumName + " by " + artistName; else { // it's a compilation, so
		 * lets see if the album has an artist and // if it does, use that
		 * if(track.getAlbumArtist() != null) key = albumName + " by " +
		 * track.getAlbumArtist(); else // as a worst case, just call the
		 * compilation album VA key = albumName + " by " + "Various Artists"; }
		 */
		String key = albumName + Album.Separator + artistName;

		Album album = (Album) get(key);

		// if not in there, then create a new album object and put it in
		if (album == null)
		{
			// create a new album object
			album = new Album();
			// add the object to the hash table
			// if( compilation )
			put(key, album);
		}

		// add this track's stats to the album object
		album.analyze(track);
	}

}
