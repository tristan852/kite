package net.kite.internal.cli;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class KiteCLI {
	
	private static final char COMMAND_ARGUMENT_SEPARATOR_CHARACTER = ' ';
	private static final String COMMAND_ARGUMENT_SEPARATOR_REGEX = "\\s+";
	
	private static final String VERSION_PROGRAM_ARGUMENT = "--version";
	private static final String QUIET_PROGRAM_ARGUMENT = "--quiet";
	private static final String VERBOSE_PROGRAM_ARGUMENT = "--verbose";
	private static final String SCRIPT_PROGRAM_ARGUMENT = "--script";
	
	public void onStart(String[] programArguments) {
		boolean quiet = System.console() == null;
		boolean verbose = false;
		
		PrintStream errorStream = quiet ? System.err : System.out;
		
		String scriptFile = null;
		
		int n = programArguments.length;
		for(int i = 0; i < n; i++) {
			
			String argument = programArguments[i];
			switch(argument) {
				case VERSION_PROGRAM_ARGUMENT -> {
					
					String version = Kite.getVersion();
					System.out.println(version);
					
					return;
				}
				
				case QUIET_PROGRAM_ARGUMENT -> {
					
					quiet = true;
					errorStream = System.err;
				}
				
				case VERBOSE_PROGRAM_ARGUMENT -> {
					
					verbose = true;
				}
				
				case SCRIPT_PROGRAM_ARGUMENT -> {
					
					i++;
					if (i == n) {
						
						errorStream.println("Please provide a script file using '--script <script-file>'!");
						return;
					}
					
					scriptFile = programArguments[i];
					quiet = true;
					errorStream = System.err;
				}
				
				default -> {
					
					errorStream.printf("Unknown program argument: %s%n", argument);
					return;
				}
			}
		}
		
		if(verbose) {
			
			quiet = false;
			errorStream = System.out;
		}
		
		Kite solver = Kite.createInstance();
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
		
		if(scriptFile != null) {
			
			try(
					Reader reader = new FileReader(scriptFile);
					BufferedReader bufferedReader = new BufferedReader(reader)
			) {
				
				while(true) {
					
					String message = bufferedReader.readLine();
					if(message == null) return;
					
					if(!quiet) System.out.printf("> %s%n", message);
					
					boolean exit = processCommandMessage(message, solver, errorStream);
					if(exit) return;
				}
				
			} catch(IOException exception) {
				
				errorStream.printf("Script file parsing raised an exception: %s%n", exception);
				return;
			}
		}
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			
			if(!quiet) {
				
				System.out.print("> ");
				System.out.flush();
			}
			
			String message;
			
			try {
				
				message = scanner.nextLine();
				
			} catch(NoSuchElementException exception) {
				
				return;
			}
			
			boolean exit = processCommandMessage(message, solver, errorStream);
			if(exit) return;
		}
	}
	
	private boolean processCommandMessage(String message, Kite solver, PrintStream errorStream) {
		message = message.trim();
		if(message.isBlank()) return false;
		
		int i = message.indexOf(COMMAND_ARGUMENT_SEPARATOR_CHARACTER);
		
		String commandName;
		String[] commandArguments;
		
		if(i < 0) {
			
			commandName = message;
			commandArguments = new String[] {};
			
		} else {
			
			commandName = message.substring(0, i);
			
			message = message.substring(i + 1);
			message = message.trim();
			
			commandArguments = message.split(COMMAND_ARGUMENT_SEPARATOR_REGEX);
		}
		
		commandName = commandName.toLowerCase();
		
		Command command = Commands.command(commandName);
		if(command == null) {
			
			errorStream.printf("Command not found: %s%n", commandName);
			return false;
		}
		
		return command.execute(commandArguments, solver, errorStream);
	}
	
}
