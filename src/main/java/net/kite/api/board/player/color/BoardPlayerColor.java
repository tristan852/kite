package net.kite.api.board.player.color;

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
	RED('X'),
	
	/**
	 * The player color of the yellow player.
	 * This player goes second.
	 */
	YELLOW('O');
	
	private final String name;
	
	private final char character;
	
	BoardPlayerColor(char character) {
		this.name = name().toLowerCase();
		this.character = character;
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
	 * Returns the name of this player
	 * color that should for example
	 * be displayed inside a CLI.
	 *
	 * @return name of this player color
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a {@code char} corresponding
	 * to the character that is being used
	 * for this player color inside of
	 * board string representations.
	 *
	 * @return player character
	 */
	public char getCharacter() {
		return character;
	}
	
	/**
	 * Returns a color given the requested
	 * color name.
	 * {@code color("red")} returns {@link BoardPlayerColor#RED} and
	 * {@code color("yellow")} returns {@link BoardPlayerColor#YELLOW}.
	 *
	 * @param colorName the color name in snake case
	 * @return corresponding color
	 */
	public static BoardPlayerColor color(String colorName) {
		String redName = RED.getName();
		
		return colorName.charAt(0) == redName.charAt(0) ? RED : YELLOW;
	}
	
}
