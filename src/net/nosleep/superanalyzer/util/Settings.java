package net.nosleep.superanalyzer.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

public class Settings
{
	public static final int ENGLISH = 0;
	public static final int GERMAN = 1;
	
	private static Settings instance = null;
	private File _file;

	private Integer _version = 1;
	private Integer _theme = Theme.BLUE;
	private String _libraryFilePath = null;
	private Integer _language;

	public static Settings getInstance()
	{
		if (instance == null)
		{
			instance = new Settings();
		}
		return instance;
	}

	private Settings()
	{
		_file = new File(System.getProperty("java.io.tmpdir") + "/SuperAnalyzerSettings.txt");
	}

	public void start()
	{
		if(Locale.getDefault() == Locale.GERMAN)
			_language = GERMAN;
		else
			_language = ENGLISH;
		
		read();
	}

	public void save()
	{
		write();
	}

	private void read()
	{

		if (!_file.exists())
		{
			return;
		}

		try
		{
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(_file)));
			_version = (Integer) d.readObject();
			_theme = (Integer) d.readObject();
			_libraryFilePath = (String) d.readObject();
			_language = (Integer) d.readObject();
			d.close();
		}
		catch (Exception e)
		{
			System.out.println("ERROR: unable to read settings file");
		}

		updateLocale();
		
		// String filePath = null;

		/*
		 * try { FileInputStream fs = new FileInputStream(pathToPrefs);
		 * BufferedReader in = new BufferedReader(new InputStreamReader(fs,
		 * "UTF-8")); filePath = in.readLine(); in.close(); fs.close(); } catch
		 * (IOException e) {
		 * System.out.println("ERROR: unable to open file at location: " +
		 * e.toString()); }
		 * 
		 * return filePath;
		 */
	}

	private void write()
	{

		try
		{
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(_file)));
			e.writeObject(_version);
			e.writeObject(_theme);
			e.writeObject(_libraryFilePath);
			e.writeObject(_language);
			e.close();
		}
		catch (Exception e)
		{
			System.out.println("ERROR: unable to write settings file");
		}

		/*
		 * try { FileOutputStream fs = new FileOutputStream(pathToPrefs);
		 * BufferedWriter in = new BufferedWriter(new OutputStreamWriter(fs,
		 * "UTF-8")); in.write(path, 0, path.length()); in.close(); fs.close();
		 * } catch (IOException e) {
		 * System.out.println("ERROR: unable to save file location: " +
		 * e.toString()); }
		 */
	}

	public int getTheme()
	{
		return _theme;
	}

	public void setTheme(int theme)
	{
		_theme = theme;
	}

	public String getLibraryFilePath()
	{
		return _libraryFilePath;
	}

	public void setLibraryFilePath(String path)
	{
		_libraryFilePath = path;
	}

	public int getLanguage()
	{
		return _language;
	}

	public void setLanguage(int value)
	{
		_language = value;
		updateLocale();
	}

	public void updateLocale()
	{
		switch(_language)
		{
		case ENGLISH:
			Locale.setDefault(new Locale("en", "EN"));
			break;
		case GERMAN:
			Locale.setDefault(new Locale("de", "DE"));
			break;
		}
	}
	
}
