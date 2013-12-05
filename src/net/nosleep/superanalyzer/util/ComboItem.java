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

public class ComboItem
{
	private String _value;
	private int _kind;

	public ComboItem(String value, int kind)
	{
		_value = value;
		_kind = kind;
	}

	public String toString()
	{
		if (_value.length() > 50)
			return _value.substring(0, 50) + "...";
		else
			return _value;
	}

	public String getValue()
	{
		int index = _value.indexOf(": ");
		if (index >= 0)
		{
			index += 2;
			return _value.substring(index);
		}
		else
			return _value;
	}

	public int getKind()
	{
		return _kind;
	}
}
