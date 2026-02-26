package net.kite.api.board.analysis.move;

import net.kite.api.Kite;
import net.kite.api.board.evaluation.BoardEvaluation;

/**
 * Represents the analysis result of
 * a single move played on the board.
 * <p>
 * A move analysis consists of the
 * one-based column index of the move,
 * the numerical evaluation of the
 * resulting position and a qualitative
 * classification given by
 * {@link MoveQuality}.
 */
public class MoveAnalysis {
	
	private final int moveColumnIndex;
	private final int moveEvaluation;
	
	private final MoveQuality moveQuality;
	
	/**
	 * Creates a new move analysis
	 * for a specific move.
	 * <p>
	 * This constructor is meant for internal use!
	 * Use the methods provided by the solver
	 * class {@link Kite} to obtain move
	 * analyses.
	 *
	 * @param moveColumnIndex one-based
	 * column index in which the move
	 * was played
	 * @param moveEvaluation evaluation
	 * score of the resulting position
	 * @param moveQuality qualitative
	 * classification of the move
	 */
	public MoveAnalysis(int moveColumnIndex, int moveEvaluation, MoveQuality moveQuality) {
		this.moveColumnIndex = moveColumnIndex;
		this.moveEvaluation = moveEvaluation;
		this.moveQuality = moveQuality;
	}
	
	/**
	 * Returns a formatted string
	 * representation of this move
	 * analysis.
	 * <p>
	 * If the terminal supports it,
	 * ANSI coloring will automatically
	 * be enabled.
	 *
	 * @return formatted string
	 * representation of this move
	 * analysis
	 */
	@Override
	public String toString() {
		boolean fancyConsole = System.console() != null;
		return toString(fancyConsole);
	}
	
	/**
	 * Returns a formatted string
	 * representation of this move
	 * analysis.
	 * <p>
	 * If {@code ansiColored} is
	 * {@code true}, the string will be
	 * wrapped in ANSI color codes
	 * depending on the move quality.
	 *
	 * @param ansiColored whether ANSI
	 * coloring should be applied
	 * @return formatted string
	 * representation of this move
	 * analysis
	 */
	public String toString(boolean ansiColored) {
		String s = BoardEvaluation.formatEvaluationCompactly(moveEvaluation);
		String displayName = moveQuality.getDisplayName();
		
		s = String.format("%s (%s)", displayName, s);
		
		if(!ansiColored) return s;
		
		return switch(moveQuality) {
			
			case BEST -> net.kite.internal.util.ansi.AnsiUtil.greenAnsi(s);
			case GOOD -> net.kite.internal.util.ansi.AnsiUtil.brightGreenAnsi(s);
			case INACCURACY -> net.kite.internal.util.ansi.AnsiUtil.brightYellowAnsi(s);
			case MISTAKE -> net.kite.internal.util.ansi.AnsiUtil.brightRedAnsi(s);
			case BLUNDER -> net.kite.internal.util.ansi.AnsiUtil.redAnsi(s);
			case MISSED_WIN -> net.kite.internal.util.ansi.AnsiUtil.yellowAnsi(s);
			case FORCED -> net.kite.internal.util.ansi.AnsiUtil.brightCyanAnsi(s);
		};
	}
	
	/**
	 * Returns the one-based column
	 * index in which the move
	 * was played.
	 *
	 * @return move column index
	 */
	public int getMoveColumnIndex() {
		return moveColumnIndex;
	}
	
	/**
	 * Returns the evaluation score
	 * of the resulting position
	 * after the move was played.
	 *
	 * @return evaluation score
	 */
	public int getMoveEvaluation() {
		return moveEvaluation;
	}
	
	/**
	 * Returns the qualitative
	 * classification of this move.
	 *
	 * @return move quality
	 */
	public MoveQuality getMoveQuality() {
		return moveQuality;
	}
	
	/**
	 * Represents the qualitative
	 * classification of a move.
	 * <p>
	 * Move qualities range from
	 * {@link MoveQuality#BEST}
	 * (strongest) down to
	 * {@link MoveQuality#BLUNDER}
	 * (weakest).
	 * <p>
	 * Additionally,
	 * {@link MoveQuality#MISSED_WIN}
	 * and {@link MoveQuality#FORCED}
	 * describe special situations
	 * independent of pure evaluation
	 * loss.
	 */
	public enum MoveQuality {
		
		/**
		 * One of the strongest available moves
		 * in the current position (preserves
		 * the optimal evaluation).
		 */
		BEST("Best"),
		
		/**
		 * A strong move that keeps
		 * a good evaluation but is
		 * not the optimal one.
		 */
		GOOD("Good"),
		
		/**
		 * A slightly suboptimal move
		 * that worsens the evaluation
		 * but does not change the
		 * predicted game outcome.
		 */
		INACCURACY("Inaccuracy"),
		
		/**
		 * A clearly inferior move
		 * that additionally changes
		 * the predicted game outcome.
		 */
		MISTAKE("Mistake"),
		
		/**
		 * A mistake that additionally
		 * worsens the evaluation
		 * by quite a bit.
		 */
		BLUNDER("Blunder"),
		
		/**
		 * A move that fails to capitalize
		 * on a mistake by the opponent.
		 */
		MISSED_WIN("Missed Win"),
		
		/**
		 * A move that is forced
		 * due to being the only
		 * legal move.
		 */
		FORCED("Forced");
		
		private final String name;
		private final String displayName;
		
		MoveQuality(String displayName) {
			this.name = name().toLowerCase();
			this.displayName = displayName;
		}
		
		/**
		 * Returns the name of this move
		 * quality that should for example
		 * be displayed inside a CLI.
		 *
		 * @return name of this move
		 * quality
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Returns the name of this move
		 * quality that should for example
		 * be displayed inside a user
		 * interface.
		 *
		 * @return display name of this
		 * move quality
		 */
		public String getDisplayName() {
			return displayName;
		}
	}
	
}
