package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public class OutcomeCommand extends Command {
	
	public OutcomeCommand() {
		super("outcome", "Show the game outcome", "outcome");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		BoardOutcome outcome = solver.gameOutcome();
		String s = outcome.getName();
		
		if(outcome == BoardOutcome.UNDECIDED) s = AnsiUtil.boldCyanAnsi(s);
		else if(outcome == BoardOutcome.RED_WIN) s = AnsiUtil.boldRedAnsi(s);
		else if(outcome == BoardOutcome.YELLOW_WIN) s = AnsiUtil.boldYellowAnsi(s);
		else if(outcome == BoardOutcome.DRAW) s = AnsiUtil.boldCyanAnsi(s);
		
		System.out.println(s);
		return false;
	}
	
}
