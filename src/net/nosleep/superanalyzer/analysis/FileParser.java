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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JProgressBar;

import net.nosleep.superanalyzer.util.Misc;

public class FileParser
{

	/*
	 * The Analysis object that will take care of analyzing each track we find
	 * in the file.
	 */
	private Analysis _analysis;

	/*
	 * The iTunes xml file to parse.
	 */
	private File _inputFile;

	/*
	 * Keeps track of the number of bytes read to estimate the progress bar.
	 */
	private int _byteCount;

	/*
	 * The progress bar to update as the file is parsed.
	 */
	private JProgressBar _progressBar;

	/*
	 * Holds a list of all the Track Id's that are in the Music playlist so that
	 * we know which ones to include in the analysis.
	 */
	private Vector<Integer> _musicPlaylist;

	/**
	 * Simple constructor to set the data members.
	 */
	public FileParser(Analysis analysis, File inputFile, JProgressBar progressBar)
	{

		_analysis = analysis;
		_inputFile = inputFile;
		_byteCount = 0;
		_progressBar = progressBar;
	}

	/**
	 * The starting point for using a FileParser object. This method begins the
	 * parsing of the given file, updating the given progress bar throughout the
	 * parsing.
	 */
	public void parse()
	{

		long startTime = Calendar.getInstance().getTimeInMillis();

		_progressBar.setMinimum(0);
		_progressBar.setMaximum((int) _inputFile.length() * 2);
		_byteCount = 0;

		// make sure the file exists
		if (!_inputFile.exists())
		{
			System.err.println("ERROR: file not found");
			System.exit(1);
		}

		try
		{
			// phase 1 - read the music playlist
			readMusicPlaylist(_inputFile);

			// phase 2 - read the tracks in the music playlist
			readMusicTracks(_inputFile);

		}
		catch (IOException e)
		{
			System.out.println("ERROR: file could not be read");
			System.exit(1);
		}

		_progressBar.setValue(_progressBar.getMaximum());
		long endTime = Calendar.getInstance().getTimeInMillis();

		System.out.println("Analysis time: " + (((double) endTime - (double) startTime) / 1000.0) + " seconds");
	}

	private void readMusicPlaylist(File file) throws IOException
	{

		_musicPlaylist = new Vector<Integer>();

		FileInputStream fs = new FileInputStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(fs, "UTF-8"));

		// jump to the start of the playlists toward the end of the file
		scanToLine(in, "<key>Playlists</key>");

		// jump to the start of the music playlist. this is how we identify the
		// music playlist since we can't use the name, since it changes with
		// the language (e.g. in German it's Musik)
		boolean found = scanToLine(in, "<key>Music</key><true/>");

		// handle the case when we don't find the music playlist
		if (found == false)
		{
			// close it up
			in.close();
			fs.close();

			// open it again
			fs = new FileInputStream(file);
			in = new BufferedReader(new InputStreamReader(fs, "UTF-8"));

			// jump to the start of the playlists toward the end of the file
			scanToLine(in, "<key>Playlists</key>");
		}

		// read in all the track info
		while (true)
		{

			if (readPlaylistItem(in) == false)
				break;
		}

		in.close();
		fs.close();
	}

	private void readMusicTracks(File file) throws IOException
	{
		FileInputStream fs = new FileInputStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(fs, "UTF-8"));

		// this will find the first <dict> line, after the xml header
		scanToLine(in, "<dict>");

		// this will find the second <dict> line, after the library info
		scanToLine(in, "<dict>");

		// read in all the track info
		while (true)
		{

			// this call is expecting to see a key tag before a track's dict
			// tag
			if (!readTrack(in))
				break;
		}

		in.close();
		fs.close();
	}

	private boolean readPlaylistItem(BufferedReader in) throws IOException
	{
		String s;
		boolean shouldStop = false;

		while (true)
		{
			s = readLine(in);
			// stop when we reach the playlist's ending <array> tag
			if (s == null || s.indexOf("</array>") >= 0)
			{
				shouldStop = true;
				break;
			}

			processPlaylistLine(s);
		}

		if (shouldStop == true)
			return false;
		else
			return true;
	}

	/**
	 * This method just finds each line in the library file that decscribes a
	 * track, creates a new Track object, and then calls the processLine()
	 * method to parse the line into the track object.
	 */
	private boolean readTrack(BufferedReader in) throws IOException
	{
		String s;

		// the first line of each track section begins with a key tag
		s = readLine(in);
		if (s.indexOf("<key>") < 0)
			return false;

		// now read the <dict> tag that follows it, and cancel if it's not a
		// dict tag
		s = readLine(in);
		if (s.indexOf("<dict>") < 0)
			return false;

		Track track = new Track();

		// ok, so now we're dealing with a list of key tags, each of which has a
		// keyword
		// that indicates the kind of info stored in the key. so what we do is
		// read in
		// the key's name and value into strings, and then we switch on the key
		// keyword
		// to properly add the value to a track object.
		while (true)
		{
			s = readLine(in);
			// stop when we reach the track's ending dict tag
			if (s.indexOf("</dict>") >= 0)
				break;
			// otherwise, we process the line
			processTrackLine(s, track);
		}

		boolean isMusicFile = true;

		// see if the track is in the music playlist
		if (_musicPlaylist.contains(track.getTrackID()) == false)
			isMusicFile = false;
		else
			// this saves memory if we remove the track id from the playlist
			// vector once we've found the actual track and processed it
			_musicPlaylist.remove(track.getTrackID());

		if (track.getLocation() != null)
			if (track.getLocation().toLowerCase().endsWith("m4v") == true)
			{
				isMusicFile = false;
			}

		if (isMusicFile == true)
			_analysis.analyze(track);

		return true;
	}

	/**
	 * Advances through a file until it finds a line in the input stream that
	 * matches the given token exactly.
	 */
	private boolean scanToLine(BufferedReader in, String token) throws IOException
	{
		String s;
		while (true)
		{

			s = readLine(in);

			if (s == null)
				return false;

			if (s.compareTo(token) == 0)
				return true;
		}
	}

	/**
	 * Reads a line of text from the input stream and updates the byte count.
	 */
	private String readLine(BufferedReader in) throws IOException
	{

		String s = in.readLine();

		if (s != null)
		{
			_byteCount += s.length();
			_progressBar.setValue(_byteCount);

			s = s.trim();
		}

		return s;
	}

	private void processPlaylistLine(String s)
	{

		int start = s.indexOf("<key>");
		if (start < 0)
			return;

		int stop = s.indexOf("</key>");
		if (stop < 0)
			return;

		String key = s.substring(start + "<key>".length(), stop).trim();

		if (key.equals("Track ID") == false)
			return;

		start = s.indexOf("<integer>");
		if (start < 0)
			return;

		stop = s.indexOf("</integer>");
		if (stop < 0)
			return;

		String number = s.substring(start + "<integer>".length(), stop).trim();

		try
		{
			_musicPlaylist.add(Integer.parseInt(number));
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * Breaks a line into its key and value parts, and then gives the track
	 * object, the key, and the value to the addDataToTrack() method to populate
	 * the object with the data.
	 */
	private void processTrackLine(String s, Track t)
	{
		String value = "";

		int pos = s.indexOf("</key>");
		if (pos < 0)
			return;
		String name = s.substring(5, pos);

		// get the text after the </key> tag
		String more = s.substring(pos + 6);

		if (more.compareTo("<true/>") == 0)
			value = "true";
		else
		{
			// find the start of the value field
			int start = more.indexOf(">") + 1;
			int end = more.indexOf("</");
			if (end < start)
				end = more.length();
			value = more.substring(start, end);
		}

		addDataToTrack(t, name, value);
	}

	/**
	 * Takes a key,value pair and adds the data to a track object.
	 */
	private void addDataToTrack(Track t, String name, String value)
	{
		boolean found = false;

		// if it's the track id field - integer
		if (name.compareTo("Track ID") == 0)
		{
			found = true;
			t.setTrackID(new Integer(value));
		}

		// if it's the name field - string
		if (name.compareTo("Name") == 0)
		{
			found = true;
			t.setName(Misc.cleanString(value));
		}

		// if it's the artist field - string
		if (name.compareTo("Artist") == 0)
		{
			found = true;
			t.setArtist(Misc.cleanString(value));
		}

		// if it's the album artist field - string
		if (name.compareTo("Album Artist") == 0)
		{
			found = true;
			t.setAlbumArtist(Misc.cleanString(value));
		}

		// if it's the album field - string
		if (name.compareTo("Album") == 0)
		{
			found = true;
			t.setAlbum(Misc.cleanString(value));
		}

		// if it's the genre field - string
		if (name.compareTo("Genre") == 0)
		{
			found = true;
			t.setGenre(Misc.cleanString(value));
		}

		// if it's the kind field - string
		if (name.compareTo("Kind") == 0)
		{
			found = true;
			t.setKind(value);
		}

		// if it's the size field - integer
		if (name.compareTo("Size") == 0)
		{
			found = true;
			t.setSize(new Long(value));
		}

		// if it's the total time field - integer
		if (name.compareTo("Total Time") == 0)
		{
			int tt = (int) ((double) Long.parseLong(value) / 1000.0);
			t.setTotalTime(new Integer(tt));
		}

		// if it's the disc number field - integer
		if (name.compareTo("Disc Number") == 0)
		{
			found = true;
			t.setDiscNumber(new Integer(value));
		}

		// if it's the disc count field - integer
		if (name.compareTo("Disc Count") == 0)
		{
			found = true;
			t.setDiscCount(new Integer(value));
		}

		// if it's the track number field - integer
		if (name.compareTo("Track Number") == 0)
		{
			found = true;
			t.setTrackNumber(new Integer(value));
		}

		// if it's the artwork count field - integer
		if (name.compareTo("Artwork Count") == 0)
		{
			found = true;
			t.setHasArtwork(new Boolean(true));
		}

		// if it's the track count field - integer
		if (name.compareTo("Track Count") == 0)
		{
			found = true;
			t.setTrackCount(new Integer(value));
		}

		// if it's the year field - integer
		if (name.compareTo("Year") == 0)
		{
			found = true;
			if (value.length() == 4)
			{
				t.setYear(new Integer(value));
				t.setDecade(value.substring(0, 3) + "0");
			}
		}

		// if it's the date modified field - date
		/*
		 * if (name.compareTo("Date Modified") == 0) { found = true;
		 * t.setDateModified(Misc.parseXmlDate(value)); }
		 */

		// if it's the date added field - date
		if (name.compareTo("Date Added") == 0)
		{
			found = true;
			t.setDateAdded(Misc.parseXmlDate(value));
		}

		// if it's the bit rate field - integer
		if (name.compareTo("Bit Rate") == 0)
		{
			found = true;
			t.setBitRate(new Integer(value));
		}

		// if it's the sample rate field - integer
		if (name.compareTo("Sample Rate") == 0)
		{
			found = true;
			t.setSampleRate(new Integer(value));
		}

		// if it's the play count field - integer
		if (name.compareTo("Play Count") == 0)
		{
			found = true;
			t.setPlayCount(new Integer(value));
		}

		// if it's the play date utc field - date
		if (name.compareTo("Play Date UTC") == 0)
		{
			found = true;
			t.setPlayDate(Misc.parseXmlDate(value));
		}

		// if it's the rating field - integer
		if (name.compareTo("Rating") == 0)
		{
			found = true;
			t.setRating(new Integer(value));
		}

		// if it's the artwork count field - integer
		if (name.compareTo("Artwork Count") == 0)
		{
			found = true;
			t.setArtworkCount(new Integer(value));
		}

		// if it's the track type field - string
		if (name.compareTo("Track Type") == 0)
		{
			found = true;
			t.setTrackType(value);
		}

		// if it's the location field - string
		if (name.compareTo("Location") == 0)
		{
			found = true;
			t.setLocation(value);
		}

		// if it's the file folder count field - integer
		if (name.compareTo("File Folder Count") == 0)
		{
			found = true;
			t.setFileFolderCount(new Integer(value));
		}

		// if it's the library folder count field - integer
		if (name.compareTo("Library Folder Count") == 0)
		{
			found = true;
			t.setLibraryFolderCount(new Integer(value));
		}

		// if it's the compilation flag - boolean
		if (name.compareTo("Compilation") == 0)
		{
			found = true;
			if (value.compareTo("true") == 0)
				t.setCompilation(new Boolean(true));
			else
				t.setCompilation(new Boolean(false));
		}

		// if it's the bitrate field - integer
		if (name.compareTo("Bit Rate") == 0)
		{
			found = true;
			t.setBitRate(new Integer(value));
		}

		if (!found)
		{
		}
	}

}
