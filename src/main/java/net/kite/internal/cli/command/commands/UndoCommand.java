package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class UndoCommand extends Command {
	
	public UndoCommand() {
		super("undo", "undo [move-amount]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length == 0) {
			
			if(!solver.canUndoMove()) {
				
				errorStream.println("No moves have been played yet!");
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.undoMove();
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			int n;
			
			try {
				
				n = Integer.parseInt(s);
				
			} catch(NumberFormatException exception) {
				
				errorStream.printf("Unknown integer value for argument 'move-amount': %s%n", s);
				if(exitOnError) System.exit(1);
				return false;
			}
			
			int playedMoves = solver.playedMoveAmount();
			if(n > playedMoves) {
				
				errorStream.println("That many moves have not been played yet!");
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.undoMoves(n);
			
		} else {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
