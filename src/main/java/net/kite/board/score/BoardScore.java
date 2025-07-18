package net.kite.board.score;

public class BoardScore {
	
	public static final int DRAW = 0;
	
	public static final int INVALID = -19;
	
	private static final int MAXIMAL_WON_FILLED_CELL_AMOUNT = 42;
	
	private static final int RED_SMALLEST_WON_FILLED_CELL_AMOUNT = 7;
	private static final int YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT = 8;
	
	public static int minimal(int filledCellAmount) {
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		
		int n = filledCellAmount + 2;
		int m = redAtTurn ? YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT : RED_SMALLEST_WON_FILLED_CELL_AMOUNT;
		
		if(n < m) n = m;
		
		return -((MAXIMAL_WON_FILLED_CELL_AMOUNT - n) >> 1) - 1;
	}
	
	public static int maximal(int filledCellAmount) {
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		
		int n = filledCellAmount + 1;
		int m = redAtTurn ? RED_SMALLEST_WON_FILLED_CELL_AMOUNT : YELLOW_SMALLEST_WON_FILLED_CELL_AMOUNT;
		
		if(n < m) n = m;
		
		return ((MAXIMAL_WON_FILLED_CELL_AMOUNT - n) >> 1) + 1;
	}
	
	public static int win(int filledCellAmount) {
		return ((MAXIMAL_WON_FILLED_CELL_AMOUNT - filledCellAmount) >> 1) + 1;
	}
	
	public static int lose(int filledCellAmount) {
		return -((MAXIMAL_WON_FILLED_CELL_AMOUNT - filledCellAmount) >> 1) - 1;
	}
	
}
