package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class UndoCommand extends Command {
	
	public UndoCommand() {
		super("undo", "undo [move-amount]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			solver.undoMove();
			
		} else {
			
			int n = Integer.parseInt(arguments[0]);
			solver.undoMoves(n);
		}
		
		return false;
	}
	
}
