package net.kite.internal.cli;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.util.Scanner;

public class KiteCLI {
	
	private static final char COMMAND_ARGUMENT_SEPARATOR_CHARACTER = ' ';
	private static final String COMMAND_ARGUMENT_SEPARATOR_STRING = " ";
	
	public void onStart() {
		Kite solver = Kite.createInstance();
		Scanner scanner = new Scanner(System.in);
		
		String name = Kite.getName();
		String version = Kite.getVersion();
		String author = Kite.getAuthor();
		
		String message = String.format("%s v%s by %s%n%nEnter 'help' to get a list of available commands.%nAlso, please be careful as there is very little input sanitization!%n", name, version, author);
		System.out.println(message);
		
		while(true) {
			
			message = scanner.nextLine();
			
			int i = message.indexOf(COMMAND_ARGUMENT_SEPARATOR_CHARACTER);
			
			String commandName;
			String[] commandArguments;
			
			if(i < 0) {
				
				commandName = message;
				commandArguments = new String[] {};
				
			} else {
				
				commandName = message.substring(0, i);
				
				message = message.substring(i + 1);
				
				commandArguments = message.split(COMMAND_ARGUMENT_SEPARATOR_STRING);
			}
			
			Command command = Commands.command(commandName);
			if(command == null) {
				
				System.err.printf("Command not found: %s%n", commandName);
				continue;
			}
			
			boolean exit = command.execute(commandArguments, solver);
			if(exit) break;
		}
	}
	
}
