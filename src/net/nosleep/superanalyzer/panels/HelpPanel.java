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

package net.nosleep.superanalyzer.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.nosleep.superanalyzer.HomeWindow;
import net.nosleep.superanalyzer.util.Constants;
import net.nosleep.superanalyzer.util.Misc;

public class HelpPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public HelpPanel(final HomeWindow parent)
	{
		setLayout(new BorderLayout());

		JPanel descPanel = new JPanel(new BorderLayout());

		JEditorPane editPane = null;

		// read the content of the help template from the jar file
		URL url = this.getClass().getResource("/media/help.html");
		try
		{
			InputStream is = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String content = "";
			while (in.ready())
			{
				String line = in.readLine();

				line = line.replace("VERSION", Constants.VERSION);
				line = line.replace("WHAT_IT_DOES_TEXT", Misc.getString("WHAT_IT_DOES_TEXT"));
				line = line.replace("WHAT_IT_DOES", Misc.getString("WHAT_IT_DOES"));
				line = line.replace("HOW_TO_USE_IT_TEXT", Misc.getString("HOW_TO_USE_IT_TEXT"));
				line = line.replace("HOW_TO_USE_IT", Misc.getString("HOW_TO_USE_IT"));
				line = line.replace("DOESNT_WORK_TEXT", Misc.getString("DOESNT_WORK_TEXT"));
				line = line.replace("DOESNT_WORK", Misc.getString("DOESNT_WORK"));
				line = line.replace("OPEN_SOURCE_TEXT", Misc.getString("OPEN_SOURCE_TEXT"));
				line = line.replace("OPEN_SOURCE", Misc.getString("OPEN_SOURCE"));
				line = line.replace("CREDITS", Misc.getString("CREDITS"));

				content += line;
			}

			in.close();
			is.close();

			editPane = new JEditorPane();
			editPane.setContentType("text/html");
			editPane.setText(content);
			editPane.setEditable(false);
			editPane.setBorder(new EmptyBorder(20, 20, 20, 20));
			JScrollPane scroller = new JScrollPane(editPane);
			scroller.setBorder(new javax.swing.border.MatteBorder(5, 5, 5, 5, Color.white));
			descPanel.add(scroller, BorderLayout.CENTER);
		}
		catch (Exception e)
		{
		}

		add(descPanel, BorderLayout.CENTER);

		if(editPane!= null)
			editPane.setCaretPosition(0);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		JButton backButton = new JButton(Misc.getString("BACK"));
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(Color.white);
		backButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				parent.showContentPanel();
			}
		});
		buttonPanel.add(backButton, BorderLayout.WEST);

		add(buttonPanel, BorderLayout.NORTH);
	}

}
