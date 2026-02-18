package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class PrintCommand extends Command {
	
	public PrintCommand() {
		super("print", "print [moves-only]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length == 0) {
			
			System.out.println(solver.boardString());
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean movesOnly;
			if(s.equalsIgnoreCase("true")) movesOnly = true;
			else if(s.equalsIgnoreCase("false")) movesOnly = false;
			else {
				
				errorStream.printf("Unknown boolean value for argument 'movesOnly': %s%n", s);
				if(exitOnError) System.exit(1);
				return false;
			}
			
			if(movesOnly) System.out.println(solver.boardMovesString());
			else System.out.println(solver.boardString());
			
		} else {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
