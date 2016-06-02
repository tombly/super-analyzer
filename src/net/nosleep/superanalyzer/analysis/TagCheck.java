package net.nosleep.superanalyzer.analysis;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Vector;

import net.nosleep.superanalyzer.util.Constants;
import net.nosleep.superanalyzer.util.Misc;

public class TagCheck
{

	public static final int WARNING_LEVEL_GOOD = 1;
	public static final int WARNING_LEVEL_BAD = 2;
	public static final int WARNING_LEVEL_WARN = 3;

	/*
	 * The cutoff percentage at which we warn the user that their stats may be
	 * inaccurate.
	 */
	public static final int CUTOFF = 10;

	Vector<TagCheckItem> _tagPairs;

	// counts of missing tag information
	private int tagCount;
	private int tagAny;
	private int tagArtist;
	private int tagAlbum;
	private int tagAlbumArtist;
	private int tagYear;
	private int tagAlbumTrackNumber;
	private int tagAlbumTrackCount;
	private int tagGenre;
	private int tagRating;
	private int tagArtwork;

	public TagCheck()
	{
		tagCount = 0;
		tagAny = 0;
		tagArtist = 0;
		tagAlbum = 0;
		tagAlbumArtist = 0;
		tagYear = 0;
		tagAlbumTrackNumber = 0;
		tagAlbumTrackCount = 0;
		tagGenre = 0;
		tagRating = 0;
		tagArtwork = 0;
	}

	public boolean tagsAreIncomplete()
	{

		if (tagAny / (double) tagCount * 100.0 >= 10.0)
			return true;
		else
			return false;
	}

	private static double getPercentage(int count, int total)
	{
		return ((double) count / (double) total) * 100.0;
	}

	public void finish()
	{
		_tagPairs = new Vector<TagCheckItem>();

		_tagPairs.add(new TagCheckItem(Misc.getString("OVERALL"), getPercentage(tagAny, tagCount), tagAny, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("ARTIST"), getPercentage(tagArtist, tagCount), tagArtist, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("ALBUM_ARTIST"), getPercentage(tagAlbumArtist, tagCount),
				tagAlbumArtist, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("ALBUM"), getPercentage(tagAlbum, tagCount), tagAlbum, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("YEAR"), getPercentage(tagYear, tagCount), tagYear, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("TRACK_NUMBER"), getPercentage(tagAlbumTrackNumber, tagCount),
				tagAlbumTrackNumber, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("ALBUM_TRACK_COUNT"),
				getPercentage(tagAlbumTrackCount, tagCount), tagAlbumTrackCount, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("GENRE"), getPercentage(tagGenre, tagCount), tagGenre, true));
		_tagPairs.add(new TagCheckItem(Misc.getString("RATING"), getPercentage(tagRating, tagCount), tagRating, true));
		_tagPairs
				.add(new TagCheckItem(Misc.getString("ARTWORK"), getPercentage(tagArtwork, tagCount), tagArtwork, true));
	}

	public Vector<TagCheckItem> getPairs()
	{
		return _tagPairs;
	}

	/**
	 * Collects information about tag information that is missing. The results
	 * are stored in an array for use by the caller.
	 */
	@SuppressWarnings("unused")
	public void check(Track track)
	{
		boolean anythingMissing = false;

		if (track.getArtist() == null)
		{
			tagArtist++;
			anythingMissing = true;
		}

		// only warn about a missing album artist if it's not a compilation
		// (of it we don't know whether it's a compilation)
		if (track.getAlbumArtist() == null)
			if (track.getCompilation() == null || track.getCompilation() == false)
			{
				if (Constants.writeMissingToStdOut == true)
					System.out.println("Album artist missing for: " + track.getName());
				tagAlbumArtist++;
				anythingMissing = true;
			}

		if (track.getAlbum() == null)
		{
			tagAlbum++;
			anythingMissing = true;
		}

		if (track.getYear() == null)
		{
			tagYear++;
			anythingMissing = true;
		}

		if (track.getTrackNumber() == null)
		{
			tagAlbumTrackNumber++;
			anythingMissing = true;
		}

		if (track.getTrackCount() == null)
		{
			if (Constants.writeMissingToStdOut == true) //for debugging
				System.out.println("Track count missing for: " + track.getName());
			tagAlbumTrackCount++;
			anythingMissing = true;
		}

		if (track.getGenre() == null)
		{
			tagGenre++;
			anythingMissing = true;
		}

		if (track.getRating().intValue() == 0)
		{
			tagRating++;
			anythingMissing = true;
		}

		// don't count this as missing tag info, since no stats currently use it
		if (track.getHasArtwork() == null || track.getHasArtwork() == false)
		{
			tagArtwork++;
		}

		if (anythingMissing)
			tagAny++;

		tagCount++;
	}

	public class TagCheckItem
	{
		public String Name;
		public double Percentage;
		public int Count;
		public boolean Required;

		public TagCheckItem(String name, double percentage, int count, boolean required)
		{
			Name = name;
			Percentage = percentage;
			Count = count;
			Required = required;
		}

		public String getValue()
		{
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			return decimalFormat.format(Percentage) + "% (" + Count + " " + Misc.getString("MISSING") + ")";
		}

		public Color getColor()
		{
			if (Percentage > CUTOFF && Count > 0)
			{
				if (Required == true)
					return Color.red.darker();
				else
					return Color.yellow.darker();
			}
			else
			{
				return Color.green.darker().darker();
			}
		}
	}
}
