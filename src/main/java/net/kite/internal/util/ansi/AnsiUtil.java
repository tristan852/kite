package net.kite.internal.util.ansi;

import org.fusesource.jansi.Ansi;

public final class AnsiUtil {
	
	private static final int WHITE_RGB = 0xFFFFFF;
	
	private static boolean ansiCodesDisabled;
	
	public static void restoreCheckpoint() {
		if(ansiCodesDisabled) return;
		
		System.out.println(Ansi.ansi().restoreCursorPosition().eraseScreen(Ansi.Erase.FORWARD));
		System.out.flush();
	}
	
	public static void createCheckpoint() {
		if(ansiCodesDisabled) return;
		
		System.out.println(Ansi.ansi().saveCursorPosition());
		System.out.flush();
	}
	
	public static void switchToAlternateScreenBuffer() {
		if(ansiCodesDisabled) return;
		
		System.out.println(Ansi.ansi().saveCursorPosition().a("\033[?1049h").cursor(0, 0));
		System.out.flush();
	}
	
	public static void switchToNormalScreenBuffer() {
		if(ansiCodesDisabled) return;
		
		System.out.println(Ansi.ansi().a("\033[?1049l").restoreCursorPosition());
		System.out.flush();
	}
	
	public static void clearScreen() {
		if(ansiCodesDisabled) return;
		
		System.out.println(Ansi.ansi().eraseScreen().cursor(0, 0));
		System.out.flush();
	}
	
	public static String redAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgRed().a(string).reset().toString();
	}
	
	public static String boldRedAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgRed().bold().a(string).reset().toString();
	}
	
	public static String boldRedBackgroundAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().bgRed().fgRgb(WHITE_RGB).bold().a(string).reset().toString();
	}
	
	public static String yellowAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgYellow().a(string).reset().toString();
	}
	
	public static String boldYellowAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgYellow().bold().a(string).reset().toString();
	}
	
	public static String boldYellowBackgroundAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().bgYellow().fgRgb(WHITE_RGB).bold().a(string).reset().toString();
	}
	
	public static String greenAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgGreen().a(string).reset().toString();
	}
	
	public static String boldGreenAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgGreen().bold().a(string).reset().toString();
	}
	
	public static String cyanAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgCyan().a(string).reset().toString();
	}
	
	public static String boldCyanAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgCyan().bold().a(string).reset().toString();
	}
	
	public static String magentaAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgMagenta().a(string).reset().toString();
	}
	
	public static void enableAnsiCodes() {
		ansiCodesDisabled = false;
	}
	
	public static void disableAnsiCodes() {
		ansiCodesDisabled = true;
	}
	
	public static boolean areAnsiCodesDisabled() {
		return ansiCodesDisabled;
	}
	
}
