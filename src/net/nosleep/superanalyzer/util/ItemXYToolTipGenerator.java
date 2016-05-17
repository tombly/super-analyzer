/**
 * 
 */
package net.nosleep.superanalyzer.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * @author Elias
 * 
 *         This class acts like
 *         {@link org.jfree.charts.labels.StandardXYToolTipGenerator}, except
 *         for displaying the item key in the tooltip as opposed to the standard
 *         series key.
 *
 */
public class ItemXYToolTipGenerator extends AbstractXYItemLabelGenerator
		implements XYToolTipGenerator, Cloneable, PublicCloneable, Serializable {

	/**
	 * auto-generated
	 */
	private static final long serialVersionUID = 1985366627439816731L;

	/** The default tooltip format. */
	public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2})";
	public String formatString;

	/**
	 * Creates a tool tip generator using default number formatters.
	 */
	public ItemXYToolTipGenerator() {
		this(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
	}

	/**
	 * Creates a tool tip generator using the specified number formatters.
	 *
	 * @param formatString
	 *            the item label format string (<code>null</code> not
	 *            permitted).
	 * @param xFormat
	 *            the format object for the x values (<code>null</code> not
	 *            permitted).
	 * @param yFormat
	 *            the format object for the y values (<code>null</code> not
	 *            permitted).
	 */
	public ItemXYToolTipGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
		this.formatString = formatString;
	}

	/**
	 * Creates a tool tip generator using the specified number formatters.
	 *
	 * @param formatString
	 *            the label format string (<code>null</code> not permitted).
	 * @param xFormat
	 *            the format object for the x values (<code>null</code> not
	 *            permitted).
	 * @param yFormat
	 *            the format object for the y values (<code>null</code> not
	 *            permitted).
	 */
	public ItemXYToolTipGenerator(String formatString, DateFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
		this.formatString = formatString;
	}

	/**
	 * Creates a tool tip generator using the specified formatters (a number
	 * formatter for the x-values and a date formatter for the y-values).
	 *
	 * @param formatString
	 *            the item label format string (<code>null</code> not
	 *            permitted).
	 * @param xFormat
	 *            the format object for the x values (<code>null</code>
	 *            permitted).
	 * @param yFormat
	 *            the format object for the y values (<code>null</code> not
	 *            permitted).
	 *
	 * @since 1.0.4
	 */
	public ItemXYToolTipGenerator(String formatString, NumberFormat xFormat, DateFormat yFormat) {
		super(formatString, xFormat, yFormat);
		this.formatString = formatString;
	}

	/**
	 * Creates a tool tip generator using the specified date formatters.
	 *
	 * @param formatString
	 *            the label format string (<code>null</code> not permitted).
	 * @param xFormat
	 *            the format object for the x values (<code>null</code> not
	 *            permitted).
	 * @param yFormat
	 *            the format object for the y values (<code>null</code> not
	 *            permitted).
	 */
	public ItemXYToolTipGenerator(String formatString, DateFormat xFormat, DateFormat yFormat) {
		super(formatString, xFormat, yFormat);
		this.formatString = formatString;
	}

	/**
	 * Generates the tool tip text for an item in a dataset.
	 *
	 * @param dataset
	 *            the dataset (<code>null</code> not permitted).
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 *
	 * @return The tooltip text (possibly <code>null</code>).
	 */
	public String generateToolTip(XYDataset dataset, int series, int item) {
		return generateLabelString(dataset, series, item);
	}
	
	/**
     * Generates a label string for an item in the dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String generateLabelString(XYDataset dataset, int series, int item) {
        String result = null;
        Object[] items = createItemArray(dataset, series, item);
        result = MessageFormat.format(this.formatString, items);
        return result;
    }

	protected Object[] createItemArray(XYDataset dataset, int series, int item) {

		Object[] result = super.createItemArray(dataset, series, item);
		result[0] = ((KeyXYDataset)dataset).getItemKey(series, item).toString();
		
		return result;
	}

	/**
	 * Tests this object for equality with an arbitrary object.
	 *
	 * @param obj
	 *            the other object (<code>null</code> permitted).
	 *
	 * @return A boolean.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StandardXYToolTipGenerator)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns an independent copy of the generator.
	 *
	 * @return A clone.
	 *
	 * @throws CloneNotSupportedException
	 *             if cloning is not supported.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
