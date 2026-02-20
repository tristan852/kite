package net.kite.api;

import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.api.skill.level.SkillLevel;

/**
 * Defines the public API for Kite
 * while keeping internal components
 * hidden.
 * This interface is not intended
 * for direct use by users of this
 * library.
 */
public interface KiteApi {
	
	String compactBoardAnalysisString(boolean ansiColored);
	String compactBoardString(boolean ansiColored);
	
	String fancyBoardAnalysisString(boolean ansiColored);
	String fancyBoardString(boolean ansiColored);
	
	String boardAnalysisString(boolean ansiColored);
	String boardMovesString();
	String boardString(boolean ansiColored);
	
	int cellColumnHeight(int cellColumnIndex);
	boolean cellOccupied(int cellX, int cellY);
	
	BoardPlayerColor cellPlayerColor(int cellX, int cellY);
	BoardPlayerColor activePlayerColor();
	
	BoardLine[] winLines();
	
	boolean canUndoMove();
	boolean canPlayMove();
	
	BoardOutcome gameOutcome();
	boolean gameOver();
	
	int playedMoveAmount();
	
	KiteApi startRecordingPerformanceMetrics();
	KiteApi stopRecordingPerformanceMetrics();
	KiteApi printAndResetPerformanceMetrics();
	
	int skilledMove(SkillLevel skillLevel);
	int optimalMove();
	int randomMove();
	
	float[] evaluatePlayerPerformances(float[] playerRatingApproximations);
	float[] evaluatePlayerPerformances();
	float evaluatePlayerPerformance(BoardPlayerColor playerColor);
	
	int[] evaluateAllMoves(int[] moveScores);
	int[] evaluateAllMoves();
	
	int evaluateMove(int moveColumnIndex);
	int evaluateBoard();
	
	KiteApi seedRandomness();
	KiteApi seedRandomness(long seed);
	
	boolean moveLegal(int moveColumnIndex);
	
	KiteApi playMoves(String moveColumnIndices);
	KiteApi playMoves(int... moveColumnIndices);
	KiteApi playMove(int moveColumnIndex);
	
	KiteApi undoMoves(int moveAmount);
	KiteApi undoMove();
	
	KiteApi setupBoard(String moveColumnIndicesString);
	KiteApi setupBoard(int... moveColumnIndices);
	
	KiteApi clearBoard();
	
}
