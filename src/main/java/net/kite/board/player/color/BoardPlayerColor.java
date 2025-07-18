package net.kite.board.player.color;

public enum BoardPlayerColor {
	
	RED("X"),
	YELLOW("O");
	
	private final String string;
	
	BoardPlayerColor(String string) {
		this.string = string;
	}
	
	public BoardPlayerColor opposite() {
		return this == RED ? YELLOW : RED;
	}
	
	public String getString() {
		return string;
	}
	
}
