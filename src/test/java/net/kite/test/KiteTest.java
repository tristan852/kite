package net.kite.test;

import net.kite.Kite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KiteTest {
	
	private static final int[][] TEST_BOARD_MOVES = new int[][] {
			{ },
			{ 4 },
			{ 7, 7 },
			{ 7, 3, 1 },
			{ 5, 2, 2, 5 },
			{ 6, 4, 1, 2, 3 },
			{ 3, 6, 4, 6, 2, 5 },
			{ 5, 5, 2, 3, 2, 4, 2 },
			{ 6, 1, 5, 5, 6, 4, 4, 3 },
			{ 6, 1, 4, 6, 1, 6, 5, 5, 5 },
			{ 2, 6, 2, 7, 4, 1, 2, 5, 6, 4 },
			{ 4, 6, 2, 6, 6, 3, 3, 3, 5, 5, 6 },
			{ 3, 1, 2, 1, 6, 6, 7, 5, 2, 5, 4, 4 },
			{ 4, 1, 1, 2, 4, 4, 3, 2, 5, 3, 1, 5, 3 },
			{ 7, 4, 3, 3, 3, 5, 5, 3, 6, 3, 4, 2, 1, 1 },
			{ 1, 7, 7, 5, 5, 1, 4, 3, 4, 2, 5, 4, 4, 4, 6 },
			{ 7, 1, 5, 6, 1, 7, 7, 5, 5, 3, 7, 6, 4, 7, 3, 1 },
			{ 2, 6, 3, 2, 4, 2, 2, 6, 3, 1, 2, 4, 2, 7, 6, 5, 5 },
			{ 5, 6, 2, 7, 4, 2, 1, 3, 6, 7, 2, 3, 4, 1, 7, 6, 5, 7 },
			{ 2, 2, 1, 7, 1, 7, 2, 2, 2, 4, 6, 4, 2, 1, 5, 7, 4, 5, 4 },
			{ 3, 1, 5, 7, 3, 3, 6, 5, 2, 6, 3, 5, 7, 4, 3, 7, 6, 4, 6, 5 },
			{ 5, 1, 7, 2, 7, 5, 2, 2, 7, 1, 2, 2, 5, 7, 3, 5, 1, 2, 1, 6, 7 }
	};
	
	private static final int[] TEST_BOARD_EVALUATIONS = new int[] {
			  1,  -1,   1,   4,   0,   4,  18,  -2,   0, -16,  16,
			  5, -15,  -3,  11, -12,  -2,   1,  10,  12,  -4,   3
	};
	
	@Test
	public void testKite() {
		Assertions.assertDoesNotThrow(() -> {
			
			Kite solver = Kite.createInstance();
			
			int n = TEST_BOARD_MOVES.length;
			for(int i = 0; i < n; i++) {
				
				int[] testBoardMoves = TEST_BOARD_MOVES[i];
				int testBoardEvaluation = TEST_BOARD_EVALUATIONS[i];
				
				solver.setupBoard(testBoardMoves);
				
				int e = solver.evaluateBoard();
				Assertions.assertEquals(e, testBoardEvaluation);
			}
			
		});
	}
	
}
