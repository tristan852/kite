package net.kite;

import net.kite.board.Board;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.player.color.BoardPlayerColor;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCache;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.skill.level.SkillLevel;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is the public API to a {@link Kite} solver.
 * Use {@link Kite#createInstance()} to obtain a reference
 * to a newly created solver.
 * Use the public methods of this class to interact with
 * the solver. Each solver is driven by a single game state
 * that can be updated using {@link Kite#playMove(int)},
 * {@link Kite#undoMove()} and {@link Kite#clearBoard()}.
 */
public class Kite {
	
	private static final String NAME = "Kite";
	private static final String VERSION = "1.7.9";
	private static final String AUTHOR = "tristan852";
	
	private static final int BOARD_WIDTH = 7;
	private static final int GAME_PLAYER_AMOUNT = 2;
	
	private static final int[] ORDERED_MOVE_COLUMN_INDICES = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int INVALID_MOVE_COLUMN_INDEX = 0;
	
	private static final int MAXIMAL_MOVE_SCORE_LOSS = 36;
	
	private final Board board;
	
	private final int[] moveScores = new int[BOARD_WIDTH];
	
	private Kite() {
		BoardScoreCache boardScoreCache = new BoardScoreCache();
		
		this.board = new Board(boardScoreCache);
		
		OpeningBoardScoreCaches.ensureDefaultIsLoaded(null);
		
		board.evaluate();
	}
	
	/**
	 * Returns a string representation of
	 * the internal game state of this solver.
	 * <p>
	 * This method is equivalent to
	 * {@link Kite#boardString}.
	 * <p>
	 * Note that this method also blocks
	 * any concurrent access from other
	 * threads to this solver instance.
	 *
	 * @return game state string representation
	 */
	@Override
	public String toString() {
		synchronized(this) {
			
			return board.toString();
		}
	}
	
	/**
	 * Returns the sequence of move that
	 * were currently played onto the
	 * game state.
	 * A move is represented by its
	 * 1-indexed column number (from left
	 * to right).
	 *
	 * @return played moves string representation
	 */
	public synchronized String boardMovesString() {
		return board.movesString();
	}
	
	/**
	 * Returns a string representation of
	 * the internal game state.
	 * The string representation consists of
	 * a list of played moves as well as a board
	 * showing the stones of the players.
	 * The stones of the player with color {@link BoardPlayerColor#RED}
	 * are shown as 'X' whereas the stones of
	 * the player with color {@link BoardPlayerColor#YELLOW}
	 * are shown as 'O'.
	 *
	 * @return game state string representation
	 */
	public synchronized String boardString() {
		return board.toString();
	}
	
	/**
	 * Returns the height of any given column.
	 * This height of a column is equal to the
	 * number of stones played into it.
	 *
	 * @param cellColumnIndex the index of the column (one indexed from left to right)
	 * @return height of the column
	 */
	public synchronized int cellColumnHeight(int cellColumnIndex) {
		cellColumnIndex--;
		
		return board.cellColumnHeight(cellColumnIndex);
	}
	
	/**
	 * Returns the player color of the stone in a
	 * given cell if the cell is not empty or
	 * {@code null} if the cell is empty.
	 *
	 * @param cellX x coordinate of the cell (zero indexed from left to right)
	 * @param cellY y coordinate of the cell (zero indexed from bottom to top)
	 * @return player color of the stone or {@code null} if no stone
	 */
	public synchronized BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		return board.cellPlayerColor(cellX, cellY);
	}
	
	/**
	 * Returns the color of the currently active
	 * player, i.e. the player that is allowed to
	 * move next.
	 * If the game has already ended this method
	 * still returns the color of the player
	 * that would be allowed to move next if the
	 * game had not ended yet.
	 *
	 * @return player color of the currently active player
	 */
	public synchronized BoardPlayerColor activePlayerColor() {
		return board.activePlayerColor();
	}
	
	/**
	 * Returns the outcome of the game.
	 * If the game has not ended yet {@link BoardOutcome#UNDECIDED}
	 * will be returned.
	 * <p>
	 * Note that this is not a prediction of
	 * who is going to win (use {@link Kite#evaluateBoard()}
	 * for that).
	 *
	 * @return game outcome
	 */
	public synchronized BoardOutcome gameOutcome() {
		return board.getOutcome();
	}
	
	/**
	 * Returns whether the game has been
	 * decided or not.
	 * This method returns {@code true} if and only if the game
	 * has ended in a draw or a win for either player.
	 * Also note that this method only returns {@code true}
	 * if and only if there is a legal move left to play
	 * for the currently active player.
	 *
	 * @return whether the game has finished
	 */
	public synchronized boolean gameOver() {
		return board.over();
	}
	
	/**
	 * Returns the number of total moves that
	 * have been played so far by both sides
	 * combined.
	 *
	 * @return number of moves played so far
	 */
	public synchronized int playedMoveAmount() {
		return board.playedMoveAmount();
	}
	
	/**
	 * Evaluates the game state and returns
	 * a legal move that is chosen according
	 * to the provided {@code skillLevel}.
	 * The skill based move is chosen at random
	 * according to a probability distribution
	 * that is more spread out for weaker
	 * skill levels and more centered for
	 * stronger skill levels.
	 *
	 * @param skillLevel the skill level that the move should be based on
	 * @return a skill based one-indexed column number to play in (indexed from left to right) or {@code 0} if no legal move
	 */
	public synchronized int skilledMove(SkillLevel skillLevel) {
		boolean perfect = skillLevel == SkillLevel.PERFECT || skillLevel == SkillLevel.SUPER_GRANDMASTER;
		
		if(perfect) return optimalMove();
		if(skillLevel == SkillLevel.RANDOM) return randomMove();
		if(skillLevel == SkillLevel.ADAPTIVE) return adaptiveMove();
		
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		int n = board.playedMoveAmount();
		
		int optimalMoveScore = Integer.MIN_VALUE;
		int theoreticallyWorstScoreLoss = BoardScore.maximalScoreLoss(n);
		
		int maximalScoreLoss = skillLevel.getMaximalScoreLoss();
		maximalScoreLoss = maximalScoreLoss * theoreticallyWorstScoreLoss / MAXIMAL_MOVE_SCORE_LOSS;
		
		int minimalScore = Integer.MIN_VALUE + 2;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = board.moveLegal(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, minimalScore - 1) : Integer.MIN_VALUE;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore > optimalMoveScore) {
				
				optimalMoveScore = moveScore;
				minimalScore = optimalMoveScore - maximalScoreLoss;
			}
		}
		
		int totalWeight = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore >= minimalScore) {
				
				int weight = moveScore - minimalScore + 1;
				weight *= weight * weight;
				
				totalWeight += weight;
			}
		}
		
		Random random = ThreadLocalRandom.current();
		int weightIndex = random.nextInt(totalWeight);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore < minimalScore) continue;
			
			int weight = moveScore - minimalScore + 1;
			weight *= weight * weight;
			
			if(weightIndex < weight) return moveColumnIndex + 1;
			weightIndex -= weight;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	private int adaptiveMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int bestAbsoluteMoveScore = Integer.MAX_VALUE - 1;
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegal(moveColumnIndex)) continue;
			
			int moveScore = board.evaluateMove(moveColumnIndex, -bestAbsoluteMoveScore - 1);
			if(moveScore < 0) moveScore = -moveScore;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore < bestAbsoluteMoveScore) {
				
				bestAbsoluteMoveScore = moveScore;
				n = 1;
				
				continue;
			}
			
			if(moveScore == bestAbsoluteMoveScore) n++;
		}
		
		Random random = ThreadLocalRandom.current();
		int index = random.nextInt(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegal(moveColumnIndex)) continue;
			
			int absoluteMoveScore = moveScores[moveColumnIndex];
			if(absoluteMoveScore != bestAbsoluteMoveScore) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	/**
	 * Evaluates the game state and returns
	 * an optimal move. If the game state
	 * only has one optimal move, the best
	 * move is returned.
	 * If the game state has multiple
	 * optimal moves, an optimal move
	 * is chosen uniformly at random and
	 * will be returned.
	 *
	 * @return an optimal one-indexed column number to play in (indexed from left to right) or {@code 0} if no legal move
	 */
	public synchronized int optimalMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int optimalMoveScore = Integer.MIN_VALUE + 2;
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = board.moveLegal(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, optimalMoveScore - 1) : Integer.MIN_VALUE;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore > optimalMoveScore) {
				
				optimalMoveScore = moveScore;
				n = 1;
				
			} else if(moveScore == optimalMoveScore) {
				
				n++;
			}
		}
		
		Random random = ThreadLocalRandom.current();
		int index = random.nextInt(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore != optimalMoveScore) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	/**
	 * Returns one of the available legal moves
	 * chosen uniformly at random.
	 *
	 * @return a random one-indexed column number to play in (indexed from left to right) or {@code 0} if no legal move
	 */
	public synchronized int randomMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegal(moveColumnIndex)) n++;
		}
		
		Random random = ThreadLocalRandom.current();
		int index = random.nextInt(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegal(moveColumnIndex)) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	/**
	 * Uses all the moves played so far onto
	 * this solver instance to evaluate the
	 * performance of both players.
	 * If the game that is currently
	 * loaded is already over (see {@link Kite#gameOver()})
	 * then the entire match will be used
	 * to give an Elo approximation.
	 * <p>
	 * Note that Elo rating approximation based
	 * on a few moves are even an entire game
	 * can still be inaccurate.
	 * The returned Elo rating approximation
	 * is based upon the approximate Elo
	 * ratings of the different {@link SkillLevel}s
	 * (see also {@link SkillLevel#getApproximateEloRating()}).
	 * <p>
	 * The Elo rating approximation of the red player will
	 * be the first element of the returned array and the
	 * rating approximation of the yellow player will be
	 * the second element.
	 *
	 * @param playerRatingApproximations the buffer of length {@code 2} to write Elo rankings to
	 * @return Elo rating approximation of both players
	 */
	public synchronized float[] evaluatePlayerPerformances(float[] playerRatingApproximations) {
		board.approximateEloRatingOfBothPlayer(playerRatingApproximations);
		
		return playerRatingApproximations;
	}
	
	/**
	 * Uses all the moves played so far onto
	 * this solver instance to evaluate the
	 * performance of both players.
	 * If the game that is currently
	 * loaded is already over (see {@link Kite#gameOver()})
	 * then the entire match will be used
	 * to give an Elo approximation.
	 * <p>
	 * Note that Elo rating approximation based
	 * on a few moves are even an entire game
	 * can still be inaccurate.
	 * The returned Elo rating approximation
	 * is based upon the approximate Elo
	 * ratings of the different {@link SkillLevel}s
	 * (see also {@link SkillLevel#getApproximateEloRating()}).
	 * <p>
	 * The Elo rating approximation of the red player will
	 * be the first element of the returned array and the
	 * rating approximation of the yellow player will be
	 * the second element.
	 * Use {@link Kite#evaluatePlayerPerformances(float[] playerRatingApproximations)}
	 * if you already have a buffer to write the player
	 * evaluations into.
	 *
	 * @return Elo rating approximation of both players
	 */
	public synchronized float[] evaluatePlayerPerformances() {
		float[] eloBuffer = new float[GAME_PLAYER_AMOUNT];
		
		board.approximateEloRatingOfBothPlayer(eloBuffer);
		
		return eloBuffer;
	}
	
	/**
	 * Uses all the moves played so far onto
	 * this solver instance to evaluate the
	 * performance of a given player.
	 * If the game that is currently
	 * loaded is already over (see {@link Kite#gameOver()})
	 * then the entire match will be used
	 * to give an Elo approximation.
	 * <p>
	 * Note that Elo rating approximation based
	 * on a few moves are even an entire game
	 * can still be inaccurate.
	 * The returned Elo rating approximation
	 * is based upon the approximate Elo
	 * ratings of the different {@link SkillLevel}s
	 * (see also {@link SkillLevel#getApproximateEloRating()}).
	 *
	 * @param playerColor the color of the player whose performance is to be evaluated
	 * @return Elo rating approximation
	 */
	public synchronized float evaluatePlayerPerformance(BoardPlayerColor playerColor) {
		return board.approximateEloRatingOfPlayer(playerColor);
	}
	
	/**
	 * Evaluates each legal move in the same way
	 * that ({@link Kite#evaluateMove(int moveColumnIndex)})
	 * would do.
	 * Illegal moves are given the evaluation score
	 * {@link Integer#MIN_VALUE}.
	 * The parameter {@code moveScores} is used as a
	 * buffer to hold the move evaluations and
	 * therefore needs to be of size {@code 7}.
	 *
	 * @param moveScores the buffer to write move evaluations into
	 * @return move evaluations ({@link Integer#MIN_VALUE} for illegal moves)
	 */
	public synchronized int[] evaluateAllMoves(int[] moveScores) {
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegal(x)) moveScores[x] = board.evaluateMove(x);
			else moveScores[x] = Integer.MIN_VALUE;
		}
		
		return moveScores;
	}
	
	/**
	 * Evaluates each legal move in the same way
	 * that ({@link Kite#evaluateMove(int moveColumnIndex)})
	 * would do.
	 * Illegal moves are given the evaluation score
	 * {@link Integer#MIN_VALUE}.
	 * <p>
	 * Use {@link Kite#evaluateAllMoves(int[] moveScores)}
	 * if you already have a buffer to write the move
	 * evaluations into.
	 *
	 * @return move evaluations ({@link Integer#MIN_VALUE} for illegal moves)
	 */
	public synchronized int[] evaluateAllMoves() {
		int[] moveScores = new int[BOARD_WIDTH];
		
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegal(x)) moveScores[x] = board.evaluateMove(x);
			else moveScores[x] = Integer.MIN_VALUE;
		}
		
		return moveScores;
	}
	
	/**
	 * Evaluates the game state that is
	 * reached by playing the given move
	 * from the perspective of the player
	 * that is currently taking their turn.
	 * For the game evaluation perfect play
	 * will be assumed for both sides.
	 * The move's score is either zero if
	 * the game will end in a draw, positive
	 * if the active player will win or negative
	 * if the active player will lose.
	 * A score of {@code n > 0} means the active
	 * player will win with their {@code n}th to last stone.
	 * A score of {@code n < 0} means the opponent
	 * of the active player will win with their
	 * {@code -n}th to last stone.
	 *
	 * @param moveColumnIndex the one-indexed column number (from left to right)
	 * @return move evaluation
	 */
	public synchronized int evaluateMove(int moveColumnIndex) {
		moveColumnIndex--;
		
		return board.evaluateMove(moveColumnIndex);
	}
	
	/**
	 * Evaluates the current game state
	 * from the perspective of the player
	 * that is currently taking their turn.
	 * For the game evaluation perfect play
	 * will be assumed for both sides.
	 * The board's score is either zero if
	 * the game will end in a draw, positive
	 * if the active player will win or negative
	 * if the active player will lose.
	 * A score of {@code n > 0} means the active
	 * player will win with their {@code n}th to last stone.
	 * A score of {@code n < 0} means the opponent
	 * of the active player will win with their
	 * {@code -n}th to last stone.
	 *
	 * @return board evaluation
	 */
	public synchronized int evaluateBoard() {
		return board.evaluate();
	}
	
	/**
	 * Tests whether a given move is legal.
	 * A move is legal if the game has not ended yet
	 * (implying the board is also not entirely full)
	 * and when the column of the move is not full yet.
	 *
	 * @param moveColumnIndex the one-indexed column number (from left to right)
	 * @return whether the move is legal
	 */
	public synchronized boolean moveLegal(int moveColumnIndex) {
		moveColumnIndex--;
		
		return board.moveLegal(moveColumnIndex);
	}
	
	/**
	 * Plays multiple moves on behalf of the player
	 * that is allowed to move next by inserting one
	 * of their stones into the given column.
	 * The internal game state will be updated
	 * unless no move is provided.
	 *
	 * @param moveColumnIndices the one-indexed column numbers (columns indexed from left to right)
	 */
	public synchronized void playMoves(int... moveColumnIndices) {
		for(int moveColumnIndex : moveColumnIndices) {
			
			moveColumnIndex--;
			
			board.playMove(moveColumnIndex);
		}
	}
	
	/**
	 * Plays a move on behalf of the player that is
	 * currently taking their turn by inserting one
	 * of their stones into the given column.
	 * The internal game state will be updated.
	 *
	 * @param moveColumnIndex the one-indexed column number (from left to right)
	 */
	public synchronized void playMove(int moveColumnIndex) {
		moveColumnIndex--;
		
		board.playMove(moveColumnIndex);
	}
	
	/**
	 * Updates the internal game state by undoing
	 * the last {@code moveAmount} moves.
	 *
	 * @param moveAmount number of moves to undo
	 */
	public synchronized void undoMoves(int moveAmount) {
		for(int i = 0; i < moveAmount; i++) {
			
			board.undoMove();
		}
	}
	
	/**
	 * Updates the internal game state by undoing
	 * the last move.
	 */
	public synchronized void undoMove() {
		board.undoMove();
	}
	
	/**
	 * Plays multiple moves on behalf of the player
	 * that is allowed to move next by inserting one
	 * of their stones into the given column.
	 * The internal game state will be updated
	 * unless no move is provided.
	 *
	 * @param moveColumnIndices the one-indexed move column numbers (columns indexed from left to right)
	 */
	public synchronized void setupBoard(int... moveColumnIndices) {
		int n = board.playedMoveAmount();
		int l = moveColumnIndices.length;
		
		for(int i = 0; i < l; i++) {
			
			int moveColumnIndex = moveColumnIndices[i] - 1;
			
			if(i == n) {
				
				board.playMove(moveColumnIndex);
				n++;
				
				continue;
			}
			
			int x = board.playedMove(i);
			if(x == moveColumnIndex) {
				
				continue;
			}
			
			while(n > i) {
				
				board.undoMove();
				n--;
			}
			
			board.playMove(moveColumnIndex);
			n++;
		}
		
		while(n > l) {
			
			board.undoMove();
			n--;
		}
	}
	
	/**
	 * Clears the internal game state.
	 * After this method has been called the
	 * game state will be a completely
	 * empty board.
	 */
	public synchronized void clearBoard() {
		int n = board.playedMoveAmount();
		
		while(n != 0) {
			
			board.undoMove();
			n--;
		}
	}
	
	/**
	 * Returns the name of
	 * the Kite solver.
	 *
	 * @return name
	 */
	public static String getName() {
		return NAME;
	}
	
	/**
	 * Returns the version of
	 * the Kite solver.
	 *
	 * @return version
	 */
	public static String getVersion() {
		return VERSION;
	}
	
	/**
	 * Returns the name of the author of
	 * the Kite solver.
	 *
	 * @return author name
	 */
	public static String getAuthor() {
		return AUTHOR;
	}
	
	/**
	 * Creates a new instance of the Kite solver.
	 * Most of the solver's state is exclusive to one
	 * instance and is not shared across instances.
	 * This includes for example the game state and
	 * the transposition table/score cache.
	 * Some state, for example the opening book, only
	 * exists once and <b>is</b> shared across solvers.
	 * Solver instances are thread-safe by not allowing
	 * multiple threads to use them in parallel.
	 * If you are creating the first (or one of the first)
	 * solver instances some additional time might be
	 * spent on warming up the solver and initializing
	 * solver-shared state, like the opening book.
	 *
	 * @return a newly created Kite solver instance
	 */
	public static Kite createInstance() {
		return new Kite();
	}
	
}
