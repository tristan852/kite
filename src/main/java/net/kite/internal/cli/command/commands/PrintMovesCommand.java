package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class PrintMovesCommand extends Command {
	
	private static final String BOARD_MOVES_STRING_FORMAT = "%s(%s)";
	
	public PrintMovesCommand() {
		super("print-moves", "pm", "Show the sequence of played moves and undone moves (not included by default)", "print-moves [include-undone]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		boolean includeUndoneMoves;
		
		if(arguments.length == 0) {
			
			includeUndoneMoves = false;
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			if(s.equals("true") || s.equals("include-undone")) includeUndoneMoves = true;
			else if(s.equals("false")) includeUndoneMoves = false;
			else {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown boolean value for argument 'include-undone': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(includeUndoneMoves) {
			
			if(solver.boardEmpty() && !solver.canRedoMove()) {
				
				if(quiet) System.out.println();
				else System.out.println("No moves played or undone so far.");
				
			} else {
				
				String s = String.format(BOARD_MOVES_STRING_FORMAT, solver.boardMovesString(), solver.boardUndoneMovesString());
				System.out.println(s);
			}
			
		} else {
			
			if(solver.boardEmpty()) {
				
				if(quiet) System.out.println();
				else System.out.println("No moves played so far.");
				
			} else {
				
				System.out.println(solver.boardMovesString());
			}
		}
		
		return false;
	}
	
}
