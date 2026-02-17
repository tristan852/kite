package net.kite.internal.cli;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class KiteCLI {
	
	private static final char COMMAND_ARGUMENT_SEPARATOR_CHARACTER = ' ';
	private static final String COMMAND_ARGUMENT_SEPARATOR_REGEX = "\\s+";
	
	private static final String VERSION_PROGRAM_ARGUMENT = "--version";
	private static final String QUIET_PROGRAM_ARGUMENT = "--quiet";
	
	public void onStart(String[] programArguments) {
		boolean quiet = System.console() == null;
		
		if(programArguments.length != 0) {
			if(programArguments.length != 1) {
				
				System.err.println("Too many program arguments!");
				return;
			}
			
			String argument = programArguments[0];
			if(argument.equals(VERSION_PROGRAM_ARGUMENT)) {
				
				String version = Kite.getVersion();
				System.out.println(version);
				
				return;
			}
			
			if(argument.equals(QUIET_PROGRAM_ARGUMENT)) quiet = true;
		}
		
		Kite solver = Kite.createInstance();
		Scanner scanner = new Scanner(System.in);
		
		if(!quiet) {
			
			Runtime runtime = Runtime.getRuntime();
			Thread shutdownThread = new Thread(() -> System.out.println("\nExiting Kite..."));
			
			runtime.addShutdownHook(shutdownThread);
			
			String name = Kite.getName();
			String version = Kite.getVersion();
			String author = Kite.getAuthor();
			
			String message = String.format(
					" __  __    __    ______   ______   %n/\\ \\/ /   /\\ \\  /\\__  _\\ /\\  ___\\  %n\\ \\  _\"-. \\ \\ \\ \\/_/\\ \\/ \\ \\  __\\  %n \\ \\_\\ \\_\\ \\ \\_\\   \\ \\_\\  \\ \\_____\\%n  \\/_/\\/_/  \\/_/    \\/_/   \\/_____/%n%n%s v%s by %s%n%nEnter 'help' to get a list of available commands.%n",
					name,
					version,
					author
			);
			
			System.out.println(message);
		}
		
		while(true) {
			
			String message;
			
			try {
				
				message = scanner.nextLine();
				message = message.trim();
				
				if(message.isBlank()) continue;
				
			} catch(NoSuchElementException exception) {
				
				return;
			}
			
			int i = message.indexOf(COMMAND_ARGUMENT_SEPARATOR_CHARACTER);
			
			String commandName;
			String[] commandArguments;
			
			if(i < 0) {
				
				commandName = message;
				commandArguments = new String[] {};
				
			} else {
				
				commandName = message.substring(0, i);
				
				message = message.substring(i + 1);
				
				commandArguments = message.split(COMMAND_ARGUMENT_SEPARATOR_REGEX);
			}
			
			commandName = commandName.toLowerCase();
			
			Command command = Commands.command(commandName);
			if(command == null) {
				
				System.err.printf("Command not found: %s%n", commandName);
				continue;
			}
			
			boolean exit = command.execute(commandArguments, solver);
			if(exit) return;
		}
	}
	
}
