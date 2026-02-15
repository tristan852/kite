package net.kite.internal.cli.command;

import net.kite.internal.cli.command.commands.*;

import java.util.HashMap;
import java.util.Map;

public class Commands {
	
	public static final Command[] COMMANDS = new Command[] {
			new HelpCommand(),
			new ExitCommand(),
			
			new CanPlayCommand(),
			new CanUndoCommand(),
			new ClearCommand(),
			new ColorCommand(),
			new EvaluateCommand(),
			new EvaluateMovesCommand(),
			new HeightCommand(),
			new LegalCommand(),
			new LinesCommand(),
			new MoveCountCommand(),
			new OccupiedCommand(),
			new OptimalCommand(),
			new OutcomeCommand(),
			new OverCommand(),
			new PerformanceCommand(),
			new PlayCommand(),
			new PrintCommand(),
			new RandomCommand(),
			new SeedCommand(),
			new SetupCommand(),
			new SkilledCommand(),
			new UndoCommand(),
			
			new BenchmarkCommand()
	};
	
	private static final Map<String, Command> MAPPED_COMMANDS = new HashMap<>();
	
	static {
		for(Command command : COMMANDS) {
			
			String name = command.getName();
			MAPPED_COMMANDS.put(name, command);
		}
	}
	
	public static Command command(String commandName) {
		return MAPPED_COMMANDS.get(commandName);
	}
	
}
