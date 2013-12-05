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

/**
 * A class that holds all the info about each track. Integer and double objects
 * are used rather than the simple data types because we can keep better track
 * of which data members have and have not been set.
 */
class Track
{
	private Integer trackID;
	private String name;
	private String artist;
	private String albumArtist;
	private String album;
	private String genre;
	private String kind;
	private Long size;
	private Integer totalTime;
	private Integer discNumber;
	private Integer discCount;
	private Integer trackNumber;
	private Integer trackCount;
	private Integer year;
	// private Date dateModified;
	private Date dateAdded;
	private Integer bitRate;
	private Integer sampleRate;
	private Integer playCount;
	private Date playDate;
	private Integer rating;
	private Integer artworkCount;
	private String trackType;
	private String location;
	private Integer fileFolderCount;
	private Integer libraryFolderCount;
	private Boolean compilation;
	private String decade;
	private Boolean hasArtwork;

	public Track()
	{
		// set to null if we can't assume a default value
		trackID = null;
		name = null;
		artist = null;
		albumArtist = null;
		album = null;
		genre = null;
		kind = null;
		size = null;
		totalTime = null;
		discNumber = null;
		trackCount = null;
		year = null;
		// dateModified = null;
		dateAdded = null;
		bitRate = null;
		sampleRate = null;
		playDate = null;
		trackType = null;
		location = null;
		fileFolderCount = null;
		libraryFolderCount = null;
		decade = null;
		hasArtwork = null;

		// set these to defaults
		compilation = new Boolean(false); // ok to assume false here
		rating = new Integer(0); // ok to assume no rating means 0 stars
		playCount = new Integer(0); // ok to assume that no play count means a
		// count of 0
	}

	public void print()
	{
		System.out.println("Name: " + name + ", " + artist);
	}

	public void setTrackID(Integer trackID)
	{
		this.trackID = trackID;
	}

	public Integer getTrackID()
	{
		return trackID;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public String getArtist()
	{
		return artist;
	}

	public void setAlbumArtist(String albumArtist)
	{
		this.albumArtist = albumArtist;
	}

	public String getAlbumArtist()
	{
		return albumArtist;
	}

	public void setAlbum(String album)
	{
		this.album = album;
	}

	public String getAlbum()
	{
		return album;
	}

	public void setDecade(String decade)
	{
		this.decade = decade;
	}

	public String getDecade()
	{
		return decade;
	}

	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	public String getGenre()
	{
		return genre;
	}

	public void setKind(String kind)
	{
		this.kind = kind;
	}

	public String getKind()
	{
		return kind;
	}

	public void setHasArtwork(Boolean b)
	{
		hasArtwork = b;
	}

	public Boolean getHasArtwork()
	{
		return hasArtwork;
	}

	public void setSize(Long size)
	{
		this.size = size;
	}

	public Long getSize()
	{
		return size;
	}

	public void setTotalTime(Integer totalTime)
	{
		// check to make sure the time is not less than 0, which is illegal
		if (totalTime.intValue() < 0)
		{
			this.totalTime = new Integer(0);
			// System.out.println( "tt: " + totalTime.intValue() + ", for " +
			// name + "  " + artist );
		}
		else
			this.totalTime = totalTime;
	}

	public Integer getTotalTime()
	{
		return totalTime;
	}

	public void setDiscNumber(Integer discNumber)
	{
		this.discNumber = discNumber;
	}

	public Integer getDiscNumber()
	{
		return discNumber;
	}

	public void setDiscCount(Integer discCount)
	{
		this.discCount = discCount;
	}

	public Integer getDiscCount()
	{
		return discCount;
	}

	public void setTrackNumber(Integer trackNumber)
	{
		this.trackNumber = trackNumber;
	}

	public Integer getTrackNumber()
	{
		return trackNumber;
	}

	public void setTrackCount(Integer trackCount)
	{
		this.trackCount = trackCount;
	}

	public Integer getTrackCount()
	{
		return trackCount;
	}

	public void setYear(Integer year)
	{
		Calendar c = Calendar.getInstance();
		if (year.intValue() > c.get(Calendar.YEAR))
			this.year = new Integer(c.get(Calendar.YEAR));
		else
			this.year = year;
	}

	public Integer getYear()
	{
		return year;
	}

	/*
	 * public void setDateModified(Date dateModified) { this.dateModified =
	 * dateModified; }
	 * 
	 * public Date getDateModified() { return dateModified; }
	 */
	public void setDateAdded(Date dateAdded)
	{
		this.dateAdded = dateAdded;
	}

	public Date getDateAdded()
	{
		return dateAdded;
	}

	public void setBitRate(Integer bitRate)
	{
		this.bitRate = bitRate;
	}

	public Integer getBitRate()
	{
		return bitRate;
	}

	public void setSampleRate(Integer sampleRate)
	{
		this.sampleRate = sampleRate;
	}

	public Integer getSampleRate()
	{
		return sampleRate;
	}

	public void setPlayCount(Integer playCount)
	{
		this.playCount = playCount;
	}

	public Integer getPlayCount()
	{
		return playCount;
	}

	public void setPlayDate(Date playDate)
	{
		this.playDate = playDate;
	}

	public Date getPlayDate()
	{
		return playDate;
	}

	public void setRating(Integer rating)
	{
		this.rating = rating;
	}

	public Integer getRating()
	{
		return rating;
	}

	public Integer getRatingStar()
	{
		switch (rating.intValue())
		{
		case 0:
			return new Integer(0);
		case 20:
			return new Integer(1);
		case 40:
			return new Integer(2);
		case 60:
			return new Integer(3);
		case 80:
			return new Integer(4);
		case 100:
			return new Integer(5);
		default:
			return null;
		}
	}

	public void setArtworkCount(Integer artworkCount)
	{
		this.artworkCount = artworkCount;
	}

	public Integer getArtworkCount()
	{
		return artworkCount;
	}

	public void setTrackType(String trackType)
	{
		this.trackType = trackType;
	}

	public String getTrackType()
	{
		return trackType;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getLocation()
	{
		return location;
	}

	public void setFileFolderCount(Integer fileFolderCount)
	{
		this.fileFolderCount = fileFolderCount;
	}

	public Integer fileFolderCount()
	{
		return fileFolderCount;
	}

	public void setLibraryFolderCount(Integer libraryFolderCount)
	{
		this.libraryFolderCount = libraryFolderCount;
	}

	public Integer getLibraryFolderCount()
	{
		return libraryFolderCount;
	}

	public void setCompilation(Boolean compilation)
	{
		this.compilation = compilation;
	}

	public Boolean getCompilation()
	{
		return compilation;
	}

}
