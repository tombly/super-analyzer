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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import net.nosleep.superanalyzer.util.DPoint;
import net.nosleep.superanalyzer.util.StringInt;

/**
 * Describes a collection of artists. The keys are artist names and the values
 * are Artist objects.
 */
class Artists extends Hashtable
{

	Vector<StringInt> _mostPlayed;

	public Artists()
	{
	}

	public void finish()
	{
		_mostPlayed = Analysis.findMostPlayed(this);
	}

	public Vector<StringInt> getMostPlayed()
	{
		return _mostPlayed;
	}

	/**
	 * Creates a set of data points where each data point represents the average
	 * song play count and the average song rating for each artist.
	 */
	@SuppressWarnings("rawtypes")
	public Vector<DPoint> getArtistPlayCountVsRating(Vector<String> artistsList)
	{
		Vector<DPoint> points = new Vector<DPoint>(500);
				
		for (Iterator<String> artistListIterator = artistsList.iterator(); artistListIterator.hasNext(); ) //iterates through the artists as determined by Analysis.java
		{
			String currentKey = (String) artistListIterator.next(); //the key is always the artist name as a String
			Artist a = (Artist) (get(currentKey));
			if (a == null){
				System.out.println("Artist not found: " + currentKey);
				continue;
			}
			Stat s = a.getStats();

			if (s.getAvgPlayCount() < 0.2 || s.getTrackCount() < 3) //if the average play count is less than 0.2 or the artist has less than 3 tracks, it gets skipped
				continue;
			points.add(new DPoint(s.getAvgPlayCount(), s.getAvgRating()/2, Analysis.getColor(a.getGenre()), currentKey, a.getGenre())); //dividing by two is easier than changing getAvgRating to double
		}

		return points;
	}
	
	public void analyze(Track track)
	{
		// get the artist name
		String artistName = track.getArtist();

		// if there is an album artist, and it's not a compilation, use that
		// one. if we use the album artist for compilations, then users who set
		// the album artist to "Various Artists" will get all their compilations
		// grouped together as a single artist, which isn't good.
		if (track.getAlbumArtist() != null && track.getCompilation() == false)
			artistName = track.getAlbumArtist();

		// don't collect artist info if we don't know what artist it is
		if (artistName == null)
			return;

		// now see if that artist is in the hashtable yet
		Artist artist = (Artist) get(artistName);

		// if not in there, then create a new artist oject and put it in
		if (artist == null)
		{
			// create a new artist object
			artist = new Artist();
			// add the object to the hash table
			put(artistName, artist);
		}

		// add this track's stats to the artist object
		artist.analyze(track);
	}

}
