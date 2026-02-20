package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class RedoCommand extends Command {
	
	public RedoCommand() {
		super("redo", "r", "Redo one or more moves", "redo [move-amount]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			if(!solver.canRedoMove()) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("No moves left to redo!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.redoMove();
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			int n;
			
			try {
				
				n = Integer.parseInt(s);
				
			} catch(NumberFormatException exception) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown integer value for argument 'move-amount': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			int undoneMoves = solver.undoneMoveAmount();
			if(n > undoneMoves) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("That many moves have not been undone!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.redoMoves(n);
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
