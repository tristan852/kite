package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class OptimalCommand extends Command {
	
	public OptimalCommand() {
		super("optimal", "optimal");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			return false;
		}
		
		int move = solver.optimalMove();
		if(move == 0) {
			
			errorStream.println("The game is over!");
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
