package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class SetupCommand extends Command {
	
	public SetupCommand() {
		super("setup", "setup [moves]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(String s : arguments) stringBuilder.append(s);
		
		solver.setupBoard(stringBuilder.toString());
		
		return false;
	}
	
}
