package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class RandomCommand extends Command {
	
	public RandomCommand() {
		super("random", "random");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int move = solver.randomMove();
		if(move == 0) {
			
			errorStream.println("The game is over!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
