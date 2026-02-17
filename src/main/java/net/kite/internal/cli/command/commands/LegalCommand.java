package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class LegalCommand extends Command {
	
	public LegalCommand() {
		super("legal", "legal <move>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 1) {
			
			System.err.println("Incorrect number of arguments!");
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "move", true);
		if(x < 0) return false;
		
		System.out.println(solver.moveLegal(x));
		return false;
	}
	
}
