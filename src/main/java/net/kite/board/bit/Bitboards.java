package net.kite.board.bit;

public class Bitboards {
	
	public static final long EMPTY = 0x0000000000000000L;
	public static final long FULL_BOARD = 0x003F3F3F3F3F3F3FL;
	
	public static final long EMPTY_CEILING = 0x0001010101010101L;
	
	private static final long[][] cellBitboards;
	private static final long[] cellPositionBitboards;
	
	private static final long[] bottomCellBitboards;
	
	static {
		int width = Bitboard.getWidth();
		int height = Bitboard.getHeight();
		int size = Bitboard.getSize();
		
		cellBitboards = new long[width][height];
		cellPositionBitboards = new long[size];
		bottomCellBitboards = new long[width];
		
		long cellBitboard = 1;
		
		for(int x = 0; x < width; x++) {
			
			bottomCellBitboards[x] = cellBitboard;
			
			for(int y = 0; y < height; y++) {
				
				cellBitboards[x][y] = cellBitboard;
				cellBitboard <<= 1;
			}
		}
		
		cellBitboard = 1;
		
		for(int p = 0; p < size; p++) {
			
			cellPositionBitboards[p] = cellBitboard;
			cellBitboard <<= 1;
		}
	}
	
	public static long bottomCellBitboard(int cellX) {
		return bottomCellBitboards[cellX];
	}
	
	public static long cellBitboard(int cellPosition) {
		return cellPositionBitboards[cellPosition];
	}
	
	public static long cellBitboard(int cellX, int cellY) {
		return cellBitboards[cellX][cellY];
	}
	
}
