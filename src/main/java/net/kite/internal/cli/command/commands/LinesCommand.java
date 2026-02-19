package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.line.BoardLine;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class LinesCommand extends Command {
	
	public LinesCommand() {
		super("lines", "Show completed lines on the board", "lines");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		BoardLine[] lines = solver.winLines();
		if(lines == null) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("The game has not ended yet or has ended in a draw!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		for(BoardLine line : lines) System.out.println(line);
		
		return false;
	}
	
}
