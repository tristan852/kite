package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.analysis.move.MoveAnalysis;
import net.kite.api.board.evaluation.BoardEvaluation;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class EvaluateCommand extends Command {
	
	public EvaluateCommand() {
		super("evaluate", "eval", "Evaluate the board or a specific move", "evaluate [move]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			int score = solver.evaluateBoard();
			System.out.println(formatScore(score));
			
		} else if(arguments.length == 1) {
			
			int x = parseCoordinateArgument(arguments[0], "move", 1, 7, errorStream, exitOnError);
			if(x < 0) return false;
			
			if(!solver.moveLegal(x)) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Move is not legal: %s", x)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			MoveAnalysis analysis = solver.analyseMove(x);
			
			boolean ansiDisabled = AnsiUtil.areAnsiCodesDisabled();
			String s = analysis.toString(!ansiDisabled);
			
			System.out.println(s);
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
	private static String formatScore(int score) {
		String s = BoardEvaluation.formatEvaluation(score);
		
		if(score == 0) return AnsiUtil.boldBrightYellowAnsi(s);
		if(score < 0) return AnsiUtil.boldBrightRedAnsi(s);
		
		return AnsiUtil.boldBrightGreenAnsi(s);
	}
	
}
