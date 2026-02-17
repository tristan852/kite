package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class CanPlayCommand extends Command {
	
	public CanPlayCommand() {
		super("can-play", "can-play");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		System.out.println(solver.canPlayMove());
		
		return false;
	}
	
}
