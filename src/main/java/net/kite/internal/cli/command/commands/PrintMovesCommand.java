package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class PrintMovesCommand extends Command {
	
	private static final String EMPTY_BOARD_MOVES_STRING = "";
	
	public PrintMovesCommand() {
		super("print-moves", "pm", "Show the sequence of played moves", "print-moves");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(solver.boardEmpty()) {
			
			if(quiet) System.out.println(EMPTY_BOARD_MOVES_STRING);
			else System.out.println("No moves played so far.");
			
		} else {
			
			System.out.println(solver.boardMovesString());
		}
		
		return false;
	}
	
}
