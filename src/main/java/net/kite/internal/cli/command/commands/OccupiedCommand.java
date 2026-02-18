package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class OccupiedCommand extends Command {
	
	public OccupiedCommand() {
		super("occupied", "occupied <x> <y>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 2) {
			
			errorStream.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true, errorStream);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", false, errorStream);
		if(y < 0) return false;
		
		System.out.println(solver.cellOccupied(x, y));
		
		return false;
	}
	
}
