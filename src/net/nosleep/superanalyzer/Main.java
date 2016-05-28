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

package net.nosleep.superanalyzer;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.nosleep.superanalyzer.util.Misc;

/**
 * This is the starting point of the application. It just creates an instance of
 * a UI object which displays the main window of the application.
 */
public class Main
{

	public static void main(String[] args)
	{		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}

		try
		{
			new HomeWindow();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, Misc.getString("CRASH_MESSAGE"), "Super Analyzer",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

	}

}
