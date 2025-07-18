package net.kite.command.commands;

import net.kite.Kite;
import net.kite.board.Board;
import net.kite.command.Command;

public class ClearCommand extends Command {
	
	private static final String NAME = "clear";
	
	public ClearCommand() {
		super(NAME);
	}
	
	@Override
	public boolean execute(String[] arguments) {
		Kite kite = Kite.getKite();
		Board board = new Board();
		
		kite.setBoard(board);
		
		System.out.println("Board was cleared successfully");
		
		return false;
	}
	
}
