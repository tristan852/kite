package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.io.PrintStream;
import java.util.Scanner;

public class HelpCommand extends Command {
	
	private static final int LARGEST_COMMAND_NAME_LENGTH = 36;
	
	public HelpCommand() {
		super("help", "h", "Show help information", "help");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println("Available commands:");
		
		for(Command[] commands : Commands.CATEGORIZED_COMMANDS) {
			
			System.out.println();
			
			for(Command command : commands) {
				
				String alias = command.getAlias();
				String helpMessage = command.getHelpMessage();
				String description = command.getDescription();
				
				String s = alias == null ? helpMessage : String.format("%s (alias: %s)", helpMessage, alias);
				System.out.printf("  %-" + LARGEST_COMMAND_NAME_LENGTH + "s  %s%n", s, description);
			}
		}
		
		return false;
	}
	
}
