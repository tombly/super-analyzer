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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.views.TagView;
import net.nosleep.superanalyzer.panels.FeedbackPanel;
import net.nosleep.superanalyzer.panels.HelpPanel;
import net.nosleep.superanalyzer.panels.HomePanel;
import net.nosleep.superanalyzer.panels.StartupPanel;
import net.nosleep.superanalyzer.panels.WaitingPanel;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.Settings;
import net.nosleep.superanalyzer.util.Theme;

/**
 * This is the main window of the application. It has a rootPanel that is just a
 * holder that can hold either: 1) the start panel, with the Analysis button, 2)
 * the progress panel while the analysis is running, or 3) the home panel.
 * 
 * Of these three, only an instance of the home panel is kept as a data member
 * so that menu events can be passed along to it after an analysis has been
 * performed. The other two panels are created on-demand.
 */
public class HomeWindow extends JFrame
{

	private static final long serialVersionUID = 1L;

	/*
	 * As self-reference that we can use in other classes and threads.
	 */
	private HomeWindow _frame;

	/*
	 * The panel that is always inside the frame. It holds either the content
	 * panel or the info panel.
	 */
	private JPanel _rootPanel;

	/*
	 * The panel that holds the current content, which is either the start-up
	 * panel, the processing panel, or the home panel.
	 */
	private JPanel _contentPanel;

	/*
	 * The panel that has the buttons and chart panels.
	 */
	private HomePanel _homePanel;

	/*
	 * Our one and only Analysis object.
	 */
	private Analysis _analysis;

	/*
	 * We need to keep a reference to all the menu items so that we can enable
	 * and disable as the program runs.
	 */
	private JMenuItem analyzePlaylistMenuItem;
	private JMenuItem tagReportMenuItem;
	private JMenuItem helpMenuItem;
	private JMenuItem feedbackMenuItem;
	private JMenuItem saveAnalysisPdfMenuItem;
	private JMenuItem saveAnalysisHtmlMenuItem;
	private JMenuItem saveAlbumListPdfMenuItem;
	private JMenuItem saveImageMenuItem;

	private JMenu themeMenu;
	private JRadioButtonMenuItem themeMenuItemBlue;
	private JRadioButtonMenuItem themeMenuItemRed;
	private JRadioButtonMenuItem themeMenuItemGreen;
	private JRadioButtonMenuItem themeMenuItemSilver;

	//private JMenuItem languageMenu;
	//private JRadioButtonMenuItem languageMenuItemEnglish;
	//private JRadioButtonMenuItem languageMenuItemGerman;

	/*
	 * Keep track of the application state, either 1) the user hasn't done
	 * anything yet, 2) the analysis is currently running, or 3) the analysis is
	 * complete.
	 */
	private int _state;
	private static final int START = 0;
	private static final int ANALYZING = 1;
	private static final int DONE = 2;

	/*
	 * A reference to the library file to be analyzed.
	 */
	private LibraryFile _libraryFile;

	/**
	 * Build the window.
	 **/
	public HomeWindow()
	{

		setTitle("Super Analyzer");
		setLocation(100, 100);
		setMinimumSize(new Dimension(700, 500));

		Settings.getInstance().start();

		_contentPanel = new JPanel(new BorderLayout());
		_contentPanel.setOpaque(false);
		_contentPanel.add(new StartupPanel(this));

		_rootPanel = new JPanel(new BorderLayout());
		_rootPanel.setOpaque(false);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		_rootPanel.setPreferredSize(new Dimension(gd.getDisplayMode().getWidth()/2, gd.getDisplayMode().getHeight()/2)); //window adapts to screen aspect ratio
		_rootPanel.add(_contentPanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(Color.white);
		getContentPane().add(_rootPanel);

		makeMenus();
		setMenuUsability();
		pack();
		setVisible(true);

		_state = START;

		_frame = this;

		_libraryFile = new LibraryFile(_frame);

		// check for an update
		new UpdateChecker(_frame);
	}

	public void showContentPanel()
	{
		_rootPanel.removeAll();
		_rootPanel.add(_contentPanel);
		_rootPanel.updateUI();
	}

	public void setMenuUsability()
	{
		switch (_state)
		{

		case START:
			analyzePlaylistMenuItem.setEnabled(true);
			tagReportMenuItem.setEnabled(false);
			helpMenuItem.setEnabled(true);
			feedbackMenuItem.setEnabled(true);
			saveAnalysisPdfMenuItem.setEnabled(false);
			saveAnalysisHtmlMenuItem.setEnabled(false);
			saveAlbumListPdfMenuItem.setEnabled(false);
			saveImageMenuItem.setEnabled(false);
			themeMenu.setEnabled(false);
			//languageMenu.setEnabled(true);
			break;

		case ANALYZING:
			analyzePlaylistMenuItem.setEnabled(false);
			tagReportMenuItem.setEnabled(false);
			helpMenuItem.setEnabled(true);
			feedbackMenuItem.setEnabled(true);
			saveAnalysisPdfMenuItem.setEnabled(false);
			saveAnalysisHtmlMenuItem.setEnabled(false);
			saveAlbumListPdfMenuItem.setEnabled(false);
			saveImageMenuItem.setEnabled(false);
			themeMenu.setEnabled(false);
			//languageMenu.setEnabled(false);
			break;

		case DONE:
			analyzePlaylistMenuItem.setEnabled(true);
			tagReportMenuItem.setEnabled(true);
			helpMenuItem.setEnabled(true);
			feedbackMenuItem.setEnabled(true);
			saveAnalysisPdfMenuItem.setEnabled(true);
			saveAnalysisHtmlMenuItem.setEnabled(true);
			saveAlbumListPdfMenuItem.setEnabled(true);
			saveImageMenuItem.setEnabled(false);
			themeMenu.setEnabled(true);
			//languageMenu.setEnabled(true);
			break;
		}
	}

	public void setSaveImageMenuItemEnabled(boolean enabled)
	{
		saveImageMenuItem.setEnabled(enabled);
	}

	public void performAnalysis()
	{

		if (_libraryFile.findLibraryFile() == false)
			return;

		// create the panel with the progress bar
		final WaitingPanel waitingPanel = new WaitingPanel();
		_contentPanel.removeAll();
		_contentPanel.add(waitingPanel);
		_contentPanel.updateUI();

		// perform the analysis in a thread so that the user interface remains
		// responsive
		new Thread(new Runnable()
		{
			public void run()
			{

				// update the app state and the menu items
				_state = ANALYZING;
				setMenuUsability();

				// release the UI to make its memory available
				_homePanel = null;

				// perform the analysis
				_analysis = new Analysis();
				_analysis.analyze(_libraryFile.getFile(), waitingPanel.getProgressBar());

				// create the panel that holds the buttons and chart panels
				_homePanel = new HomePanel(_frame, _analysis);

				_contentPanel.removeAll();
				_contentPanel.add(_homePanel, BorderLayout.CENTER);
				_contentPanel.updateUI();

				// clear the window and put the home panel in it
				_rootPanel.removeAll();
				_rootPanel.add(_contentPanel, BorderLayout.CENTER);
				_rootPanel.updateUI();

				// update the app state and the menu items
				_state = DONE;
				setMenuUsability();
			}
		}).start();
	}

	protected void makeMenus()
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu(Misc.getString("MENU"));
		menu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(menu);

		analyzePlaylistMenuItem = new JMenuItem(Misc.getString("ANALYZE_PLAYLIST") + "...");
		analyzePlaylistMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{

				// ask the user to find the library file, and if they
				// successfully choose one, then go straight to the
				// analysis
				if (_libraryFile.askUser() == true)
				{
					performAnalysis();
				}
			}
		});
		menu.add(analyzePlaylistMenuItem);

		tagReportMenuItem = new JMenuItem(Misc.getString("VIEW_TAG_REPORT"));
		tagReportMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if (_homePanel != null)
					_homePanel.showView(TagView.Id);
			}
		});
		menu.add(tagReportMenuItem);

		addThemeMenu(menu);
		
		addLanguageMenu(menu);
		
		helpMenuItem = new JMenuItem(Misc.getString("INFO_AND_HELP"));
		helpMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				_rootPanel.removeAll();
				_rootPanel.add(new HelpPanel(_frame), BorderLayout.CENTER);
				_rootPanel.updateUI();
			}
		});
		menu.add(helpMenuItem);

		feedbackMenuItem = new JMenuItem(Misc.getString("SEND_FEEDBACK") + "...");
		feedbackMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				_rootPanel.removeAll();
				_rootPanel.add(new FeedbackPanel(_frame), BorderLayout.CENTER);
				_rootPanel.updateUI();
			}
		});
		menu.add(feedbackMenuItem);

		JMenuItem exitMenuItem = new JMenuItem(Misc.getString("EXIT"));
		exitMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				System.exit(0);
			}
		});
		menu.add(exitMenuItem);

		menu = new JMenu(Misc.getString("SHARE"));
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);

		saveAnalysisPdfMenuItem = new JMenuItem(Misc.getString("SAVE_ANALYSIS_AS_PDF") + "...");
		saveAnalysisPdfMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				performLongOp(1);
			}
		});
		menu.add(saveAnalysisPdfMenuItem);

		saveAnalysisHtmlMenuItem = new JMenuItem(Misc.getString("SAVE_ANALYSIS_AS_HTML") + "...");
		saveAnalysisHtmlMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				performLongOp(2);
			}
		});
		menu.add(saveAnalysisHtmlMenuItem);

		saveAlbumListPdfMenuItem = new JMenuItem(Misc.getString("SAVE_LIST_OF_ALBUMS_AS_PDF") + "...");
		saveAlbumListPdfMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				performLongOp(3);
			}
		});
		menu.add(saveAlbumListPdfMenuItem);

		saveImageMenuItem = new JMenuItem(Misc.getString("SAVE_CHART_AS_IMAGE") + "...");
		saveImageMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if (_homePanel != null)
					_homePanel.saveChartAsImage(_frame);
			}
		});
		menu.add(saveImageMenuItem);

		setJMenuBar(menuBar);
	}
	
	private void addThemeMenu(JMenu menu)
	{
		themeMenu = new JMenu(Misc.getString("COLOR_THEME"));
		ButtonGroup group = new ButtonGroup();

		themeMenuItemBlue = new JRadioButtonMenuItem(Misc.getString("BLUE"));
		group.add(themeMenuItemBlue);
		themeMenuItemBlue.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setTheme(Theme.BLUE);
				Settings.getInstance().save();
				_homePanel.refresh();

			}
		});
		themeMenu.add(themeMenuItemBlue);

		themeMenuItemRed = new JRadioButtonMenuItem(Misc.getString("RED"));
		group.add(themeMenuItemRed);
		themeMenuItemRed.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setTheme(Theme.RED);
				Settings.getInstance().save();
				_homePanel.refresh();
			}
		});
		themeMenu.add(themeMenuItemRed);

		themeMenuItemGreen = new JRadioButtonMenuItem(Misc.getString("GREEN"));
		group.add(themeMenuItemGreen);
		themeMenuItemGreen.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setTheme(Theme.GREEN);
				Settings.getInstance().save();
				_homePanel.refresh();
			}
		});
		themeMenu.add(themeMenuItemGreen);

		themeMenuItemSilver = new JRadioButtonMenuItem(Misc.getString("SILVER"));
		group.add(themeMenuItemSilver);
		themeMenuItemSilver.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setTheme(Theme.SILVER);
				Settings.getInstance().save();
				_homePanel.refresh();
			}
		});
		themeMenu.add(themeMenuItemSilver);

		switch (Settings.getInstance().getTheme())
		{
		case Theme.BLUE:
			themeMenuItemBlue.setSelected(true);
			break;
		case Theme.RED:
			themeMenuItemRed.setSelected(true);
			break;
		case Theme.GREEN:
			themeMenuItemGreen.setSelected(true);
			break;
		case Theme.SILVER:
			themeMenuItemSilver.setSelected(true);
			break;
		}

		menu.add(themeMenu);
	}
	
	private void addLanguageMenu(JMenu menu)
	{
		/*
		languageMenu = new JMenu(Misc.getString("LANGUAGE"));
		ButtonGroup languageGroup = new ButtonGroup();

		languageMenuItemEnglish = new JRadioButtonMenuItem(Misc.getString("ENGLISH"));
		languageGroup.add(languageMenuItemEnglish);
		languageMenuItemEnglish.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setLanguage(Settings.ENGLISH);
				Settings.getInstance().save();
			}
		});
		languageMenu.add(languageMenuItemEnglish);

		languageMenuItemGerman = new JRadioButtonMenuItem(Misc.getString("GERMAN"));
		languageGroup.add(languageMenuItemGerman);
		languageMenuItemGerman.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				Settings.getInstance().setLanguage(Settings.GERMAN);
				Settings.getInstance().save();
				_frame.invalidate();
			}
		});
		languageMenu.add(languageMenuItemGerman);

		switch (Settings.getInstance().getLanguage())
		{
		case Settings.ENGLISH:
			languageMenuItemEnglish.setSelected(true);
			break;
		case Settings.GERMAN:
			languageMenuItemGerman.setSelected(true);
			break;
		}

		menu.add(languageMenu);
		*/
	}

	private void performLongOp(final int op)
	{
		// create the panel with the progress bar
		final WaitingPanel waitingPanel = new WaitingPanel();
		_contentPanel.removeAll();
		_contentPanel.add(waitingPanel);
		_contentPanel.updateUI();

		// perform the analysis in a thread so that the user interface remains
		// responsive
		new Thread(new Runnable()
		{
			public void run()
			{

				// update the app state and the menu items
				_state = ANALYZING;
				setMenuUsability();

				// release the home panel to free up any memory
				_homePanel = null;

				switch (op)
				{
				case 1:
					Share.saveAnalysisPdf(_frame, _analysis, waitingPanel.getProgressBar());
					break;
				case 2:
					// Share.saveListOfArtistsAsTxt(_frame, _analysis,
					// waitingPanel.getProgressBar());
					Share.saveAnalysisHtml(_frame, _analysis, waitingPanel.getProgressBar());
					break;
				case 3:
					Share.saveListOfAlbums(_frame, _analysis, waitingPanel.getProgressBar());
					break;
				}

				// create the panel that holds the buttons and chart panels
				_homePanel = new HomePanel(_frame, _analysis);

				// add the home panel to the content panel
				_contentPanel.removeAll();
				_contentPanel.add(_homePanel, BorderLayout.CENTER);
				_contentPanel.updateUI();

				// clear the window and put the home panel in it
				showContentPanel();

				// update the app state and the menu items
				_state = DONE;
				setMenuUsability();
			}
		}).start();

	}

}
