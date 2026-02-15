package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class LegalCommand extends Command {
	
	private static final int MOVE_CHARACTER_OFFSET = 48;
	
	public LegalCommand() {
		super("legal", "legal <move>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		char c = arguments[0].charAt(0);
		int i = c - MOVE_CHARACTER_OFFSET;
		
		System.out.println(solver.moveLegal(i));
		return false;
	}
	
}
