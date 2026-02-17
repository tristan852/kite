package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class UndoCommand extends Command {
	
	public UndoCommand() {
		super("undo", "undo [move-amount]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			if(!solver.canUndoMove()) {
				
				System.err.println("No moves have been played yet!");
				return false;
			}
			
			solver.undoMove();
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			int n;
			
			try {
				
				n = Integer.parseInt(s);
				
			} catch(NumberFormatException exception) {
				
				System.err.printf("Unknown integer value for argument 'move-amount': %s%n", s);
				return false;
			}
			
			int playedMoves = solver.playedMoveAmount();
			if(n > playedMoves) {
				
				System.err.println("That many moves have not been played yet!");
				return false;
			}
			
			solver.undoMoves(n);
			
		} else {
			
			System.err.println("Too many arguments!");
		}
		
		return false;
	}
	
}
