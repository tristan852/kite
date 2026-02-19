package net.kite.internal.cli.command;

import net.kite.api.Kite;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class Command {
	
	private final String name;
	private final String alias;
	private final String description;
	private final String helpMessage;
	
	public Command(String name, String description, String helpMessage) {
		this(name, null, description, helpMessage);
	}
	
	public Command(String name, String alias, String description, String helpMessage) {
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.helpMessage = helpMessage;
	}
	
	public abstract boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner);
	
	public String getName() {
		return name;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getHelpMessage() {
		return helpMessage;
	}
	
	public static int parseCoordinateArgument(String argument, String argumentName, int min, int max, PrintStream errorStream, boolean exitOnError) {
		int i;
		
		try {
			
			i = Integer.parseInt(argument);
			
		} catch(NumberFormatException exception) {
			
			errorStream.println(AnsiUtil.redAnsi(String.format("Unknown integer value for argument '%s': \"%s\"", argumentName, argument)));
			if(exitOnError) System.exit(1);
			return Integer.MIN_VALUE;
		}
		
		if(i < min || i > max) {
			
			errorStream.println(AnsiUtil.redAnsi(String.format("Integer value for argument '%s' is out of bounds: \"%s\"", argumentName, argument)));
			if(exitOnError) System.exit(1);
			return Integer.MIN_VALUE;
		}
		
		return i;
	}
	
}
