package net.kite.internal.util.ansi;

import org.fusesource.jansi.Ansi;

public final class AnsiUtil {
	
	private static final int WHITE = 0xFFFFFF;
	
	private static boolean ansiColorsDisabled;
	
	public static String redAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgRed().a(string).reset().toString();
	}
	
	public static String boldRedAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgRed().bold().a(string).reset().toString();
	}
	
	public static String boldRedBackgroundAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().bgRed().fgRgb(WHITE).bold().a(string).reset().toString();
	}
	
	public static String yellowAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgYellow().a(string).reset().toString();
	}
	
	public static String boldYellowAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgYellow().bold().a(string).reset().toString();
	}
	
	public static String boldYellowBackgroundAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().bgYellow().fgRgb(WHITE).bold().a(string).reset().toString();
	}
	
	public static String greenAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgGreen().a(string).reset().toString();
	}
	
	public static String boldGreenAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgGreen().bold().a(string).reset().toString();
	}
	
	public static String cyanAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgCyan().a(string).reset().toString();
	}
	
	public static String boldCyanAnsi(String string) {
		if(ansiColorsDisabled) return string;
		
		return Ansi.ansi().fgCyan().bold().a(string).reset().toString();
	}
	
	public static void enableAnsiColors() {
		ansiColorsDisabled = false;
	}
	
	public static void disableAnsiColors() {
		ansiColorsDisabled = true;
	}
	
	public static boolean areAnsiColorsDisabled() {
		return ansiColorsDisabled;
	}
	
}
