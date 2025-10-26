package net.kite.board.line;

import net.kite.Kite;

/**
 * Represents a set of cells of the board
 * that are connected using a straight line.
 * This data container is being used to return
 * the win lines as returned by
 * {@link Kite#winLines()}.
 */
public class BoardLine {
	
	private static final String TO_STRING_FORMAT = "[(%s, %s), (%s, %s)]";
	
	private final int startCellX;
	private final int startCellY;
	
	private final int endCellX;
	private final int endCellY;
	
	private final int directionX;
	private final int directionY;
	
	private final int length;
	
	/**
	 * Creates a new line object that
	 * marks some straight line across
	 * the board.
	 * <p>
	 * <b>Warning</b>: This constructor is intended for internal use only!
	 *
	 * @param startCellX x-coordinate of start cell
	 * @param startCellY y-coordinate of start cell
	 * @param endCellX x-coordinate of end cell
	 * @param endCellY y-coordinate of end cell
	 * @param directionX x-direction
	 * @param directionY y-direction
	 * @param length length of the line
	 */
	public BoardLine(int startCellX, int startCellY, int endCellX, int endCellY, int directionX, int directionY, int length) {
		this.startCellX = startCellX;
		this.startCellY = startCellY;
		this.endCellX = endCellX;
		this.endCellY = endCellY;
		this.directionX = directionX;
		this.directionY = directionY;
		this.length = length;
	}
	
	/**
	 * Returns the string representation
	 * of this line in the format:
	 * {@code [(startX, startY), (endX, endY)]}.
	 *
	 * @return string representation
	 */
	@Override
	public String toString() {
		return TO_STRING_FORMAT.formatted(startCellX, startCellY, endCellX, endCellY);
	}
	
	/**
	 * Returns the x-coordinate of the
	 * cell that was determined to be
	 * the start of this line.
	 * The start cell sits at one of
	 * the ends of the line.
	 *
	 * @return x-coordinate of start cell
	 */
	public int getStartCellX() {
		return startCellX;
	}
	
	/**
	 * Returns the y-coordinate of the
	 * cell that was determined to be
	 * the start of this line.
	 * The start cell sits at one of
	 * the ends of the line.
	 *
	 * @return y-coordinate of start cell
	 */
	public int getStartCellY() {
		return startCellY;
	}
	
	/**
	 * Returns the x-coordinate of the
	 * cell that was determined to be
	 * the end of this line.
	 * The end cell sits at one of
	 * the ends of the line.
	 *
	 * @return x-coordinate of end cell
	 */
	public int getEndCellX() {
		return endCellX;
	}
	
	/**
	 * Returns the y-coordinate of the
	 * cell that was determined to be
	 * the end of this line.
	 * The end cell sits at one of
	 * the ends of the line.
	 *
	 * @return y-coordinate of end cell
	 */
	public int getEndCellY() {
		return endCellY;
	}
	
	/**
	 * Returns the change in x
	 * when going from one cell
	 * of the line to its immediate
	 * neighbour (going from the
	 * start of the line to the end).
	 *
	 * @return x-direction
	 */
	public int getDirectionX() {
		return directionX;
	}
	
	/**
	 * Returns the change in y
	 * when going from one cell
	 * of the line to its immediate
	 * neighbour (going from the
	 * start of the line to the end).
	 *
	 * @return y-direction
	 */
	public int getDirectionY() {
		return directionY;
	}
	
	/**
	 * Returns the length of this
	 * line, i.e. the number of cells
	 * that this line consists of.
	 *
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	
}
