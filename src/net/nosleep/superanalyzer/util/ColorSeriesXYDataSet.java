package net.nosleep.superanalyzer.util;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Similar to {@link SimpleXYDataSet}, but requires DPoint's as parameter, which it then sorts into colored series
 */

public class ColorSeriesXYDataSet extends SimpleXYDataSet {

	Vector<Vector<DPoint>> _points = new Vector<>();



	public ColorSeriesXYDataSet(Vector<DPoint> points) {
		Vector<DPoint> tempPoints = points;

		Vector<Color> colors = new Vector<>(); //this will store all the already used colors

		DPoint d;
		for (int i = 0; i < tempPoints.size(); i++)
		{
			d = tempPoints.get(i);
			if (colors.contains(d.color)) //do we now this color?
			{
				_points.get(colors.indexOf(d.color)).add(d); //add d at the position of the color in the colors-Vector
			}
			else {
				_points.add(new Vector<DPoint>()); //add new sub-vetor to _points
				colors.add(d.color); //add the new color
				_points.get(colors.indexOf(d.color)).add(d); //add d at the position of the color in the colors-Vector
			}
		}
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
	 * @return the y-value for the specified series and item.
	 */
	public Number getX(int series, int item)
	{
		return ((DPoint) _points.get(series).elementAt(item)).x;
		/*
		 * if (series == 0) { return new Double(Math.cos(-10.0 + this.translate
		 * + (item / 10.0))); } else { return new Double(2 * (Math.sin(-10.0 +
		 * this.translate + (item / 10.0)))); }
		 */
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
		return ((DPoint) _points.get(series).elementAt(item)).y;
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
		return _points.size();
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
		return _points.get(series).firstElement().secondName; //gets the genre for the first element in the series, assuming they are all of the same genre
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
		return _points.get(series).size();
	}

	@Override
	public Comparable getItemKey(int series, int item) {
		return ((DPoint) _points.get(series).elementAt(item)).name; 
	}
	
	public Color getColor(int series){
		return _points.get(series).firstElement().color; //returns color of the first element in the series
	}
}
