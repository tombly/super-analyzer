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
 * A simple class to deal with dates in the library file. This class can take a
 * date string from the library file as input, parse it, and even perform some
 * simple calculations on the date.
 */
public class IDate
{
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;

	public IDate(String s)
	{
		year = Integer.parseInt(s.substring(0, 4));
		month = Integer.parseInt(s.substring(5, 7));
		day = Integer.parseInt(s.substring(8, 10));
		hour = Integer.parseInt(s.substring(11, 13));
		minute = Integer.parseInt(s.substring(14, 16));
		second = Integer.parseInt(s.substring(17, 19));
	}

	public void subtractHours(int h)
	{
		Calendar local = new GregorianCalendar();
		local.set(Calendar.HOUR_OF_DAY, hour); // 0..23
		local.set(Calendar.MINUTE, minute);
		local.set(Calendar.SECOND, second);
		local.set(Calendar.MONTH, month);
		local.set(Calendar.DATE, day);
		local.set(Calendar.YEAR, year);

		local.add(Calendar.HOUR_OF_DAY, -h);

		hour = local.get(Calendar.HOUR_OF_DAY);
		minute = local.get(Calendar.MINUTE);
		second = local.get(Calendar.SECOND);
		month = local.get(Calendar.MONTH);
		day = local.get(Calendar.DATE);
		year = local.get(Calendar.YEAR);
	}

	public String toString()
	{
		String time = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
		String date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
		return date + " " + time;
	}

	public String getStandardString()
	{
		String month = String.valueOf(getMonth());
		if (month.length() == 1)
			month = "0" + month;

		String day = String.valueOf(getDay());
		if (day.length() == 1)
			day = "0" + day;

		return getYear() + "-" + month + "-" + day;
	}

	public Date getDate()
	{
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);

		return c.getTime();
	}

	public int getYear()
	{
		return year;
	}

	public int getMonth()
	{
		return month;
	}

	public int getDay()
	{
		return day;
	}

	public int getHour()
	{
		return hour;
	}

	public int getMinute()
	{
		return minute;
	}

	public int getSecond()
	{
		return second;
	}

}
