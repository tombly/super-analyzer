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

package net.nosleep.superanalyzer.util;

import java.util.*;

/**
 * A simple class to compare two strings. It is used in the application to
 * alphabetize lists.
 */
public class StringComparator implements Comparator
{

	public int compare(Object o1, Object o2)
	{
		String s1 = (String) o1;
		String s2 = (String) o2;

		return s1.compareToIgnoreCase(s2);
	}

	public boolean equals(Object o1, Object o2)
	{
		String s1 = (String) o1;
		String s2 = (String) o2;

		if (s1.compareTo(s2) == 0)
			return true;
		else
			return false;
	}

}
