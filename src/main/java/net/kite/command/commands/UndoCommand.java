package net.kite.command.commands;

import net.kite.Kite;
import net.kite.board.Board;
import net.kite.command.Command;

public class UndoCommand extends Command {
	
	private static final String NAME = "undo";
	
	public UndoCommand() {
		super(NAME);
	}
	
	@Override
	public boolean execute(String[] arguments) {
		Kite kite = Kite.getKite();
		Board board = kite.getBoard();
		
		board.undoMove();
		
		System.out.println("Moves was undone successfully");
		
		return false;
	}
	
}
