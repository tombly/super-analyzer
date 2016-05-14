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
		Color[] colors = new Color[11];

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
		colors[1] = new Color(23, 91, 255);
		colors[2] = new Color(36, 96, 255);
		colors[3] = new Color(54, 121, 255);
		colors[4] = new Color(72, 146, 255);
		colors[5] = new Color(88, 160, 255);
		colors[6] = new Color(105, 174, 255);
		colors[7] = new Color(129, 191, 255);
		colors[8] = new Color(152, 208, 255);
		colors[9] = new Color(174, 222, 255);
		colors[10] = new Color(196, 235, 255);
	}
	
	private static void setRedColor(Color[] colors)
	{
		colors[0] = new Color(255, 49, 55);
		colors[1] = new Color(255, 59, 67);
		colors[2] = new Color(255, 69, 78);
		colors[3] = new Color(255, 85, 93);
		colors[4] = new Color(255, 101, 107);
		colors[5] = new Color(255, 119, 127);
		colors[6] = new Color(255, 136, 147);
		colors[7] = new Color(255, 152, 162);
		colors[8] = new Color(255, 168, 176);
		colors[9] = new Color(255, 191, 197);
		colors[10] = new Color(255, 214, 217);
	}

	private static void setGreenColor(Color[] colors)
	{
		colors[0] = new Color(1, 183, 4);
		colors[1] = new Color(43, 205, 37);
		colors[2] = new Color(84, 226, 69);
		colors[3] = new Color(105, 230, 90);
		colors[4] = new Color(125, 233, 110);
		colors[5] = new Color(141, 235, 130);
		colors[6] = new Color(156, 237, 150);
		colors[7] = new Color(170, 238, 164);
		colors[8] = new Color(184, 239, 177);
		colors[9] = new Color(201, 247, 197);
		colors[10] = new Color(218, 255, 216);
	}

	private static void setSilverColor(Color[] colors)
	{
		colors[0] = new Color(102, 102, 102);
		colors[1] = new Color(115, 115, 115);
		colors[2] = new Color(127, 127, 127);
		colors[3] = new Color(140, 140, 140);
		colors[4] = new Color(153, 153, 153);
		colors[5] = new Color(166, 166, 166);
		colors[6] = new Color(179, 179, 179);
		colors[7] = new Color(192, 192, 192);
		colors[8] = new Color(204, 204, 204);
		colors[9] = new Color(217, 217, 217);
		colors[10] = new Color(230, 230, 230);
	}

}
