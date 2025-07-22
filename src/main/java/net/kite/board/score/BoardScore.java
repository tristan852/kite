package net.kite.board.score;

public class BoardScore {
	
	public static final int DRAW = 0;
	
	public static final int INVALID = -19;
	
	private static final int[] WINS;
	private static final int[] LOSSES;
	
	private static final int[] MINIMUMS;
	private static final int[] MAXIMUMS;
	
	private static final int MAXIMAL_WON_FILLED_CELL_AMOUNT = 42;
	
	private static final int RED_SMALLEST_WON_FILLED_CELL_AMOUNT = 7;
	private static final int YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT = 8;
	
	static {
		int l = MAXIMAL_WON_FILLED_CELL_AMOUNT + 1;
		
		WINS = new int[l];
		LOSSES = new int[l];
		
		MINIMUMS = new int[MAXIMAL_WON_FILLED_CELL_AMOUNT];
		MAXIMUMS = new int[MAXIMAL_WON_FILLED_CELL_AMOUNT];
		
		for(int i = 0; i < l; i++) {
			
			WINS[i] = ((MAXIMAL_WON_FILLED_CELL_AMOUNT - i) >> 1) + 1;
			LOSSES[i] = -((MAXIMAL_WON_FILLED_CELL_AMOUNT - i) >> 1) - 1;
		}
		
		for(int i = 0; i < MAXIMAL_WON_FILLED_CELL_AMOUNT; i++) {
			
			boolean redAtTurn = (i & 1) == 0;
			
			int n = i + 2;
			int n2 = i + 1;
			
			int m = redAtTurn ? YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT : RED_SMALLEST_WON_FILLED_CELL_AMOUNT;
			int m2 = redAtTurn ? RED_SMALLEST_WON_FILLED_CELL_AMOUNT : YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT;
			
			if(n < m) n = m;
			if(n2 < m2) n2 = m2;
			
			MINIMUMS[i] = -((MAXIMAL_WON_FILLED_CELL_AMOUNT - n) >> 1) - 1;
			MAXIMUMS[i] = ((MAXIMAL_WON_FILLED_CELL_AMOUNT - n2) >> 1) + 1;
		}
	}
	
	public static int minimal(int filledCellAmount) {
		return MINIMUMS[filledCellAmount];
	}
	
	public static int maximal(int filledCellAmount) {
		return MAXIMUMS[filledCellAmount];
	}
	
	public static int win(int filledCellAmount) {
		return WINS[filledCellAmount];
	}
	
	public static int loss(int filledCellAmount) {
		return LOSSES[filledCellAmount];
	}
	
}
