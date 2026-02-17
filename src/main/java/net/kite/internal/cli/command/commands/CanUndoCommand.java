package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class CanUndoCommand extends Command {
	
	public CanUndoCommand() {
		super("can-undo", "can-undo");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		System.out.println(solver.canUndoMove());
		
		return false;
	}
	
}
