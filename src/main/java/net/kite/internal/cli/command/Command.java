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
	
	public static int parseCoordinateArgument(String argument, String argumentName, boolean xCoordinate) {
		int i;
		
		try {
			
			i = Integer.parseInt(argument);
			
		} catch(NumberFormatException exception) {
			
			System.err.printf("Unknown integer value for argument '%s': %s%n", argumentName, argument);
			return Integer.MIN_VALUE;
		}
		
		int max = xCoordinate ? 7 : 6;
		
		if(i < 0 || i >= max) {
			
			System.err.printf("Integer value for argument '%s' is out of bounds: %s%n", argumentName, argument);
			return Integer.MIN_VALUE;
		}
		
		return i;
	}
	
}
