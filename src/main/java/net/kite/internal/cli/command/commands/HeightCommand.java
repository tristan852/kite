package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class HeightCommand extends Command {
	
	public HeightCommand() {
		super("height", "height <x>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 1) {
			
			System.err.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true);
		if(x < 0) return false;
		
		System.out.println(solver.cellColumnHeight(x));
		
		return false;
	}
	
}
