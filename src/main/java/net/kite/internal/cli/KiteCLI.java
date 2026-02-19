package net.kite.internal.cli;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.*;
import java.util.Locale;
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
				}
				
				case VERBOSE_PROGRAM_ARGUMENT -> {
					
					verbose = true;
				}
				
				case SCRIPT_PROGRAM_ARGUMENT -> {
					
					i++;
					if (i == n) {
						
						errorStream.println(AnsiUtil.redAnsi("Please provide a script file using '--script <script-file>'!"));
						System.exit(2);
					}
					
					scriptFile = programArguments[i];
					quiet = true;
				}
				
				default -> {
					
					errorStream.println(AnsiUtil.redAnsi(String.format("Unknown program argument: %s", argument)));
					System.exit(2);
				}
			}
		}
		
		if(verbose) quiet = false;
		
		Kite solver = Kite.createInstance();
		if(!quiet) {
			
			Runtime runtime = Runtime.getRuntime();
			Thread shutdownThread = new Thread(() -> System.out.println("\nExiting Kite..."));
			
			runtime.addShutdownHook(shutdownThread);
			
			String name = Kite.getName();
			String version = Kite.getVersion();
			String author = Kite.getAuthor();
			
			String s = AnsiUtil.boldYellowAnsi(String.format("%s v%s", name, version));
			String message = String.format(
					" __  __    __    ______   ______   %n/\\ \\/ /   /\\ \\  /\\__  _\\ /\\  ___\\  %n\\ \\  _\"-. \\ \\ \\ \\/_/\\ \\/ \\ \\  __\\  %n \\ \\_\\ \\_\\ \\ \\_\\   \\ \\_\\  \\ \\_____\\%n  \\/_/\\/_/  \\/_/    \\/_/   \\/_____/%n%n%s by %s%n",
					s,
					author
			);
			
			System.out.println(message);
			if(scriptFile == null) {
				
				s = AnsiUtil.greenAnsi("help");
				System.out.printf("Enter '%s' to get a list of available commands.%n%n", s);
			}
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
					
					boolean exit = processCommandMessage(message, solver, errorStream, true, quiet, null);
					if(exit) return;
				}
				
			} catch(IOException exception) {
				
				errorStream.println(AnsiUtil.redAnsi(String.format("Script file parsing raised an exception: %s", exception)));
				System.exit(2);
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
			
			boolean exit = processCommandMessage(message, solver, errorStream, false, quiet, scanner);
			if(exit) return;
		}
	}
	
	private boolean processCommandMessage(String message, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		message = message.trim();
		if(message.isBlank()) return false;
		
		message = message.toLowerCase(Locale.ROOT);
		
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
		
		Command command = Commands.command(commandName);
		if(command == null) {
			
			errorStream.println(AnsiUtil.redAnsi(String.format("Command not found: %s", commandName)));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		return command.execute(commandArguments, solver, errorStream, exitOnError, quiet, scanner);
	}
	
}
