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

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

public class Misc
{

	public static String getTooltip(String message)
	{
		return "<html><table><tr><td>" + breakText(message) + "</td></tr></table></html>";
	}
	
	public static String getString(String id)
	{
		try
		{
			return ResourceBundle.getBundle("SuperAnalyzer").getString(id);			
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Could not find resource string for " + id);
		}
		return "";
	}

	public static String getFormattedCountAsInt(int count, String unitsSingular, String unitsPlural)
	{
		return formatCount(count, unitsSingular, unitsPlural, false);
	}

	public static String getFormattedCount(double count, String unitsSingular, String unitsPlural)
	{
		return formatCount(count, unitsSingular, unitsPlural, true);
	}

	public static String getFormattedDuration(double seconds)
	{
		double secondsInMinute = 60.0;
		double secondsInHour = 60.0 * 60.0;
		double secondsInDay = 60.0 * 60.0 * 24.0;
		double secondsInWeek = 60.0 * 60.0 * 24.0 * 7.0;
		double secondsInMonth = 60.0 * 60.0 * 24.0 * 30.0;
		double secondsInYear = 60.0 * 60.0 * 24.0 * 365.0;

		double minutes = seconds / secondsInMinute;
		double hours = seconds / secondsInHour;
		double days = seconds / secondsInDay;
		double weeks = seconds / secondsInWeek;
		double months = seconds / secondsInMonth;
		double years = seconds / secondsInYear;

		if (minutes < 1.0)
			return formatDuration(seconds, Misc.getString("SECOND"), Misc.getString("SECONDS"));

		if (minutes >= 1.0 && hours < 1.0)
			return formatDuration(minutes, Misc.getString("MINUTE"), Misc.getString("MINUTES"));

		if (hours >= 1.0 && days < 1.0)
			return formatDuration(hours, Misc.getString("HOUR"), Misc.getString("HOURS"));

		if (days >= 1.0 && weeks < 1.0)
			return formatDuration(days, Misc.getString("DAY"), Misc.getString("DAYS"));

		if (weeks >= 1.0 && months < 1.0)
			return formatDuration(weeks, Misc.getString("WEEK"), Misc.getString("WEEKS"));

		if (months >= 1.0 && years < 1.0)
			return formatDuration(months, Misc.getString("MONTH"), Misc.getString("MONTHS"));

		return formatDuration(years, Misc.getString("YEAR"), Misc.getString("YEARS"));
	}

	public static String capitalizeByLocale(String s)
	{
		if(Locale.getDefault().getLanguage().compareTo("de") == 0)
			return s.substring(0,1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
		else
			return s.toLowerCase();
	}
	
	private static String formatDuration(double value, String unitSingular, String unitPlural)
	{
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		if (value > 1.0 && value < 1.01)
			return decimalFormat.format(value) + " " + capitalizeByLocale(unitSingular);
		else
			return decimalFormat.format(value) + " " + capitalizeByLocale(unitPlural);
	}

	private static String formatCount(double value, String unitSingular, String unitPlural, boolean showFraction)
	{		
		NumberFormat numberFormat = NumberFormat.getInstance();
		if (showFraction == true)
		{
			numberFormat.setMinimumFractionDigits(0);
			numberFormat.setMaximumFractionDigits(1);
		}

		if (value > 1.0 && value < 1.01)
			return numberFormat.format(value) + " " + capitalizeByLocale(unitSingular);
		else
			return numberFormat.format(value) + " " + capitalizeByLocale(unitPlural);
	}

	public static String getFormattedPercentage(double percentage)
	{
		DecimalFormat wholeFormat = new DecimalFormat("0");
		return wholeFormat.format(percentage) + "%";
	}

	public static String getFormattedBitrate(double bitrate)
	{
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return decimalFormat.format(bitrate) + " kbps";
	}

	public static void formatChart(Plot plot)
	{
		plot.setBackgroundPaint(Color.white);
		plot.setOutlineVisible(false);
		plot.setNoDataMessage(Misc.getString("NO_DATA_TO_DISPLAY"));

		if ((plot instanceof XYPlot))
		{
			XYPlot xyPlot = (XYPlot) plot;
			xyPlot.setDomainGridlinePaint(Color.gray);
			xyPlot.setRangeGridlinePaint(Color.gray);
		}
	}

	public static Date parseXmlDate(String xml)
	{
		Date date = null;

		try
		{
			// DateFormat format = new
			// SimpleDateFormat("yyyy-MM-ddTHH:mm:ss -0000");
			java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			// explicitly set timezone of input if needed
			df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
			date = df.parse(xml);

			// date = format.parse(xml);
		}
		catch (ParseException e)
		{
			System.out.println("Failed to parse xml date: " + xml);
		}
		return date;
	}

	public static String getFormattedByteCount(double bytes)
	{
		double result = bytes;
		String units = "MB";

		// make it into KB
		result /= 1024.0;

		// make it into MB
		result /= 1024.0;

		// if more than a gigabyte, make it into GB
		if (result / 1024.0 > 1.0)
		{
			result /= 1024.0;
			units = "GB";

			// if more than a terabyte, make it into TB
			if (result / 1024.0 > 1.0)
			{
				result /= 1024.0;
				units = "TB";
			}
		}

		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return decimalFormat.format(result) + " " + units;
	}
	
	public static String getFormattedByteCount(long bytes)
	{
		long result = bytes;
		String units = "MB";

		// make it into KB
		result /= 1024.0;

		// make it into MB
		result /= 1024.0;

		// if more than a gigabyte, make it into GB
		if (result / 1024.0 > 1.0)
		{
			result /= 1024.0;
			units = "GB";

			// if more than a terabyte, make it into TB
			if (result / 1024.0 > 1.0)
			{
				result /= 1024.0;
				units = "TB";
			}
		}

		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return decimalFormat.format(result) + " " + units;
	}

	/**
	 * Returns today's date in the form MM/DD/YY.
	 **/
	public static String getTodaysDate()
	{
		Date date = new Date();
		Format formatter = new SimpleDateFormat("MM/dd/yy");
		String s = formatter.format(date);
		return s;
	}

	/**
	 * Adds HTML <br>
	 * tags every so often to break up long lines of text. This is used to make
	 * the tool tips look nice.
	 **/
	public static String breakText(String s)
	{
		String str = "";
		boolean due = false;

		for (int i = 0; i < s.length(); i++)
		{
			str += s.charAt(i);
			if ((i + 1) % 30 == 0)
				due = true;
			if (due == true)
			{
				if (s.charAt(i) == ' ')
				{
					str += "<br>";
					due = false;
				}
			}
		}
		return str;
	}

	/**
	 * Replaces HTML encoded characters with the character they represent. For
	 * example: &#38; becomes an &.
	 **/
	public static String cleanString(String s)
	{
		int index;
		index = s.indexOf("&#38;");
		if (index != -1)
		{
			s = s.substring(0, index) + "&" + s.substring(index + 5, s.length());
		}
		return s;
	}

	public static void printMemoryInfo(String tag)
	{
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long total = rt.totalMemory();
		long max = rt.maxMemory();

		System.out.println("Memory (" + tag + "): " + getFormattedByteCount(free) + "/" + getFormattedByteCount(total) + " (max " + getFormattedByteCount(max) + ")");
	}

}
