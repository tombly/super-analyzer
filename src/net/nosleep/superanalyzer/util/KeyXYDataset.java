package net.nosleep.superanalyzer.util;

import org.jfree.data.xy.XYDataset;

/**
 * 
 * A dataset with keys and x,y values for each entry. The keys are sorted into series.
 *
 */
public interface KeyXYDataset extends XYDataset {
	
    /**
     * Returns the key for a item.
     *
     * @param series  the series index (in the range <code>0</code> to
     *     <code>getSeriesCount() - 1</code>).
     * @param item  the item index (in the range <code>0</code> to
     *     <code>getItemCount() - 1</code>).   
     *
     * @return The key for the item.
     */
    public Comparable<?> getItemKey(int series, int item);

}
