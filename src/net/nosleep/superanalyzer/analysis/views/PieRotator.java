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
		this.plot.setStartAngle(PieRotator.angle);
		PieRotator.angle = PieRotator.angle + 0.00;
		if (PieRotator.angle >= 360.0)
		{
			PieRotator.angle = 0.0;
		}
	}
}