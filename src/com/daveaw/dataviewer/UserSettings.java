package com.daveaw.dataviewer;

import java.awt.Color;
import java.awt.Font;

public interface UserSettings {
	public static Color background = new Color(50,50,50);
	public static Color background_sub_frame = new Color(65,65,65);
	public static Color background_display = new Color(25,25,25);
	
	public static Color foreground = new Color(192,192,192);
	public static Color foreground_highlighted_constant = new Color(50,145,255);
	
	public static int FONTSIZE = 16;
	
	public static Font font = new Font(Font.MONOSPACED, 0, FONTSIZE);
	public static Font menu_font = new Font(Font.MONOSPACED, 0, 12);
	
	public void updateUserSettings();
}
