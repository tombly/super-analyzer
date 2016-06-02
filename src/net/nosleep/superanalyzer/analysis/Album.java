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

import java.util.*;

/**
 * This class is a holder for album information. It includes a Stat object that
 * has statistics about an album, along with some other characteristics about
 * albums. It does <b>not</b> store the name of the album, since albums are hashed by
 * their name, so we do not want to store it twice.
 */
public class Album implements StatHolder
{

	// used to separate the album name from the artist name
	public static String Separator = "|||";
	public static String SeparatorRegEx = "\\|\\|\\|";

	// private String artistName;
	private Stat stats;
	private String genre;
	private boolean isCompilation;
	private String year;
	private Vector<Disc> discs;

	/**
	 * A simple constructor to set our data members.
	 */
	public Album(/* String artistName */)
	{

		// artistName = artistName;
		stats = new Stat();
		isCompilation = false;
		discs = new Vector<Disc>();
	}

	/**
	 * Tells the Stat object to analyze a track.
	 */
	public void analyze(Track track)
	{
		Integer discNumber = track.getDiscNumber();
		Integer tracksOnDisc = track.getTrackCount();

		// look through the discs for this album and see if there is already a
		// disc object for this disc number or not.
		Disc disc = null;
		Iterator<Disc> i = discs.iterator();
		while (i.hasNext())
		{
			Disc d = (Disc) i.next();
			if (discNumber != null && d.getDiscNumber() != null)
			{
				if (d.getDiscNumber().equals(discNumber))
				{
					disc = d;
					break;
				}
			}
		}
		// if we didn't find it, then make a new one
		if (disc == null)
			discs.add(new Disc(discNumber, tracksOnDisc));

		isCompilation = track.getCompilation();
		genre = track.getGenre();
		if (track.getYear() != null)
			year = track.getYear().toString();
		else
			year = "";
		stats.analyze(track);
	}

	/*
	 * public String getArtistName() { return artistName; }
	 */
	public Stat getStats()
	{
		return stats;
	}

	public Vector<Disc> getDiscs()
	{
		return discs;
	}

	public String getGenre()
	{
		return genre;
	}

	public String getYear()
	{
		return year;
	}

	public boolean getIsCompilation()
	{
		return isCompilation;
	}

}
