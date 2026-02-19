package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class SetupCommand extends Command {
	
	private static final int MOVE_CHARACTER_OFFSET = 48;
	
	public SetupCommand() {
		super("setup", "Set up the game from a sequence of moves", "setup [moves]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			solver.clearBoard();
			return false;
		}
		
		if(arguments.length != 1) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		String moves = arguments[0];
		if(moves.isBlank()) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("String value for argument 'moves' is empty!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(!moves.matches("[1-7]+")) {
			
			errorStream.println(AnsiUtil.brightRedAnsi(String.format("Invalid move found in moves argument: \"%s\"", moves)));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		solver.clearBoard();
		
		int n = moves.length();
		for(int i = 0; i < n; i++) {
			
			int x = moves.charAt(i) - MOVE_CHARACTER_OFFSET;
			if(solver.moveLegal(x)) {
				
				solver.playMove(x);
				
			} else {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Illegal Connect Four game: \"%s\"", moves)));
				if(exitOnError) System.exit(1);
				return false;
			}
		}
		
		return false;
	}
	
}
