package net.kite.internal;

import net.kite.api.KiteApi;
import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.board.player.color.BoardPlayerColor;
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
	private static final int GAME_PLAYER_AMOUNT = 2;
	
	private static final int[] ORDERED_MOVE_COLUMN_INDICES = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int INVALID_MOVE_COLUMN_INDEX = 0;
	
	private static final int MAXIMAL_MOVE_SCORE_LOSS = 36;
	
	private static final int MOVE_COLUMN_INDEX_SMALLEST_CHARACTER = 49;
	
	private static final double METRICS_THROUGHPUT_CONVERSION_FACTOR = 1000.0;
	private static final String METRICS_STRING_PATTERN = "positions evaluated      : %d\naverage evaluation time  : %s\naverage node evaluations : %.2f\nnode throughput          : %.2f Mn/s";
	private static final String COLORED_METRICS_STRING_PATTERN;
	
	private static final String[] BENCHMARK_RESOURCE_PATHS = new String[] {
			"/benchmarks/endgame_easy.txt",
			"/benchmarks/midgame_easy.txt",
			"/benchmarks/midgame_medium.txt",
			"/benchmarks/opening_easy.txt",
			"/benchmarks/opening_medium.txt",
			"/benchmarks/opening_hard.txt"
	};
	
	private static final char BENCHMARK_ENTRY_SEPARATOR_CHARACTER = ' ';
	
	static {
		synchronized(AnsiUtil.class) {
			
			boolean disabled = AnsiUtil.areAnsiCodesDisabled();
			if(disabled) AnsiUtil.enableAnsiCodes();
			
			COLORED_METRICS_STRING_PATTERN =
					AnsiUtil.cyanAnsi("positions evaluated      : ") +
					AnsiUtil.brightYellowAnsi("%d") +
					AnsiUtil.cyanAnsi("\naverage evaluation time  : ") +
					AnsiUtil.brightYellowAnsi("%s") +
					AnsiUtil.cyanAnsi("\naverage node evaluations : ") +
					AnsiUtil.brightYellowAnsi("%.2f") +
					AnsiUtil.cyanAnsi("\nnode throughput          : ") +
					AnsiUtil.brightYellowAnsi("%.2f") +
					AnsiUtil.cyanAnsi(" Mn/s");
			
			if(disabled) AnsiUtil.disableAnsiCodes();
		}
	}
	
	private final Board board;
	private final Random random;
	
	private final int[] playedMoves = new int[BOARD_SIZE];
	private final int[] playedMoveRows = new int[BOARD_SIZE];
	
	private final int[] moveScores = new int[BOARD_WIDTH];
	
	private int playedMoveAmount;
	private int undoneMoveAmount;
	
	private int metricsEvaluationAmount;
	private int metricsNodeEvaluationAmount;
	private long metricsEvaluationTime;
	private long metricsRecordingStartTime;
	
	public Kite() {
		this.board = new Board();
		this.random = new Random();
		
		OpeningBoardScoreCaches.ensureDefaultIsLoaded(null);
		
		board.evaluate();
	}
	
	@Override
	public String compactBoardAnalysisString(boolean ansiColored) {
		return board.toString(false, false, false, ansiColored);
	}
	
	@Override
	public String fancyBoardAnalysisString(boolean ansiColored) {
		return board.toString(false, true, true, ansiColored);
	}
	
	@Override
	public String boardAnalysisString(boolean ansiColored) {
		return board.toString(false, true, false, ansiColored);
	}
	
	@Override
	public String boardMovesString() {
		return board.movesString();
	}
	
	@Override
	public String compactBoardString(boolean ansiColored) {
		return board.toString(true, false, false, ansiColored);
	}
	
	@Override
	public String fancyBoardString(boolean ansiColored) {
		return board.toString(true, true, true, ansiColored);
	}
	
	@Override
	public String boardString(boolean ansiColored) {
		return board.toString(true, true, false, ansiColored);
	}
	
	@Override
	public int cellColumnHeight(int cellColumnIndex) {
		cellColumnIndex--;
		
		return board.cellColumnHeight(cellColumnIndex);
	}
	
	@Override
	public boolean cellOccupied(int cellX, int cellY) {
		return board.cellFilled(cellX, cellY);
	}
	
	@Override
	public BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
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
	public int undoneMoveAmount() {
		return undoneMoveAmount;
	}
	
	@Override
	public boolean boardEmpty() {
		return playedMoveAmount == 0;
	}
	
	@Override
	public int lastMoveRow() {
		return playedMoveRows[playedMoveAmount - 1];
	}
	
	@Override
	public int lastMove() {
		return playedMoves[playedMoveAmount - 1] + 1;
	}
	
	@Override
	public int playedMoveRow(int moveIndex) {
		return playedMoveRows[moveIndex];
	}
	
	@Override
	public int playedMove(int moveIndex) {
		return playedMoves[moveIndex] + 1;
	}
	
	@Override
	public int playedMoveAmount() {
		return playedMoveAmount;
	}
	
	@Override
	public KiteApi startRecordingPerformanceMetrics() {
		board.resetEvaluationMetrics();
		
		metricsRecordingStartTime = System.nanoTime();
		
		return null;
	}
	
	@Override
	public KiteApi stopRecordingPerformanceMetrics() {
		long endTime = System.nanoTime();
		
		metricsEvaluationAmount += board.getEvaluationAmount();
		metricsNodeEvaluationAmount += board.getNodeEvaluationAmount();
		metricsEvaluationTime += endTime - metricsRecordingStartTime;
		
		return null;
	}
	
	@Override
	public KiteApi printAndResetPerformanceMetrics() {
		double averageTime = 0;
		double averageAmount = 0;
		double throughput = 0;
		
		if(metricsEvaluationAmount != 0) {
			
			averageTime = (double) metricsEvaluationTime / metricsEvaluationAmount;
			averageAmount = (double) metricsNodeEvaluationAmount / metricsEvaluationAmount;
		}
		
		if(metricsEvaluationTime != 0) {
			
			throughput = (double) metricsNodeEvaluationAmount / metricsEvaluationTime;
			throughput *= METRICS_THROUGHPUT_CONVERSION_FACTOR;
		}
		
		String s = TimeUtil.formatDuration(averageTime);
		
		boolean noAnsiCodes = AnsiUtil.areAnsiCodesDisabled() || System.console() == null;
		
		String pattern = noAnsiCodes ? METRICS_STRING_PATTERN : COLORED_METRICS_STRING_PATTERN;
		String message = String.format(Locale.ROOT, pattern, metricsEvaluationAmount, s, averageAmount, throughput);
		System.out.println(message);
		
		metricsEvaluationAmount = 0;
		metricsNodeEvaluationAmount = 0;
		metricsEvaluationTime = 0;
		
		return null;
	}
	
	@Override
	public int skilledMove(SkillLevel skillLevel) {
		boolean perfect = skillLevel == SkillLevel.PERFECT || skillLevel == SkillLevel.SUPER_GRANDMASTER;
		
		if(perfect) return optimalMove();
		if(skillLevel == SkillLevel.RANDOM) return randomMove();
		if(skillLevel == SkillLevel.ADAPTIVE) return adaptiveMove();
		
		if(board.over()) return INVALID_MOVE_COLUMN_INDEX;
		
		int optimalMoveScore = Integer.MIN_VALUE;
		int theoreticallyWorstScoreLoss = BoardScore.maximalScoreLoss(playedMoveAmount);
		
		int maximalScoreLoss = skillLevel.getMaximalScoreLoss();
		maximalScoreLoss = maximalScoreLoss * theoreticallyWorstScoreLoss / MAXIMAL_MOVE_SCORE_LOSS;
		
		int minimalScore = Integer.MIN_VALUE + 2;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = board.moveLegalWhileGameNotOver(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, minimalScore - 1) : Integer.MIN_VALUE;
			
			moveScores[moveColumnIndex] = moveScore;
			
			if(moveScore > optimalMoveScore) {
				
				optimalMoveScore = moveScore;
				minimalScore = optimalMoveScore - maximalScoreLoss;
			}
		}
		
		int totalWeight = 0;
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore >= minimalScore) {
				
				int weight = moveScore - minimalScore + 1;
				weight *= weight * weight;
				
				totalWeight += weight;
			}
		}
		
		int weightIndex = random.randomInteger(totalWeight);
		
		for(int moveColumnIndex : ORDERED_MOVE_COLUMN_INDICES) {
			
			int moveScore = moveScores[moveColumnIndex];
			if(moveScore < minimalScore) continue;
			
			int weight = moveScore - minimalScore + 1;
			weight *= weight * weight;
			
			if(weightIndex < weight) return moveColumnIndex + 1;
			weightIndex -= weight;
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
			
			int moveScore = board.evaluateMove(moveColumnIndex, -bestAbsoluteMoveScore - 1);
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
			
			int moveScore = board.moveLegalWhileGameNotOver(moveColumnIndex) ? board.evaluateMove(moveColumnIndex, optimalMoveScore - 1) : Integer.MIN_VALUE;
			
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
	public float[] evaluatePlayerPerformances(float[] playerRatingApproximations) {
		board.approximateEloRatingOfBothPlayer(playerRatingApproximations);
		
		return playerRatingApproximations;
	}
	
	@Override
	public float[] evaluatePlayerPerformances() {
		float[] eloBuffer = new float[GAME_PLAYER_AMOUNT];
		
		board.approximateEloRatingOfBothPlayer(eloBuffer);
		
		return eloBuffer;
	}
	
	@Override
	public float evaluatePlayerPerformance(BoardPlayerColor playerColor) {
		return board.approximateEloRatingOfPlayer(playerColor);
	}
	
	@Override
	public int[] evaluateAllMoves(int[] moveScores) {
		if(board.over()) {
			
			Arrays.fill(moveScores, Integer.MIN_VALUE);
			return moveScores;
		}
		
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(board.moveLegalWhileGameNotOver(x)) moveScores[x] = board.evaluateMove(x);
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
			
			if(board.moveLegalWhileGameNotOver(x)) moveScores[x] = board.evaluateMove(x);
			else moveScores[x] = Integer.MIN_VALUE;
		}
		
		return moveScores;
	}
	
	@Override
	public int evaluateMove(int moveColumnIndex) {
		moveColumnIndex--;
		
		return board.evaluateMove(moveColumnIndex);
	}
	
	@Override
	public int evaluateBoard() {
		return board.evaluate();
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
		moveColumnIndex--;
		
		return board.moveLegal(moveColumnIndex);
	}
	
	@Override
	public KiteApi playMoves(String moveColumnIndices) {
		int n = moveColumnIndices.length();
		for(int i = 0; i < n; i++) {
			
			int moveColumnIndex = moveColumnIndices.charAt(i) - MOVE_COLUMN_INDEX_SMALLEST_CHARACTER;
			
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
			
			moveColumnIndex--;
			
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
		moveColumnIndex--;
		
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
		int moveColumnIndex = playedMoves[playedMoveAmount];
		board.playMove(moveColumnIndex);
		
		if(undoneMoveAmount != 0) undoneMoveAmount--;
		playedMoveAmount++;
		
		return moveColumnIndex + 1;
	}
	
	@Override
	public KiteApi undoMoves(int moveAmount) {
		int n = playedMoveAmount - moveAmount;
		
		while(playedMoveAmount > n) {
			
			board.undoMove();
			playedMoveAmount--;
		}
		
		undoneMoveAmount += moveAmount;
		
		return null;
	}
	
	@Override
	public int undoMove() {
		board.undoMove();
		
		playedMoveAmount--;
		undoneMoveAmount++;
		
		return playedMoves[playedMoveAmount] + 1;
	}
	
	@Override
	public KiteApi setupBoard(String moveColumnIndicesString) {
		int l = moveColumnIndicesString.length();
		for(int i = 0; i < l; i++) {
			
			int moveColumnIndex = moveColumnIndicesString.charAt(i) - MOVE_COLUMN_INDEX_SMALLEST_CHARACTER;
			
			if(i == playedMoveAmount) {
				
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
			
			int x = playedMoves[i];
			if(x == moveColumnIndex) {
				
				continue;
			}
			
			undoneMoveAmount += playedMoveAmount - i;
			
			while(playedMoveAmount > i) {
				
				board.undoMove();
				playedMoveAmount--;
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
			
			board.undoMove();
			playedMoveAmount--;
		}
		
		return null;
	}
	
	@Override
	public KiteApi setupBoard(int... moveColumnIndices) {
		int l = moveColumnIndices.length;
		for(int i = 0; i < l; i++) {
			
			int moveColumnIndex = moveColumnIndices[i] - 1;
			
			if(i == playedMoveAmount) {
				
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
			
			int x = playedMoves[i];
			if(x == moveColumnIndex) {
				
				continue;
			}
			
			undoneMoveAmount += playedMoveAmount - i;
			
			while(playedMoveAmount > i) {
				
				board.undoMove();
				playedMoveAmount--;
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
			
			board.undoMove();
			playedMoveAmount--;
		}
		
		return null;
	}
	
	@Override
	public KiteApi clearBoard() {
		undoneMoveAmount += playedMoveAmount;
		
		while(playedMoveAmount != 0) {
			
			board.undoMove();
			playedMoveAmount--;
		}
		
		return null;
	}
	
	public static boolean runBenchmark(boolean printMetrics) {
		if(printMetrics) {
			
			boolean noAnsiCodes = AnsiUtil.areAnsiCodesDisabled() || System.console() == null;
			
			for(int i = 1; i < 3; i++) {
				
				if(i == 2) {
					
					if(noAnsiCodes) System.out.println();
					else AnsiUtil.moveCursorToBeginningOfLine();
				}
				
				String message = String.format("Performing warmup... (%s/2)", i);
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
