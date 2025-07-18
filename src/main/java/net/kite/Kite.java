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
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized String boardString() {
		return board.toString();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		return board.cellPlayerColor(cellX, cellY);
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized BoardOutcome gameOutcome() {
		return board.getOutcome();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized boolean gameOver() {
		return board.over();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized int playedMoveAmount() {
		return board.playedMoveAmount();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized int evaluateMove(int moveColumnIndex) {
		return board.evaluateMove(moveColumnIndex);
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized int evaluateBoard() {
		return board.evaluate();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized boolean moveLegal(int moveColumnIndex) {
		return board.moveLegal(moveColumnIndex);
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized void playMove(int moveColumnIndex) {
		board.playMove(moveColumnIndex);
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized void undoMove() {
		board.undoMove();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public synchronized void clearBoard() {
		board = new Board();
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public static String getName() {
		return NAME;
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public static String getVersion() {
		return VERSION;
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
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
	 * @return win game outcome
	 */
	public static Kite getKite() {
		synchronized(Kite.class) {
			
			if(kite == null) kite = new Kite();
		}
		
		return kite;
	}
	
}
