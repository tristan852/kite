package net.kite.api;

import net.kite.api.board.analysis.game.GameAnalysis;
import net.kite.api.board.analysis.move.MoveAnalysis;
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
	String boardUndoneMovesString();
	String boardMovesString();
	String boardString(boolean ansiColored);
	
	int cellColumnHeight(int cellColumnIndex);
	boolean cellOccupied(int cellX, int cellY);
	
	BoardPlayerColor cellPlayerColor(int cellX, int cellY);
	BoardPlayerColor activePlayerColor();
	
	BoardLine[] winLines();
	
	boolean canRedoMove();
	boolean canUndoMove();
	boolean canPlayMove();
	
	BoardOutcome gameOutcome();
	boolean gameOver();
	
	boolean boardEmpty();
	
	int lastMoveRow();
	int lastMove();
	
	int playedMoveRow(int moveIndex);
	int playedMove(int moveIndex);
	
	int legalMoveAmount();
	int undoneMoveAmount();
	int playedMoveAmount();
	
	KiteApi startRecordingPerformanceMetrics();
	KiteApi stopRecordingPerformanceMetrics();
	long printAndResetPerformanceMetrics();
	long resetPerformanceMetrics();
	boolean isRecordingMetrics();
	
	int adaptiveMove();
	int skilledMove(SkillLevel skillLevel);
	int optimalMove();
	int randomMove();
	
	MoveAnalysis analyseMove(int moveColumnIndex);
	
	GameAnalysis[] analyseGame(GameAnalysis[] playerGameAnalyses);
	GameAnalysis[] analyseGame();
	GameAnalysis analyseGame(BoardPlayerColor playerColor);
	
	int[] evaluateAllMoves(int[] moveScores);
	int[] evaluateAllMoves();
	
	int evaluateMove(int moveColumnIndex);
	int evaluateBoard();
	
	boolean randomBoolean();
	
	double randomDouble(double origin, double bound);
	double randomDouble();
	
	float randomFloat(float origin, float bound);
	float randomFloat();
	
	long randomLong(long origin, long bound);
	long randomLong(long bound);
	
	int randomInteger(int origin, int bound);
	int randomInteger(int bound);
	
	long seedRandomness();
	KiteApi seedRandomness(long seed);
	
	long randomSeed();
	
	boolean moveLegal(int moveColumnIndex);
	
	KiteApi clearRedoHistory();
	
	KiteApi playMoves(String moveColumnIndicesString);
	KiteApi playMoves(int... moveColumnIndices);
	KiteApi playMove(int moveColumnIndex);
	
	KiteApi redoMoves(int moveAmount);
	int redoMove();
	
	KiteApi undoMoves(int moveAmount);
	int undoMove();
	
	KiteApi setupBoard(String moveColumnIndicesString);
	KiteApi setupBoard(int... moveColumnIndices);
	
	KiteApi clearBoard();
	
}
