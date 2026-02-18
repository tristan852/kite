package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.io.PrintStream;
import java.util.Scanner;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help", "Show help information", "help");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println("Available commands:");
		
		int maxLength = 0;
		
		for(Command command : Commands.COMMANDS) {
			
			String helpMessage = command.getHelpMessage();
			int l = helpMessage.length();
			
			if(l > maxLength) maxLength = l;
		}
		
		for(Command[] commands : Commands.CATEGORIZED_COMMANDS) {
			
			System.out.println();
			
			for(Command command : commands) {
				
				String helpMessage = command.getHelpMessage();
				String description = command.getDescription();
				
				System.out.printf("  %-" + maxLength + "s  %s%n", helpMessage, description);
			}
		}
		
		return false;
	}
	
}
