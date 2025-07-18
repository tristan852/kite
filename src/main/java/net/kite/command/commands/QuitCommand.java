package net.kite.command.commands;

import net.kite.command.Command;

public class QuitCommand extends Command {
	
	private static final String NAME = "quit";
	
	public QuitCommand() {
		super(NAME);
	}
	
	@Override
	public boolean execute(String[] arguments) {
		System.out.println("Engine is now terminating...");
		
		return true;
	}
	
}
