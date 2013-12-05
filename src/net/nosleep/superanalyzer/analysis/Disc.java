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

package net.nosleep.superanalyzer.analysis;


/**
 * This class is a holder for disc information.
 */
class Disc
{
	private Integer discNumber;
	private Integer tracksOnDisc;

	public Disc(Integer discNumber, Integer tracksOnDisc)
	{
		this.discNumber = discNumber;
		this.tracksOnDisc = tracksOnDisc;
	}

	public Integer getDiscNumber()
	{
		return discNumber;
	}

	public void setDiscNumber(Integer discNumber)
	{
		this.discNumber = discNumber;
	}

	public Integer getTracksOnDisc()
	{
		return tracksOnDisc;
	}

	public void setTracksOnDisc(Integer tracksOnDisc)
	{
		this.tracksOnDisc = tracksOnDisc;
	}

}
