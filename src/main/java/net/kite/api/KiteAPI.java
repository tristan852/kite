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
public interface KiteAPI {
	
	String boardAnalysisString();
	String boardMovesString();
	String boardString();
	
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
	
	void startRecordingPerformanceMetrics();
	void stopRecordingPerformanceMetrics();
	void printAndResetPerformanceMetrics();
	
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
	
	void seedRandomness();
	void seedRandomness(long seed);
	
	boolean moveLegal(int moveColumnIndex);
	
	void playMoves(String moveColumnIndices);
	void playMoves(int... moveColumnIndices);
	void playMove(int moveColumnIndex);
	
	void undoMoves(int moveAmount);
	void undoMove();
	
	void setupBoard(String moveColumnIndicesString);
	void setupBoard(int... moveColumnIndices);
	
	void clearBoard();
	
}
