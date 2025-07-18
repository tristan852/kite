package net.kite.command;

public abstract class Command {
	
	private final String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	public abstract boolean execute(String[] arguments);
	
	public String getName() {
		return name;
	}
	
}
