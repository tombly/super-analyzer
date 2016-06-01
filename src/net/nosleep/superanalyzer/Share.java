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

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import net.nosleep.superanalyzer.analysis.Album;
import net.nosleep.superanalyzer.analysis.Analysis;
import net.nosleep.superanalyzer.analysis.views.AlbumLikesView;
import net.nosleep.superanalyzer.analysis.views.EncodingKindView;
import net.nosleep.superanalyzer.analysis.views.GenreView;
import net.nosleep.superanalyzer.analysis.views.GrowthView;
import net.nosleep.superanalyzer.analysis.views.IStatisticView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedAAView;
import net.nosleep.superanalyzer.analysis.views.MostPlayedDGView;
import net.nosleep.superanalyzer.analysis.views.PlayCountView;
import net.nosleep.superanalyzer.analysis.views.QualityView;
import net.nosleep.superanalyzer.analysis.views.RatingView;
import net.nosleep.superanalyzer.analysis.views.SummaryView;
import net.nosleep.superanalyzer.analysis.views.TimeView;
import net.nosleep.superanalyzer.analysis.views.WordView;
import net.nosleep.superanalyzer.analysis.views.YearView;
import net.nosleep.superanalyzer.util.GenericFileFilter;
import net.nosleep.superanalyzer.util.GenericFolderFilter;
import net.nosleep.superanalyzer.util.Misc;
import net.nosleep.superanalyzer.util.StringComparator;
import net.nosleep.superanalyzer.util.StringPair;
import net.nosleep.superanalyzer.util.StringPairComparator;
import net.nosleep.superanalyzer.util.StringTriple;

public class Share
{

	public static File askForFile(JFrame window, String extension)
	{

		// get the home folder
		String homeFolder = System.getProperty("user.home");

		// if there is a desktop folder, switch to that one
		if (new File(homeFolder + File.separatorChar + "Desktop").exists() == true)
			homeFolder += File.separatorChar + "Desktop";

		// see where the user wants to save it to
		JFileChooser chooser = new JFileChooser(homeFolder);
		String fileDescription = "";
		if (extension.compareTo("png") == 0)
			fileDescription = Misc.getString("PNG_IMAGE");
		if (extension.compareTo("pdf") == 0)
			fileDescription = Misc.getString("PDF_DOCUMENT");

		chooser.setFileFilter(new GenericFileFilter(fileDescription, "." + extension, null));

		int returnVal = chooser.showSaveDialog(window);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			String path = chooser.getSelectedFile().getAbsolutePath();

			if (path.toLowerCase().endsWith("." + extension) == false)
				path += "." + extension;

			return new File(path);
		}

		return null;
	}

	public static File askForFolder(JFrame window)
	{

		// get the home folder
		String homeFolder = System.getProperty("user.home");

		// if there is a desktop folder, switch to that one
		if (new File(homeFolder + File.separatorChar + "Desktop").exists() == true)
			homeFolder += File.separatorChar + "Desktop";

		// see where the user wants to save it to
		JFileChooser chooser = new JFileChooser(homeFolder);
		String fileDescription = "CHOOSE_FOLDER";

		chooser.setFileFilter(new GenericFolderFilter(fileDescription));

		int returnVal = chooser.showSaveDialog(window);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			String path = chooser.getSelectedFile().getAbsolutePath();

			return new File(path);
		}

		return null;
	}

	public static void saveChartImage(JFrame window, IStatisticView view)
	{
		File file = askForFile(window, "png");
		if (file == null)
			return;

		try
		{
			view.saveImage(file, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveAnalysisPdf(JFrame window, Analysis analysis, JProgressBar progressBar)
	{

		File pdfFile = askForFile(window, "pdf");
		if (pdfFile == null)
			return;

		Misc.printMemoryInfo("pdfstart");

		new SimpleDateFormat();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		String infoString = Misc.getString("CREATED_ON") + " " + dateFormat.format(Calendar.getInstance().getTime());

		int viewCount = 15;
		int viewsDone = 0;

		progressBar.setMinimum(0);
		progressBar.setMaximum(viewCount);

		Dimension d = new Dimension(500, 400);

		try
		{

			String tmpPath = System.getProperty("java.io.tmpdir") + "/image.png";

			// create the pdf document object
			Document document = new Document();

			// create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

			// we open the document
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.NORMAL,
					new Color(0x00, 0x00, 0x00));

			Paragraph p = new Paragraph(Misc.getString("MY_MUSIC_COLLECTION"), titleFont);
			p.setSpacingAfter(4);
			document.add(p);

			Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0x88, 0x88,
					0x88));

			p = new Paragraph("The Super Analyzer by Nosleep Software", subtitleFont);
			p.setSpacingAfter(-2);
			document.add(p);

			p = new Paragraph(infoString, subtitleFont);
			p.setSpacingAfter(30);
			document.add(p);

			PdfPTable table = new PdfPTable(2);
			table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			table.setTotalWidth(500f);
			table.setLockedWidth(true);

			Font statNameFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0x66, 0x66,
					0x66));

			Font statValueFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0x00, 0x00,
					0x00));

			Vector<StringTriple> statPairs = SummaryView.createStatPairs(analysis);
			Paragraph statParagraph = new Paragraph();

			Font summaryTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0x00, 0x00,
					0x00));
			Paragraph titleParagraph = new Paragraph(Misc.getString("SUMMARY"), summaryTitleFont);
			statParagraph.add(titleParagraph);
			
			Paragraph spaceParagraph = new Paragraph("", statNameFont);
			statParagraph.add(spaceParagraph);

			for (int i = 0; i < statPairs.size(); i++)
			{
				Paragraph statLine = new Paragraph();
				StringTriple triple = statPairs.elementAt(i);
				Phrase namePhrase = new Phrase(triple.Name + ": ", statNameFont);
				Phrase valuePhrase = new Phrase(triple.Value, statValueFont);
				statLine.add(namePhrase);
				statLine.add(valuePhrase);
				statParagraph.add(statLine);
			}
			table.addCell(statParagraph);

			viewsDone++;
			progressBar.setValue(viewsDone);

			GenreView genreView = new GenreView(analysis);
			genreView.saveImage(new File(tmpPath), d);
			genreView = null;

			Image img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			AlbumLikesView likesView = new AlbumLikesView(analysis);
			likesView.saveImage(new File(tmpPath), d);
			likesView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			YearView yearView = new YearView(analysis);
			yearView.saveImage(new File(tmpPath), d);
			yearView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			RatingView ratingView = new RatingView(analysis);
			ratingView.saveImage(new File(tmpPath), d);
			ratingView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			TimeView timeView = new TimeView(analysis);
			timeView.saveImage(new File(tmpPath), d);
			timeView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			QualityView qualityView = new QualityView(analysis);
			qualityView.saveImage(new File(tmpPath), d);
			qualityView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			PlayCountView playCountView = new PlayCountView(analysis);
			playCountView.saveImage(new File(tmpPath), d);
			playCountView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			MostPlayedAAView mostPlayedAAView = new MostPlayedAAView(analysis);

			mostPlayedAAView.saveImage(new File(tmpPath), d);
			img = Image.getInstance(tmpPath);
			table.addCell(img);

			mostPlayedAAView.saveImageExtra(new File(tmpPath), d);
			img = Image.getInstance(tmpPath);
			table.addCell(img);

			mostPlayedAAView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			MostPlayedDGView mostPlayedDGView = new MostPlayedDGView(analysis);

			mostPlayedDGView.saveImage(new File(tmpPath), d);
			img = Image.getInstance(tmpPath);
			table.addCell(img);

			mostPlayedDGView.saveImageExtra(new File(tmpPath), d);
			img = Image.getInstance(tmpPath);
			table.addCell(img);

			mostPlayedDGView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			EncodingKindView encodingKindView = new EncodingKindView(analysis);
			encodingKindView.saveImage(new File(tmpPath), d);
			encodingKindView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			GrowthView growthView = new GrowthView(analysis);
			growthView.saveImage(new File(tmpPath), d);
			growthView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			viewsDone++;
			progressBar.setValue(viewsDone);

			WordView wordView = new WordView(analysis);
			wordView.saveImage(new File(tmpPath), d);
			wordView = null;

			img = Image.getInstance(tmpPath);
			table.addCell(img);

			table.addCell("");

			viewsDone++;
			progressBar.setValue(viewsDone);
			Misc.printMemoryInfo("pdfend");

			document.add(table);

			// step 5: we close the document
			document.close();

		}
		catch (DocumentException de)
		{
			System.err.println(de.getMessage());
		}
		catch (IOException ioe)
		{
			System.err.println(ioe.getMessage());
		}
	}

	public static void saveListOfAlbums(JFrame window, Analysis analysis, JProgressBar progressBar)
	{
		saveListOfAlbumsAsPdf(window, analysis, progressBar);
	}

	public static void saveListOfAlbumsAsTxt(JFrame window, Analysis analysis, JProgressBar progressBar)
	{
		File file = askForFile(window, "txt");
		if (file == null)
			return;

		Hashtable albums = analysis.getHash(Analysis.KIND_ALBUM);

		Vector v = new Vector();
		Enumeration keys = albums.elements();
		Integer index = 0;
		while (keys.hasMoreElements())
		{
			String name = (String) keys.nextElement();
			Album a = (Album) albums.get(name);
			if (a.getIsCompilation() == false)
				v.add(name + "\n");
			index++;
		}

		Object[] list = v.toArray();

		Arrays.sort(list, new StringComparator());

		try
		{
			FileOutputStream os = new FileOutputStream(file);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

			for (int i = 0; i < list.length; i++)
			{
				out.write((String) list[i]);
			}

			out.close();
			os.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	public static void saveListOfArtistsAsTxt(JFrame window, Analysis analysis, JProgressBar progressBar)
	{
		File file = askForFile(window, "txt");
		if (file == null)
			return;

		Hashtable albums = analysis.getHash(Analysis.KIND_ALBUM);

		Vector<String> list = new Vector<String>();
		Enumeration keys = albums.keys();
		Integer index = 0;
		String regex = Album.SeparatorRegEx;
		while (keys.hasMoreElements())
		{
			String albumartist = (String) keys.nextElement();
			String[] parts = albumartist.split(regex);
			// StringPair pair = new StringPair(parts[1], parts[0]);
			if (list.contains(parts[1]) == false)
				list.add(parts[1]);
			index++;
		}

		Object lista[] = list.toArray();

		Arrays.sort(lista, new StringComparator());

		try
		{
			FileOutputStream os = new FileOutputStream(file);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

			for (int i = 0; i < lista.length; i++)
			{
				out.write((String) lista[i] + "\n");
			}

			out.close();
			os.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	public static void saveListOfAlbumsAsPdf(JFrame window, Analysis analysis, JProgressBar progressBar)
	{
		File file = askForFile(window, "pdf");
		if (file == null)
			return;

		Hashtable albums = analysis.getHash(Analysis.KIND_ALBUM);

		DecimalFormat timeFormat = new DecimalFormat("0.0");

		StringPair list[] = new StringPair[albums.size()];
		Enumeration keys = albums.keys();
		Integer index = 0;
		String regex = Album.SeparatorRegEx;
		while (keys.hasMoreElements())
		{
			String albumartist = (String) keys.nextElement();
			String[] parts = albumartist.split(regex);
			StringPair pair = new StringPair(parts[1], parts[0]);
			list[index] = pair;
			index++;
		}

		Arrays.sort(list, new StringPairComparator());

		int done = 0;
		progressBar.setMinimum(0);
		progressBar.setMaximum(list.length);

		String infoString = NumberFormat.getInstance().format(list.length) + " ";
		if (list.length == 1)
			infoString += Misc.getString("ALBUM") + ", ";
		else
			infoString += Misc.getString("ALBUMS") + ", ";
		new SimpleDateFormat();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		infoString += "created on " + dateFormat.format(Calendar.getInstance().getTime());

		try
		{
			String tmpPath = System.getProperty("java.io.tmpdir") + "/image.png";

			// create the pdf document object
			Document document = new Document();

			// create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter.getInstance(document, new FileOutputStream(file));

			// we open the document
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.NORMAL,
					new Color(0x00, 0x00, 0x00));
			Paragraph p = new Paragraph(Misc.getString("MY_ALBUMS"), titleFont);
			p.setSpacingAfter(4);
			document.add(p);

			Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0x88, 0x88,
					0x88));
			p = new Paragraph("The Super Analyzer by Nosleep Software", subtitleFont);
			p.setSpacingAfter(-2);
			document.add(p);

			p = new Paragraph(infoString, subtitleFont);
			p.setSpacingAfter(30);
			document.add(p);

			FontSelector albumSelector = new FontSelector();
			Color albumColor = new Color(0x55, 0x55, 0x55);
			albumSelector.addFont(FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, albumColor));
			Font albumAsianFont = FontFactory.getFont("MSung-Light", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
			albumAsianFont.setSize(8);
			albumAsianFont.setColor(albumColor);
			albumSelector.addFont(albumAsianFont);

			FontSelector artistSelector = new FontSelector();
			Color artistColor = new Color(0x77, 0x77, 0x77);
			artistSelector.addFont(FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, artistColor));
			Font artistAsianFont = FontFactory.getFont("MSung-Light", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
			artistAsianFont.setSize(8);
			artistAsianFont.setColor(artistColor);
			artistSelector.addFont(artistAsianFont);

			for (index = 0; index < list.length; index++)
			{
				p = new Paragraph();
				p.setLeading(9);

				// separate the string into the album and artist parts

				Phrase phrase = albumSelector.process(list[index].Value);
				p.add(phrase);

				phrase = artistSelector.process(" " + Misc.getString("BY") + " " + list[index].Name);
				p.add(phrase);

				document.add(p);

				done++;
				progressBar.setValue(done);
			}

			// step 5: we close the document
			document.close();

		}
		catch (DocumentException de)
		{
			System.err.println(de.getMessage());
		}
		catch (IOException ioe)
		{
			System.err.println(ioe.getMessage());
		}

	}

	public static void saveAnalysisHtml(JFrame window, Analysis analysis, JProgressBar progressBar)
	{

		File file = askForFolder(window);
		if (file == null)
			return;

		// create the folder if it does not exist
		file.mkdir();

		String path = file.getAbsolutePath() + File.separatorChar;

		Misc.printMemoryInfo("htmlstart");

		new SimpleDateFormat();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		String infoString = "created on " + dateFormat.format(Calendar.getInstance().getTime());

		int viewCount = 15;
		int viewsDone = 0;

		progressBar.setMinimum(0);
		progressBar.setMaximum(viewCount);

		Dimension d = new Dimension(650, 400);

		StringBuffer summaryHtml = new StringBuffer();

		try
		{

			Vector<StringTriple> statPairs = SummaryView.createStatPairs(analysis);
			for (int i = 0; i < statPairs.size(); i++)
			{
				StringTriple triple = statPairs.elementAt(i);

				summaryHtml.append("<tr><td align=\"right\">");
				summaryHtml.append("<span class=\"Description\">");
				summaryHtml.append(triple.Name);
				summaryHtml.append(":&nbsp;</span>");
				summaryHtml.append("</td><td align=\"left\"><span class=\"DescriptionDark\">");
				summaryHtml.append(triple.Value);
				summaryHtml.append("</span></td></tr>");
			}

			viewsDone++;
			progressBar.setValue(viewsDone);

			GenreView genreView = new GenreView(analysis);
			genreView.saveImage(new File(path + "genres.png"), d);
			genreView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			AlbumLikesView likesView = new AlbumLikesView(analysis);
			likesView.saveImage(new File(path + "likes.png"), d);
			likesView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			YearView yearView = new YearView(analysis);
			yearView.saveImage(new File(path + "release_year.png"), d);
			yearView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			RatingView ratingView = new RatingView(analysis);
			ratingView.saveImage(new File(path + "ratings.png"), d);
			ratingView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			TimeView timeView = new TimeView(analysis);
			timeView.saveImage(new File(path + "listening_times.png"), d);
			timeView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			QualityView qualityView = new QualityView(analysis);
			qualityView.saveImage(new File(path + "quality.png"), d);
			qualityView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			PlayCountView playCountView = new PlayCountView(analysis);
			playCountView.saveImage(new File(path + "play_count.png"), d);
			playCountView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			MostPlayedAAView mostPlayedAAView = new MostPlayedAAView(analysis);
			mostPlayedAAView.saveImage(new File(path + "most_played_artists.png"), d);
			mostPlayedAAView.saveImageExtra(new File(path + "most_played_albums.png"), d);
			mostPlayedAAView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			MostPlayedDGView mostPlayedDGView = new MostPlayedDGView(analysis);
			mostPlayedDGView.saveImage(new File(path + "most_played_decades.png"), d);
			mostPlayedDGView.saveImageExtra(new File(path + "most_played_genres.png"), d);
			mostPlayedDGView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			EncodingKindView encodingKindView = new EncodingKindView(analysis);
			encodingKindView.saveImage(new File(path + "encodings.png"), d);
			encodingKindView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			GrowthView growthView = new GrowthView(analysis);
			growthView.saveImage(new File(path + "growth.png"), d);
			growthView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);

			WordView wordView = new WordView(analysis);
			wordView.saveImage(new File(path + "song_words.png"), d);
			wordView = null;

			viewsDone++;
			progressBar.setValue(viewsDone);
			Misc.printMemoryInfo("htmlend");

		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}

		// create the report html file
		File reportFile = new File(path + "index.html");

		// read the content of the report template from the jar file
		URL url = analysis.getClass().getResource("/media/report.html");
		try
		{
			InputStream is = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String content = "";
			while (in.ready())
			{
				String line = in.readLine();

				if (line.indexOf("<!--date-->") != -1)
					content += infoString;

				if (line.indexOf("<!--textstats-->") != -1)
					content += summaryHtml;

				content += line;
			}

			// now write out the file to the destination
			try
			{
				FileOutputStream os = new FileOutputStream(reportFile);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));

				out.write(content);

				out.close();
				os.close();
			}
			catch (IOException e)
			{
				System.out.println(e);
			}

			in.close();
			is.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}

	}

}
