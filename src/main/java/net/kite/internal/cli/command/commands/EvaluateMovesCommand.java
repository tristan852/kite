package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class EvaluateMovesCommand extends Command {
	
	private static final int BOARD_WIDTH = 7;
	
	private static final String MOVE_EVALUATION_SEPARATOR_STRING = ", ";
	private static final String POSITIVE_MOVE_EVALUATION_STRING_PREFIX = "+";
	private static final String ILLEGAL_MOVE_EVALUATION_STRING = "-";
	
	private final int[] moveEvaluations;
	
	public EvaluateMovesCommand() {
		super("evaluate-moves", "evaluate-moves");
		
		this.moveEvaluations = new int[BOARD_WIDTH];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		solver.evaluateAllMoves(moveEvaluations);
		
		for(int i = 0; i < BOARD_WIDTH; i++) {
			
			int e = moveEvaluations[i];
			String s;
			
			if(e == Integer.MIN_VALUE) s = ILLEGAL_MOVE_EVALUATION_STRING;
			else {
				
				s = String.valueOf(e);
				if(e > 0) s = POSITIVE_MOVE_EVALUATION_STRING_PREFIX + s;
			}
			
			if(i != 0) System.out.print(MOVE_EVALUATION_SEPARATOR_STRING);
			System.out.print(s);
		}
		
		System.out.println();
		
		return false;
	}
	
}
