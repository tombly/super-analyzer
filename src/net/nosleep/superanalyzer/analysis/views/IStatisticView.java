package net.nosleep.superanalyzer.analysis.views;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import net.nosleep.superanalyzer.panels.HomePanel;

public interface IStatisticView
{

	public int getId();

	public void willDisappear();

	public JPanel getPanel(HomePanel homePanel);

	public void saveImage(File image, Dimension d) throws IOException;

	public void saveImageExtra(File file, Dimension d) throws IOException;

	public boolean canSaveImage();

}
