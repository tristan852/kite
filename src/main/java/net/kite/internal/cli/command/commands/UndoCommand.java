package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class UndoCommand extends Command {
	
	public UndoCommand() {
		super("undo", "u", "Undo one or more moves", "undo [move-amount]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			if(solver.boardEmpty()) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("No moves have been played yet!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.undoMove();
			
			System.out.println("Move undone.");
			
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
			
			if(n <= 0) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("Integer value for argument 'move-amount' has to be positive!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			int playedMoves = solver.playedMoveAmount();
			if(n > playedMoves) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("That many moves have not been played yet!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.undoMoves(n);
			
			System.out.println("Moves undone.");
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
