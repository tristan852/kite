package net.kite.board.bit;

public class Bitboard {
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	
	private static final int SIZE = 64;
	
	private static final String TO_STRING_CELL_STRING = "O";
	private static final String TO_STRING_MISSING_CELL_STRING = ".";
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	
	public static String toString(long bitboard) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int y = HEIGHT - 1; y >= 0; y--) {
			for(int x = 0; x < WIDTH; x++) {
				
				int p = HEIGHT * x + y;
				
				long board = 1L << p;
				boolean contained = (bitboard & board) != 0;
				
				String s = contained ? TO_STRING_CELL_STRING : TO_STRING_MISSING_CELL_STRING;
				stringBuilder.append(s);
			}
			
			if(y != 0) stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
		}
		
		return stringBuilder.toString();
	}
	
	public static int firstCellPosition(long bitboard) {
		return Long.numberOfTrailingZeros(bitboard);
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
