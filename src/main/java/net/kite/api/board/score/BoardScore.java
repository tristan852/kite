package net.kite.api.board.score;

import net.kite.api.Kite;

/**
 * Utility class for working with board evaluation scores.
 * A score represents the evaluation of a board
 * or of a move.
 * For a move, the score equals the score of the
 * resulting board but from the perspective of
 * the currently playing player.
 * <p>
 * A score of {@code 0} represents a draw.
 * A score {@code > 0} represents a win for
 * the current player.
 * A score {@code < 0} represents a loss for
 * the current player.
 * <p>
 * If {@code score = s > 0}, the current player
 * wins using their {@code s}-th to last stone
 * under optimal play.
 * <p>
 * If {@code score = -s < 0}, the opponent wins
 * using their {@code s}-th to last stone
 * under optimal play.
 */
public class BoardScore {
	
	private static final String COMPACT_FORMATTED_WIN_SCORE_PATTERN = "W(+%s)";
	private static final String FORMATTED_WIN_SCORE_PATTERN = "Win (+%s)";
	
	private static final String COMPACT_FORMATTED_LOSS_SCORE_PATTERN = "L(%s)";
	private static final String FORMATTED_LOSS_SCORE_PATTERN = "Loss (%s)";
	
	private static final String COMPACT_FORMATTED_DRAW_SCORE = "D(0)";
	private static final String FORMATTED_DRAW_SCORE = "Draw (0)";
	
	private static final int FULL_BOARD_PLAYED_MOVE_AMOUNT = 42;
	
	/**
	 * Returns the number of remaining total moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move score using e.g.
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then it holds that:
	 * {@code gameOverInTotalMoves(-moveScore, playedMovesBeforeMove + 1) + 1
	 * == gameOverInTotalMoves(moveScore, playedMovesBeforeMove)},
	 * assuming the move with score {@code moveScore} is played
	 * and optimal play continues from the resulting position.
	 * <p>
	 * The expression
	 * {@code gameOverInTotalMoves(-moveScore, playedMovesBeforeMove + 1)}
	 * denotes the remaining moves after the move.
	 * Adding {@code 1} accounts for the move itself.
	 * <p>
	 * The expression
	 * {@code gameOverInTotalMoves(moveScore, playedMovesBeforeMove)}
	 * denotes the remaining moves before the move is played.
	 *
	 * @param score the board evaluation score
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining total moves under optimal play
	 */
	public static int gameOverInTotalMoves(int score, int playedMoveAmount) {
		if(score == 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount;
		if(score < 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - (playedMoveAmount & 1) + (score << 1) - playedMoveAmount + 2;
		
		score = -score;
		return FULL_BOARD_PLAYED_MOVE_AMOUNT + (playedMoveAmount & 1) + (score << 1) - playedMoveAmount + 1;
	}
	
	/**
	 * Returns the number of remaining red player moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move score using
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then:
	 * <p>
	 * For the position after the move use
	 * {@code gameOverInRedPlayerMoves(-moveScore, playedMovesBeforeMove + 1)}.
	 * <p>
	 * For the position before the move use
	 * {@code gameOverInRedPlayerMoves(moveScore, playedMovesBeforeMove)}.
	 *
	 * @param score the board evaluation score
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining red player moves under optimal play
	 */
	public static int gameOverInRedPlayerMoves(int score, int playedMoveAmount) {
		if(score == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount - (playedMoveAmount & 1) + 1) >> 1;
		if(score < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT - ((playedMoveAmount & 1) << 1) + (score << 1) - playedMoveAmount + 1) >> 1) + 1;
		
		score = -score;
		return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (score << 1) - playedMoveAmount) >> 1) + 1;
	}
	
	/**
	 * Returns the number of remaining yellow player moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move score using
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then:
	 * <p>
	 * For the position after the move use
	 * {@code gameOverInYellowPlayerMoves(-moveScore, playedMovesBeforeMove + 1)}.
	 * <p>
	 * For the position before the move use
	 * {@code gameOverInYellowPlayerMoves(moveScore, playedMovesBeforeMove)}.
	 *
	 * @param score the board evaluation score
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining yellow player moves under optimal play
	 */
	public static int gameOverInYellowPlayerMoves(int score, int playedMoveAmount) {
		if(score == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount + (playedMoveAmount & 1)) >> 1;
		if(score < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (score << 1) - playedMoveAmount) >> 1) + 1;
		
		score = -score;
		return (FULL_BOARD_PLAYED_MOVE_AMOUNT + ((playedMoveAmount & 1) << 1) + (score << 1) - playedMoveAmount + 1) >> 1;
	}
	
	/**
	 * Returns the score from the perspective
	 * of the opposite player.
	 * <p>
	 * This is equivalent to {@code -score}.
	 *
	 * @param score the board evaluation score
	 * @return score from the opponent's perspective
	 */
	public static int forOppositePlayer(int score) {
		return -score;
	}
	
	/**
	 * Returns whether the score represents a win
	 * for the current player.
	 *
	 * @param score the board evaluation score
	 * @return {@code true} if {@code score > 0}
	 */
	public static boolean isWin(int score) {
		return score > 0;
	}
	
	/**
	 * Returns whether the score represents a loss
	 * for the current player.
	 *
	 * @param score the board evaluation score
	 * @return {@code true} if {@code score < 0}
	 */
	public static boolean isLoss(int score) {
		return score < 0;
	}
	
	/**
	 * Returns whether the score represents a draw.
	 *
	 * @param score the board evaluation score
	 * @return {@code true} if {@code score == 0}
	 */
	public static boolean isDraw(int score) {
		return score == 0;
	}
	
	/**
	 * Returns a compact formatted representation
	 * of the score.
	 * <p>
	 * Wins are formatted as {@code W(+s)}.
	 * Losses are formatted as {@code L(s)}.
	 * Draws are formatted as {@code D(0)}.
	 *
	 * @param score the board evaluation score
	 * @return compact formatted score string
	 */
	public static String formatScoreCompactly(int score) {
		if(score == 0) return COMPACT_FORMATTED_DRAW_SCORE;
		if(score < 0) return COMPACT_FORMATTED_LOSS_SCORE_PATTERN.formatted(score);
		
		return COMPACT_FORMATTED_WIN_SCORE_PATTERN.formatted(score);
	}
	
	/**
	 * Returns a formatted representation
	 * of the score.
	 * <p>
	 * Wins are formatted as {@code Win (+s)}.
	 * Losses are formatted as {@code Loss (s)}.
	 * Draws are formatted as {@code Draw (0)}.
	 *
	 * @param score the board evaluation score
	 * @return formatted score string
	 */
	public static String formatScore(int score) {
		if(score == 0) return FORMATTED_DRAW_SCORE;
		if(score < 0) return FORMATTED_LOSS_SCORE_PATTERN.formatted(score);
		
		return FORMATTED_WIN_SCORE_PATTERN.formatted(score);
	}
	
}
