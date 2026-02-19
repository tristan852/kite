package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class HelpCommand extends Command {
	
	private static final int LARGEST_COMMAND_NAME_LENGTH = 36;
	private static final String PADDING_STRING = " ";
	
	public HelpCommand() {
		super("help", "h", "Show help information", "help");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
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
				
				String s;
				int l;
				if(alias == null) {
					
					s = AnsiUtil.yellowAnsi(helpMessage);
					l = helpMessage.length();
					
				} else {
					
					s = AnsiUtil.yellowAnsi(helpMessage) + AnsiUtil.cyanAnsi(" (alias: ") + AnsiUtil.yellowAnsi(alias) + AnsiUtil.cyanAnsi(")");
					l = helpMessage.length() + alias.length() + 10;
				}
				
				s += PADDING_STRING.repeat(LARGEST_COMMAND_NAME_LENGTH - l);
				
				System.out.printf("  %s  %s%n", s, description);
			}
		}
		
		System.out.println();
		return false;
	}
	
}
