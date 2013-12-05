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

import java.io.File;
import javax.swing.filechooser.*;

/**
 * This file filter can be used to restrict the file choices in file open/save
 * windows.
 */
public class GenericFileFilter extends FileFilter
{
	String description;
	String extension;
	String filename;

	public GenericFileFilter(String d, String e, String f)
	{
		description = d;
		extension = e;
		filename = f;
	}

	/**
	 * This method returns the name of this type of file.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * This method says that only directories and files ending in .xml should be
	 * shown in a file open/save dialog box.
	 */
	public boolean accept(File f)
	{
		if (f != null)
		{
			if (f.isDirectory())
				return true;

			if (filename != null)
			{
				if (f.getName().compareTo(filename) == 0)
					return true;
			}
			else
			{
				if (f.getName().toLowerCase().indexOf(extension) != -1)
					return true;
			}
		}

		return false;
	}

}
