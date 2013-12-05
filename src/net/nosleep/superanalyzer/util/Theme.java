package net.nosleep.superanalyzer.util;

import java.awt.Color;
import java.awt.Font;

public class Theme
{

	public static final int BLUE = 1;
	public static final int RED = 2;
	public static final int GREEN = 3;
	public static final int SILVER = 4;

	public static Font getFont(int size)
	{
		return new Font("Verdana", Font.PLAIN, size);
	}

	public static Font getBoldFont(int size)
	{
		return new Font("Verdana", Font.BOLD, size);
	}

	public static Color[] getColorSet()
	{
		Color[] colors = new Color[6];

		int theme = Settings.getInstance().getTheme();

		switch (theme)
		{
		case BLUE:
			setBlueColor(colors);
			break;
		case RED:
			setRedColor(colors);
			break;
		case GREEN:
			setGreenColor(colors);
			break;
		case SILVER:
			setSilverColor(colors);
			break;
		}

		return colors;
	}

	private static void setBlueColor(Color[] colors)
	{
		colors[0] = new Color(11, 86, 255);
		colors[1] = new Color(36, 96, 255);
		colors[2] = new Color(72, 146, 255);
		colors[3] = new Color(105, 174, 255);
		colors[4] = new Color(152, 208, 255);
		colors[5] = new Color(196, 235, 255);
	}

	private static void setRedColor(Color[] colors)
	{
		colors[0] = new Color(255, 49, 55);
		colors[1] = new Color(255, 69, 78);
		colors[2] = new Color(255, 101, 107);
		colors[3] = new Color(255, 136, 147);
		colors[4] = new Color(255, 168, 176);
		colors[5] = new Color(255, 214, 217);
	}

	private static void setGreenColor(Color[] colors)
	{
		colors[0] = new Color(1, 183, 4);
		colors[1] = new Color(84, 226, 69);
		colors[2] = new Color(125, 233, 110);
		colors[3] = new Color(156, 237, 150);
		colors[4] = new Color(184, 239, 177);
		colors[5] = new Color(218, 255, 216);
	}

	private static void setSilverColor(Color[] colors)
	{
		colors[0] = new Color(102, 102, 102);
		colors[1] = new Color(127, 127, 127);
		colors[2] = new Color(153, 153, 153);
		colors[3] = new Color(179, 179, 179);
		colors[4] = new Color(204, 204, 204);
		colors[5] = new Color(230, 230, 230);
	}

}
