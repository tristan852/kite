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
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean movesOnly;
			if(s.equalsIgnoreCase("true")) movesOnly = true;
			else if(s.equalsIgnoreCase("false")) movesOnly = false;
			else {
				
				System.err.printf("Unknown boolean value for argument 'movesOnly': %s%n", s);
				return false;
			}
			
			if(movesOnly) System.out.println(solver.boardMovesString());
			else System.out.println(solver.boardString());
			
		} else {
			
			System.err.println("Too many arguments!");
		}
		
		return false;
	}
	
}
