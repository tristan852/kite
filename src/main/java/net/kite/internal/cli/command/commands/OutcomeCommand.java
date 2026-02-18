package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class OutcomeCommand extends Command {
	
	public OutcomeCommand() {
		super("outcome", "outcome");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			return false;
		}
		
		System.out.println(solver.gameOutcome());
		
		return false;
	}
	
}
