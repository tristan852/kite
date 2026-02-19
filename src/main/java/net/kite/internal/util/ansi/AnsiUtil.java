package net.kite.internal.util.ansi;

import org.fusesource.jansi.Ansi;

public final class AnsiUtil {
	
	private static final String SWITCH_TO_ALTERNATE_SCREEN_BUFFER_ANSI = "\033[?1049h";
	private static final String SWITCH_TO_NORMAL_SCREEN_BUFFER_ANSI = "\033[?1049l";
	
	private static final int WHITE_RGB = 0xFFFFFF;
	
	private static boolean ansiCodesDisabled;
	
	public static void restoreCheckpoint() {
		if(ansiCodesDisabled) return;
		
		System.out.print(Ansi.ansi().restoreCursorPosition().eraseScreen(Ansi.Erase.FORWARD));
		System.out.flush();
	}
	
	public static void createCheckpoint() {
		if(ansiCodesDisabled) return;
		
		System.out.print(Ansi.ansi().saveCursorPosition());
		System.out.flush();
	}
	
	public static void switchToAlternateScreenBuffer() {
		if(ansiCodesDisabled) return;
		
		System.out.print(Ansi.ansi().saveCursorPosition().a(SWITCH_TO_ALTERNATE_SCREEN_BUFFER_ANSI).cursor(0, 0));
		System.out.flush();
	}
	
	public static void switchToNormalScreenBuffer() {
		if(ansiCodesDisabled) return;
		
		System.out.print(Ansi.ansi().a(SWITCH_TO_NORMAL_SCREEN_BUFFER_ANSI).restoreCursorPosition());
		System.out.flush();
	}
	
	public static void clearScreen() {
		if(ansiCodesDisabled) return;
		
		System.out.print(Ansi.ansi().eraseScreen().cursor(0, 0));
		System.out.flush();
	}
	
	public static String brightRedAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightRed().a(string).reset().toString();
	}
	
	public static String boldBrightRedAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightRed().bold().a(string).reset().toString();
	}
	
	public static String boldBrightRedBackgroundAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().bgBrightRed().fgRgb(WHITE_RGB).bold().a(string).reset().toString();
	}
	
	public static String brightYellowAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightYellow().a(string).reset().toString();
	}
	
	public static String boldBrightYellowAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightYellow().bold().a(string).reset().toString();
	}
	
	public static String boldBrightYellowBackgroundAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().bgBrightYellow().fgRgb(WHITE_RGB).bold().a(string).reset().toString();
	}
	
	public static String brightGreenAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightGreen().a(string).reset().toString();
	}
	
	public static String boldBrightGreenAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightGreen().bold().a(string).reset().toString();
	}
	
	public static String cyanAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgCyan().a(string).reset().toString();
	}
	
	public static String boldBrightCyanAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightCyan().bold().a(string).reset().toString();
	}
	
	public static String brightMagentaAnsi(String string) {
		if(ansiCodesDisabled) return string;
		
		return Ansi.ansi().fgBrightMagenta().a(string).reset().toString();
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
