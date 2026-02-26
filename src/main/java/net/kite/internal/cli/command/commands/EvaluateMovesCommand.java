package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.evaluation.BoardEvaluation;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class EvaluateMovesCommand extends Command {
	
	private static final int BOARD_WIDTH = 7;
	
	private static final String MOVE_EVALUATION_SEPARATOR_STRING = ", ";
	private static final String ILLEGAL_MOVE_EVALUATION_STRING = "-";
	private static final String GAME_OVER_MOVE_EVALUATIONS_STRING = "-, -, -, -, -, -, -";
	private static final String COLORED_GAME_OVER_MOVE_EVALUATIONS_STRING;
	
	static {
		synchronized(AnsiUtil.class) {
			
			boolean disabled = AnsiUtil.areAnsiCodesDisabled();
			if(disabled) AnsiUtil.enableAnsiCodes();
			
			String s1 = AnsiUtil.boldBrightCyanAnsi("-");
			String s2 = ", ";
			
			COLORED_GAME_OVER_MOVE_EVALUATIONS_STRING = s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1;
			
			if(disabled) AnsiUtil.disableAnsiCodes();
		}
	}
	
	private final int[] moveEvaluations;
	
	public EvaluateMovesCommand() {
		super("evaluate-moves", "em", "Evaluate all possible moves", "evaluate-moves");
		
		this.moveEvaluations = new int[BOARD_WIDTH];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(solver.gameOver()) {
			
			String s = AnsiUtil.areAnsiCodesDisabled() ? GAME_OVER_MOVE_EVALUATIONS_STRING : COLORED_GAME_OVER_MOVE_EVALUATIONS_STRING;
			System.out.println(s);
			return false;
		}
		
		solver.evaluateAllMoves(moveEvaluations);
		
		for(int i = 0; i < BOARD_WIDTH; i++) {
			
			int e = moveEvaluations[i];
			String s;
			
			if(e == Integer.MIN_VALUE) s = AnsiUtil.boldBrightCyanAnsi(ILLEGAL_MOVE_EVALUATION_STRING);
			else {
				
				s = BoardEvaluation.formatEvaluationCompactly(e);
				
				if(e == 0) s = AnsiUtil.boldBrightYellowAnsi(s);
				else if(e < 0) s = AnsiUtil.boldBrightRedAnsi(s);
				else s = AnsiUtil.boldBrightGreenAnsi(s);
			}
			
			if(i != 0) System.out.print(MOVE_EVALUATION_SEPARATOR_STRING);
			System.out.print(s);
		}
		
		System.out.println();
		
		return false;
	}
	
}
