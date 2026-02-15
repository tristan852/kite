package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class PrintCommand extends Command {
	
	public PrintCommand() {
		super("print", "print [moves-only]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			System.out.println(solver.boardString());
			
		} else {
			
			boolean movesOnly = Boolean.parseBoolean(arguments[0]);
			if(movesOnly) System.out.println(solver.boardMovesString());
			else System.out.println(solver.boardString());
		}
		
		return false;
	}
	
}
