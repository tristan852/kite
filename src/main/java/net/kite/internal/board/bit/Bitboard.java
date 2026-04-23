package net.kite.internal.board.bit;

import java.util.Locale;

public final class Bitboard {
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	
	private static final int SIZE = 64;
	
	private static final int LARGEST_CELL_POSITION = 63;
	
	private static final int RIGHT_SHIFT_AMOUNT = 8;
	
	private static final String TO_STRING_CELL_STRING = "O";
	private static final String TO_STRING_RED_CELL_STRING = "X";
	private static final String TO_STRING_YELLOW_CELL_STRING = "O";
	private static final String TO_STRING_MISSING_CELL_STRING = ".";
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	
	private static final int TO_HEXADECIMAL_STRING_LENGTH = 16;
	private static final String TO_HEXADECIMAL_STRING_PADDING_STRING = "0";
	
	public static String toHexadecimalString(long bitboard) {
		String string = Long.toHexString(bitboard);
		string = string.toUpperCase(Locale.ROOT);
		
		int n = TO_HEXADECIMAL_STRING_LENGTH - string.length();
		return TO_HEXADECIMAL_STRING_PADDING_STRING.repeat(n) + string;
	}
	
	public static String toBoardString(long bitboard) {
		long b = bitboard;
		
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			
			b |= b >>> 1;
			b &= Bitboards.FULL_EXTENDED_BOARD;
		}
		
		int n = Long.bitCount(b) - BOARD_WIDTH;
		boolean redAtTurn = (n & 1) == 0;
		
		b >>>= 1;
		long activeBitboard = bitboard & b;
		
		String s1;
		String s2;
		
		if(redAtTurn) {
			
			s1 = TO_STRING_RED_CELL_STRING;
			s2 = TO_STRING_YELLOW_CELL_STRING;
			
		} else {
			
			s1 = TO_STRING_YELLOW_CELL_STRING;
			s2 = TO_STRING_RED_CELL_STRING;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int y = BOARD_HEIGHT - 1; y >= 0; y--) {
			
			long board = 1L << y;
			
			for(int x = 0; x < BOARD_WIDTH; x++) {
				
				boolean contained = (b & board) != 0;
				
				String s;
				if(contained) {
					
					s = (activeBitboard & board) == 0 ? s2 : s1;
					
				} else s = TO_STRING_MISSING_CELL_STRING;
				
				stringBuilder.append(s);
				
				board <<= RIGHT_SHIFT_AMOUNT;
			}
			
			if(y != 0) stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
		}
		
		return stringBuilder.toString();
	}
	
	public static String toString(long bitboard) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int y = HEIGHT - 1; y >= 0; y--) {
			
			long board = 1L << y;
			
			for(int x = 0; x < WIDTH; x++) {
				
				boolean contained = (bitboard & board) != 0;
				
				String s = contained ? TO_STRING_CELL_STRING : TO_STRING_MISSING_CELL_STRING;
				stringBuilder.append(s);
				
				board <<= RIGHT_SHIFT_AMOUNT;
			}
			
			if(y != 0) stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
		}
		
		return stringBuilder.toString();
	}
	
	public static int firstCellPosition(long bitboard) {
		return Long.numberOfTrailingZeros(bitboard);
	}
	
	public static int lastCellPosition(long bitboard) {
		return LARGEST_CELL_POSITION - Long.numberOfLeadingZeros(bitboard);
	}
	
	public static long toggleCell(long bitboard, int cellPosition) {
		long board = 1L << cellPosition;
		
		return bitboard ^ board;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	public static int getSize() {
		return SIZE;
	}
	
}
