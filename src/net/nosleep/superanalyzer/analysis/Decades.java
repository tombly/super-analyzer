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

import net.nosleep.superanalyzer.util.StringInt;

/**
 * Describes a collection of decades. The keys are decades names and the values
 * are Decade objects.
 */
@SuppressWarnings("serial")
class Decades extends Hashtable<String, Decade>
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

	public void analyze(Track track)
	{
		// get the decade name
		String decadeName = track.getDecade();

		// if this track has no decade, then skip it
		if (decadeName == null)
			return;

		// now see if that decade is in the hash yet
		Decade decade = (Decade) get(decadeName);

		// if not in there, then create a new decade oject and put it in
		if (decade == null)
		{
			// create a new decade object
			decade = new Decade();
			// add the object to the hash table
			put(decadeName, decade);
		}

		// add this track's stats to the decade object
		decade.analyze(track);
	}

}
