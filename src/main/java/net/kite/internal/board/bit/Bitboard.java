package net.kite.internal.board.bit;

import java.util.Locale;

public final class Bitboard {
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	private static final int LOGARITHMIC_WIDTH = 3;
	
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
	
	private static final int TO_BOARD_MOVES_STRING_MOVE_CHARACTER_OFFSET = 49;
	
	private static final int TO_HEXADECIMAL_STRING_LENGTH = 16;
	private static final String TO_HEXADECIMAL_STRING_PADDING_STRING = "0";
	
	public static String toHexadecimalString(long bitboard) {
		String string = Long.toHexString(bitboard);
		string = string.toUpperCase(Locale.ROOT);
		
		int n = TO_HEXADECIMAL_STRING_LENGTH - string.length();
		return TO_HEXADECIMAL_STRING_PADDING_STRING.repeat(n) + string;
	}
	
	public static String toBoardMovesString(long bitboard) {
		long b = bitboard;
		
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			
			b |= b >>> 1;
			b &= Bitboards.FULL_EXTENDED_BOARD;
		}
		
		b >>>= 1;
		b &= Bitboards.FULL_EXTENDED_BOARD;
		
		long activeBitboard = bitboard & b;
		int n = Long.bitCount(b);
		
		boolean redIsActive = (n & 1) == 0;
		if(!redIsActive) activeBitboard ^= b;
		
		StringBuilder stringBuilder = toBoardMovesString(b, activeBitboard, Bitboards.EMPTY_CEILING, n);
		return stringBuilder == null ? null : stringBuilder.toString();
	}
	
	private static StringBuilder toBoardMovesString(long maskBitboard, long activeBitboard, long ceilingBitboard, int totalMoves) {
		if(maskBitboard == 0) return new StringBuilder(totalMoves);
		
		long moves = activeBitboard & ceilingBitboard;
		long newActiveBitboard = maskBitboard ^ activeBitboard;
		while(moves != 0) {
			
			long move = Long.lowestOneBit(moves);
			moves ^= move;
			
			long newMaskBitboard = maskBitboard ^ move;
			long newCeilingBitboard = ceilingBitboard + move;
			
			StringBuilder stringBuilder = toBoardMovesString(newMaskBitboard, newActiveBitboard, newCeilingBitboard, totalMoves);
			if(stringBuilder != null) {
				
				int x = Long.numberOfTrailingZeros(move);
				x >>>= LOGARITHMIC_WIDTH;
				
				char c = (char) (TO_BOARD_MOVES_STRING_MOVE_CHARACTER_OFFSET + x);
				return stringBuilder.insert(0, c);
			}
		}
		
		return null;
	}
	
	public static String toBoardString(long bitboard) {
		long b = bitboard;
		
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			
			b |= b >>> 1;
			b &= Bitboards.FULL_EXTENDED_BOARD;
		}
		
		b >>>= 1;
		b &= Bitboards.FULL_EXTENDED_BOARD;
		
		long activeBitboard = bitboard & b;
		
		boolean redAtTurn = (Long.bitCount(b) & 1) == 0;
		
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
