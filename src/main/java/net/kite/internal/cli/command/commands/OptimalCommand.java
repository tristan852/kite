package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class OptimalCommand extends Command {
	
	public OptimalCommand() {
		super("optimal", "optimal");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		System.out.println(solver.optimalMove());
		
		return false;
	}
	
}
