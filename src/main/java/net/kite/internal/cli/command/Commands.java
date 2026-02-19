package net.kite.internal.cli.command;

import net.kite.internal.cli.command.commands.*;

import java.util.HashMap;
import java.util.Map;

public final class Commands {
	
	public static final Command[] COMMANDS = new Command[] {
			new HelpCommand(),
			new ExitCommand(),
			new SeedCommand(),
			new GameCommand(),
			
			new PlayCommand(),
			new UndoCommand(),
			new SetupCommand(),
			new ClearCommand(),
			
			new EvaluateCommand(),
			new EvaluateMovesCommand(),
			new OptimalCommand(),
			new RandomCommand(),
			new SkilledCommand(),
			new PerformanceCommand(),
			new BenchmarkCommand(),
			
			new PrintCommand(),
			new PrintMovesCommand(),
			new CanPlayCommand(),
			new CanUndoCommand(),
			new OverCommand(),
			new OutcomeCommand(),
			new MoveCountCommand(),
			new OccupiedCommand(),
			new ColorCommand(),
			new HeightCommand(),
			new LegalCommand(),
			new LinesCommand()
	};
	
	public static final Command[][] CATEGORIZED_COMMANDS = new Command[][] {
			{
				new HelpCommand(),
				new ExitCommand(),
				new SeedCommand(),
				new GameCommand()
			},
			{
				new PlayCommand(),
				new UndoCommand(),
				new SetupCommand(),
				new ClearCommand()
			},
			{
				new EvaluateCommand(),
				new EvaluateMovesCommand(),
				new OptimalCommand(),
				new RandomCommand(),
				new SkilledCommand(),
				new PerformanceCommand(),
				new BenchmarkCommand()
			},
			{
				new PrintCommand(),
				new PrintMovesCommand(),
				new CanPlayCommand(),
				new CanUndoCommand(),
				new OverCommand(),
				new OutcomeCommand(),
				new MoveCountCommand(),
				new OccupiedCommand(),
				new ColorCommand(),
				new HeightCommand(),
				new LegalCommand(),
				new LinesCommand()
			}
	};
	
	private static final Map<String, Command> MAPPED_COMMANDS = new HashMap<>();
	
	static {
		for(Command command : COMMANDS) {
			
			String name = command.getName();
			String alias = command.getAlias();
			
			MAPPED_COMMANDS.put(name, command);
			MAPPED_COMMANDS.put(alias, command);
		}
	}
	
	public static Command command(String commandName) {
		return MAPPED_COMMANDS.get(commandName);
	}
	
}
