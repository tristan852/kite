package net.kite.command.commands;

import net.kite.Kite;
import net.kite.board.Board;
import net.kite.command.Command;

public class PlayCommand extends Command {
	
	private static final String NAME = "play";
	
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	public PlayCommand() {
		super(NAME);
	}
	
	@Override
	public boolean execute(String[] arguments) {
		int l = arguments.length;
		if(l != 1) {
			
			String errorMessage = String.format("Incorrect number of command arguments: %s", l);
			System.err.println(errorMessage);
			
			return false;
		}
		
		Kite kite = Kite.getKite();
		Board board = kite.getBoard();
		
		String moves = arguments[0];
		l = moves.length();
		
		for(int i = 0; i < l; i++) {
			
			char move = moves.charAt(i);
			int x = move - SMALLEST_MOVE_CHARACTER;
			
			board.playMove(x);
		}
		
		System.out.println("Moves were played successfully");
		
		return false;
	}
	
}
