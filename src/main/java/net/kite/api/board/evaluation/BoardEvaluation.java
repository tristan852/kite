package net.kite.api.board.evaluation;

import net.kite.api.Kite;

/**
 * Utility class for working with board evaluation scores.
 * A score represents the evaluation of a board
 * or of a move.
 * For a move, the evaluation equals the evaluation of the
 * resulting board but from the perspective of
 * the currently playing player.
 * <p>
 * An evaluation of {@code 0} represents a draw.
 * An evaluation {@code > 0} represents a win for
 * the current player.
 * An evaluation {@code < 0} represents a loss for
 * the current player.
 * <p>
 * If {@code evaluation = e > 0}, the current player
 * wins using their {@code e}-th to last stone
 * under optimal play.
 * <p>
 * If {@code evaluation = -e < 0}, the opponent wins
 * using their {@code e}-th to last stone
 * under optimal play.
 */
public final class BoardEvaluation {
	
	private static final String FORMATTED_WIN_SCORE_PATTERN = "Win (+%s)";
	private static final String FORMATTED_LOSS_SCORE_PATTERN = "Loss (%s)";
	private static final String FORMATTED_DRAW_SCORE = "Draw (0)";
	
	private static final String COMPACT_FORMATTED_WIN_SCORE_PREFIX = "+";
	private static final String COMPACT_FORMATTED_DRAW_SCORE = "0";
	
	private static final int FULL_BOARD_PLAYED_MOVE_AMOUNT = 42;
	
	private static final int SMALLEST_EVALUATION = -18;
	private static final int LARGEST_EVALUATION = 18;
	
	/**
	 * Returns the number of remaining total moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move evaluation using e.g.
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then it holds that:
	 * {@code gameOverInTotalMoves(-moveEvaluation, playedMovesBeforeMove + 1) + 1
	 * == gameOverInTotalMoves(moveEvaluation, playedMovesBeforeMove)},
	 * assuming the move with evaluation {@code moveEvaluation} is played
	 * and optimal play continues from the resulting position.
	 * <p>
	 * The expression
	 * {@code gameOverInTotalMoves(-moveEvaluation, playedMovesBeforeMove + 1)}
	 * denotes the remaining moves after the move.
	 * Adding {@code 1} accounts for the move itself.
	 * <p>
	 * The expression
	 * {@code gameOverInTotalMoves(moveEvaluation, playedMovesBeforeMove)}
	 * denotes the remaining moves before the move is played.
	 *
	 * @param evaluation the board evaluation
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining total moves under optimal play
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}, or if {@code playedMoveAmount} is less
	 *         than {@code 0} or greater than {@code 42}
	 */
	public static int gameOverInTotalMoves(int evaluation, int playedMoveAmount) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(playedMoveAmount < 0 || playedMoveAmount > FULL_BOARD_PLAYED_MOVE_AMOUNT) {
			
			String exceptionMessage = String.format("playedMoveAmount out of range: %s (expected 0..42)", playedMoveAmount);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(evaluation == 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount;
		if(evaluation < 0) return FULL_BOARD_PLAYED_MOVE_AMOUNT - (playedMoveAmount & 1) + (evaluation << 1) - playedMoveAmount + 2;
		
		evaluation = -evaluation;
		return FULL_BOARD_PLAYED_MOVE_AMOUNT + (playedMoveAmount & 1) + (evaluation << 1) - playedMoveAmount + 1;
	}
	
	/**
	 * Returns the number of remaining red player moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move evaluation using
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then:
	 * <p>
	 * For the position after the move use
	 * {@code gameOverInRedPlayerMoves(-moveEvaluation, playedMovesBeforeMove + 1)}.
	 * <p>
	 * For the position before the move use
	 * {@code gameOverInRedPlayerMoves(moveEvaluation, playedMovesBeforeMove)}.
	 *
	 * @param evaluation the board evaluation
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining red player moves under optimal play
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}, or if {@code playedMoveAmount} is less
	 *         than {@code 0} or greater than {@code 42}
	 */
	public static int gameOverInRedPlayerMoves(int evaluation, int playedMoveAmount) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(playedMoveAmount < 0 || playedMoveAmount > FULL_BOARD_PLAYED_MOVE_AMOUNT) {
			
			String exceptionMessage = String.format("playedMoveAmount out of range: %s (expected 0..42)", playedMoveAmount);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(evaluation == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount - (playedMoveAmount & 1) + 1) >> 1;
		if(evaluation < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT - ((playedMoveAmount & 1) << 1) + (evaluation << 1) - playedMoveAmount + 1) >> 1) + 1;
		
		evaluation = -evaluation;
		return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (evaluation << 1) - playedMoveAmount) >> 1) + 1;
	}
	
	/**
	 * Returns the number of remaining yellow player moves
	 * until the game is over under optimal play.
	 * <p>
	 * If you obtained a move evaluation using
	 * {@link Kite#evaluateMove(int moveColumnIndex)},
	 * then:
	 * <p>
	 * For the position after the move use
	 * {@code gameOverInYellowPlayerMoves(-moveEvaluation, playedMovesBeforeMove + 1)}.
	 * <p>
	 * For the position before the move use
	 * {@code gameOverInYellowPlayerMoves(moveEvaluation, playedMovesBeforeMove)}.
	 *
	 * @param evaluation the board evaluation
	 * @param playedMoveAmount number of already played moves
	 * @return number of remaining yellow player moves under optimal play
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}, or if {@code playedMoveAmount} is less
	 *         than {@code 0} or greater than {@code 42}
	 */
	public static int gameOverInYellowPlayerMoves(int evaluation, int playedMoveAmount) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(playedMoveAmount < 0 || playedMoveAmount > FULL_BOARD_PLAYED_MOVE_AMOUNT) {
			
			String exceptionMessage = String.format("playedMoveAmount out of range: %s (expected 0..42)", playedMoveAmount);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(evaluation == 0) return (FULL_BOARD_PLAYED_MOVE_AMOUNT - playedMoveAmount + (playedMoveAmount & 1)) >> 1;
		if(evaluation < 0) return ((FULL_BOARD_PLAYED_MOVE_AMOUNT + (evaluation << 1) - playedMoveAmount) >> 1) + 1;
		
		evaluation = -evaluation;
		return (FULL_BOARD_PLAYED_MOVE_AMOUNT + ((playedMoveAmount & 1) << 1) + (evaluation << 1) - playedMoveAmount + 1) >> 1;
	}
	
	/**
	 * Returns the evaluation from the perspective
	 * of the opposite player.
	 * <p>
	 * This is equivalent to {@code -evaluation}.
	 *
	 * @param evaluation the board evaluation
	 * @return evaluation from the opponent's perspective
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static int forOppositePlayer(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		return -evaluation;
	}
	
	/**
	 * Returns whether the evaluation represents a win
	 * for the current player.
	 *
	 * @param evaluation the board evaluation
	 * @return {@code true} if {@code evaluation > 0}
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static boolean isWin(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		return evaluation > 0;
	}
	
	/**
	 * Returns whether the evaluation represents a loss
	 * for the current player.
	 *
	 * @param evaluation the board evaluation
	 * @return {@code true} if {@code evaluation < 0}
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static boolean isLoss(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		return evaluation < 0;
	}
	
	/**
	 * Returns whether the evaluation represents a draw.
	 *
	 * @param evaluation the board evaluation
	 * @return {@code true} if {@code evaluation == 0}
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static boolean isDraw(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		return evaluation == 0;
	}
	
	/**
	 * Returns a compact formatted representation
	 * of the evaluation.
	 * <p>
	 * Wins are formatted as {@code +s}.
	 * Losses are formatted as {@code s}.
	 * Draws are formatted as {@code 0}.
	 *
	 * @param evaluation the board evaluation
	 * @return compact formatted evaluation string
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static String formatEvaluationCompactly(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(evaluation == 0) return COMPACT_FORMATTED_DRAW_SCORE;
		if(evaluation > 0) return COMPACT_FORMATTED_WIN_SCORE_PREFIX + evaluation;
		
		return String.valueOf(evaluation);
	}
	
	/**
	 * Returns a formatted representation
	 * of the evaluation.
	 * <p>
	 * Wins are formatted as {@code Win (+s)}.
	 * Losses are formatted as {@code Loss (s)}.
	 * Draws are formatted as {@code Draw (0)}.
	 *
	 * @param evaluation the board evaluation
	 * @return formatted evaluation string
	 * @throws IllegalArgumentException if {@code evaluation} is less than {@code -18}
	 *         or greater than {@code 18}
	 */
	public static String formatEvaluation(int evaluation) {
		if(evaluation < SMALLEST_EVALUATION || evaluation > LARGEST_EVALUATION) {
			
			String exceptionMessage = String.format("evaluation out of range: %s (expected -18..18)", evaluation);
			throw new IllegalArgumentException(exceptionMessage);
		}
		
		if(evaluation == 0) return FORMATTED_DRAW_SCORE;
		if(evaluation < 0) return FORMATTED_LOSS_SCORE_PATTERN.formatted(evaluation);
		
		return FORMATTED_WIN_SCORE_PATTERN.formatted(evaluation);
	}
	
}
