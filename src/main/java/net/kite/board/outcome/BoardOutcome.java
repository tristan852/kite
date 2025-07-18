package net.kite.board.outcome;

import net.kite.board.player.color.BoardPlayerColor;

public enum BoardOutcome {
	
	RED_WIN(BoardPlayerColor.RED),
	YELLOW_WIN(BoardPlayerColor.YELLOW),
	
	DRAW(null),
	
	UNDECIDED(null);
	
	private final BoardPlayerColor winPlayerColor;
	
	BoardOutcome(BoardPlayerColor winPlayerColor) {
		this.winPlayerColor = winPlayerColor;
	}
	
	public BoardPlayerColor getWinPlayerColor() {
		return winPlayerColor;
	}
	
	public static BoardOutcome winOfPlayerColor(BoardPlayerColor playerColor) {
		return playerColor == BoardPlayerColor.RED ? RED_WIN : YELLOW_WIN;
	}
	
}
