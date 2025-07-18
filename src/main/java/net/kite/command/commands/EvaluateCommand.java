package net.kite.command.commands;

import net.kite.Kite;
import net.kite.board.Board;
import net.kite.command.Command;

public class EvaluateCommand extends Command {
	
	private static final String NAME = "evaluate";
	
	private static final String MOVE_EVALUATION_STRING_TEMPLATE = "%s: %s";
	private static final String OPTIMAL_MOVE_EVALUATION_STRING_TEMPLATE = "%s: %s (optimal)";
	
	private final int[] moveScores;
	
	public EvaluateCommand() {
		super(NAME);
		
		int l = Board.getWidth();
		
		this.moveScores = new int[l];
	}
	
	@Override
	public boolean execute(String[] arguments) {
		Kite kite = Kite.getKite();
		Board board = kite.getBoard();
		
		if(!board.canPlayMoves()) {
			
			System.err.println("Current board does not have any legal moves!");
			return false;
		}
		
		int optimalMoveScore = Integer.MIN_VALUE;
		
		int l = moveScores.length;
		for(int x = 0; x < l; x++) {
			
			if(!board.moveLegal(x)) continue;
			
			int score = board.evaluateMove(x);
			
			moveScores[x] = score;
			
			if(score > optimalMoveScore) optimalMoveScore = score;
		}
		
		for(int x = 0; x < l; x++) {
			
			if(!board.moveLegal(x)) continue;
			
			int move = x + 1;
			int score = moveScores[x];
			boolean optimal = score == optimalMoveScore;
			
			String s = optimal ? OPTIMAL_MOVE_EVALUATION_STRING_TEMPLATE : MOVE_EVALUATION_STRING_TEMPLATE;
			s = s.formatted(move, score);
			
			System.out.println(s);
		}
		
		return false;
	}
	
}
