package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class ExitCommand extends Command {
	
	public ExitCommand() {
		super("exit", "exit");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		return true;
	}
	
}
