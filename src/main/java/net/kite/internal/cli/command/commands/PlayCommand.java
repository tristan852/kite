package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class PlayCommand extends Command {
	
	private static final int MOVE_CHARACTER_OFFSET = 48;
	
	public PlayCommand() {
		super("play", "play [moves]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 1) {
			
			errorStream.println("Incorrect number of arguments!");
			return false;
		}
		
		String moves = arguments[0];
		if(moves.isBlank()) {
			
			errorStream.println("String value for argument 'moves' is empty!");
			return false;
		}
		
		if(!moves.matches("[1-7]+")) {
			
			errorStream.printf("Invalid move found in moves argument: %s%n", moves);
			return false;
		}
		
		int n = moves.length();
		for(int i = 0; i < n; i++) {
			
			int x = moves.charAt(i) - MOVE_CHARACTER_OFFSET;
			if(solver.moveLegal(x)) {
				
				solver.playMove(x);
				
			} else {
				
				errorStream.printf("Illegal move '%s' found in moves argument: %s%n", x, moves);
				return false;
			}
		}
		
		return false;
	}
	
}
