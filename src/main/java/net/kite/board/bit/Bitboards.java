package net.kite.board.bit;

public class Bitboards {
	
	public static final long EMPTY = 0x0000000000000000L;
	public static final long FULL_BOARD = 0x003F3F3F3F3F3F3FL;
	
	public static final long EMPTY_CEILING = 0x0001010101010101L;
	
	public static final long ODD_BOARD_ROWS = 0x0015151515151515L;
	public static final long EVEN_BOARD_ROWS = 0x002A2A2A2A2A2A2AL;
	
	public static final long[] ORDERED_EVEN_BOARD_ROWS = new long[] {
			0x0020202020202020L,
			0x0008080808080808L,
			0x0002020202020202L
	};
	
	public static final long SYMMETRY_PRUNE_BITBOARD = 0x000000003F3F3F3FL;
	
	private static final long[][] cellBitboards;
	private static final long[] cellPositionBitboards;
	private static final long[] cellsBelowCellPositionBitboards;
	
	private static final long[] bottomCellBitboards;
	
	static {
		int width = Bitboard.getWidth();
		int height = Bitboard.getHeight();
		int size = Bitboard.getSize();
		
		cellBitboards = new long[width][height];
		cellPositionBitboards = new long[size];
		cellsBelowCellPositionBitboards = new long[size];
		
		bottomCellBitboards = new long[width];
		
		long cellBitboard = 1;
		int p = 0;
		
		for(int x = 0; x < width; x++) {
			
			bottomCellBitboards[x] = cellBitboard;
			long column = 0;
			
			for(int y = 0; y < height; y++) {
				
				cellBitboards[x][y] = cellBitboard;
				
				cellPositionBitboards[p] = cellBitboard;
				cellsBelowCellPositionBitboards[p] = column;
				
				column |= cellBitboard;
				
				cellBitboard <<= 1;
				p++;
			}
		}
	}
	
	public static long bottomCellBitboard(int cellX) {
		return bottomCellBitboards[cellX];
	}
	
	public static long cellsBelowCellBitboard(int cellPosition) {
		return cellsBelowCellPositionBitboards[cellPosition];
	}
	
	public static long cellBitboard(int cellPosition) {
		return cellPositionBitboards[cellPosition];
	}
	
	public static long cellBitboard(int cellX, int cellY) {
		return cellBitboards[cellX][cellY];
	}
	
}
