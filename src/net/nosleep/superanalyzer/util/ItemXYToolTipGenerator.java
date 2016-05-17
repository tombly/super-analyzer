/**
 * 
 */
package net.nosleep.superanalyzer.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * @author Elias
 * 
 * This class acts like {@link org.jfree.charts.labels.StandardXYToolTipGenerator}, except for displaying the item key in the tooltip as opposed to the standard series key.
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
	
	/**
     * Creates a tool tip generator using default number formatters.
     */
	public ItemXYToolTipGenerator() {
		this(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(),
                NumberFormat.getNumberInstance());
	}

	/**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code>
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code>
     *                 not permitted).
     */
	public ItemXYToolTipGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
		// TODO Auto-generated constructor stub
	}

	/**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the label format string (<code>null</code> not
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code>
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code>
     *                 not permitted).
     */
	public ItemXYToolTipGenerator(String formatString, DateFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
		// TODO Auto-generated constructor stub
	}

	/**
     * Creates a tool tip generator using the specified formatters (a
     * number formatter for the x-values and a date formatter for the
     * y-values).
     *
     * @param formatString  the item label format string (<code>null</code>
     *                      not permitted).
     * @param xFormat  the format object for the x values (<code>null</code>
     *                 permitted).
     * @param yFormat  the format object for the y values (<code>null</code>
     *                 not permitted).
     *
     * @since 1.0.4
     */
	public ItemXYToolTipGenerator(String formatString, NumberFormat xFormat, DateFormat yFormat) {
		super(formatString, xFormat, yFormat);
		// TODO Auto-generated constructor stub
	}

	/**
     * Creates a tool tip generator using the specified date formatters.
     *
     * @param formatString  the label format string (<code>null</code> not
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code>
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code>
     *                 not permitted).
     */
	public ItemXYToolTipGenerator(String formatString, DateFormat xFormat, DateFormat yFormat) {
		super(formatString, xFormat, yFormat);
		// TODO Auto-generated constructor stub
	}
	
	/**
     * Generates the tool tip text for an item in a dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The tooltip text (possibly <code>null</code>).
     */
	 public String generateToolTip(XYDataset dataset, int series, int item) {
	        return generateLabelString(dataset, series, item);
	 }
	 
	 protected Object[] createItemArray(XYDataset dataset, int series,
			 int item) {
		 
		 
		 Object[] result = new Object[3];
		 result[0] = dataset.getSeriesKey(series).toString();

		 double x = dataset.getXValue(series, item);
		 if (xDateFormat != null) {
			 result[1] = this.xDateFormat.format(new Date((long) x));
		 }
		 else {
			 result[1] = this.xFormat.format(x);
		 }

		 double y = dataset.getYValue(series, item);
		 if (Double.isNaN(y) && dataset.getY(series, item) == null) {
			 result[2] = this.nullYString;
		 }
		 else {
			 if (this.yDateFormat != null) {
				 result[2] = this.yDateFormat.format(new Date((long) y));
			 }
			 else {
				 result[2] = this.yFormat.format(y);
			 }
		 }
		 return result;
	 }

	 

}
