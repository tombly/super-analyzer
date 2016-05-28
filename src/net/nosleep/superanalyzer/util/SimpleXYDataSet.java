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

import java.util.Vector;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;

/**
 * A dummy dataset for an XY plot.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for
 * demo purposes - it is NOT intended to show how you should go about writing
 * your own datasets.
 */
public class SimpleXYDataSet extends AbstractXYDataset implements KeyXYDataset
{

	Vector _points;

	/**
	 * Use the translate to change the data and demonstrate dynamic data
	 * changes.
	 */
	private double translate;

	/**
	 * Default constructor.
	 */
	public SimpleXYDataSet(Vector points)
	{
		this.translate = 0.0;
		_points = points;
	}
	
	public SimpleXYDataSet()
	{
		this._points = null;
	}

	/**
	 * Returns the translation factor.
	 * 
	 * @return the translation factor.
	 */
	public double getTranslate()
	{
		return this.translate;
	}

	/**
	 * Sets the translation constant for the x-axis.
	 * 
	 * @param translate
	 *            the translation factor.
	 */
	public void setTranslate(double translate)
	{
		this.translate = translate;
		notifyListeners(new DatasetChangeEvent(this, this));
	}

	/**
	 * Returns the x-value for the specified series and item. Series are
	 * numbered 0, 1, ...
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * 
	 * @return the x-value for the specified series and item.
	 */
	public Number getX(int series, int item)
	{
		// return new Double(-10.0 + this.translate + (item / 10.0));
		return ((DPoint) _points.elementAt(item)).x;
	}

	/**
	 * Returns the y-value for the specified series and item. Series are
	 * numbered 0, 1, ...
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @param item
	 *            the index (zero-based) of the required item.
	 * 
	 * @return the y-value for the specified series and item.
	 */
	public Number getY(int series, int item)
	{
		return ((DPoint) _points.elementAt(item)).y;
		/*
		 * if (series == 0) { return new Double(Math.cos(-10.0 + this.translate
		 * + (item / 10.0))); } else { return new Double(2 * (Math.sin(-10.0 +
		 * this.translate + (item / 10.0)))); }
		 */
	}

	/**
	 * Returns the number of series in the dataset.
	 * 
	 * @return the number of series in the dataset.
	 */
	public int getSeriesCount()
	{
		return 1;
	}

	/**
	 * Returns the key for a series.
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * 
	 * @return The key for the series.
	 */
	public Comparable getSeriesKey(int series)
	{
		return "Likes vs. Plays";
	}

	/**
	 * Returns the number of items in the specified series.
	 * 
	 * @param series
	 *            the index (zero-based) of the series.
	 * @return the number of items in the specified series.
	 * 
	 */
	public int getItemCount(int series)
	{
		return _points.size();
	}

	@Override
	public Comparable getItemKey(int series, int item) {
		if (series != 0) return null;
		return ((DPoint) _points.elementAt(item)).name; 
	}

}
