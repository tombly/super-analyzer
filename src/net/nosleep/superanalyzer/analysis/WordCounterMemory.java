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
import java.util.Scanner;
import java.util.Vector;

public class WordCounterMemory
{

	private Hashtable<String, Integer> words;
	private Vector<CommonWord> mostCommonWords;

	public WordCounterMemory()
	{
		words = new Hashtable<String, Integer>();
	}

	public void checkWords(Track track)
	{

		// get the track name in lower case
		String name = track.getName().toLowerCase();

		@SuppressWarnings("resource")
		Scanner scn = new Scanner(name).useDelimiter("[\\s()\\]\\[!@#\\$%\\^&\\*\\{\\}:;<>\\/\\?\\.,\"_=\\+]");
		while (scn.hasNext())
		{

			// get the next word in the song name
			String tok = scn.next().trim();

			// skip ones less than 4 characters
			if (tok.length() < 4)
				continue;

			// skip uninteresting words
			if (wordIsUninteresting(tok) == true)
				continue;

			// now see if the word is already in the hash
			Integer count = words.get(tok);

			// if it's null, then we need to add it
			if (count == null)
				words.put(tok, 1);
			else
				words.put(tok, count + 1);
		}
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
		if (word.compareTo("album") == 0)
			return true;
		if (word.compareTo("acoustic") == 0)
			return true;
		if (word.compareTo("titel") == 0)
			return true;
		if (word.compareTo("allegro") == 0)
			return true;
		if (word.compareTo("edit") == 0)
			return true;
		if (word.compareTo("teil") == 0)
			return true;

		
		return false;
	}

	public Vector<CommonWord> getMostCommonWords()
	{
		return mostCommonWords;
	}

	/**
	 * Analyzes the hashtable of words and identifies the most common words.
	 * This should be called after the library file has been fully parsed.
	 */
	public void finish()
	{

		mostCommonWords = new Vector<CommonWord>();

		for (int j = 0; j < 25; j++)
		{
			Integer highest = 0;
			String highestWord = null;

			Iterator<String> i = words.keySet().iterator();
			while (i.hasNext())
			{
				String word = (String) i.next();
				Integer count = (Integer) words.get(word);
				if (count > highest)
				{
					highest = count;
					highestWord = word;
				}
			}

			if (highestWord != null)
			{
				mostCommonWords.add(new CommonWord(highestWord, highest));
				words.remove(highestWord);
			}
		}

		// no longer need the hash of words
		words.clear();
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
