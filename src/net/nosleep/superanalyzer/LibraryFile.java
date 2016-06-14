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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.nosleep.superanalyzer.util.GenericFileFilter;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Settings;

public class LibraryFile
{

	private File _file;
	private JFrame _window;

	public LibraryFile(JFrame window)
	{
		// remember the window so we know who to attach our dialogs to
		_window = window;

		// try to automatically find the library file
		tryToFindLibraryFile();
	}

	public File getFile()
	{
		return _file;
	}

	
	/**
	 * 
	 * @return true if a valid file was selected
	 *
	 */
	public boolean askUser()
	{

		// get the home folder
		String homeFolder = System.getProperty("user.home");

		// if there is a desktop folder, switch to that one
		if (new File(homeFolder + File.separatorChar + "Desktop").exists() == true)
			homeFolder += File.separatorChar + "Desktop";

		JFileChooser chooser = new JFileChooser(homeFolder);
		chooser.setFileFilter(new GenericFileFilter("iTunes Playlist", ".xml", null));
		int returnVal = chooser.showOpenDialog(_window);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return false;
		_file = chooser.getSelectedFile();
		
		String extension = "";

		int i = _file.getName().lastIndexOf('.');
		if (i > 0) {
		    extension = _file.getName().substring(i+1);
		}
		
		if(!extension.equals("xml")) {
			JOptionPane.showMessageDialog(_window, Misc.getString("NO_LIBRARY_FILE_SELECTED"), "Super Analyzer",
					JOptionPane.ERROR_MESSAGE);						
			_file = null;
			return false; //file extension is not xml
		}

		return true;
	}

	private void tryToFindLibraryFile()
	{
		String homeFolder = System.getProperty("user.home");

		// try 'home/Music'
		String musicFolder = homeFolder + File.separator + "Music" + File.separator + "iTunes" + File.separator
				+ "iTunes Library.xml";
		_file = new File(musicFolder);
		if (_file.exists() == true)
			return;

		// try 'home/My Music'
		musicFolder = homeFolder + File.separator + "My Music" + File.separator + "iTunes" + File.separator
				+ "iTunes Library.xml";
		_file = new File(musicFolder);
		if (_file.exists() == true)
			return;

		// see if we previously chose the location and it's saved
		String savedPath = Settings.getInstance().getLibraryFilePath();
		if (savedPath != null)
		{
			_file = new File(savedPath);
			if (_file.exists() == true)
				return;
			else
			{
				// if it's not there anymore, then clear what we saved
				Settings.getInstance().setLibraryFilePath("");
				Settings.getInstance().save();
			}
		}

		// the user will have to find it themself
		_file = null;
	}

	/**
	 * 
	 * @return true if a library file was found
	 */
	public boolean findLibraryFile()
	{
		// if the library file has not been set
		if (_file == null)
		{
			// see if its in the current folder
			_file = new File("iTunes Library.xml");
			if (_file.exists() == false)
			{
				_file = null;
				JOptionPane.showMessageDialog(_window, Misc.getString("FIND_LIBRARY_FILE"), "Super Analyzer",
						JOptionPane.ERROR_MESSAGE);

				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileFilter(new GenericFileFilter("iTunes Library File", ".xml",
						"iTunes Library.xml"));
				int returnVal = chooser.showOpenDialog(_window);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					_file = chooser.getSelectedFile();
					
					String extension = "";

					int i = _file.getName().lastIndexOf('.');
					if (i > 0) {
					    extension = _file.getName().substring(i+1);
					}
					
					if(!extension.equals("xml")){
						JOptionPane.showMessageDialog(_window, Misc.getString("NO_LIBRARY_FILE_SELECTED"), "Super Analyzer",
								JOptionPane.ERROR_MESSAGE);						
						_file = null;
						return false; //file extension is not xml
					}

					// lets save this in the temp folder of the computer so we
					// don't have to always look it up
					Settings.getInstance().setLibraryFilePath(_file.getAbsolutePath());
					Settings.getInstance().save();

					return true;
				}

				return false;
			}

			return false;
		}
		else
			return true;
	}

}
