package net.kite.api.board.analysis.game;

import net.kite.api.Kite;
import net.kite.api.board.analysis.move.MoveAnalysis;
import net.kite.internal.util.ansi.AnsiUtil;

import java.util.Locale;

/**
 * Represents the analysis result of
 * a game.
 * The game might not have been
 * fully completed.
 * <p>
 * A game analysis consists of the
 * approximate ELO performance of the
 * player and a collection of
 * {@link MoveAnalysis} instances,
 * one for each move played.
 */
public class GameAnalysis {
	
	private static final String TO_STRING_ELO_PREFIX = "approximate elo  ";
	private static final String TO_STRING_MOVES_PREFIX = "\n\nmoves";
	private static final String TO_STRING_NO_MOVES_PREFIX = "\n\nmoves               none";
	private static final String TO_STRING_MOVE_PREFIX = "\n  - ";
	private static final String TO_STRING_MOVE_INFIX = "  ";
	
	private static final String TO_STRING_BEST_MOVES_PREFIX       = "\n\nbest                  ";
	private static final String TO_STRING_GOOD_MOVES_PREFIX       = "\ngood                  ";
	private static final String TO_STRING_INACCURATE_MOVES_PREFIX = "\ninaccurate            ";
	private static final String TO_STRING_MISTAKES_PREFIX         = "\nmistakes              ";
	private static final String TO_STRING_BLUNDERS_PREFIX         = "\nblunders              ";
	private static final String TO_STRING_MISSED_WINS_PREFIX      = "\nmissed wins           ";
	private static final String TO_STRING_FORCED_MOVES_PREFIX     = "\nforced                ";
	
	private final float approximateEloPerformance;
	
	private final MoveAnalysis[] moveAnalyses;
	
	private final int bestMoveAmount;
	private final int goodMoveAmount;
	private final int inaccurateMoveAmount;
	private final int mistakeAmount;
	private final int blunderAmount;
	private final int missedWinAmount;
	private final int forcedMoveAmount;
	
	/**
	 * Creates a new game analysis.
	 * <p>
	 * This constructor is meant for internal use!
	 * Use the methods provided by the solver
	 * class {@link Kite} to obtain game
	 * analyses.
	 *
	 * @param approximateEloPerformance
	 * approximate ELO performance of
	 * the analyzed player
	 * @param moveAnalyses array of
	 * move analyses in chronological
	 * order
	 */
	public GameAnalysis(float approximateEloPerformance, MoveAnalysis[] moveAnalyses) {
		this.approximateEloPerformance = approximateEloPerformance;
		this.moveAnalyses = moveAnalyses;
		
		int bestMoveAmount = 0;
		int goodMoveAmount = 0;
		int inaccurateMoveAmount = 0;
		int mistakeAmount = 0;
		int blunderAmount = 0;
		int missedWinAmount = 0;
		int forcedMoveAmount = 0;
		
		for(MoveAnalysis analysis : moveAnalyses) {
			
			MoveAnalysis.MoveQuality moveQuality = analysis.getMoveQuality();
			switch(moveQuality) {
				
				case BEST -> bestMoveAmount++;
				case GOOD -> goodMoveAmount++;
				case INACCURACY -> inaccurateMoveAmount++;
				case MISTAKE -> mistakeAmount++;
				case BLUNDER -> blunderAmount++;
				case MISSED_WIN -> missedWinAmount++;
				case FORCED -> forcedMoveAmount++;
			}
		}
		
		this.bestMoveAmount = bestMoveAmount;
		this.goodMoveAmount = goodMoveAmount;
		this.inaccurateMoveAmount = inaccurateMoveAmount;
		this.mistakeAmount = mistakeAmount;
		this.blunderAmount = blunderAmount;
		this.missedWinAmount = missedWinAmount;
		this.forcedMoveAmount = forcedMoveAmount;
	}
	
	/**
	 * Returns a formatted string
	 * representation of this game
	 * analysis.
	 * <p>
	 * If the terminal supports it,
	 * ANSI coloring will automatically
	 * be enabled.
	 *
	 * @return formatted string
	 * representation of this game
	 * analysis
	 */
	@Override
	public String toString() {
		boolean fancyConsole = System.console() != null;
		return toString(false, fancyConsole);
	}
	
	/**
	 * Returns a formatted string
	 * representation of this game
	 * analysis.
	 * <p>
	 * If the terminal supports it,
	 * ANSI coloring will automatically
	 * be enabled.
	 *
	 * @param includeMoveAnalyses whether
	 * individual move analyses should
	 * be included in the output
	 * @return formatted string
	 * representation of this game
	 * analysis
	 */
	public String toString(boolean includeMoveAnalyses) {
		boolean fancyConsole = System.console() != null;
		return toString(includeMoveAnalyses, fancyConsole);
	}
	
	/**
	 * Returns a formatted string
	 * representation of this game
	 * analysis.
	 *
	 * @param includeMoveAnalyses whether
	 * move analyses should be listed
	 * @param ansiColored whether ANSI
	 * coloring should be applied
	 * @return formatted string
	 * representation of this game
	 * analysis
	 */
	public String toString(boolean includeMoveAnalyses, boolean ansiColored) {
		StringBuilder stringBuilder = new StringBuilder();
		
		String s = String.format(Locale.ROOT, "%.2f", approximateEloPerformance);
		
		stringBuilder.append(TO_STRING_ELO_PREFIX);
		
		int l = s.length();
		while(l < 7) {
			
			stringBuilder.append(' ');
			l++;
		}
		
		stringBuilder.append(s);
		
		stringBuilder.append(TO_STRING_BEST_MOVES_PREFIX);
		s = String.valueOf(bestMoveAmount);
		if(bestMoveAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && bestMoveAmount != 0 ? AnsiUtil.greenAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_GOOD_MOVES_PREFIX);
		s = String.valueOf(goodMoveAmount);
		if(goodMoveAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && goodMoveAmount != 0 ? AnsiUtil.brightGreenAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_INACCURATE_MOVES_PREFIX);
		s = String.valueOf(inaccurateMoveAmount);
		if(inaccurateMoveAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && inaccurateMoveAmount != 0 ? AnsiUtil.brightYellowAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_MISTAKES_PREFIX);
		s = String.valueOf(mistakeAmount);
		if(mistakeAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && mistakeAmount != 0 ? AnsiUtil.brightRedAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_BLUNDERS_PREFIX);
		s = String.valueOf(blunderAmount);
		if(blunderAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && blunderAmount != 0 ? AnsiUtil.redAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_MISSED_WINS_PREFIX);
		s = String.valueOf(missedWinAmount);
		if(missedWinAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && missedWinAmount != 0 ? AnsiUtil.yellowAnsi(s) : s);
		
		stringBuilder.append(TO_STRING_FORCED_MOVES_PREFIX);
		s = String.valueOf(forcedMoveAmount);
		if(forcedMoveAmount < 10) stringBuilder.append(" ");
		stringBuilder.append(ansiColored && forcedMoveAmount != 0 ? AnsiUtil.brightCyanAnsi(s) : s);
		
		if(!includeMoveAnalyses) return stringBuilder.toString();
		if(moveAnalyses.length == 0) {
			
			stringBuilder.append(TO_STRING_NO_MOVES_PREFIX);
			return stringBuilder.toString();
		}
		
		stringBuilder.append(TO_STRING_MOVES_PREFIX);
		
		for(MoveAnalysis moveAnalysis : moveAnalyses) {
			
			int x = moveAnalysis.getMoveColumnIndex();
			
			stringBuilder.append(TO_STRING_MOVE_PREFIX);
			stringBuilder.append(x);
			
			s = moveAnalysis.toString(ansiColored);
			
			stringBuilder.append(TO_STRING_MOVE_INFIX);
			stringBuilder.append(s);
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * Returns the {@link MoveAnalysis}
	 * at the given move index.
	 *
	 * @param moveIndex zero-based
	 * move index
	 * @return move analysis at the
	 * given index
	 */
	public MoveAnalysis moveAnalysis(int moveIndex) {
		return moveAnalyses[moveIndex];
	}
	
	/**
	 * Returns the total amount of
	 * analyzed moves.
	 *
	 * @return number of moves
	 */
	public int moveAmount() {
		return moveAnalyses.length;
	}
	
	/**
	 * Returns the approximate ELO
	 * performance of the analyzed
	 * player.
	 *
	 * @return approximate ELO
	 * performance
	 */
	public float getApproximateEloPerformance() {
		return approximateEloPerformance;
	}
	
	/**
	 * Returns all move analyses of
	 * this game in chronological
	 * order.
	 *
	 * @return array of move analyses
	 */
	public MoveAnalysis[] getMoveAnalyses() {
		return moveAnalyses;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#BEST}.
	 *
	 * @return amount of best moves
	 */
	public int getBestMoveAmount() {
		return bestMoveAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#GOOD}.
	 *
	 * @return amount of good moves
	 */
	public int getGoodMoveAmount() {
		return goodMoveAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#INACCURACY}.
	 *
	 * @return amount of inaccurate moves
	 */
	public int getInaccurateMoveAmount() {
		return inaccurateMoveAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#MISTAKE}.
	 *
	 * @return amount of mistakes
	 */
	public int getMistakeAmount() {
		return mistakeAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#BLUNDER}.
	 *
	 * @return amount of blunders
	 */
	public int getBlunderAmount() {
		return blunderAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#MISSED_WIN}.
	 *
	 * @return amount of missed wins
	 */
	public int getMissedWinAmount() {
		return missedWinAmount;
	}
	
	/**
	 * Returns the amount of moves
	 * classified as
	 * {@link MoveAnalysis.MoveQuality#FORCED}.
	 *
	 * @return amount of forced moves
	 */
	public int getForcedMoveAmount() {
		return forcedMoveAmount;
	}
	
}
