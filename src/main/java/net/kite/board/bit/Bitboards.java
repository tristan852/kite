package net.kite.board.bit;

public class Bitboards {
	
	public static final long EMPTY = 0x0000000000000000L;
	public static final long FULL_BOARD = 0x003F3F3F3F3F3F3FL;
	
	public static final long EMPTY_CEILING = 0x0001010101010101L;
	
	private static final long[][] cellBitboards;
	
	static {
		int width = Bitboard.getWidth();
		int height = Bitboard.getHeight();
		
		cellBitboards = new long[width][height];
		
		long cellBitboard = 1;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				
				cellBitboards[x][y] = cellBitboard;
				cellBitboard <<= 1;
			}
		}
	}
	
	public static long cellBitboard(int cellX, int cellY) {
		return cellBitboards[cellX][cellY];
	}
	
}
