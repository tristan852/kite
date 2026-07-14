package net.kite.api.exception;

import java.io.Serial;

/**
 * Thrown to indicate that a move is illegal in the current
 * board state.
 * <p>
 * This exception is raised when a move refers to a valid
 * column but cannot be played, for example because the
 * column is full or the game has already ended.
 * <p>
 * The stored {@code moveColumnIndex} is 1-indexed and
 * corresponds to the column number as exposed by the
 * public API.
 */
public final class IllegalMoveException extends IllegalStateException {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	private final int moveColumnIndex;
	
	/**
	 * Constructs a new {@code IllegalMoveException}
	 * for the specified column.
	 *
	 * @param moveColumnIndex the 1-indexed column in which
	 *        the illegal move was attempted
	 * @param message a detail message describing the reason
	 *        the move is illegal
	 */
	public IllegalMoveException(int moveColumnIndex, String message) {
		super(message);
		
		this.moveColumnIndex = moveColumnIndex;
	}
	
	/**
	 * Returns the 1-indexed column in which the illegal
	 * move was attempted.
	 *
	 * @return the column number (1-indexed)
	 */
	public int getMoveColumnIndex() {
		return moveColumnIndex;
	}
	
}
