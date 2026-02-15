package net.kite.internal.cli.command;

import net.kite.api.Kite;

public abstract class Command {
	
	private final String name;
	private final String helpMessage;
	
	public Command(String name, String helpMessage) {
		this.name = name;
		this.helpMessage = helpMessage;
	}
	
	public abstract boolean execute(String[] arguments, Kite solver);
	
	public String getName() {
		return name;
	}
	
	public String getHelpMessage() {
		return helpMessage;
	}
	
}
