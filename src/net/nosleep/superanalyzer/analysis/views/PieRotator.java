package net.nosleep.superanalyzer.analysis.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.jfree.chart.plot.PiePlot3D;

/**
 * The rotator.
 */
public class PieRotator extends Timer implements ActionListener
{

	/** The plot. */
	private PiePlot3D plot;

	/** The angle. */
	public static double angle = 90.0;

	/**
	 * Constructor.
	 * 
	 * @param plot
	 *            the plot.
	 */
	PieRotator(PiePlot3D plot)
	{
		super(200, null);
		this.plot = plot;
		addActionListener(this);
	}

	/**
	 * Modifies the starting angle.
	 * 
	 * @param event
	 *            the action event.
	 */
	public void actionPerformed(ActionEvent event)
	{
		this.plot.setStartAngle(this.angle);
		this.angle = this.angle + 0.00;
		if (this.angle >= 360.0)
		{
			this.angle = 0.0;
		}
	}
}