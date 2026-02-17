package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class ColorCommand extends Command {
	
	public ColorCommand() {
		super("color", "color [x y]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			System.out.println(solver.activePlayerColor());
			return false;
		}
		
		if(arguments.length != 2) {
			
			System.err.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", true);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", false);
		if(y < 0) return false;
		
		System.out.println(solver.cellPlayerColor(x, y));
		
		return false;
	}
	
}
