package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.score.BoardScore;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public class EvaluateMovesCommand extends Command {
	
	private static final int BOARD_WIDTH = 7;
	
	private static final String MOVE_EVALUATION_SEPARATOR_STRING = ", ";
	private static final String ILLEGAL_MOVE_EVALUATION_STRING = "-";
	private static final String GAME_OVER_MOVE_EVALUATIONS_STRING;
	
	static {
		String s1 = AnsiUtil.boldCyanAnsi("-");
		String s2 = ", ";
		
		GAME_OVER_MOVE_EVALUATIONS_STRING = s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1;
	}
	
	private final int[] moveEvaluations;
	
	public EvaluateMovesCommand() {
		super("evaluate-moves", "em", "Evaluate all possible moves", "evaluate-moves");
		
		this.moveEvaluations = new int[BOARD_WIDTH];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(solver.gameOver()) {
			
			System.out.println(GAME_OVER_MOVE_EVALUATIONS_STRING);
			return false;
		}
		
		solver.evaluateAllMoves(moveEvaluations);
		
		for(int i = 0; i < BOARD_WIDTH; i++) {
			
			int e = moveEvaluations[i];
			String s;
			
			if(e == Integer.MIN_VALUE) s = AnsiUtil.boldCyanAnsi(ILLEGAL_MOVE_EVALUATION_STRING);
			else {
				
				s = BoardScore.formatScoreCompactly(e);
				
				if(e == 0) s = AnsiUtil.boldYellowAnsi(s);
				else if(e < 0) s = AnsiUtil.boldRedAnsi(s);
				else s = AnsiUtil.boldGreenAnsi(s);
			}
			
			if(i != 0) System.out.print(MOVE_EVALUATION_SEPARATOR_STRING);
			System.out.print(s);
		}
		
		System.out.println();
		
		return false;
	}
	
}
