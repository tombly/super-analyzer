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
import java.awt.FlowLayout;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.nosleep.superanalyzer.HomeWindow;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Theme;

public class FeedbackPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JTextArea textArea;
	HomeWindow _parent;

	private String _introString = Misc.getString("FEEDBACK_INSTRUCTIONS");

	public FeedbackPanel(final HomeWindow parent)
	{

		_parent = parent;

		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		add(panel, BorderLayout.CENTER);

		// make a panel with some instructions in it
		JPanel instructionsPanel = new JPanel(new BorderLayout());
		instructionsPanel.setOpaque(false);
		JTextArea instructionsLabel = new JTextArea(_introString);
		instructionsLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
		instructionsLabel.setFont(Theme.getFont(12));
		instructionsLabel.setLineWrap(true);
		instructionsLabel.setEditable(false);
		instructionsLabel.setWrapStyleWord(true);
		instructionsPanel.add(instructionsLabel, BorderLayout.CENTER);

		JPanel titlePanel = new JPanel(new FlowLayout());
		titlePanel.setOpaque(false);
		JLabel titleLabel = new JLabel(Misc.getString("SEND_FEEDBACK"));
		titleLabel.setFont(Theme.getBoldFont(20));
		titlePanel.add(titleLabel);
		titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		instructionsPanel.add(titlePanel, BorderLayout.NORTH);

		panel.add(instructionsPanel, BorderLayout.NORTH);

		// add a panel with a text area in it for the comments
		JPanel feedbackPanel = new JPanel(new BorderLayout());
		feedbackPanel.setOpaque(false);
		textArea = new JTextArea();
		textArea.setFont(Theme.getFont(12));
		textArea.setLineWrap(true);
		textArea.setEditable(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		feedbackPanel.add(textArea, BorderLayout.CENTER);
		feedbackPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.add(feedbackPanel, BorderLayout.CENTER);

		// add a panel with Send/Cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		JButton sendButton = new JButton(Misc.getString("SEND"));
		sendButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{

				if (textArea.getText().length() == 0)
					JOptionPane.showMessageDialog(parent, Misc.getString("PLEASE_ENTER_SOME_FEEDBACK"), "Super Analyzer",
							JOptionPane.INFORMATION_MESSAGE);
				else
					sendFeedback();
			}
		});
		buttonPanel.add(sendButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				parent.showContentPanel();
			}
		});
		buttonPanel.add(cancelButton);
	}

	private String sendFeedback()
	{
		String result = "";

		try
		{
			String addy = "http://www.nosleep.net/feedback.php?re="
					+ URLEncoder.encode("Super Analyzer Feedback", "UTF-8") + "&message="
					+ URLEncoder.encode(textArea.getText(), "UTF-8");

			URL url = new URL(addy);

			InputStream in = url.openStream();
			BufferedInputStream bufIn = new BufferedInputStream(in);

			byte content[] = new byte[1000];
			int offset = 0;
			int toRead = 1000;
			for (;;)
			{
				int amount = bufIn.read(content, offset, toRead);
				if (amount == -1)
					break;
				offset += amount;
			}

			String html = new String(content);
			result = html.substring(0, offset);
		}
		catch (MalformedURLException mue)
		{
			result = "Invalid URL";
		}
		catch (IOException ioe)
		{
			result = "I/O Error - " + ioe;
		}

		JOptionPane.showMessageDialog(_parent, Misc.getString("THANKS") + "!", "Super Analyzer", JOptionPane.INFORMATION_MESSAGE);

		_parent.showContentPanel();

		return result;
	}
}
