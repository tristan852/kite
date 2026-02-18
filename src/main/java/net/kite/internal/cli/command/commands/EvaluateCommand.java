package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class EvaluateCommand extends Command {
	
	public EvaluateCommand() {
		super("evaluate", "Evaluate the board or a specific move", "evaluate [move]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			System.out.println(solver.evaluateBoard());
			
		} else if(arguments.length == 1) {
			
			int x = parseCoordinateArgument(arguments[0], "move", true, errorStream, exitOnError);
			if(x < 0) return false;
			
			if(!solver.moveLegal(x)) {
				
				errorStream.printf("Move is not legal: %s%n", x);
				if(exitOnError) System.exit(1);
				return false;
			}
			
			System.out.println(solver.evaluateMove(x));
			
		} else {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
