package net.kite;

import net.kite.board.Board;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.player.color.BoardPlayerColor;

/**
 * This is the singleton public interface to {@link Kite}.
 * Use {@link Kite#getKite()} to obtain a reference
 * to the singleton.
 * If you are the first to call this method then
 * the solver will have to be created/initialized first.
 * Use the public methods of this class to interact with
 * the solver. The solver is driven by a single game state
 * that can be updated using {@link Kite#playMove(int)},
 * {@link Kite#undoMove()} and {@link Kite#clearBoard()}.
 */
public class Kite {
	
	private static Kite kite;
	
	private static final String NAME = "Kite";
	private static final String VERSION = "1.0.0";
	private static final String AUTHOR = "tristan852";
	
	private Board board;
	
	private Kite() {
		this.board = new Board();
		
		board.evaluate();
		
		kite = this;
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
	 * Returns the player color of the stone in a
	 * given cell if the cell is not empty or
	 * {@code null} if the cell is empty.
	 *
	 * @param cellX x coordinate of the cell (zero indexed from right to left)
	 * @param cellY y coordinate of the cell (zero indexed from bottom to top)
	 * @return player color of the stone or {@code null} if no stone
	 */
	public synchronized BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		return board.cellPlayerColor(cellX, cellY);
	}
	
	/**
	 * Returns the outcome of the game.
	 * If the game has not ended yet {@link BoardOutcome#UNDECIDED}
	 * will be returned.
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
	 * Evaluates the game state that is
	 * reached by playing the given move
	 * from the perspective of the player
	 * that is currently at turn.
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
	 * that is currently at turn.
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
	 * Plays a move on behalf of the player that is
	 * currently at turn by inserting one of their stones
	 * into the given column.
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
	 * the last move.
	 */
	public synchronized void undoMove() {
		board.undoMove();
	}
	
	/**
	 * Clears the internal game state.
	 * After this method has been called the
	 * game state will be a completely
	 * empty board.
	 */
	public synchronized void clearBoard() {
		board = new Board();
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
	 * Obtains a reference to the singleton Kite solver.
	 * When you are the first to call this method
	 * then the solver has to be created and
	 * initialized first before you get your reference.
	 *
	 * @return a reference to the Kite solver
	 */
	public static Kite getKite() {
		synchronized(Kite.class) {
			
			if(kite == null) kite = new Kite();
		}
		
		return kite;
	}
	
}
