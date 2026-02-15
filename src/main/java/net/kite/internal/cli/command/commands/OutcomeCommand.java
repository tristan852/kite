package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class OutcomeCommand extends Command {
	
	public OutcomeCommand() {
		super("outcome", "outcome");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		System.out.println(solver.gameOutcome());
		
		return false;
	}
	
}
