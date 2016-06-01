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

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

import net.nosleep.superanalyzer.util.StringInt;

public class WordCounterDisk
{

	private static int MAX_WORD_LENGTH = 10;
	private static int MAX_COUNT_LENGTH = 4;
	private Vector<CommonWord> mostCommonWords;
	private RandomAccessFile file;
	private Hashtable words;
	private File tempFile;

	private static String spaces1 = " ";
	private static String spaces2 = "  ";
	private static String spaces3 = "   ";
	private static String spaces4 = "    ";
	private static String spaces5 = "     ";
	private static String spaces6 = "      ";

	public WordCounterDisk()
	{

		words = new Hashtable();

		tempFile = new File(System.getProperty("java.io.tmpdir") + "/words");

		try
		{
			file = new RandomAccessFile(tempFile, "rw");
			FileChannel fc = (FileChannel) file.getChannel();
			fc.truncate(0);
			fc.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void checkWords(Track track)
	{

		// get the track name in lower case
		String name = track.getName().toLowerCase();

		Scanner scn = new Scanner(name).useDelimiter("[\\s()\\]\\[!@#\\$%\\^&\\*\\{\\}:;<>\\/\\?\\.,\"_=\\+]");
		while (scn.hasNext())
		{

			// get the next word in the song name
			String tok = scn.next().trim();

			// skip ones less than 4 characters or longer than the max
			// characters
			if (tok.length() < 4 || tok.length() > MAX_WORD_LENGTH)
				continue;

			// skip uninteresting words
			if (wordIsUninteresting(tok) == true)
				continue;

			if (words.size() < 1000)
			{
				// now see if the word is already in the hash
				Integer count = (Integer) words.get(tok);

				// if it's null, then we need to add it
				if (count == null)
					words.put(tok, 1);
				else
					words.put(tok, count + 1);
			}
			else
			{
				processWords();
			}
		}
	}

	private void processWords()
	{
		Vector<StringInt> pairs = new Vector<StringInt>(words.size());

		Enumeration keys = words.keys();
		while (keys.hasMoreElements() == true)
		{
			String key = (String) keys.nextElement();
			Integer count = (Integer) words.get(key);

			pairs.add(new StringInt(key, count));

			// processWord(key, count);
		}

		processPairs(pairs);

		words.clear();
	}

	private void processPairs(Vector<StringInt> pairs)
	{

		int bytesOffset = 0;
		ByteBuffer bytes = ByteBuffer.allocate((MAX_WORD_LENGTH + MAX_COUNT_LENGTH) * 1000);

		int fileOffset = 0;

		try
		{
			file = new RandomAccessFile(tempFile, "rw");
			FileChannel fc = (FileChannel) file.getChannel();

			int count = 0;

			while (true)
			{

				bytes.rewind();
				int nread = fc.read(bytes);
				if (nread <= 0)
					break;

				int foundCount = 0;
				bytesOffset = 0;
				while (true)
				{
					if (bytesOffset >= nread)
						break;

					byte[] entryBytes = new byte[MAX_WORD_LENGTH + MAX_COUNT_LENGTH];

					for (int i = bytesOffset, j = 0; i < bytesOffset + MAX_WORD_LENGTH + MAX_COUNT_LENGTH; i++, j++)
					{
						entryBytes[j] = bytes.get(i);
					}

					bytesOffset += MAX_WORD_LENGTH + MAX_COUNT_LENGTH;
					fileOffset += MAX_WORD_LENGTH + MAX_COUNT_LENGTH;

					String s = new String(entryBytes);
					String nextWord = s.substring(0, MAX_WORD_LENGTH).trim();

					for (int p = 0; p < pairs.size(); p++)
					{
						StringInt pair = pairs.elementAt(p);

						if (pair.StringVal.compareTo(nextWord) == 0)
						{

							String countString = s.substring(MAX_WORD_LENGTH, MAX_WORD_LENGTH + MAX_COUNT_LENGTH);
							count = Integer.parseInt(countString.trim());
							String entry = getEntryString(pair.StringVal, count + pair.IntVal);
							ByteBuffer writeBuffer = ByteBuffer.wrap(entry.getBytes());

							// move the file position back to the start of
							// the entry that we
							// read so that it overwrites it
							long currentFilePos = fc.position();
							fc.position(fileOffset - (MAX_WORD_LENGTH + MAX_COUNT_LENGTH));
							int written = fc.write(writeBuffer);

							fc.position(currentFilePos);

							pairs.removeElementAt(p);
							p--;

						}

						if (pairs.size() == 0)
							break;

					}
				}

				if (pairs.size() == 0)
					break;
			}

			// we're done searching the whole file, so now add any we didn't
			// find
			for (int i = 0; i < pairs.size(); i++)
			{
				StringInt pair = pairs.elementAt(i);

				// the word is not in there, so add it
				String entry = getEntryString(pair.StringVal, pair.IntVal);
				ByteBuffer writeBuffer = ByteBuffer.wrap(entry.getBytes());
				fc.position(fc.size());
				int written = fc.write(writeBuffer);

			}

			if (fc != null)
				fc.close();

		}
		catch (Exception e)
		{

			System.out.println("ERROR: " + e.toString());

		}
		finally
		{

		}
	}

	private static String getEntryString(String word, int count)
	{
		switch (word.length())
		{
		case 4:
			word = spaces6 + word;
			break;
		case 5:
			word = spaces5 + word;
			break;
		case 6:
			word = spaces4 + word;
			break;
		case 7:
			word = spaces3 + word;
			break;
		case 8:
			word = spaces2 + word;
			break;
		case 9:
			word = spaces1 + word;
			break;
		}

		String countString = String.valueOf(count);
		switch (countString.length())
		{
		case 1:
			word += spaces3 + countString;
			break;
		case 2:
			word += spaces2 + countString;
			break;
		case 3:
			word += spaces1 + countString;
			break;
		case 4:
			word += countString;
			break;
		}

		return word;
	}

	private static boolean wordIsUninteresting(String word)
	{
		if (word.compareTo("live") == 0)
			return true;
		if (word.compareTo("bonus") == 0)
			return true;
		if (word.compareTo("instrumental") == 0)
			return true;
		if (word.compareTo("track") == 0)
			return true;
		if (word.compareTo("feat") == 0)
			return true;
		if (word.compareTo("with") == 0)
			return true;
		if (word.compareTo("what") == 0)
			return true;
		if (word.compareTo("when") == 0)
			return true;
		if (word.compareTo("where") == 0)
			return true;
		if (word.compareTo("this") == 0)
			return true;
		if (word.compareTo("that") == 0)
			return true;
		if (word.compareTo("it's") == 0)
			return true;
		if (word.compareTo("remix") == 0)
			return true;
		if (word.compareTo("version") == 0)
			return true;

		return false;
	}

	public Vector getMostCommonWords()
	{
		return mostCommonWords;
	}

	/**
	 * Analyzes the hashtable of words and identifies the most common words.
	 * This should be called after the library file has been fully parsed.
	 */
	public void finish()
	{

		processWords();

		mostCommonWords = new Vector<CommonWord>();

		try
		{
			file = new RandomAccessFile(tempFile, "rw");
			FileChannel fc = (FileChannel) file.getChannel();

			int count = 0;

			while (true)
			{

				ByteBuffer bytes = ByteBuffer.allocate(MAX_WORD_LENGTH + MAX_COUNT_LENGTH);
				int nread = fc.read(bytes);
				if (nread <= 0)
					break;

				String s = new String(bytes.array());
				String nextWord = s.substring(0, MAX_WORD_LENGTH).trim();
				String countString = s.substring(MAX_WORD_LENGTH, MAX_WORD_LENGTH + MAX_COUNT_LENGTH);
				count = Integer.parseInt(countString.trim());

				updateOrAdd(nextWord, count);
			}

			if (fc != null)
				fc.close();

		}
		catch (Exception e)
		{

			System.out.println("ERROR: " + e.toString());

		}
		finally
		{

		}

		Collections.sort(mostCommonWords, new CommonWordComparator());
	}

	private void updateOrAdd(String word, int count)
	{

		Collections.sort(mostCommonWords, new CommonWordComparator());

		if (mostCommonWords.size() < 25)
		{
			mostCommonWords.add(new CommonWord(word, count));
		}
		else
		{
			for (int i = mostCommonWords.size() - 1; i >= 0; i--)
			{
				CommonWord cw = (CommonWord) mostCommonWords.elementAt(i);

				if (cw.Count < count)
				{
					mostCommonWords.removeElementAt(i);
					mostCommonWords.add(new CommonWord(word, count));
					break;
				}
			}
		}
	}

	public class CommonWordComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			WordCounterDisk.CommonWord s1 = (WordCounterDisk.CommonWord) o1;
			WordCounterDisk.CommonWord s2 = (WordCounterDisk.CommonWord) o2;

			return s2.Count - s1.Count;
		}

		public boolean equals(Object o1, Object o2)
		{
			WordCounterDisk.CommonWord s1 = (WordCounterDisk.CommonWord) o1;
			WordCounterDisk.CommonWord s2 = (WordCounterDisk.CommonWord) o2;

			if (s1.Count == s2.Count)
				return true;
			else
				return false;
		}
	}

	public class CommonWord
	{
		public String Word;
		public int Count;

		public CommonWord(String word, int count)
		{
			Word = word;
			Count = count;
		}

	}

}
