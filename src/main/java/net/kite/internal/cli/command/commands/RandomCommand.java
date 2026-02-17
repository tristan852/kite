package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class RandomCommand extends Command {
	
	public RandomCommand() {
		super("random", "random");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		int move = solver.randomMove();
		if(move == 0) {
			
			System.err.println("The game is over!");
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
