package net.kite.internal;

import net.kite.api.KiteApi;
import net.kite.api.board.analysis.game.GameAnalysis;
import net.kite.api.board.analysis.move.MoveAnalysis;
import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.api.exception.IllegalMoveException;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.board.Board;
import net.kite.internal.board.score.BoardScore;
import net.kite.internal.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.internal.util.ansi.AnsiUtil;
import net.kite.internal.util.random.Random;
import net.kite.internal.util.time.TimeUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

public final class Kite implements KiteApi {
	
	private static final int BOARD_SIZE = 42;
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	
	private static final int GAME_PLAYER_AMOUNT = 2;
	
	private static final int[] ORDERED_MOVE_COLUMN_INDICES = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int INVALID_MOVE_COLUMN_INDEX = 0;
	
	private static final int MAXIMAL_MOVE_SCORE_LOSS = 36;
	private static final int MINIMAL_NO_LOSS_PLAYED_MOVE_AMOUNT = 41;
	
	private static final int NOTICED_WINNING_MOVE_WEIGHT = 1000000;
	
	private static final int MOVE_COLUMN_INDEX_SMALLEST_CHARACTER = 49;
	private static final int MOVE_COLUMN_INDEX_LARGEST_CHARACTER = 55;
	
	private static final String EMPTY_MOVES_STRING = "";
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	private static final double METRICS_THROUGHPUT_CONVERSION_FACTOR = 1000.0;
	private static final String METRICS_STRING_PATTERN = "positions evaluated      : %,d\naverage evaluation time  : %s\naverage node evaluations : %s\nnode throughput          : %s%s";
	private static final String COLORED_METRICS_STRING_PATTERN;
	private static final String METRICS_STRING_MISSING_VALUE_STRING = "N/A";
	
	private static final String[] BENCHMARK_RESOURCE_PATHS = new String[] {
			"/benchmarks/endgame_easy.txt",
			"/benchmarks/midgame_easy.txt",
			"/benchmarks/midgame_medium.txt",
			"/benchmarks/opening_easy.txt",
			"/benchmarks/opening_medium.txt",
			"/benchmarks/opening_hard.txt"
	};
	
	private static final char BENCHMARK_ENTRY_SEPARATOR_CHARACTER = ' ';
	
	private static final int LARGEST_PROBABILITY = 100;
	
	static {
		synchronized(AnsiUtil.class) {
			
			boolean disabled = AnsiUtil.areAnsiCodesDisabled();
			if(disabled) AnsiUtil.enableAnsiCodes();
			
			COLORED_METRICS_STRING_PATTERN =
					AnsiUtil.cyanAnsi("positions evaluated      : ") +
					AnsiUtil.brightYellowAnsi("%,d") +
					AnsiUtil.cyanAnsi("\naverage evaluation time  : ") +
					AnsiUtil.brightYellowAnsi("%s") +
					AnsiUtil.cyanAnsi("\naverage node evaluations : ") +
					AnsiUtil.brightYellowAnsi("%s") +
					AnsiUtil.cyanAnsi("\nnode throughput          : ") +
					AnsiUtil.brightYellowAnsi("%s") +
					AnsiUtil.cyanAnsi("%s");
			
			if(disabled) AnsiUtil.disableAnsiCodes();
		}
	}
	
	private final Board board;
	private final Random random;
	
	private final int[] playedMoves = new int[BOARD_SIZE];
	private final int[] playedMoveRows = new int[BOARD_SIZE];
	
	private final int[] savedPlayedMoves = new int[BOARD_SIZE];
	private final int[] savedPlayedMoveRows = new int[BOARD_SIZE];
	
	private int playedMoveAmount;
	private int undoneMoveAmount;
	
	private final int[] moveScores = new int[BOARD_WIDTH];
	private final int[] moveWeights = new int[BOARD_WIDTH];
	
	private int metricsEvaluationAmount;
	private int metricsNodeEvaluationAmount;
	private long metricsEvaluationTime;
	private boolean recordingMetrics;
	
	public Kite() {
		this.board = new Board();
		this.random = new Random();
		
		OpeningBoardScoreCaches.ensureDefaultIsLoaded(null);
		
		board.evaluate(Integer.MIN_VALUE, Integer.MAX_VALUE, playedMoves);
	}
	
	@Override
	public String compactBoardAnalysisString(boolean ansiColored) {
		return board.toString(false, false, false, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public String fancyBoardAnalysisString(boolean ansiColored) {
		return board.toString(false, true, true, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public String boardAnalysisString(boolean ansiColored) {
		return board.toString(false, true, false, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public String boardMovesString() {
		if(playedMoveAmount == 0) return EMPTY_MOVES_STRING;
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < playedMoveAmount; i++) {
			
			int move = playedMoves[i];
			char moveCharacter = (char) (SMALLEST_MOVE_CHARACTER + move);
			
			stringBuilder.append(moveCharacter);
		}
		
		return stringBuilder.toString();
	}
	
	@Override
	public String compactBoardString(boolean ansiColored) {
		return board.toString(true, false, false, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public String fancyBoardString(boolean ansiColored) {
		return board.toString(true, true, true, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public String boardString(boolean ansiColored) {
		return board.toString(true, true, false, ansiColored, playedMoveAmount, playedMoves);
	}
	
	@Override
	public int cellColumnHeight(int cellColumnIndex) {
		if(cellColumnIndex < 1 || cellColumnIndex > BOARD_WIDTH) {
			
			String message = String.format("cellColumnIndex must be between 1 and 7 (inclusive) but got: %s", cellColumnIndex);
			throw new IndexOutOfBoundsException(message);
		}
		
		cellColumnIndex--;
		
		return board.cellColumnHeight(cellColumnIndex);
	}
	
	@Override
	public boolean cellOccupied(int cellX, int cellY) {
		if(cellX < 0 || cellX >= BOARD_WIDTH) {
			
			String message = String.format("cellX must be between 0 and 6 (inclusive) but got: %s", cellX);
			throw new IllegalArgumentException(message);
		}
		
		if(cellY < 0 || cellY >= BOARD_HEIGHT) {
			
			String message = String.format("cellY must be between 0 and 5 (inclusive) but got: %s", cellY);
			throw new IllegalArgumentException(message);
		}
		
		return board.cellFilled(cellX, cellY);
	}
	
	@Override
	public BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		if(cellX < 0 || cellX >= BOARD_WIDTH) {
			
			String message = String.format("cellX must be between 0 and 6 (inclusive) but got: %s", cellX);
			throw new IllegalArgumentException(message);
		}
		
		if(cellY < 0 || cellY >= BOARD_HEIGHT) {
			
			String message = String.format("cellY must be between 0 and 5 (inclusive) but got: %s", cellY);
			throw new IllegalArgumentException(message);
		}
		
		return board.cellPlayerColor(cellX, cellY);
	}
	
	@Override
	public BoardPlayerColor activePlayerColor() {
		boolean redAtTurn = (playedMoveAmount & 1) == 0;
		return redAtTurn ? BoardPlayerColor.RED : BoardPlayerColor.YELLOW;
	}
	
	@Override
	public BoardLine[] winLines() {
		return board.winningPlayerLines();
	}
	
	@Override
	public boolean canRedoMove() {
		return undoneMoveAmount != 0;
	}
	
	@Override
	public boolean canUndoMove() {
		return playedMoveAmount != 0;
	}
	
	@Override
	public boolean canPlayMove() {
		return board.canPlayMove();
	}
	
	@Override
	public BoardOutcome gameOutcome() {
		return board.outcome();
	}
	
	@Override
	public boolean gameOver() {
		return board.over();
	}
	
	@Override
	public boolean boardEmpty() {
		return playedMoveAmount == 0;
	}
	
	@Override
	public int lastMoveRow() {
		if(playedMoveAmount == 0) {
			
			throw new IllegalStateException("No move has been played yet!");
		}
		
		return playedMoveRows[playedMoveAmount - 1];
	}
	
	@Override
	public int lastMove() {
		if(playedMoveAmount == 0) {
			
			throw new IllegalStateException("No move has been played yet!");
		}
		
		return playedMoves[playedMoveAmount - 1] + 1;
	}
	
	@Override
	public int playedMoveRow(int moveIndex) {
		int n = playedMoveAmount + undoneMoveAmount;
		if(n == 0) {
			
			throw new IllegalStateException("No move has been played or undone yet!");
		}
		
		if(moveIndex < 0 || moveIndex >= n) {
			
			String message = String.format("moveIndex must be between 0 and %s (inclusive) but got: %s", n - 1, moveIndex);
			throw new IllegalArgumentException(message);
		}
		
		return playedMoveRows[moveIndex];
	}
	
	@Override
	public int playedMove(int moveIndex) {
		int n = playedMoveAmount + undoneMoveAmount;
		if(n == 0) {
			
			throw new IllegalStateException("No move has been played or undone yet!");
		}
		
		if(moveIndex < 0 || moveIndex >= n) {
			
			String message = String.format("moveIndex must be between 0 and %s (inclusive) but got: %s", n - 1, moveIndex);
			throw new IllegalArgumentException(message);
		}
		
		return playedMoves[moveIndex] + 1;
	}
	
	@Override
	public int legalMoveAmount() {
		return board.legalMoveAmount();
	}
	
	@Override
	public int undoneMoveAmount() {
		return undoneMoveAmount;
	}
	
	@Override
	public int playedMoveAmount() {
		return playedMoveAmount;
	}
	
	@Override
	public KiteApi startRecordingPerformanceMetrics() {
		board.resetEvaluationMetrics();
		
		recordingMetrics = true;
		
		return null;
	}
	
	@Override
	public KiteApi stopRecordingPerformanceMetrics() {
		if(!recordingMetrics) {
			
			throw new IllegalStateException("Cannot stop the recording of performance metrics if the recording has not started yet!");
		}
		
		metricsEvaluationAmount += board.getEvaluationAmount();
		metricsNodeEvaluationAmount += board.getNodeEvaluationAmount();
		metricsEvaluationTime += board.getEvaluationTime();
		
		recordingMetrics = false;
		
		return null;
	}
	
	@Override
	public int printAndResetPerformanceMetrics() {
		String s1;
		String s2;
		String s3;
		String s4;
		
		if(metricsEvaluationAmount == 0) {
			
			s1 = METRICS_STRING_MISSING_VALUE_STRING;
			s2 = METRICS_STRING_MISSING_VALUE_STRING;
			
		} else {
			
			double averageTime = (double) metricsEvaluationTime / metricsEvaluationAmount;
			double averageAmount = (double) metricsNodeEvaluationAmount / metricsEvaluationAmount;
			
			s1 = TimeUtil.formatDuration(averageTime);
			s2 = String.format(Locale.ROOT, "%,.2f", averageAmount);
		}
		
		if(metricsEvaluationTime == 0 || metricsNodeEvaluationAmount == 0) {
			
			s3 = METRICS_STRING_MISSING_VALUE_STRING;
			s4 = "";
			
		} else {
			
			double throughput = (double) metricsNodeEvaluationAmount / metricsEvaluationTime;
			throughput *= METRICS_THROUGHPUT_CONVERSION_FACTOR;
			
			s3 = String.format(Locale.ROOT, "%,.2f", throughput);
			s4 = " Mn/s";
		}
		
		boolean noAnsiCodes = AnsiUtil.areAnsiCodesDisabled() || System.console() == null;
		
		String pattern = noAnsiCodes ? METRICS_STRING_PATTERN : COLORED_METRICS_STRING_PATTERN;
		String message = String.format(Locale.ROOT, pattern, metricsEvaluationAmount, s1, s2, s3, s4);
		System.out.println(message);
		
		int n = metricsNodeEvaluationAmount;
		
		metricsEvaluationAmount = 0;
		metricsNodeEvaluationAmount = 0;
		metricsEvaluationTime = 0;
		
		return n;
	}
	
	@Override
	public int resetPerformanceMetrics() {
		int n = metricsNodeEvaluationAmount;
		
		metricsEvaluationAmount = 0;
		metricsNodeEvaluationAmount = 0;
		metricsEvaluationTime = 0;
		
		return n;
	}
	
	@Override
	public boolean isRecordingMetrics() {
		return recordingMetrics;
	}
	
	@Override
	public int skilledMove(SkillLevel skillLevel) {
		boolean perfect = skillLevel == SkillLevel.PERFECT;
		
		if(perfect) return optimalMove();
		if(skillLevel == SkillLevel.RANDOM) return randomMove();
		if(skillLevel == SkillLevel.ADAPTIVE) return adaptiveMove();
		
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int immediateWinMoveScore;
		int immediateLossMoveScore;
		
		if(playedMoveAmount >= MINIMAL_NO_LOSS_PLAYED_MOVE_AMOUNT) {
			
			immediateWinMoveScore = Integer.MIN_VALUE;
			immediateLossMoveScore = Integer.MIN_VALUE;
			
		} else {
			
			immediateWinMoveScore = BoardScore.win(playedMoveAmount + 1);
			immediateLossMoveScore = -BoardScore.maximal(playedMoveAmount + 1);
		}
		
		int maximalScoreLoss = skillLevel.getMaximalEvaluationLoss();
		
		int openingKnowledgeDepth = skillLevel.getOpeningKnowledgeDepth();
		boolean openingKnowledgeApplies = playedMoveAmount < openingKnowledgeDepth;
		if(openingKnowledgeApplies) {
			
			int f = openingKnowledgeDepth - playedMoveAmount + 1;
			maximalScoreLoss /= f;
		}
		
		int optimalMoveScore = Integer.MIN_VALUE;
		int theoreticallyWorstScoreLoss = BoardScore.maximalScoreLoss(playedMoveAmount);
		
		maximalScoreLoss = maximalScoreLoss * theoreticallyWorstScoreLoss / MAXIMAL_MOVE_SCORE_LOSS;
		
		int minimalScore = Integer.MIN_VALUE + 2;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = board.moveLegalWhileGameNotOver(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, minimalScore - 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves) : Integer.MIN_VALUE;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore > optimalMoveScore) {
				
				optimalMoveScore = moveScore;
				minimalScore = optimalMoveScore - maximalScoreLoss;
			}
		}
		
		int totalWeight = 0;
		
		Arrays.fill(moveWeights, 0);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore >= minimalScore) {
				
				if(moveScore == immediateWinMoveScore) {
					
					int p = skillLevel.getImmediateWinNoticeProbability();
					if(random.randomInteger(LARGEST_PROBABILITY) < p) {
						
						moveWeights[moveColumnIndex] = NOTICED_WINNING_MOVE_WEIGHT;
						totalWeight += NOTICED_WINNING_MOVE_WEIGHT;
						
						continue;
					}
				}
				
				if(moveScore == immediateLossMoveScore) {
					
					int p = skillLevel.getImmediateLossNoticeProbability();
					if(random.randomInteger(LARGEST_PROBABILITY) < p) continue;
				}
				
				int weight = moveScore - minimalScore + 1;
				weight *= weight * weight;
				
				moveWeights[moveColumnIndex] = weight;
				totalWeight += weight;
			}
		}
		
		boolean uniformDistribution = false;
		if(totalWeight == 0) {
			
			uniformDistribution = true;
			
			for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
				
				int moveScore = moveScores[moveColumnIndex];
				if(moveScore >= minimalScore) totalWeight++;
			}
		}
		
		int weightIndex = random.randomInteger(totalWeight);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore < minimalScore) continue;
			
			if(uniformDistribution) {
				
				if(weightIndex == 0) return moveColumnIndex + 1;
				weightIndex--;
				
			} else {
				
				int weight = moveWeights[moveColumnIndex];
				
				if(weightIndex < weight) return moveColumnIndex + 1;
				weightIndex -= weight;
			}
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	private int adaptiveMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int bestAbsoluteMoveScore = Integer.MAX_VALUE - 1;
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegalWhileGameNotOver(moveColumnIndex)) continue;
			
			int moveScore = board.evaluateMove(moveColumnIndex, -bestAbsoluteMoveScore - 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves);
			if(moveScore < 0) moveScore = -moveScore;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore < bestAbsoluteMoveScore) {
				
				bestAbsoluteMoveScore = moveScore;
				n = 1;
				
				continue;
			}
			
			if(moveScore == bestAbsoluteMoveScore) n++;
		}
		
		int index = random.randomInteger(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegalWhileGameNotOver(moveColumnIndex)) continue;
			
			int absoluteMoveScore = moveScores[moveColumnIndex];
			if(absoluteMoveScore != bestAbsoluteMoveScore) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	@Override
	public int optimalMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int optimalMoveScore = Integer.MIN_VALUE + 2;
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = board.moveLegalWhileGameNotOver(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, optimalMoveScore - 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves) : Integer.MIN_VALUE;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore > optimalMoveScore) {
				
				optimalMoveScore = moveScore;
				n = 1;
				
			} else if(moveScore == optimalMoveScore) {
				
				n++;
			}
		}
		
		int index = random.randomInteger(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore != optimalMoveScore) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	@Override
	public int randomMove() {
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int n = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegalWhileGameNotOver(moveColumnIndex)) n++;
		}
		
		int index = random.randomInteger(n);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(!board.moveLegalWhileGameNotOver(moveColumnIndex)) continue;
			
			if(index == 0) return moveColumnIndex + 1;
			index--;
		}
		
		// impossible to reach
		return INVALID_MOVE_COLUMN_INDEX;
	}
	
	@Override
	public MoveAnalysis analyseMove(int moveColumnIndex) {
		if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
			
			String message = String.format("moveColumnIndex must be between 1 and 7 (inclusive) but got: %s", moveColumnIndex);
			throw new IndexOutOfBoundsException(message);
		}
		
		moveColumnIndex--;
		
		if(!board.moveLegal(moveColumnIndex)) {
			
			moveColumnIndex++;
			
			String message = String.format(Locale.ROOT, "Cannot evaluate illegal move: '%,d'", moveColumnIndex);
			throw new IllegalMoveException(moveColumnIndex, message);
		}
		
		return board.analyseMove(moveColumnIndex, playedMoveAmount, playedMoves);
	}
	
	@Override
	public GameAnalysis[] analyseGame(GameAnalysis[] playerGameAnalyses) {
		board.evaluateGamePerformanceOfBothPlayers(playerGameAnalyses, playedMoveAmount, playedMoves);
		
		return playerGameAnalyses;
	}
	
	@Override
	public GameAnalysis[] analyseGame() {
		GameAnalysis[] gameAnalyses = new GameAnalysis[GAME_PLAYER_AMOUNT];
		
		board.evaluateGamePerformanceOfBothPlayers(gameAnalyses, playedMoveAmount, playedMoves);
		
		return gameAnalyses;
	}
	
	@Override
	public GameAnalysis analyseGame(BoardPlayerColor playerColor) {
		return board.evaluateGamePerformanceOfPlayer(playerColor, playedMoveAmount, playedMoves);
	}
	
	@Override
	public int[] evaluateAllMoves(int[] moveScores) {
		if(board.over()) {
			
			Arrays.fill(moveScores, Integer.MIN_VALUE);
			return moveScores;
		}
		
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegalWhileGameNotOver(x)) moveScores[x] = board.evaluateMove(x, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves);
			else moveScores[x] = Integer.MIN_VALUE;
		}
		
		return moveScores;
	}
	
	@Override
	public int[] evaluateAllMoves() {
		int[] moveScores = new int[BOARD_WIDTH];
		
		if(board.over()) {
			
			Arrays.fill(moveScores, Integer.MIN_VALUE);
			return moveScores;
		}
		
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegalWhileGameNotOver(x)) moveScores[x] = board.evaluateMove(x, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves);
			else moveScores[x] = Integer.MIN_VALUE;
		}
		
		return moveScores;
	}
	
	@Override
	public int evaluateMove(int moveColumnIndex) {
		if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
			
			String message = String.format("moveColumnIndex must be between 1 and 7 (inclusive) but got: %s", moveColumnIndex);
			throw new IndexOutOfBoundsException(message);
		}
		
		moveColumnIndex--;
		
		if(!board.moveLegal(moveColumnIndex)) {
			
			moveColumnIndex++;
			
			String message = String.format(Locale.ROOT, "Cannot evaluate illegal move: '%,d'", moveColumnIndex);
			throw new IllegalMoveException(moveColumnIndex, message);
		}
		
		return board.evaluateMove(moveColumnIndex, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, playedMoveAmount, playedMoves);
	}
	
	@Override
	public int evaluateBoard() {
		return board.evaluate(Integer.MIN_VALUE, Integer.MAX_VALUE, playedMoves);
	}
	
	@Override
	public KiteApi seedRandomness() {
		random.setRandomSeed();
		
		return null;
	}
	
	@Override
	public KiteApi seedRandomness(long seed) {
		random.setSeed(seed);
		
		return null;
	}
	
	@Override
	public boolean moveLegal(int moveColumnIndex) {
		if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
			
			String message = String.format("moveColumnIndex must be between 1 and 7 (inclusive) but got: %s", moveColumnIndex);
			throw new IndexOutOfBoundsException(message);
		}
		
		moveColumnIndex--;
		
		return board.moveLegal(moveColumnIndex);
	}
	
	@Override
	public KiteApi clearRedoHistory() {
		undoneMoveAmount = 0;
		
		return null;
	}
	
	@Override
	public KiteApi playMoves(String moveColumnIndicesString) {
		int n = moveColumnIndicesString.length();
		for(int i = 0; i < n; i++) {
			
			char c = moveColumnIndicesString.charAt(i);
			if(c < MOVE_COLUMN_INDEX_SMALLEST_CHARACTER || c > MOVE_COLUMN_INDEX_LARGEST_CHARACTER) {
				
				String message = String.format("moveColumnIndicesString must contain indices between 1 and 7 (inclusive) but found: %c", c);
				throw new IndexOutOfBoundsException(message);
			}
		}
		
		int savedUndoneMoveAmount = undoneMoveAmount;
		
		for(int i = 0; i < n; i++) {
			
			int moveColumnIndex = moveColumnIndicesString.charAt(i) - MOVE_COLUMN_INDEX_SMALLEST_CHARACTER;
			
			if(!board.moveLegal(moveColumnIndex)) {
				
				while(i > 0) {
					
					playedMoveAmount--;
					int lastMove = playedMoves[playedMoveAmount];
					
					board.undoMove(lastMove);
					
					playedMoves[playedMoveAmount] = savedPlayedMoves[playedMoveAmount];
					playedMoveRows[playedMoveAmount] = savedPlayedMoveRows[playedMoveAmount];
					
					i--;
				}
				
				undoneMoveAmount = savedUndoneMoveAmount;
				
				moveColumnIndex++;
				
				String message = String.format(Locale.ROOT, "moveColumnIndicesString contains an illegal move: '%,d'", moveColumnIndex);
				throw new IllegalMoveException(moveColumnIndex, message);
			}
			
			savedPlayedMoves[playedMoveAmount] = playedMoves[playedMoveAmount];
			savedPlayedMoveRows[playedMoveAmount] = playedMoveRows[playedMoveAmount];
			
			board.playMove(moveColumnIndex);
			
			if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
				
				undoneMoveAmount--;
				
			} else {
				
				int row = board.cellColumnHeight(moveColumnIndex);
				
				playedMoves[playedMoveAmount] = moveColumnIndex;
				playedMoveRows[playedMoveAmount] = row;
				undoneMoveAmount = 0;
			}
			
			playedMoveAmount++;
		}
		
		return null;
	}
	
	@Override
	public KiteApi playMoves(int... moveColumnIndices) {
		for(int moveColumnIndex : moveColumnIndices) {
			
			if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
				
				String message = String.format(Locale.ROOT, "moveColumnIndices must contain indices between 1 and 7 (inclusive) but found: %,d", moveColumnIndex);
				throw new IndexOutOfBoundsException(message);
			}
		}
		
		int savedUndoneMoveAmount = undoneMoveAmount;
		
		int n = moveColumnIndices.length;
		for(int i = 0; i < n; i++) {
			
			int moveColumnIndex = moveColumnIndices[i] - 1;
			
			if(!board.moveLegal(moveColumnIndex)) {
				
				while(i > 0) {
					
					playedMoveAmount--;
					int lastMove = playedMoves[playedMoveAmount];
					
					board.undoMove(lastMove);
					
					playedMoves[playedMoveAmount] = savedPlayedMoves[playedMoveAmount];
					playedMoveRows[playedMoveAmount] = savedPlayedMoveRows[playedMoveAmount];
					
					i--;
				}
				
				undoneMoveAmount = savedUndoneMoveAmount;
				
				moveColumnIndex++;
				
				String message = String.format(Locale.ROOT, "moveColumnIndices contains an illegal move: '%,d'", moveColumnIndex);
				throw new IllegalMoveException(moveColumnIndex, message);
			}
			
			savedPlayedMoves[playedMoveAmount] = playedMoves[playedMoveAmount];
			savedPlayedMoveRows[playedMoveAmount] = playedMoveRows[playedMoveAmount];
			
			board.playMove(moveColumnIndex);
			
			if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
				
				undoneMoveAmount--;
				
			} else {
				
				int row = board.cellColumnHeight(moveColumnIndex);
				
				playedMoves[playedMoveAmount] = moveColumnIndex;
				playedMoveRows[playedMoveAmount] = row;
				undoneMoveAmount = 0;
			}
			
			playedMoveAmount++;
		}
		
		return null;
	}
	
	@Override
	public KiteApi playMove(int moveColumnIndex) {
		if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
			
			String message = String.format("moveColumnIndex must be between 1 and 7 (inclusive) but got: %s", moveColumnIndex);
			throw new IndexOutOfBoundsException(message);
		}
		
		moveColumnIndex--;
		
		if(!board.moveLegal(moveColumnIndex)) {
			
			moveColumnIndex++;
			
			String message = String.format(Locale.ROOT, "Cannot play illegal move: '%,d'", moveColumnIndex);
			throw new IllegalMoveException(moveColumnIndex, message);
		}
		
		board.playMove(moveColumnIndex);
		
		if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
			
			undoneMoveAmount--;
			
		} else {
			
			int row = board.cellColumnHeight(moveColumnIndex);
			
			playedMoves[playedMoveAmount] = moveColumnIndex;
			playedMoveRows[playedMoveAmount] = row;
			undoneMoveAmount = 0;
		}
		
		playedMoveAmount++;
		
		return null;
	}
	
	@Override
	public KiteApi redoMoves(int moveAmount) {
		if(moveAmount <= 0) {
			
			String message = String.format("Number of moves to redo has to be positive but got: %s", moveAmount);
			throw new IllegalArgumentException(message);
		}
		
		if(moveAmount > undoneMoveAmount) {
			
			throw new IllegalStateException("That many moves have not been undone yet!");
		}
		
		int n = playedMoveAmount + moveAmount;
		while(playedMoveAmount < n) {
			
			int moveColumnIndex = playedMoves[playedMoveAmount];
			board.playMove(moveColumnIndex);
			
			playedMoveAmount++;
		}
		
		undoneMoveAmount -= moveAmount;
		if(undoneMoveAmount < 0) undoneMoveAmount = 0;
		
		return null;
	}
	
	@Override
	public int redoMove() {
		if(undoneMoveAmount == 0) {
			
			throw new IllegalStateException("No move has been undone yet!");
		}
		
		int moveColumnIndex = playedMoves[playedMoveAmount];
		board.playMove(moveColumnIndex);
		
		if(undoneMoveAmount != 0) undoneMoveAmount--;
		playedMoveAmount++;
		
		return moveColumnIndex + 1;
	}
	
	@Override
	public KiteApi undoMoves(int moveAmount) {
		if(moveAmount <= 0) {
			
			String message = String.format("Number of moves to undo has to be positive but got: %s", moveAmount);
			throw new IllegalArgumentException(message);
		}
		
		if(moveAmount > playedMoveAmount) {
			
			throw new IllegalStateException("That many moves have not been played yet!");
		}
		
		int n = playedMoveAmount - moveAmount;
		
		while(playedMoveAmount > n) {
			
			playedMoveAmount--;
			int lastMove = playedMoves[playedMoveAmount];
			
			board.undoMove(lastMove);
		}
		
		undoneMoveAmount += moveAmount;
		
		return null;
	}
	
	@Override
	public int undoMove() {
		if(playedMoveAmount == 0) {
			
			throw new IllegalStateException("No move has been played yet!");
		}
		
		playedMoveAmount--;
		undoneMoveAmount++;
		
		int lastMove = playedMoves[playedMoveAmount];
		board.undoMove(lastMove);
		
		return lastMove + 1;
	}
	
	@Override
	public KiteApi setupBoard(String moveColumnIndicesString) {
		int l = moveColumnIndicesString.length();
		for(int i = 0; i < l; i++) {
			
			char c = moveColumnIndicesString.charAt(i);
			if(c < MOVE_COLUMN_INDEX_SMALLEST_CHARACTER || c > MOVE_COLUMN_INDEX_LARGEST_CHARACTER) {
				
				String message = String.format("moveColumnIndicesString must contain indices between 1 and 7 (inclusive) but found: %c", c);
				throw new IndexOutOfBoundsException(message);
			}
		}
		
		int savedPlayedMoveAmount = playedMoveAmount;
		int savedUndoneMoveAmount = undoneMoveAmount;
		
		int m = savedPlayedMoveAmount + savedUndoneMoveAmount;
		for(int i = 0; i < m; i++) {
			
			savedPlayedMoves[i] = playedMoves[i];
			savedPlayedMoveRows[i] = playedMoveRows[i];
		}
		
		int reusedMoveAmount = 0;
		for(int i = 0; i < l; i++) {
			
			int moveColumnIndex = moveColumnIndicesString.charAt(i) - MOVE_COLUMN_INDEX_SMALLEST_CHARACTER;
			
			if(i == playedMoveAmount) {
				
				if(!board.moveLegal(moveColumnIndex)) {
					
					while(playedMoveAmount > reusedMoveAmount) {
						
						playedMoveAmount--;
						int lastMove = playedMoves[playedMoveAmount];
						
						board.undoMove(lastMove);
					}
					
					while(playedMoveAmount < savedPlayedMoveAmount) {
						
						int x = savedPlayedMoves[playedMoveAmount];
						int y = savedPlayedMoveRows[playedMoveAmount];
						
						playedMoves[playedMoveAmount] = x;
						playedMoveRows[playedMoveAmount] = y;
						
						board.playMove(x);
						playedMoveAmount++;
					}
					
					int index = playedMoveAmount;
					while(index < m) {
						
						playedMoves[index] = savedPlayedMoves[index];
						playedMoveRows[index] = savedPlayedMoveRows[index];
						
						index++;
					}
					
					undoneMoveAmount = savedUndoneMoveAmount;
					
					String message = String.format("moveColumnIndicesString is not a legal Connect Four game: \"%s\"", moveColumnIndicesString);
					throw new IllegalMoveException(moveColumnIndex + 1, message);
				}
				
				board.playMove(moveColumnIndex);
				
				if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
					
					undoneMoveAmount--;
					
				} else {
					
					int row = board.cellColumnHeight(moveColumnIndex);
					
					playedMoves[playedMoveAmount] = moveColumnIndex;
					playedMoveRows[playedMoveAmount] = row;
					undoneMoveAmount = 0;
				}
				
				playedMoveAmount++;
				continue;
			}
			
			int otherMove = playedMoves[i];
			if(otherMove == moveColumnIndex) {
				
				reusedMoveAmount++;
				continue;
			}
			
			undoneMoveAmount += playedMoveAmount - i;
			
			while(playedMoveAmount > i) {
				
				playedMoveAmount--;
				int lastMove = playedMoves[playedMoveAmount];
				
				board.undoMove(lastMove);
			}
			
			if(!board.moveLegal(moveColumnIndex)) {
				
				while(playedMoveAmount < savedPlayedMoveAmount) {
					
					int x = savedPlayedMoves[playedMoveAmount];
					int y = savedPlayedMoveRows[playedMoveAmount];
					
					playedMoves[playedMoveAmount] = x;
					playedMoveRows[playedMoveAmount] = y;
					
					board.playMove(x);
					playedMoveAmount++;
				}
				
				int index = playedMoveAmount;
				while(index < m) {
					
					playedMoves[index] = savedPlayedMoves[index];
					playedMoveRows[index] = savedPlayedMoveRows[index];
					
					index++;
				}
				
				undoneMoveAmount = savedUndoneMoveAmount;
				
				String message = String.format("moveColumnIndicesString is not a legal Connect Four game: \"%s\"", moveColumnIndicesString);
				throw new IllegalMoveException(moveColumnIndex + 1, message);
			}
			
			board.playMove(moveColumnIndex);
			
			if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
				
				undoneMoveAmount--;
				
			} else {
				
				int row = board.cellColumnHeight(moveColumnIndex);
				
				playedMoves[playedMoveAmount] = moveColumnIndex;
				playedMoveRows[playedMoveAmount] = row;
				undoneMoveAmount = 0;
			}
			
			playedMoveAmount++;
		}
		
		undoneMoveAmount += playedMoveAmount - l;
		
		while(playedMoveAmount > l) {
			
			playedMoveAmount--;
			int lastMove = playedMoves[playedMoveAmount];
			
			board.undoMove(lastMove);
		}
		
		return null;
	}
	
	@Override
	public KiteApi setupBoard(int... moveColumnIndices) {
		for(int moveColumnIndex : moveColumnIndices) {
			
			if(moveColumnIndex < 1 || moveColumnIndex > BOARD_WIDTH) {
				
				String message = String.format(Locale.ROOT, "moveColumnIndices must contain indices between 1 and 7 (inclusive) but found: %,d", moveColumnIndex);
				throw new IndexOutOfBoundsException(message);
			}
		}
		
		int savedPlayedMoveAmount = playedMoveAmount;
		int savedUndoneMoveAmount = undoneMoveAmount;
		
		int m = savedPlayedMoveAmount + savedUndoneMoveAmount;
		for(int i = 0; i < m; i++) {
			
			savedPlayedMoves[i] = playedMoves[i];
			savedPlayedMoveRows[i] = playedMoveRows[i];
		}
		
		int reusedMoveAmount = 0;
		
		int l = moveColumnIndices.length;
		for(int i = 0; i < l; i++) {
			
			int moveColumnIndex = moveColumnIndices[i] - 1;
			
			if(i == playedMoveAmount) {
				
				if(!board.moveLegal(moveColumnIndex)) {
					
					while(playedMoveAmount > reusedMoveAmount) {
						
						playedMoveAmount--;
						int lastMove = playedMoves[playedMoveAmount];
						
						board.undoMove(lastMove);
					}
					
					while(playedMoveAmount < savedPlayedMoveAmount) {
						
						int x = savedPlayedMoves[playedMoveAmount];
						int y = savedPlayedMoveRows[playedMoveAmount];
						
						playedMoves[playedMoveAmount] = x;
						playedMoveRows[playedMoveAmount] = y;
						
						board.playMove(x);
						playedMoveAmount++;
					}
					
					int index = playedMoveAmount;
					while(index < m) {
						
						playedMoves[index] = savedPlayedMoves[index];
						playedMoveRows[index] = savedPlayedMoveRows[index];
						
						index++;
					}
					
					undoneMoveAmount = savedUndoneMoveAmount;
					
					StringBuilder stringBuilder = new StringBuilder();
					for(int move : moveColumnIndices) stringBuilder.append((char) ('0' + move));
					
					String message = String.format("moveColumnIndices is not a legal Connect Four game: \"%s\"", stringBuilder);
					throw new IllegalMoveException(moveColumnIndex + 1, message);
				}
				
				board.playMove(moveColumnIndex);
				
				if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
					
					undoneMoveAmount--;
					
				} else {
					
					int row = board.cellColumnHeight(moveColumnIndex);
					
					playedMoves[playedMoveAmount] = moveColumnIndex;
					playedMoveRows[playedMoveAmount] = row;
					undoneMoveAmount = 0;
				}
				
				playedMoveAmount++;
				continue;
			}
			
			int otherMove = playedMoves[i];
			if(otherMove == moveColumnIndex) {
				
				reusedMoveAmount++;
				continue;
			}
			
			undoneMoveAmount += playedMoveAmount - i;
			
			while(playedMoveAmount > i) {
				
				playedMoveAmount--;
				int lastMove = playedMoves[playedMoveAmount];
				
				board.undoMove(lastMove);
			}
			
			if(!board.moveLegal(moveColumnIndex)) {
				
				while(playedMoveAmount < savedPlayedMoveAmount) {
					
					int x = savedPlayedMoves[playedMoveAmount];
					int y = savedPlayedMoveRows[playedMoveAmount];
					
					playedMoves[playedMoveAmount] = x;
					playedMoveRows[playedMoveAmount] = y;
					
					board.playMove(x);
					playedMoveAmount++;
				}
				
				int index = playedMoveAmount;
				while(index < m) {
					
					playedMoves[index] = savedPlayedMoves[index];
					playedMoveRows[index] = savedPlayedMoveRows[index];
					
					index++;
				}
				
				undoneMoveAmount = savedUndoneMoveAmount;
				
				StringBuilder stringBuilder = new StringBuilder();
				for(int move : moveColumnIndices) stringBuilder.append((char) ('0' + move));
				
				String message = String.format("moveColumnIndices is not a legal Connect Four game: \"%s\"", stringBuilder);
				throw new IllegalMoveException(moveColumnIndex + 1, message);
			}
			
			board.playMove(moveColumnIndex);
			
			if(undoneMoveAmount != 0 && playedMoves[playedMoveAmount] == moveColumnIndex) {
				
				undoneMoveAmount--;
				
			} else {
				
				int row = board.cellColumnHeight(moveColumnIndex);
				
				playedMoves[playedMoveAmount] = moveColumnIndex;
				playedMoveRows[playedMoveAmount] = row;
				undoneMoveAmount = 0;
			}
			
			playedMoveAmount++;
		}
		
		undoneMoveAmount += playedMoveAmount - l;
		
		while(playedMoveAmount > l) {
			
			playedMoveAmount--;
			int lastMove = playedMoves[playedMoveAmount];
			
			board.undoMove(lastMove);
		}
		
		return null;
	}
	
	@Override
	public KiteApi clearBoard() {
		undoneMoveAmount += playedMoveAmount;
		
		while(playedMoveAmount != 0) {
			
			playedMoveAmount--;
			int lastMove = playedMoves[playedMoveAmount];
			
			board.undoMove(lastMove);
		}
		
		return null;
	}
	
	public static boolean runBenchmark(boolean printMetrics) {
		if(printMetrics) {
			
			boolean noAnsiCodes = AnsiUtil.areAnsiCodesDisabled() || System.console() == null;
			
			String s1 = net.kite.api.Kite.getVersion();
			String s2 = net.kite.api.Kite.getBuildCommit();
			
			String s = String.format("%s (%s)", s1, s2);
			if(!noAnsiCodes) s = AnsiUtil.brightMagentaAnsi(s);
			
			System.out.printf("Kite version    : %s%n", s);
			
			s = System.getProperty("java.version");
			if(!noAnsiCodes) s = AnsiUtil.brightMagentaAnsi(s);
			
			System.out.printf("Java version    : %s%n", s);
			
			s = System.getProperty("java.vendor");
			if(!noAnsiCodes) s = AnsiUtil.brightMagentaAnsi(s);
			
			System.out.printf("Java vendor     : %s%n", s);
			
			s = System.getProperty("os.name");
			if(!noAnsiCodes) s = AnsiUtil.brightMagentaAnsi(s);
			
			System.out.printf("OS name         : %s%n", s);
			
			s = System.getProperty("os.arch");
			if(!noAnsiCodes) s = AnsiUtil.brightMagentaAnsi(s);
			
			System.out.printf("OS architecture : %s%n%n", s);
			
			for(int i = 1; i < 4; i++) {
				
				if(i != 1) {
					
					if(noAnsiCodes) System.out.println();
					else AnsiUtil.moveCursorToBeginningOfLine();
				}
				
				String message = String.format("Performing warmup... (%s/3)", i);
				System.out.print(message);
				
				if(!runAndRecordBenchmark(false)) return false;
			}
			
			System.out.println();
		}
		
		boolean successful = runAndRecordBenchmark(printMetrics);
		
		if(printMetrics) {
			
			String message = successful ? "\nBenchmark completed successfully!" : "\nBenchmark was not completed successfully!";
			System.out.println(message);
		}
		
		return successful;
	}
	
	private static boolean runAndRecordBenchmark(boolean recordMetrics) {
		net.kite.api.Kite solver = net.kite.api.Kite.createInstance();
		
		boolean successful = true;
		for(String resourcePath : BENCHMARK_RESOURCE_PATHS) {
			
			InputStream inputStream = Kite.class.getResourceAsStream(resourcePath);
			if(inputStream == null) {
				
				System.err.printf("Benchmark cannot be found in resources: %s%n", resourcePath);
				return false;
			}
			
			try(
					inputStream;
					Reader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
			) {
				
				String benchmarkDisplayName = bufferedReader.readLine();
				
				while(true) {
					
					String line = bufferedReader.readLine();
					if(line == null) break;
					
					int index = line.indexOf(BENCHMARK_ENTRY_SEPARATOR_CHARACTER);
					
					String s1 = line.substring(0, index);
					String s2 = line.substring(index + 1);
					
					int score = Integer.parseInt(s2);
					
					solver.setupBoard(s1);
					if(recordMetrics) solver.startRecordingPerformanceMetrics();
					int s = solver.evaluateBoard();
					if(recordMetrics) solver.stopRecordingPerformanceMetrics();
					
					if(s != score) {
						
						String errorMessage = String.format("Wrong evaluation: position=%s, evaluation=%s (should be %s)", s1, s, score);
						System.err.println(errorMessage);
						
						if(recordMetrics) successful = false;
						else return false;
					}
				}
				
				if(recordMetrics) {
					
					System.out.println();
					System.out.println(benchmarkDisplayName);
					
					solver.printAndResetPerformanceMetrics();
				}
				
			} catch(IOException exception) {
				
				String errorMessage = String.format("An exception occurred while loading benchmark from resources: %s", exception);
				System.err.println(errorMessage);
				
				return false;
			}
		}
		
		return successful;
	}
	
}
