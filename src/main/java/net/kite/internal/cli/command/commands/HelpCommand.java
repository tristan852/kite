package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help", "help");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		System.out.println("available commands:");
		
		for(Command command : Commands.COMMANDS) {
			
			String helpMessage = command.getHelpMessage();
			System.out.println(helpMessage);
		}
		
		return false;
	}
	
}
