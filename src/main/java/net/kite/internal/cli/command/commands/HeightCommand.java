package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class HeightCommand extends Command {
	
	public HeightCommand() {
		super("height", "height <x>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 1) {
			
			errorStream.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true, errorStream);
		if(x < 0) return false;
		
		System.out.println(solver.cellColumnHeight(x));
		
		return false;
	}
	
}
