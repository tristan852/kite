package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class RandomCommand extends Command {
	
	public RandomCommand() {
		super("random", "random");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		System.out.println(solver.randomMove());
		
		return false;
	}
	
}
