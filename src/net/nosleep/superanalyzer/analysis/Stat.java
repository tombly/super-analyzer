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

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jfree.data.time.Day;

/**
 * This is a general container for statistical information. The stats held in
 * this container can describe any of the primary categories; e.g. the
 * trackCount could refer to how many tracks are by a certain artist, or in a
 * certain genre, etc. So, each collection has one of these objects that stores
 * the core statistics for the collection.<br><br>
 * 
 * Note: Since there are so many of these objects (one for every artist/album/
 * genre/decade) this class needs to be as optimized as possible in its memory
 * requirements and speed of execution.
 */
public class Stat
{
	private int trackCount;
	private int playCountSum;

	// this counts how many tracks have been played at least once
	private int tracksPlayed;

	// how many tracks are part of a compilation
	private int compilationCount;

	private int totalPlayTime;
	private int totalTime;
	private int[] hours;
	private double[] ratings;
	private int[] bitRate;
	private Hashtable trackYear;
	private Hashtable playYear;
	private Hashtable playCounts;
	private Hashtable encodingKind;
	private Hashtable datesAdded;

	// we can't count words for each album/artist/decade, since there would
	// be too many
	// private WordCounter wordCounter;

	/**
	 * Initializes all the data members.
	 */
	public Stat()
	{
		reset();
	}

	/**
	 * Initializes all the data members.
	 */
	public void reset()
	{
		trackCount = 0;
		playCountSum = 0;
		tracksPlayed = 0;
		totalPlayTime = 0;
		totalTime = 0;
		compilationCount = 0;
		hours = new int[24];
		ratings = new double[11];
		bitRate = new int[5];
		trackYear = new Hashtable();
		playYear = new Hashtable();

		playCounts = new Hashtable();
		encodingKind = new Hashtable();
		datesAdded = new Hashtable();

		// wordCounter = new WordCounter();
	}

	/**
	 * Returns a sum of ratings for this category.
	 */
	public int getRating()
	{
		int sum = 0;

		for (int i = 0; i < ratings.length; i++)
			sum += ratings[i] * i;

		return sum;
	}

	public int getAgeSum()
	{
		int sum = 0;

		Enumeration days = datesAdded.keys();
		while(days.hasMoreElements() == true)
		{
			Day day = (Day)days.nextElement();
			Integer count = (Integer)datesAdded.get(day);
		
			long now = System.currentTimeMillis();
			
			long diff = (now - day.getMiddleMillisecond())/1000;
			
			sum += (diff/(60*60*24))*count;
		}

		return sum;
	}

	/**
	 * Analyzes a track and updates all the internal statistics based on the
	 * track.
	 */
	public void analyze(Track track)
	{
		// increment the track count
		trackCount++;

		Integer playCount = track.getPlayCount();

		// accumulate the play count
		playCountSum += playCount;

		// increment the count of play count (playcount is a hashset)
		Integer playCountCount = (Integer) playCounts.get(new Integer(playCount));
		if (playCountCount == null)
			playCounts.put(new Integer(playCount), new Integer(1));
		else
			playCounts.put(new Integer(playCount), new Integer(playCountCount.intValue() + 1));

		// see if this track has been played at least once
		if (playCount > 0)
			tracksPlayed++;

		// wordCounter.checkWords(track);

		// increment the hour-of-day counts
		Date playDate = track.getPlayDate();
		if (playDate != null)
		{
			Calendar c = Calendar.getInstance();
			c.setTime(playDate);
			hours[c.get(Calendar.HOUR_OF_DAY)]++;
		}

		// accumulate the total time
		if (track.getTotalTime() != null)
			totalTime += track.getTotalTime().longValue();

		// accumulate the total play time
		if (track.getPlayCount() != null && track.getTotalTime() != null)
			totalPlayTime += track.getPlayCount().longValue() * track.getTotalTime().longValue();

		// increment the rating counts
		if (track.getRatingStar() != null)
			ratings[track.getRatingStar().intValue()]++;

		if (track.getCompilation() != null && track.getCompilation() == true)
			compilationCount++;

		// increment the bitrate counts
		if (bitRate != null && track.getBitRate() != null)
		{
			int b = track.getBitRate().intValue();
			if (b < 64)
				bitRate[0]++;
			if (b >= 64 && b < 128)
				bitRate[1]++;
			if (b >= 128 && b < 192)
				bitRate[2]++;
			if (b >= 192 && b < 256)
				bitRate[3]++;
			if (b >= 256)
				bitRate[4]++;
		}

		if (track.getYear() != null)
		{
			// get the year of the track
			int y = track.getYear().intValue();

			// increment the year counts
			Integer trackYearCount = (Integer) trackYear.get(new Integer(y));
			if (trackYearCount == null)
				trackYear.put(new Integer(y), new Integer(1));
			else
				trackYear.put(new Integer(y), new Integer(trackYearCount.intValue() + 1));

			// accumulate the year plays
			if (track.getPlayCount() != null)
			{
				Integer playYearCount = (Integer) playYear.get(new Integer(y));
				if (playYearCount == null)
				{
					int pc = 0;
					if (track.getPlayCount() != null)
						pc = track.getPlayCount().intValue();
					playYear.put(new Integer(y), new Integer(pc));
				}
				else
					playYear.put(new Integer(y),
							new Integer(playYearCount.intValue() + track.getPlayCount().intValue()));
			}
		}

		// remember the encoding kind of this file
		if (track.getKind() != null)
		{
			Integer count = (Integer) encodingKind.get(new String(track.getKind()));
			if (count == null)
				encodingKind.put(track.getKind(), new Integer(1));
			else
				encodingKind.put(track.getKind(), new Integer(count.intValue() + 1));

		}

		// remember when the track was added
		if (track.getDateAdded() != null)
		{
			Day day = new Day(track.getDateAdded());

			if (day != null)
			{
				Integer count = (Integer) datesAdded.get(day);
				if (count == null)
					datesAdded.put(day, new Integer(1));
				else
					datesAdded.put(day, new Integer(count.intValue() + 1));
			}
		}

	}

	/**
	 * Returns the hour of the day with the most songs played.
	 */
	public int getPopularHour()
	{
		int popularValue = hours[0], popularHour = 0;
		for (int i = 0; i < 24; i++)
		{
			if (hours[i] > popularValue)
			{
				popularValue = hours[i];
				popularHour = i;
			}
		}
		return popularHour;
	}

	public double getTrackCompilationPercentage()
	{
		return ((double) compilationCount / (double) trackCount) * 100.0;
	}

	/**
	 * Calculates the average length of track in seconds.
	 */
	public double getAvgLength()
	{
		return (double) totalTime / (double) trackCount;
	}

	/**
	 * Calculates the average number of times each track has been played.
	 */
	public double getAvgPlayCount()
	{
		return (double) playCountSum / (double) trackCount;
	}

	/**
	 * Calculates the average rating of the tracks going from 0 (0 stars) to 10 (5 stars)
	 */
	public double getAvgRating()
	{
		return (double) getRating() / (double) trackCount;
	}

	public double getAvgAge()
	{
		return (double) getAgeSum() / (double) trackCount;
	}

	public int getTrackCount()
	{
		return trackCount;
	}

	public int getPlayCount()
	{
		return playCountSum;
	}

	public Hashtable getYears()
	{
		return trackYear;
	}

	public Hashtable getPlayYears()
	{
		return playYear;
	}

	public int getTotalPlayTime()
	{
		return totalPlayTime;
	}

	public int getTotalTime()
	{
		return totalTime;
	}

	public int[] getHours()
	{
		return hours;
	}

	public double[] getRatings()
	{
		return ratings;
	}

	public int[] getBitRates()
	{
		return bitRate;
	}

	public Hashtable getPlayCounts()
	{
		return playCounts;
	}

	public int getTrackCountPlayedAtLeastOnce()
	{
		return tracksPlayed;
	}

	public Hashtable getEncodingKinds()
	{
		return encodingKind;

	}

	public Hashtable getDatesAdded()
	{
		return datesAdded;

	}

}
