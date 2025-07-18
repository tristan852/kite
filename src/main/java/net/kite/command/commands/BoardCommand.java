package net.kite.command.commands;

import net.kite.Kite;
import net.kite.board.Board;
import net.kite.command.Command;

public class BoardCommand extends Command {
	
	private static final String NAME = "board";
	
	public BoardCommand() {
		super(NAME);
	}
	
	@Override
	public boolean execute(String[] arguments) {
		Kite kite = Kite.getKite();
		Board board = kite.getBoard();
		
		System.out.println(board);
		
		return false;
	}
	
}
