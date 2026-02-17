package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class OccupiedCommand extends Command {
	
	public OccupiedCommand() {
		super("occupied", "occupied <x> <y>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 2) {
			
			System.err.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", false);
		if(y < 0) return false;
		
		System.out.println(solver.cellOccupied(x, y));
		
		return false;
	}
	
}
