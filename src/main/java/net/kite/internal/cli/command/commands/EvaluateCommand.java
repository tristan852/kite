package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class EvaluateCommand extends Command {
	
	public EvaluateCommand() {
		super("evaluate", "evaluate [move]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			System.out.println(solver.evaluateBoard());
			
		} else if(arguments.length == 1) {
			
			int x = parseCoordinateArgument(arguments[0], "move", true);
			if(x < 0) return false;
			
			if(!solver.moveLegal(x)) {
				
				System.err.printf("Move is not legal: %s%n", x);
				return false;
			}
			
			System.out.println(solver.evaluateMove(x));
			
		} else {
			
			System.err.println("Too many arguments!");
		}
		
		return false;
	}
	
}
