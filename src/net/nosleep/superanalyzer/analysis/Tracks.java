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
import java.util.Vector;

/**
 * This class describes a collection of tracks. This Stat object contains
 * statistics for the overall library.
 * 
 * There are too many tracks to store them all, so this collection does not use
 * the "items" data memeber of StatCollection. It just populats the Stat object
 * and collects information about missing tag information.
 * 
 * We always keep the hashtable empty since we can't store all the tracks in it
 */
class Tracks
{
	private Stat stats;

	private TagCheck tagCheck;

	private long bitRateSum;
	private long bitRateTrackCount;
	private long fileSizeSum;
	private long fileSizeTrackCount;

	private Date earliestAddDate;
	private WordCounterMemory wordCounter;

	/**
	 * A simple constructor that instanciates our Stat object and resets all our
	 * missing tag accumulators.
	 */
	public Tracks()
	{

		stats = new Stat();

		tagCheck = new TagCheck();

		earliestAddDate = null;

		bitRateSum = 0;
		bitRateTrackCount = 0;

		fileSizeSum = 0;
		fileSizeTrackCount = 0;

		wordCounter = new WordCounterMemory();
	}

	public void finish()
	{
		wordCounter.finish();
		tagCheck.finish();
	}

	@SuppressWarnings("rawtypes")
	public Vector getMostCommonWords()
	{
		return wordCounter.getMostCommonWords();
	}

	/**
	 * Update the Stat object with the given track's info.
	 */
	public void analyze(Track track)
	{
		stats.analyze(track);
		tagCheck.check(track);
		wordCounter.checkWords(track);

		if (track.getBitRate() != null)
		{
			bitRateSum += track.getBitRate().intValue();
			bitRateTrackCount++;
		}

		if (track.getSize() != null)
		{
			fileSizeSum += track.getSize().longValue();
			fileSizeTrackCount++;
		}

		if (track.getDateAdded() != null)
		{
			if (earliestAddDate == null)
				earliestAddDate = track.getDateAdded();
			else
			{
				if (track.getDateAdded().before(earliestAddDate))
					earliestAddDate = track.getDateAdded();
			}
		}
	}

	public double getAvgBitRate()
	{
		return (double) bitRateSum / (double) bitRateTrackCount;
	}

	public double getAvgFileSize()
	{
		return (double) fileSizeSum / (double) fileSizeTrackCount;
	}

	public double getTotalLibrarySize()
	{
		return fileSizeSum;
	}

	public double getAvgTracksPlayedAtLeastOnce()
	{
		return ((double) stats.getTrackCountPlayedAtLeastOnce() / (double) stats.getTrackCount()) * 100.0;
	}

	public double getAverageGrowthRate()
	{
		if (earliestAddDate != null)
		{
			Date now = Calendar.getInstance().getTime();
			long seconds = now.getTime() - earliestAddDate.getTime();
			long weeks = (seconds / 1000) / (60 * 60 * 24 * 7);

			if (weeks > 0)
				return (double) stats.getTrackCount() / (double) weeks;
			else
				return 0;
		}
		else
			return 0;
	}

	/**
	 * Returns the age of the library in seconds.
	 */
	public double getLibraryAge()
	{
		if (earliestAddDate != null)
		{
			Date now = Calendar.getInstance().getTime();
			return (long) ((double) (now.getTime() - (double) earliestAddDate.getTime()) / 1000.0);
		}
		else
			return 0;
	}

	public TagCheck getTagCheck()
	{
		return tagCheck;
	}

	public Stat getStats()
	{
		return stats;
	}

}
