package net.kite.api;

import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.api.skill.level.SkillLevel;

import java.io.*;

/**
 * This is the public API to a {@link Kite} solver.
 * Use {@link Kite#createInstance()} to obtain a reference
 * to a newly created solver.
 * Use the public methods of this class to interact with
 * the solver. Each solver is driven by a single game state
 * that can be updated using {@link Kite#playMove(int)},
 * {@link Kite#undoMove()} and {@link Kite#clearBoard()}.
 */
public class Kite implements KiteAPI {
	
	private static final String NAME = "Kite";
	private static final String VERSION = "1.16.2";
	private static final String AUTHOR = "tristan852";
	
	private static final String[] BENCHMARK_RESOURCE_PATHS = new String[] {
			"/benchmarks/endgame_easy.txt",
			"/benchmarks/midgame_easy.txt",
			"/benchmarks/midgame_medium.txt",
			"/benchmarks/opening_easy.txt",
			"/benchmarks/opening_medium.txt",
			"/benchmarks/opening_hard.txt"
	};
	
	private static final char BENCHMARK_ENTRY_SEPARATOR_CHARACTER = ' ';
	
	private final KiteAPI internalSolver;
	
	private Kite() {
		this.internalSolver = new net.kite.internal.Kite();
	}
	
	/**
	 * Returns a string representation of
	 * the internal game state of this solver.
	 * <p>
	 * This method is equivalent to
	 * {@link Kite#boardString()}.
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
			
			return internalSolver.boardString();
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
	@Override
	public synchronized String boardMovesString() {
		return internalSolver.boardMovesString();
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
	@Override
	public synchronized String boardString() {
		return internalSolver.boardString();
	}
	
	/**
	 * Returns the height of any given column.
	 * This height of a column is equal to the
	 * number of stones played into it.
	 *
	 * @param cellColumnIndex the index of the column (one indexed from left to right)
	 * @return height of the column
	 */
	@Override
	public synchronized int cellColumnHeight(int cellColumnIndex) {
		return internalSolver.cellColumnHeight(cellColumnIndex);
	}
	
	/**
	 * Returns whether the cell is currently
	 * occupied by a stone of either
	 * player color.
	 *
	 * @param cellX x coordinate of the cell (zero indexed from left to right)
	 * @param cellY y coordinate of the cell (zero indexed from bottom to top)
	 * @return whether cell is occupied
	 */
	@Override
	public synchronized boolean cellOccupied(int cellX, int cellY) {
		return internalSolver.cellOccupied(cellX, cellY);
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
	@Override
	public synchronized BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		return internalSolver.cellPlayerColor(cellX, cellY);
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
	@Override
	public synchronized BoardPlayerColor activePlayerColor() {
		return internalSolver.activePlayerColor();
	}
	
	/**
	 * Returns the line(s) of cells that
	 * mark a win for a player.
	 * If no player has won yet ({@link BoardOutcome#UNDECIDED})
	 * or the game ended in a draw ({@link BoardOutcome#DRAW})
	 * then {@code null} is being returned instead
	 * (see also {@link BoardOutcome#isWin()}).
	 * Each line has maximal length and at most
	 * one line is being returned per each of the
	 * four unique directions (horizontally, vertically, up-right
	 * diagonally and down-right diagonally).
	 * This means that at most four lines are being returned.
	 * The maximal length of a line is {@code 7}.
	 *
	 * @return all win lines
	 */
	@Override
	public synchronized BoardLine[] winLines() {
		return internalSolver.winLines();
	}
	
	/**
	 * Returns whether there is still a move left
	 * to undo.
	 * <p>
	 * The only case in which there is no move left
	 * to undo is when the board is fully empty.
	 *
	 * @return whether a move can still be undone
	 */
	@Override
	public synchronized boolean canUndoMove() {
		return internalSolver.canUndoMove();
	}
	
	/**
	 * Returns whether there is still a legal move left
	 * to play.
	 * <p>
	 * Note that this is the negation of
	 * {@link Kite#gameOver()}
	 *
	 * @return whether a move can still be played
	 */
	@Override
	public synchronized boolean canPlayMove() {
		return internalSolver.canPlayMove();
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
	@Override
	public synchronized BoardOutcome gameOutcome() {
		return internalSolver.gameOutcome();
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
	@Override
	public synchronized boolean gameOver() {
		return internalSolver.gameOver();
	}
	
	/**
	 * Returns the number of total moves that
	 * have been played so far by both sides
	 * combined.
	 *
	 * @return number of moves played so far
	 */
	@Override
	public synchronized int playedMoveAmount() {
		return internalSolver.playedMoveAmount();
	}
	
	/**
	 * Starts recording the number of
	 * nodes visited for all position
	 * evaluations as well as the time
	 * duration between this call and the
	 * next stop-call.
	 * <p>
	 * Use {@link Kite#stopRecordingPerformanceMetrics()}
	 * to stop the recording and use this method again
	 * to continue recording the nodes visited and
	 * the time elapsed.
	 */
	@Override
	public synchronized void startRecordingPerformanceMetrics() {
		internalSolver.startRecordingPerformanceMetrics();
	}
	
	/**
	 * Pauses the recording of metrics started
	 * by {@link Kite#startRecordingPerformanceMetrics()}.
	 */
	@Override
	public synchronized void stopRecordingPerformanceMetrics() {
		internalSolver.stopRecordingPerformanceMetrics();
	}
	
	/**
	 * After using {@link Kite#startRecordingPerformanceMetrics()}
	 * and {@link Kite#stopRecordingPerformanceMetrics()},
	 * this method can be used to show the
	 * recorded metrics as well as resetting
	 * them.
	 */
	@Override
	public synchronized void printAndResetPerformanceMetrics() {
		internalSolver.printAndResetPerformanceMetrics();
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
	@Override
	public synchronized int skilledMove(SkillLevel skillLevel) {
		return internalSolver.skilledMove(skillLevel);
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
	@Override
	public synchronized int optimalMove() {
		return internalSolver.optimalMove();
	}
	
	/**
	 * Returns one of the available legal moves
	 * chosen uniformly at random.
	 *
	 * @return a random one-indexed column number to play in (indexed from left to right) or {@code 0} if no legal move
	 */
	@Override
	public synchronized int randomMove() {
		return internalSolver.randomMove();
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
	 * <p>
	 * If any player color has not played any moves,
	 * an Elo approximation of {@code 3000} is returned
	 * for that player.
	 *
	 * @param playerRatingApproximations the buffer of length {@code 2} to write Elo rankings to
	 * @return Elo rating approximation of both players
	 */
	@Override
	public synchronized float[] evaluatePlayerPerformances(float[] playerRatingApproximations) {
		return internalSolver.evaluatePlayerPerformances(playerRatingApproximations);
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
	 * <p>
	 * If any player color has not played any moves,
	 * an Elo approximation of {@code 3000} is returned
	 * for that player.
	 *
	 * @return Elo rating approximation of both players
	 */
	@Override
	public synchronized float[] evaluatePlayerPerformances() {
		return internalSolver.evaluatePlayerPerformances();
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
	 * <p>
	 * If the requested player color has not played any moves,
	 * an Elo approximation of {@code 3000} is returned.
	 *
	 * @param playerColor the color of the player whose performance is to be evaluated
	 * @return Elo rating approximation
	 */
	@Override
	public synchronized float evaluatePlayerPerformance(BoardPlayerColor playerColor) {
		return internalSolver.evaluatePlayerPerformance(playerColor);
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
	@Override
	public synchronized int[] evaluateAllMoves(int[] moveScores) {
		return internalSolver.evaluateAllMoves(moveScores);
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
	@Override
	public synchronized int[] evaluateAllMoves() {
		return internalSolver.evaluateAllMoves();
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
	@Override
	public synchronized int evaluateMove(int moveColumnIndex) {
		return internalSolver.evaluateMove(moveColumnIndex);
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
	@Override
	public synchronized int evaluateBoard() {
		return internalSolver.evaluateBoard();
	}
	
	/**
	 * Reseeds the random number generator used
	 * for move generation (e.g., via
	 * {@link Kite#skilledMove(SkillLevel skillLevel)}
	 * or {@link Kite#optimalMove()}).
	 * <p>
	 * If move generation was previously made deterministic
	 * using {@link Kite#seedRandomness(long seed)}, calling
	 * this method will revert it back to non-deterministic
	 * behavior.
	 */
	@Override
	public synchronized void seedRandomness() {
		internalSolver.seedRandomness();
	}
	
	/**
	 * Seeds the random number generator used for
	 * generating moves via e.g. {@link Kite#skilledMove(SkillLevel skillLevel)}
	 * or {@link Kite#optimalMove()}.
	 * <p>
	 * Move generation is non-deterministic by default but
	 * can be made deterministic by using this method.
	 * Deterministic move generation is particularly useful
	 * when generated games need to be reproducible.
	 *
	 * @param seed the initial state of the random number generator
	 */
	@Override
	public synchronized void seedRandomness(long seed) {
		internalSolver.seedRandomness(seed);
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
	@Override
	public synchronized boolean moveLegal(int moveColumnIndex) {
		return internalSolver.moveLegal(moveColumnIndex);
	}
	
	/**
	 * Plays multiple moves on behalf of the player
	 * that is allowed to move next by inserting one
	 * of their stones into the given column.
	 * The internal game state will be updated
	 * unless no move is provided.
	 *
	 * @param moveColumnIndices the one-indexed column numbers (columns indexed from left to right) as a string
	 */
	@Override
	public synchronized void playMoves(String moveColumnIndices) {
		internalSolver.playMoves(moveColumnIndices);
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
	@Override
	public synchronized void playMoves(int... moveColumnIndices) {
		internalSolver.playMoves(moveColumnIndices);
	}
	
	/**
	 * Plays a move on behalf of the player that is
	 * currently taking their turn by inserting one
	 * of their stones into the given column.
	 * The internal game state will be updated.
	 *
	 * @param moveColumnIndex the one-indexed column number (from left to right)
	 */
	@Override
	public synchronized void playMove(int moveColumnIndex) {
		internalSolver.playMove(moveColumnIndex);
	}
	
	/**
	 * Updates the internal game state by undoing
	 * the last {@code moveAmount} moves.
	 *
	 * @param moveAmount number of moves to undo
	 */
	@Override
	public synchronized void undoMoves(int moveAmount) {
		internalSolver.undoMoves(moveAmount);
	}
	
	/**
	 * Updates the internal game state by undoing
	 * the last move.
	 */
	@Override
	public synchronized void undoMove() {
		internalSolver.undoMove();
	}
	
	/**
	 * Sets up a new position by undoing all already played
	 * moves and playing moves on behalf of the two players.
	 *
	 * @param moveColumnIndicesString the one-indexed move column numbers (columns indexed from left to right) as a string
	 */
	@Override
	public synchronized void setupBoard(String moveColumnIndicesString) {
		internalSolver.setupBoard(moveColumnIndicesString);
	}
	
	/**
	 * Sets up a new position by undoing all already played
	 * moves and playing moves on behalf of the two players.
	 *
	 * @param moveColumnIndices the one-indexed move column numbers (columns indexed from left to right)
	 */
	@Override
	public synchronized void setupBoard(int... moveColumnIndices) {
		internalSolver.setupBoard(moveColumnIndices);
	}
	
	/**
	 * Clears the internal game state.
	 * After this method has been called the
	 * game state will be a completely
	 * empty board.
	 */
	@Override
	public synchronized void clearBoard() {
		internalSolver.clearBoard();
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
	 * Runs the benchmark by Pascal Pons
	 * (see README for further information).
	 * <p>
	 * Running the benchmark consists of two
	 * warmup-runs followed by a final
	 * benchmark-run, the results of which
	 * are being printed.
	 * Note that error messages are being
	 * printed in case the evaluation of
	 * a benchmark position is wrong.
	 */
	public static boolean runBenchmark() {
		return runBenchmark(true);
	}
	
	/**
	 * Runs the benchmark by Pascal Pons
	 * (see README for further information).
	 * <p>
	 * Running the benchmark consists of two
	 * warmup-runs followed by a final
	 * benchmark-run, the results of which
	 * are being printed.
	 * Note that error messages are being
	 * printed in case the evaluation of
	 * a benchmark position is wrong.
	 *
	 * @return whether to print metrics to standard output
	 */
	public static boolean runBenchmark(boolean printMetrics) {
		if(printMetrics) {
			
			for(int i = 1; i < 3; i++) {
				
				String message = String.format("Performing warmup... (%s/2)", i);
				System.out.println(message);
				
				if(!runAndRecordBenchmark(false)) return false;
			}
		}
		
		return runAndRecordBenchmark(printMetrics);
	}
	
	private static boolean runAndRecordBenchmark(boolean recordMetrics) {
		Kite solver = Kite.createInstance();
		
		boolean successful = true;
		for(String resourcePath : BENCHMARK_RESOURCE_PATHS) {
			
			if(recordMetrics) System.out.println(resourcePath);
			
			InputStream inputStream = Kite.class.getResourceAsStream(resourcePath);
			if(inputStream == null) {
				
				System.err.printf("Benchmark cannot be found in resources: %s%n", resourcePath);
				return false;
			}
			
			try(
					inputStream;
					Reader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
			) {
				
				while(true) {
					
					String line = bufferedReader.readLine();
					if(line == null) break;
					
					int index = line.indexOf(BENCHMARK_ENTRY_SEPARATOR_CHARACTER);
					
					String s1 = line.substring(0, index);
					String s2 = line.substring(index + 1);
					
					int score = Integer.parseInt(s2);
					
					solver.setupBoard(s1);
					if(recordMetrics) solver.startRecordingPerformanceMetrics();
					int s = solver.evaluateBoard();
					if(recordMetrics) solver.stopRecordingPerformanceMetrics();
					
					if(s != score) {
						
						String errorMessage = String.format("Wrong evaluation: position=%s, evaluation=%s (should be %s)", s1, s, score);
						System.err.println(errorMessage);
						
						if(recordMetrics) successful = false;
						else return false;
					}
				}
				
				if(recordMetrics) solver.printAndResetPerformanceMetrics();
				
			} catch(IOException exception) {
				
				String errorMessage = String.format("An exception occurred while loading benchmark from resources: %s", exception);
				System.err.println(errorMessage);
				
				return false;
			}
		}
		
		return successful;
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
