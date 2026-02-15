package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class SeedCommand extends Command {
	
	public SeedCommand() {
		super("seed", "seed [seed]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			solver.seedRandomness();
			
		} else {
			
			long seed = Long.parseLong(arguments[0]);
			solver.seedRandomness(seed);
		}
		
		return false;
	}
	
}
