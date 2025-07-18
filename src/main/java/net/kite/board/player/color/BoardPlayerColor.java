package net.kite.board.player.color;

/**
 * Represents the color of a Connect Four player.
 * Connect Four is played by two players with the colors
 * {@link BoardPlayerColor#RED} and {@link BoardPlayerColor#YELLOW}.
 * {@link BoardPlayerColor#RED} does the first move.
 */
public enum BoardPlayerColor {
	
	/**
	 * The player color of the red player.
	 * This player goes first.
	 */
	RED("X"),
	
	/**
	 * The player color of the yellow player.
	 * This player goes second.
	 */
	YELLOW("O");
	
	private final String string;
	
	BoardPlayerColor(String string) {
		this.string = string;
	}
	
	/**
	 * Returns the opposite player color
	 * of this player color.
	 * If this player color is {@link BoardPlayerColor#RED}
	 * then {@link BoardPlayerColor#YELLOW} will be returned
	 * and vice versa.
	 * 
	 * @return opposite player color
	 */
	public BoardPlayerColor opposite() {
		return this == RED ? YELLOW : RED;
	}
	
	/**
	 * Returns a {@link String} with one single
	 * character corresponding to the character that
	 * is being used for this player color inside
	 * board strings.
	 *
	 * @return player character string
	 */
	public String getString() {
		return string;
	}
	
}
