package net.kite.command;

import net.kite.command.commands.*;

import java.util.HashMap;
import java.util.Map;

public class Commands {
	
	private static final Command[] COMMANDS = new Command[] {
			new PlayCommand(),
			new UndoCommand(),
			new ClearCommand(),
			new BoardCommand(),
			new EvaluateCommand(),
			new QuitCommand()
	};
	
	private static final Map<String, Command> NAME_COMMANDS = new HashMap<>();
	
	static {
		for(Command c : COMMANDS) {
			
			String name = c.getName();
			NAME_COMMANDS.put(name, c);
		}
	}
	
	public static Command command(String commandName) {
		return NAME_COMMANDS.get(commandName);
	}
	
}
