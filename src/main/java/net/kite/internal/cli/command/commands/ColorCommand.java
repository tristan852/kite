package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class ColorCommand extends Command {
	
	public ColorCommand() {
		super("color", "color [x y]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length == 0) {
			
			System.out.println(solver.activePlayerColor());
			return false;
		}
		
		if(arguments.length != 2) {
			
			errorStream.println("Incorrect number of arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true, errorStream, exitOnError);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", false, errorStream, exitOnError);
		if(y < 0) return false;
		
		System.out.println(solver.cellPlayerColor(x, y));
		
		return false;
	}
	
}
