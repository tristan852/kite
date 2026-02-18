package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

import java.io.PrintStream;
import java.util.Scanner;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help", "help");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println("Available commands:");
		
		for(Command command : Commands.COMMANDS) {
			
			String helpMessage = command.getHelpMessage();
			System.out.println(helpMessage);
		}
		
		return false;
	}
	
}
