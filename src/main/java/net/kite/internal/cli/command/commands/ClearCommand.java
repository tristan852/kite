package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class ClearCommand extends Command {
	
	public ClearCommand() {
		super("clear", "clear");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		solver.clearBoard();
		
		return false;
	}
	
}
