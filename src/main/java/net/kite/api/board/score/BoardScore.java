package net.kite.api.board.score;

// TODO doc using gpt
// doc: score for board or for move (for move its just the score of the resulting board but from view
// of currently playing person
// draw = 0
// win > 0; score = s means win using sth to last stone
// loss > 0; score = -s means win for opponent using their sth to last stone
public class BoardScore {
	
	private static final String COMPACT_FORMATTED_WIN_SCORE_PATTERN = "W(+%s)";
	private static final String FORMATTED_WIN_SCORE_PATTERN = "Win (+%s)";
	
	private static final String COMPACT_FORMATTED_LOSS_SCORE_PATTERN = "L(%s)";
	private static final String FORMATTED_LOSS_SCORE_PATTERN = "Loss (%s)";
	
	private static final String COMPACT_FORMATTED_DRAW_SCORE = "D(0)";
	private static final String FORMATTED_DRAW_SCORE = "Draw (0)";
	
	private static final int FULL_BOARD_PLAYED_MOVE_AMOUNT = 42;
	
	// gameOverInTotalMoves(boardScore) = number of remaining moves during optimal play
	// If you got a move score e.g. from Kite#evaluateMove
	// then do gameOverInTotalMoves(-moveScore, played_moves_before_move + 1) for num of remaining moves during optimal play from position after move
	// or do gameOverInTotalMoves(moveScore, played_moves_before_move) for num of remaining moves during optimal play (following move) from position before move
	
	public static int gameOverInTotalMoves(int score, int playedMoveAmount) {
		if(score == 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount;
		if(score < 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - (playedMoveAmount & 1) + (score << 1) - playedMoveAmount + 2;
		
		score = -score;
		return FULL_BOARD_PLAYED_MOVE_AMOUNT + (playedMoveAmount & 1) + (score << 1) - playedMoveAmount + 1;
	}
	
	// gameOverInRedPlayerMoves(boardScore) = number of remaining moves during optimal play
	// If you got a move score e.g. from Kite#evaluateMove
	// then do gameOverInRedPlayerMoves(-moveScore, played_moves_before_move + 1) for num of remaining moves during optimal play from position after move
	// or do gameOverInRedPlayerMoves(moveScore, played_moves_before_move) for num of remaining moves during optimal play (following move) from position before move
	public static int gameOverInRedPlayerMoves(int score, int playedMoveAmount) {
		if(score == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount - (playedMoveAmount & 1) + 1) >> 1;
		if(score < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT - ((playedMoveAmount & 1) << 1) + (score << 1) - playedMoveAmount + 1) >> 1) + 1;
		
		score = -score;
		return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (score << 1) - playedMoveAmount) >> 1) + 1;
	}
	
	// gameOverInYellowPlayerMoves(boardScore) = number of remaining moves during optimal play
	// If you got a move score e.g. from Kite#evaluateMove
	// then do gameOverInYellowPlayerMoves(-moveScore, played_moves_before_move + 1) for num of remaining moves during optimal play from position after move
	// or do gameOverInYellowPlayerMoves(moveScore, played_moves_before_move) for num of remaining moves during optimal play (following move) from position before move
	public static int gameOverInYellowPlayerMoves(int score, int playedMoveAmount) {
		if(score == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount + (playedMoveAmount & 1)) >> 1;
		if(score < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (score << 1) - playedMoveAmount) >> 1) + 1;
		
		score = -score;
		return (FULL_BOARD_PLAYED_MOVE_AMOUNT + ((playedMoveAmount & 1) << 1) + (score << 1) - playedMoveAmount + 1) >> 1;
	}
	
	public static int forOppositePlayer(int score) {
		return -score;
	}
	
	public static boolean isWin(int score) {
		return score > 0;
	}
	
	public static boolean isLoss(int score) {
		return score < 0;
	}
	
	public static boolean isDraw(int score) {
		return score == 0;
	}
	
	public static String formatScoreCompactly(int score) {
		if(score == 0) return COMPACT_FORMATTED_DRAW_SCORE;
		if(score < 0) return COMPACT_FORMATTED_LOSS_SCORE_PATTERN.formatted(score);
		
		return COMPACT_FORMATTED_WIN_SCORE_PATTERN.formatted(score);
	}
	
	public static String formatScore(int score) {
		if(score == 0) return FORMATTED_DRAW_SCORE;
		if(score < 0) return FORMATTED_LOSS_SCORE_PATTERN.formatted(score);
		
		return FORMATTED_WIN_SCORE_PATTERN.formatted(score);
	}
	
}
