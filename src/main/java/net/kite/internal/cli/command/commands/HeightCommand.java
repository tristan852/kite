package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class HeightCommand extends Command {
	
	private static final int COORDINATE_OFFSET = 48;
	
	public HeightCommand() {
		super("height", "height <x>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		char c = arguments[0].charAt(0);
		int x = c - COORDINATE_OFFSET;
		
		System.out.println(solver.cellColumnHeight(x));
		
		return false;
	}
	
}
