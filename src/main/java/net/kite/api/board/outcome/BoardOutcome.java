package net.kite.api.board.outcome;

import net.kite.api.board.player.color.BoardPlayerColor;

import java.util.Locale;

/**
 * Represents the outcome of a Connect Four game.
 * A completed game can either be a {@link BoardOutcome#DRAW},
 * a win for red ({@link BoardOutcome#RED_WIN}) or a win for
 * yellow ({@link BoardOutcome#YELLOW_WIN}).
 * {@link BoardOutcome#UNDECIDED} represents that the game
 * is not over yet.
 */
public enum BoardOutcome {
	
	/**
	 * The game outcome in which the {@link BoardPlayerColor#RED}
	 * player has won.
	 */
	RED_WIN("Red wins", BoardPlayerColor.RED),
	
	/**
	 * The game outcome in which the {@link BoardPlayerColor#YELLOW}
	 * player has won.
	 */
	YELLOW_WIN("Yellow wins", BoardPlayerColor.YELLOW),
	
	/**
	 * The game outcome for a game with no winner
	 * (meaning the board is entirely filled up).
	 */
	DRAW("Draw", null),
	
	/**
	 * The game outcome for a game that has not
	 * ended yet.
	 */
	UNDECIDED("Undecided", null);
	
	private final String name;
	private final String displayName;
	
	private final BoardPlayerColor winPlayerColor;
	
	BoardOutcome(String displayName, BoardPlayerColor winPlayerColor) {
		this.name = name().toLowerCase(Locale.ROOT);
		this.displayName = displayName;
		this.winPlayerColor = winPlayerColor;
	}
	
	/**
	 * Returns whether this outcome represents
	 * a win for either player ({@link BoardOutcome#RED_WIN}
	 * and {@link BoardOutcome#YELLOW_WIN}) or not
	 * ({@link BoardOutcome#DRAW} and {@link BoardOutcome#UNDECIDED}).
	 *
	 * @return whether this outcome represents a win
	 */
	public boolean isWin() {
		return winPlayerColor != null;
	}
	
	/**
	 * Returns the name of this outcome
	 * that should for example
	 * be displayed inside a CLI.
	 *
	 * @return name of this outcome
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the name of this
	 * outcome that should for example
	 * be displayed inside a user
	 * interface.
	 *
	 * @return display name of this outcome
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * If this game outcome represents an outcome in
	 * which either player has won the game then this
	 * method may be used to return the {@link BoardPlayerColor}
	 * of the player that won.
	 * If this game outcome does not represent a win
	 * for either player then {@code null} is returned.
	 *
	 * @return winner player color or {@code null}
	 */
	public BoardPlayerColor getWinPlayerColor() {
		return winPlayerColor;
	}
	
	/**
	 * Returns the corresponding game outcome
	 * for when a player with the given player
	 * color has won the game.
	 *
	 * @param playerColor color of the winning player
	 * @return win game outcome
	 */
	public static BoardOutcome winOfPlayerColor(BoardPlayerColor playerColor) {
		return playerColor == BoardPlayerColor.RED ? RED_WIN : YELLOW_WIN;
	}
	
}
