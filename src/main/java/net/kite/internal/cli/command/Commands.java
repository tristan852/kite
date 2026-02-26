package net.kite.internal.cli.command;

import net.kite.internal.cli.command.commands.*;

import java.util.HashMap;
import java.util.Map;

public final class Commands {
	
	public static final Command HELP = new HelpCommand();
	public static final Command SKILL_LEVELS = new SkillLevelsCommand();
	public static final Command EXIT = new ExitCommand();
	public static final Command SEED = new SeedCommand();
	public static final Command GAME = new GameCommand();
	
	public static final Command PLAY = new PlayCommand();
	public static final Command UNDO = new UndoCommand();
	public static final Command REDO = new RedoCommand();
	public static final Command SETUP = new SetupCommand();
	public static final Command CLEAR = new ClearCommand();
	
	public static final Command EVALUATE = new EvaluateCommand();
	public static final Command EVALUATE_MOVES = new EvaluateMovesCommand();
	public static final Command OPTIMAL = new OptimalCommand();
	public static final Command RANDOM = new RandomCommand();
	public static final Command SKILLED = new SkilledCommand();
	public static final Command ANALYSIS = new AnalysisCommand();
	public static final Command BENCHMARK = new BenchmarkCommand();
	
	public static final Command PRINT = new PrintCommand();
	public static final Command PRINT_MOVES = new PrintMovesCommand();
	public static final Command EMPTY = new EmptyCommand();
	public static final Command MOVE = new MoveCommand();
	public static final Command MOVE_ROW = new MoveRowCommand();
	public static final Command CAN_PLAY = new CanPlayCommand();
	public static final Command CAN_UNDO = new CanUndoCommand();
	public static final Command CAN_REDO = new CanRedoCommand();
	public static final Command OVER = new OverCommand();
	public static final Command OUTCOME = new OutcomeCommand();
	public static final Command MOVE_COUNT = new MoveCountCommand();
	public static final Command UNDONE_MOVE_COUNT = new UndoneMoveCountCommand();
	public static final Command LEGAL_MOVE_COUNT = new LegalMoveCountCommand();
	public static final Command OCCUPIED = new OccupiedCommand();
	public static final Command COLOR = new ColorCommand();
	public static final Command HEIGHT = new HeightCommand();
	public static final Command LEGAL = new LegalCommand();
	public static final Command LINES = new LinesCommand();
	
	public static final Command[] COMMANDS = new Command[] {
			HELP,
			SKILL_LEVELS,
			EXIT,
			SEED,
			GAME,
			
			PLAY,
			UNDO,
			REDO,
			SETUP,
			CLEAR,
			
			EVALUATE,
			EVALUATE_MOVES,
			OPTIMAL,
			RANDOM,
			SKILLED,
			ANALYSIS,
			BENCHMARK,
			
			PRINT,
			PRINT_MOVES,
			EMPTY,
			MOVE,
			MOVE_ROW,
			CAN_PLAY,
			CAN_UNDO,
			CAN_REDO,
			OVER,
			OUTCOME,
			MOVE_COUNT,
			UNDONE_MOVE_COUNT,
			LEGAL_MOVE_COUNT,
			OCCUPIED,
			COLOR,
			HEIGHT,
			LEGAL,
			LINES
	};
	
	public static final Command[][] CATEGORIZED_COMMANDS = new Command[][] {
			{
					HELP,
					SKILL_LEVELS,
					EXIT,
					SEED,
					GAME
			},
			{
					PLAY,
					UNDO,
					REDO,
					SETUP,
					CLEAR
			},
			{
					EVALUATE,
					EVALUATE_MOVES,
					OPTIMAL,
					RANDOM,
					SKILLED,
					ANALYSIS,
					BENCHMARK
			},
			{
					PRINT,
					PRINT_MOVES,
					EMPTY,
					MOVE,
					MOVE_ROW,
					CAN_PLAY,
					CAN_UNDO,
					CAN_REDO,
					OVER,
					OUTCOME,
					MOVE_COUNT,
					UNDONE_MOVE_COUNT,
					LEGAL_MOVE_COUNT,
					OCCUPIED,
					COLOR,
					HEIGHT,
					LEGAL,
					LINES
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
