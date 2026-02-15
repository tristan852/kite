package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class OccupiedCommand extends Command {
	
	private static final int COORDINATE_OFFSET = 48;
	
	public OccupiedCommand() {
		super("occupied", "occupied <x> <y>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		char c1 = arguments[0].charAt(0);
		char c2 = arguments[1].charAt(0);
		
		int x = c1 - COORDINATE_OFFSET;
		int y = c2 - COORDINATE_OFFSET;
		
		System.out.println(solver.cellOccupied(x, y));
		
		return false;
	}
	
}
