package net.kite.internal.board;

import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.board.bit.Bitboard;
import net.kite.internal.board.bit.Bitboards;
import net.kite.internal.board.score.BoardScore;
import net.kite.internal.board.score.cache.BoardScoreCache;
import net.kite.internal.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.internal.util.ansi.AnsiUtil;

public final class Board {
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private static final int FULL_CELL_AMOUNT = 42;
	
	private static final int UP_BITBOARD_DIRECTION = 1;
	private static final int RIGHT_BITBOARD_DIRECTION = 8;
	private static final int UP_RIGHT_BITBOARD_DIRECTION = 9;
	private static final int DOWN_RIGHT_BITBOARD_DIRECTION = 7;
	
	private static final int[] BITBOARD_CONNECTION_DIRECTIONS = new int[] {
			RIGHT_BITBOARD_DIRECTION,
			DOWN_RIGHT_BITBOARD_DIRECTION,
			UP_RIGHT_BITBOARD_DIRECTION,
			UP_BITBOARD_DIRECTION
	};
	
	private static final int[] BITBOARD_CONNECTION_DIRECTION_XS = new int[] {
			 1,
			 1,
			 1,
			 0
	};
	
	private static final int[] BITBOARD_CONNECTION_DIRECTION_YS = new int[] {
			 0,
			-1,
			 1,
			 1
	};
	
	private static final int[] ORDERED_MOVE_COLUMN_INDICES = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT = 229;
	private static final int MOVE_SCORE_COLUMN_FORK_WEIGHT = 282;
	private static final int MOVE_SCORE_IMMEDIATE_THREAT_WEIGHT = 463;
	private static final int MOVE_SCORE_SOON_THREAT_WEIGHT = 602;
	
	private static final int[] RED_MOVE_CELL_SCORES = new int[] {
			 13,  17,  33, 148,  43, 459,   0,   0,
			  9,  94,  34, 153, 146, 258,   0,   0,
			130, 167, 470, 356, 184, 221,   0,   0,
			366, 273, 356, 276, 395, 471,   0,   0,
			130, 167, 470, 356, 184, 221,   0,   0,
			  9,  94,  34, 153, 146, 258,   0,   0,
			 13,  17,  33, 148,  43, 459
	};
	
	private static final int[] YELLOW_MOVE_CELL_SCORES = new int[] {
			 20, 134,  38, 172, 114, 403,   0,   0,
			  0, 144, 137, 214, 118, 408,   0,   0,
			 96, 295, 204, 405, 159, 398,   0,   0,
			134, 421, 459, 436, 435, 445,   0,   0,
			 96, 295, 204, 405, 159, 398,   0,   0,
			  0, 144, 137, 214, 118, 408,   0,   0,
			 20, 134,  38, 172, 114, 403
	};
	
	private static final int INFINITE_MOVE_SCORE = 1000000;
	private static final int MISSING_MOVE_SCORE = Integer.MIN_VALUE;
	
	private static final int BITBOARD_CONNECTION_OPPORTUNITY_LENGTH = 3;
	
	private static final int MIRRORED_BITBOARD_SHIFT_AMOUNT = 8;
	
	private static final int LOGARITHMIC_BITBOARD_LENGTH = 3;
	private static final int LARGEST_BITBOARD_Y = 7;
	
	private static final int LARGEST_MOVE_CELL_X = 6;
	private static final int LARGEST_MOVE_CELL_Y = 5;
	
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	private static final long HASH_MIX_FIRST_MAGIC = 0xFF51AFD7ED558CCDL;
	private static final long HASH_MIX_SECOND_MAGIC = 0xC4CEB9FE1A85EC53L;
	
	private static final long EMPTY_MIXED_HASH = 0x2373BFB0BD385EEAL;
	
	private static final int HASH_MIX_SHIFT_AMOUNT = 33;
	
	private static final int COLUMN_HASH_BASE = 3;
	
	private static final int MINIMAL_CHILD_CACHE_LOOKUP_DEPTH = 16;
	
	private static final int BITBOARD_HEIGHT = 8;
	
	private static final float ELO_APPROXIMATION_FIRST_COEFFICIENT = 53.167f;
	private static final float ELO_APPROXIMATION_SECOND_COEFFICIENT = 0.000661f;
	private static final float ELO_APPROXIMATION_THIRD_COEFFICIENT = -414.261f;
	private static final float PERFECT_ELO_APPROXIMATION = 3000.0f;
	
	private static final int ELO_APPROXIMATION_RED_MIN_MOVE_AMOUNT = 1;
	private static final int ELO_APPROXIMATION_YELLOW_MIN_MOVE_AMOUNT = 2;
	
	private static final int MAXIMAL_LINE_AMOUNT = 4;
	
	private static final int MOVES_LENGTH = 294;
	
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	private static final String TO_STRING_EMPTY_CELL_String = ".";
	private static final String TO_STRING_MOVES_PREFIX_STRING = "moves: ";
	private static final String TO_STRING_EMPTY_BOARD_MOVES_STRING = "none";
	private static final String TO_STRING_MOVE_SCORE_SEPARATOR_STRING = ", ";
	private static final String TO_STRING_REMAINING_MOVE_AMOUNT_STRING = "\nmoves left (optimal play): ";
	private static final String TO_STRING_GAME_OVER_MOVE_SCORES_STRING = "";
	private static final String TO_STRING_OUTCOME_PREFIX_STRING = "\noutcome: ";
	private static final String TO_STRING_ILLEGAL_MOVE_STRING = "    ";
	
	private static final String[] TO_STRING_MOVE_STRING_PATTERNS = {
			   null,
			"  %s ",
			 " %s ",
			  " %s"
	};
	
	private static final String TO_STRING_COMPACT_MOVE_SCORES_PREFIX_STRING = "\nmove scores: ";
	private static final String TO_STRING_COMPACT_GAME_OVER_MOVE_SCORES_STRING = "-, -, -, -, -, -, -";
	private static final String COLORED_TO_STRING_COMPACT_GAME_OVER_MOVE_SCORES_STRING;
	private static final String TO_STRING_COMPACT_ILLEGAL_MOVE_STRING = "-";
	
	private static final String TO_STRING_BOARD_PREFIX_STRING = "+---+---+---+---+---+---+---+\n";
	private static final String TO_STRING_BOARD_SUFFIX_STRING = "\n+---+---+---+---+---+---+---+\n| 1 | 2 | 3 | 4 | 5 | 6 | 7 |\n+---+---+---+---+---+---+---+";
	private static final String TO_STRING_BOARD_ROW_SEPARATOR_STRING = "\n+---+---+---+---+---+---+---+\n";
	private static final String TO_STRING_BOARD_ROW_PREFIX_STRING = "| ";
	private static final String TO_STRING_BOARD_ROW_SUFFIX_STRING = " |";
	private static final String TO_STRING_BOARD_COLUMN_SEPARATOR_STRING = " | ";
	private static final String TO_STRING_BOARD_EMPTY_CELL_STRING = " ";
	
	private static final String TO_STRING_FANCY_BOARD_PREFIX_STRING = "┌───┬───┬───┬───┬───┬───┬───┐\n";
	private static final String TO_STRING_FANCY_BOARD_SUFFIX_STRING = "\n├───┼───┼───┼───┼───┼───┼───┤\n│ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │\n└───┴───┴───┴───┴───┴───┴───┘";
	private static final String TO_STRING_FANCY_BOARD_ROW_SEPARATOR_STRING = "\n├───┼───┼───┼───┼───┼───┼───┤\n";
	private static final String TO_STRING_FANCY_BOARD_ROW_PREFIX_STRING = "│ ";
	private static final String TO_STRING_FANCY_BOARD_ROW_SUFFIX_STRING = " │";
	private static final String TO_STRING_FANCY_BOARD_COLUMN_SEPARATOR_STRING = " │ ";
	
	private static final String EMPTY_MOVES_STRING = "";
	
	static {
		synchronized(AnsiUtil.class) {
			
			boolean disabled = AnsiUtil.areAnsiCodesDisabled();
			if(disabled) AnsiUtil.enableAnsiCodes();
			
			String s1 = AnsiUtil.boldBrightCyanAnsi("-");
			String s2 = ", ";
			
			COLORED_TO_STRING_COMPACT_GAME_OVER_MOVE_SCORES_STRING = s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1 + s2 + s1;
			
			if(disabled) AnsiUtil.disableAnsiCodes();
		}
	}
	
	private int filledCellAmount;
	private int evenParityCellColumnAmount = WIDTH;
	
	private int evaluationAmount;
	private int nodeEvaluationAmount;
	
	private long bitboard = Bitboards.EMPTY_CEILING;
	private long activeBitboard = Bitboards.EMPTY;
	private long maskBitboard = Bitboards.EMPTY;
	private long ceilingBitboard = Bitboards.EMPTY_CEILING;
	
	private long mixedHash = EMPTY_MIXED_HASH;
	
	private final int[] moves;
	private final int[] moveScores;
	
	private final int[] playedMoves;
	private final int[] undoneMoves;
	
	private final BoardScoreCache scoreCache;
	
	private final BoardLine[] lines = new BoardLine[MAXIMAL_LINE_AMOUNT];
	
	private final boolean[][] winCells = new boolean[WIDTH][HEIGHT];
	
	public Board() {
		this.scoreCache = new BoardScoreCache();
		
		this.moves = new int[MOVES_LENGTH];
		this.moveScores = new int[MOVES_LENGTH];
		this.playedMoves = new int[FULL_CELL_AMOUNT];
		this.undoneMoves = new int[FULL_CELL_AMOUNT];
	}
	
	@SuppressWarnings("DataFlowIssue")
	public String toString(boolean boardOnly, boolean spaciousBoard, boolean fancyBoard, boolean colored) {
		BoardOutcome outcome = null;
		
		boolean anyWinCells = false;
		if(colored) {
			
			outcome = outcome();
			if(outcome.isWin()) {
				
				BoardLine[] lines = winningPlayerLines();
				anyWinCells = true;
				
				for(BoardLine line : lines) {
					
					int n = line.getLength();
					int x = line.getStartCellX();
					int y = line.getStartCellY();
					int dx = line.getDirectionX();
					int dy = line.getDirectionY();
					
					for(int i = 0; i < n; i++) {
						
						winCells[x][y] = true;
						
						x += dx;
						y += dy;
					}
				}
			}
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		if(spaciousBoard) {
			
			String s = fancyBoard ? TO_STRING_FANCY_BOARD_PREFIX_STRING : TO_STRING_BOARD_PREFIX_STRING;
			if(colored) s = AnsiUtil.darkGrayAnsi(s);
			stringBuilder.append(s);
		}
		
		int lastMoveX = Integer.MIN_VALUE;
		int lastMoveY = 0;
		
		if(filledCellAmount != 0) {
			
			lastMoveX = playedMoves[filledCellAmount - 1];
			lastMoveY = cellColumnHeight(lastMoveX) - 1;
		}
		
		for(int y = HEIGHT - 1; y >= 0; y--) {
			
			if(spaciousBoard) {
				
				String s = fancyBoard ? TO_STRING_FANCY_BOARD_ROW_PREFIX_STRING : TO_STRING_BOARD_ROW_PREFIX_STRING;
				if(colored) s = AnsiUtil.darkGrayAnsi(s);
				stringBuilder.append(s);
			}
			
			for(int x = 0; x < WIDTH; x++) {
				
				if(spaciousBoard && x != 0) {
					
					String s = fancyBoard ? TO_STRING_FANCY_BOARD_COLUMN_SEPARATOR_STRING : TO_STRING_BOARD_COLUMN_SEPARATOR_STRING;
					if(colored) s = AnsiUtil.darkGrayAnsi(s);
					stringBuilder.append(s);
				}
				
				BoardPlayerColor cellPlayerColor = cellPlayerColor(x, y);
				String s;
				
				if(cellPlayerColor == null) {
					
					if(spaciousBoard) s = TO_STRING_BOARD_EMPTY_CELL_STRING;
					else s = colored ? AnsiUtil.darkGrayAnsi(TO_STRING_EMPTY_CELL_String) : TO_STRING_EMPTY_CELL_String;
					
				} else {
					
					s = String.valueOf(cellPlayerColor.getCharacter());
					boolean highlightCell;
					if(anyWinCells) {
						
						highlightCell = winCells[x][y];
						
					} else {
						
						highlightCell = x == lastMoveX && y == lastMoveY;
					}
					
					if(cellPlayerColor == BoardPlayerColor.RED) {
						
						s = highlightCell ? AnsiUtil.boldBrightRedBackgroundAnsi(s) : colored ? AnsiUtil.boldBrightRedAnsi(s) : s;
						
					} else {
						
						s = highlightCell ? AnsiUtil.boldBrightYellowBackgroundAnsi(s) : colored ? AnsiUtil.boldBrightYellowAnsi(s) : s;
					}
				}
				
				stringBuilder.append(s);
			}
			
			if(spaciousBoard) {
				
				String s = fancyBoard ? TO_STRING_FANCY_BOARD_ROW_SUFFIX_STRING : TO_STRING_BOARD_ROW_SUFFIX_STRING;
				if(colored) s = AnsiUtil.darkGrayAnsi(s);
				stringBuilder.append(s);
				
				if(y != 0) {
					
					s = fancyBoard ? TO_STRING_FANCY_BOARD_ROW_SEPARATOR_STRING : TO_STRING_BOARD_ROW_SEPARATOR_STRING;
					if(colored) s = AnsiUtil.darkGrayAnsi(s);
					stringBuilder.append(s);
				}
				
			} else {
				
				if(y != 0) stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
			}
		}
		
		if(spaciousBoard) {
			
			String s = fancyBoard ? TO_STRING_FANCY_BOARD_SUFFIX_STRING : TO_STRING_BOARD_SUFFIX_STRING;
			if(colored) s = AnsiUtil.darkGrayAnsi(s);
			stringBuilder.append(s);
		}
		
		if(anyWinCells) {
			
			for(int x = 0; x < WIDTH; x++) {
				for(int y = 0; y < HEIGHT; y++) {
					
					winCells[x][y] = false;
				}
			}
		}
		
		if(boardOnly) return stringBuilder.toString();
		
		if(!spaciousBoard) {
			
			stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
			stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
			stringBuilder.append(TO_STRING_MOVES_PREFIX_STRING);
			
			if(filledCellAmount == 0) {
				
				stringBuilder.append(TO_STRING_EMPTY_BOARD_MOVES_STRING);
				
			} else {
				
				for(int i = 0; i < filledCellAmount; i++) {
					
					int move = playedMoves[i];
					char moveCharacter = (char) (SMALLEST_MOVE_CHARACTER + move);
					
					stringBuilder.append(moveCharacter);
				}
			}
		}
		
		stringBuilder.append(spaciousBoard ? TO_STRING_CELL_ROW_SEPARATOR_STRING : TO_STRING_COMPACT_MOVE_SCORES_PREFIX_STRING);
		
		if(outcome == null) outcome = outcome();
		boolean gameNotOver = outcome == BoardOutcome.UNDECIDED;
		
		int remainingMoves;
		
		if(gameNotOver) {
			
			int boardScore = Integer.MIN_VALUE;
			for(int x : ORDERED_MOVE_COLUMN_INDICES) {
				
				if(moveLegalWhileGameNotOver(x)) {
					
					int moveScore = evaluateMove(x);
					if(moveScore > boardScore) boardScore = moveScore;
					
					moveScores[x] = moveScore;
				}
			}
			
			remainingMoves = net.kite.api.board.score.BoardScore.gameOverInTotalMoves(boardScore, filledCellAmount);
			
			for(int x = 0; x < WIDTH; x++) {
				
				if(!spaciousBoard && x != 0) stringBuilder.append(TO_STRING_MOVE_SCORE_SEPARATOR_STRING);
				
				if(moveLegalWhileGameNotOver(x)) {
					
					int score = moveScores[x];
					String s = net.kite.api.board.score.BoardScore.formatScoreCompactly(score);
					
					if(spaciousBoard) {
						
						int l = s.length();
						s = TO_STRING_MOVE_STRING_PATTERNS[l].formatted(s);
					}
					
					if(colored) {
						
						if(score == 0) s = AnsiUtil.boldBrightYellowAnsi(s);
						else if(score < 0) s = AnsiUtil.boldBrightRedAnsi(s);
						else s = AnsiUtil.boldBrightGreenAnsi(s);
					}
					
					stringBuilder.append(s);
					
				} else {
					
					String s = spaciousBoard ? TO_STRING_ILLEGAL_MOVE_STRING : colored ? AnsiUtil.boldBrightCyanAnsi(TO_STRING_COMPACT_ILLEGAL_MOVE_STRING) : TO_STRING_COMPACT_ILLEGAL_MOVE_STRING;
					stringBuilder.append(s);
				}
			}
			
		} else {
			
			remainingMoves = 0;
			String s = spaciousBoard ? TO_STRING_GAME_OVER_MOVE_SCORES_STRING : colored ? COLORED_TO_STRING_COMPACT_GAME_OVER_MOVE_SCORES_STRING : TO_STRING_COMPACT_GAME_OVER_MOVE_SCORES_STRING;
			stringBuilder.append(s);
		}
		
		if(spaciousBoard) {
			
			stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
			stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
			stringBuilder.append(TO_STRING_MOVES_PREFIX_STRING);
			
			if(filledCellAmount == 0) {
				
				stringBuilder.append(TO_STRING_EMPTY_BOARD_MOVES_STRING);
				
			} else {
				
				for(int i = 0; i < filledCellAmount; i++) {
					
					int move = playedMoves[i];
					char moveCharacter = (char) (SMALLEST_MOVE_CHARACTER + move);
					
					stringBuilder.append(moveCharacter);
				}
			}
		}
		
		stringBuilder.append(TO_STRING_REMAINING_MOVE_AMOUNT_STRING);
		stringBuilder.append(remainingMoves);
		
		String s = outcome.getName();
		if(colored) {
			
			if(outcome == BoardOutcome.UNDECIDED) s = AnsiUtil.boldBrightCyanAnsi(s);
			else if(outcome == BoardOutcome.RED_WIN) s = AnsiUtil.boldBrightRedAnsi(s);
			else if(outcome == BoardOutcome.YELLOW_WIN) s = AnsiUtil.boldBrightYellowAnsi(s);
			else if(outcome == BoardOutcome.DRAW) s = AnsiUtil.boldBrightCyanAnsi(s);
		}
		
		stringBuilder.append(TO_STRING_OUTCOME_PREFIX_STRING);
		stringBuilder.append(s);
		
		return stringBuilder.toString();
	}
	
	public BoardLine[] winningPlayerLines() {
		long board = activeBitboard ^ maskBitboard;
		if(!bitboardContainsConnection(board)) {
			
			throw new IllegalStateException("winningPlayerLines should not be called if the game has not ended in a win (yet)!");
		}
		
		int lineAmount = 0;
		
		long wonPlayerCells = maskBitboard ^ activeBitboard;
		
		int directionAmount = BITBOARD_CONNECTION_DIRECTIONS.length;
		for(int i = 0; i < directionAmount; i++) {
			
			int direction = BITBOARD_CONNECTION_DIRECTIONS[i];
			
			long b = winAnchorCellsBitboard(wonPlayerCells, direction);
			if(b == 0) continue;
			
			int l = Long.bitCount(b) + BITBOARD_CONNECTION_OPPORTUNITY_LENGTH;
			
			int dx = BITBOARD_CONNECTION_DIRECTION_XS[i];
			int dy = BITBOARD_CONNECTION_DIRECTION_YS[i];
			
			int p = Bitboard.lastCellPosition(b);
			
			int ex = p >>> LOGARITHMIC_BITBOARD_LENGTH;
			int ey = p & LARGEST_BITBOARD_Y;
			
			int n = l - 1;
			
			int sx = ex - dx * n;
			int sy = ey - dy * n;
			
			lines[lineAmount] = new BoardLine(sx, sy, ex, ey, dx, dy, l);
			lineAmount++;
		}
		
		BoardLine[] winningLines = new BoardLine[lineAmount];
		
		System.arraycopy(lines, 0, winningLines, 0, lineAmount);
		
		return winningLines;
	}
	
	public void approximateEloRatingOfBothPlayer(float[] eloBuffer) {
		int n = filledCellAmount;
		while(filledCellAmount != 0) {
			
			undoMove();
			
			undoneMoves[filledCellAmount] = playedMoves[filledCellAmount];
		}
		
		int redTotalScoreLoss = 0;
		int yellowTotalScoreLoss = 0;
		
		int m1 = 0;
		int m2 = 0;
		
		int previousBoardScore = evaluate();
		
		boolean redAtTurn = true;
		
		for(int i = 0; i < n; i++) {
			
			int move = undoneMoves[i];
			
			playMove(move);
			
			int boardScore = evaluate();
			if(redAtTurn) {
				
				redTotalScoreLoss += previousBoardScore + boardScore;
				m1++;
				
			} else {
				
				yellowTotalScoreLoss += previousBoardScore + boardScore;
				m2++;
			}
			
			redAtTurn = !redAtTurn;
			previousBoardScore = boardScore;
		}
		
		float f1;
		float f2;
		
		if(m1 == 0) f1 = PERFECT_ELO_APPROXIMATION;
		else {
			
			float averageScoreLoss = (float) redTotalScoreLoss / m1;
			f1 = approximateElo(averageScoreLoss);
		}
		
		if(m2 == 0) f2 = PERFECT_ELO_APPROXIMATION;
		else {
			
			float averageScoreLoss = (float) yellowTotalScoreLoss / m2;
			f2 = approximateElo(averageScoreLoss);
		}
		
		eloBuffer[0] = f1;
		eloBuffer[1] = f2;
	}
	
	public float approximateEloRatingOfPlayer(BoardPlayerColor playerColor) {
		int n = filledCellAmount;
		int minMoveAmount = playerColor == BoardPlayerColor.RED ? ELO_APPROXIMATION_RED_MIN_MOVE_AMOUNT : ELO_APPROXIMATION_YELLOW_MIN_MOVE_AMOUNT;
		
		if(n < minMoveAmount) {
			
			return PERFECT_ELO_APPROXIMATION;
		}
		
		while(filledCellAmount != 0) {
			
			undoMove();
			
			undoneMoves[filledCellAmount] = playedMoves[filledCellAmount];
		}
		
		int totalScoreLoss = 0;
		int m = 0;
		
		int previousBoardScore = evaluate();
		
		boolean playerAtTurn = playerColor == BoardPlayerColor.RED;
		
		for(int i = 0; i < n; i++) {
			
			int move = undoneMoves[i];
			
			playMove(move);
			
			int boardScore = evaluate();
			if(playerAtTurn) {
				
				totalScoreLoss += previousBoardScore + boardScore;
				m++;
			}
			
			playerAtTurn = !playerAtTurn;
			previousBoardScore = boardScore;
		}
		
		float averageScoreLoss = (float) totalScoreLoss / m;
		return approximateElo(averageScoreLoss);
	}
	
	public String movesString() {
		if(filledCellAmount == 0) return EMPTY_MOVES_STRING;
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < filledCellAmount; i++) {
			
			int move = playedMoves[i];
			char moveCharacter = (char) (SMALLEST_MOVE_CHARACTER + move);
			
			stringBuilder.append(moveCharacter);
		}
		
		return stringBuilder.toString();
	}
	
	public long columnHash() {
		long h1 = 0;
		long h2 = 0;
		
		for(int x = 0; x < WIDTH; x++) h1 = partialColumnHash(h1, x);
		for(int x = LARGEST_MOVE_CELL_X; x >= 0; x--) h2 = partialColumnHash(h2, x);
		
		if(h2 < h1) h1 = h2;
		return Long.divideUnsigned(h1, COLUMN_HASH_BASE);
	}
	
	private long partialColumnHash(long columnHash, int x) {
		long board = Bitboards.BOTTOM_CELL_BITBOARDS[x];
		
		while((board & maskBitboard) != 0) {
			
			columnHash *= COLUMN_HASH_BASE;
			
			boolean activeCell = (activeBitboard & board) != 0;
			
			if(activeCell) columnHash++;
			else columnHash += 2;
			
			board <<= 1;
		}
		
		columnHash *= COLUMN_HASH_BASE;
		return columnHash;
	}
	
	public int evaluateMove(int moveCellX) {
		playMove(moveCellX);
		
		int score = -evaluate();
		
		undoMove();
		
		return score;
	}
	
	public int evaluateMove(int moveCellX, int minScore) {
		playMove(moveCellX);
		
		int score = -evaluate(-minScore);
		
		undoMove();
		
		return score;
	}
	
	public int evaluate() {
		return evaluate(Integer.MAX_VALUE);
	}
	
	public int evaluate(int maxScore) {
		evaluationAmount++;
		
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) return BoardScore.loss(filledCellAmount);
		
		if(filledCellAmount == FULL_CELL_AMOUNT) return BoardScore.DRAW;
		
		if(activePlayerHasImmediateWin()) {
			
			return BoardScore.win(filledCellAmount + 1);
		}
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this, filledCellAmount);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		int minimalScore = BoardScore.minimal(filledCellAmount);
		int maximalScore = BoardScore.maximal(filledCellAmount);
		
		if(maximalScore > maxScore) maximalScore = maxScore;
		
		if(filledCellAmount > 0) {
			
			undoMove();
			
			int lastMove = playedMoves[filledCellAmount];
			
			openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this, filledCellAmount);
			if(openingBoardScore != Integer.MIN_VALUE) {
				
				int s = -openingBoardScore;
				if(minimalScore < s) minimalScore = s;
			}
			
			playMove(lastMove);
		}
		
		int entryKey = scoreCache.entryKey(mixedHash);
		if(entryKey >= 0) {
			
			int entryMinScore = scoreCache.entryMinimalScore(entryKey);
			int entryMaxScore = scoreCache.entryMaximalScore(entryKey);
			
			if(entryMinScore > minimalScore) minimalScore = entryMinScore;
			if(entryMaxScore < maximalScore) maximalScore = entryMaxScore;
		}
		
		while(minimalScore < maximalScore) {
			
			int score = (minimalScore + maximalScore) >> 1;
			
			int evaluationResult = evaluateWithNoImmediateWin(score, score + 1);
			if(evaluationResult <= score) {
				
				maximalScore = evaluationResult;
				
			} else {
				
				minimalScore = evaluationResult;
			}
		}
		
		return minimalScore;
	}
	
	// only interested in scores in between min and max (excluding min and max tho)
	private int evaluateWithNoImmediateWin(int minimalScore, int maximalScore) {
		nodeEvaluationAmount++;
		
		int minScore = BoardScore.minimal(filledCellAmount);
		int maxScore = BoardScore.maximalWithNoImmediateWin(filledCellAmount);
		
		if(maximalScore > 0) {
			
			long emptyCells = Bitboards.FULL_BOARD ^ maskBitboard;
			
			boolean canNoLongerWin = !bitboardContainsNonVerticalConnection(activeBitboard | emptyCells);
			if(canNoLongerWin) maxScore = 0;
		}
		
		if(evenParityCellColumnAmount == WIDTH) {
			
			long redCells = activeBitboard | (Bitboards.ODD_BOARD_ROWS & (~maskBitboard));
			long yellowCells = Bitboards.FULL_BOARD ^ redCells;
			
			long currentYellowCells = activeBitboard ^ maskBitboard;
			
			if(!canRedWinInClaimEven(redCells, yellowCells, currentYellowCells, maskBitboard)) {
				
				if(maxScore > BoardScore.DRAW) maxScore = BoardScore.DRAW;
				
				long yellowWinningStones = nonVerticalWinCellsBitboard(yellowCells);
				if(yellowWinningStones != 0) {
					
					int y = LARGEST_MOVE_CELL_Y;
					for(long row : Bitboards.DESCENDINGLY_ORDERED_EVEN_BOARD_ROWS) {
						
						if((row & yellowWinningStones) != 0) {
							
							int n = FULL_CELL_AMOUNT - LARGEST_MOVE_CELL_Y + y;
							int score = BoardScore.loss(n);
							if(maxScore > score) maxScore = score;
							
							break;
						}
						
						y -= 2;
					}
				}
			}
		}
		
		if(minScore > minimalScore) minimalScore = minScore;
		if(maxScore < maximalScore) maximalScore = maxScore;
		
		if(minimalScore >= maximalScore) return minimalScore;
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this, filledCellAmount);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		int entryKey = scoreCache.entryKey(mixedHash);
		if(entryKey >= 0) {
			
			int entryMinScore = scoreCache.entryMinimalScore(entryKey);
			int entryMaxScore = scoreCache.entryMaximalScore(entryKey);
			
			if(entryMinScore > minimalScore) minimalScore = entryMinScore;
			if(entryMaxScore < maximalScore) maximalScore = entryMaxScore;
			
			if(minimalScore >= maximalScore) return minimalScore;
		}
		
		if(filledCellAmount > MINIMAL_CHILD_CACHE_LOOKUP_DEPTH) {
			
			int max = Integer.MIN_VALUE;
			
			long movesBitboard = ceilingBitboard & Bitboards.FULL_BOARD;
			while(movesBitboard != 0) {
				
				int movePosition = Long.numberOfTrailingZeros(movesBitboard);
				
				long moveBitboard = 1L << movePosition;
				movesBitboard ^= moveBitboard;
				
				long h1 = (bitboard ^ maskBitboard) + moveBitboard;
				long h2 = Long.reverseBytes(h1) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
				
				if(h2 < h1) h1 = h2;
				
				long mixedHash = mixedHash(h1);
				
				entryKey = scoreCache.entryKey(mixedHash);
				if(entryKey >= 0) {
					
					int entryMinScore = -scoreCache.entryMaximalScore(entryKey);
					int entryMaxScore = -scoreCache.entryMinimalScore(entryKey);
					
					if(entryMinScore > minimalScore) {
						
						minimalScore = entryMinScore;
						if(minimalScore >= maximalScore) return minimalScore;
					}
					
					if(entryMaxScore > max) max = entryMaxScore;
					
				} else {
					
					max = Integer.MAX_VALUE;
				}
			}
			
			if(max < maximalScore) {
				
				maximalScore = max;
				
				if(minimalScore >= maximalScore) return minimalScore;
			}
		}
		
		long opponentThreatsBitboard = bitboardConnectionOpportunities(activeBitboard ^ maskBitboard) & (~maskBitboard);
		long immediateThreats = opponentThreatsBitboard & ceilingBitboard;
		
		if(immediateThreats != Bitboards.EMPTY) {
			
			int p = Long.numberOfTrailingZeros(immediateThreats);
			long b = 1L << p;
			
			immediateThreats ^= b;
			if(immediateThreats != Bitboards.EMPTY) {
				
				return minScore;
			}
			
			int forcedX = p >>> LOGARITHMIC_BITBOARD_LENGTH;
			
			long upperCellBitboard = b << 1;
			if((upperCellBitboard & opponentThreatsBitboard) != 0) {
				
				return minScore;
			}
			
			playMove(forcedX);
			
			int s = -evaluateWithNoImmediateWin(-maximalScore, -minimalScore);
			
			undoMove();
			
			if(s >= maximalScore) {
				
				scoreCache.updateEntry(mixedHash, s, maxScore);
				return s;
			}
			
			if(s > minimalScore) minimalScore = s;
			
			scoreCache.updateEntry(mixedHash, minScore, minimalScore);
			
			return minimalScore;
		}
		
		int movesBaseIndex = filledCellAmount * WIDTH;
		int moveAmount = 0;
		
		long movesBitboard = ceilingBitboard & Bitboards.FULL_BOARD;
		long mirroredBitboard = Long.reverseBytes(bitboard) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
		
		boolean symmetrical = bitboard == mirroredBitboard;
		if(symmetrical) movesBitboard &= Bitboards.SYMMETRY_PRUNE_BITBOARD;
		
		movesBitboard &= ~(opponentThreatsBitboard >>> 1);
		
		int moveIndex = movesBaseIndex;
		
		int bestMoveIndex = 0;
		int bestMoveScore = Integer.MIN_VALUE;
		
		while(movesBitboard != 0) {
			
			int movePosition = Long.numberOfTrailingZeros(movesBitboard);
			int moveCellX = movePosition >>> LOGARITHMIC_BITBOARD_LENGTH;
			
			long moveBitboard = 1L << movePosition;
			movesBitboard ^= moveBitboard;
			
			int moveScore = moveScore(movePosition, moveBitboard, opponentThreatsBitboard);
			if(moveScore == INFINITE_MOVE_SCORE) return maximalScore;
			
			moves[moveIndex] = moveCellX;
			moveScores[moveIndex] = moveScore;
			
			if(moveScore > bestMoveScore) {
				
				bestMoveIndex = moveIndex;
				bestMoveScore = moveScore;
			}
			
			moveAmount++;
			moveIndex++;
		}
		
		if(moveAmount == 0) {
			
			scoreCache.updateEntry(mixedHash, minScore, minScore);
			
			return minimalScore;
		}
		
		int i = 0;
		while(true) {
			
			moveScores[bestMoveIndex] = MISSING_MOVE_SCORE;
			
			int moveCellX = moves[bestMoveIndex];
			
			playMove(moveCellX);
			
			int s = -evaluateWithNoImmediateWin(-maximalScore, -minimalScore);
			
			undoMove();
			
			if(s >= maximalScore) {
				
				scoreCache.updateEntry(mixedHash, s, maxScore);
				return s;
			}
			
			if(s > minimalScore) {
				
				minimalScore = s;
			}
			
			i++;
			if(i == moveAmount) break;
			
			bestMoveIndex = movesBaseIndex;
			bestMoveScore = moveScores[movesBaseIndex];
			
			int startMoveIndex = movesBaseIndex + 1;
			int endMoveIndex = movesBaseIndex + moveAmount;
			
			for(int j = startMoveIndex; j < endMoveIndex; j++) {
				
				int moveScore = moveScores[j];
				if(moveScore > bestMoveScore) {
					
					bestMoveIndex = j;
					bestMoveScore = moveScore;
				}
			}
		}
		
		scoreCache.updateEntry(mixedHash, minScore, minimalScore);
		
		return minimalScore;
	}
	
	private int moveScore(int moveCellPosition, long moveBitboard, long opponentOpenThreats) {
		long board = activeBitboard;
		long ceiling = ceilingBitboard;
		
		board |= moveBitboard;
		ceiling += moveBitboard;
		
		long result = bitboardConnectionOpportunities(board);
		long immediateThreats = result & ceiling;
		if(Long.bitCount(immediateThreats) > 1) return INFINITE_MOVE_SCORE;
		
		immediateThreats &= result >>> 1;
		if(immediateThreats != 0) return INFINITE_MOVE_SCORE;
		
		long mask = maskBitboard;
		mask |= moveBitboard;
		
		result &= ~mask;
		result &= ~(opponentOpenThreats << 1);
		
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		int[] moveCellScores = redAtTurn ? RED_MOVE_CELL_SCORES : YELLOW_MOVE_CELL_SCORES;
		
		int moveScore = moveCellScores[moveCellPosition];
		
		moveScore += Long.bitCount(result) * MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT;
		
		long l1 = result & ceiling;
		
		moveScore += Long.bitCount(l1) * MOVE_SCORE_IMMEDIATE_THREAT_WEIGHT;
		
		ceiling <<= 1;
		long l2 = result & ceiling;
		
		moveScore += Long.bitCount(l2) * MOVE_SCORE_SOON_THREAT_WEIGHT;
		
		result &= result << 1;
		
		moveScore += Long.bitCount(result) * MOVE_SCORE_COLUMN_FORK_WEIGHT;
		
		return moveScore;
	}
	
	private boolean activePlayerHasImmediateWin() {
		long result = bitboardConnectionOpportunities(activeBitboard);
		result &= ceilingBitboard;
		
		return result != 0;
	}
	
	public boolean moveLegalWhileGameNotOver(int moveCellX) {
		long b = Bitboards.COLUMNS[moveCellX];
		b &= ceilingBitboard;
		
		return (b & Bitboards.FULL_BOARD) != 0;
	}
	
	public boolean moveLegal(int moveCellX) {
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) return false;
		
		long b = Bitboards.COLUMNS[moveCellX];
		b &= ceilingBitboard;
		
		return (b & Bitboards.FULL_BOARD) != 0;
	}
	
	public void playMove(int moveCellX) {
		long b = ceilingBitboard & Bitboards.COLUMNS[moveCellX];
		
		boolean wasEven = (b & Bitboards.ODD_BOARD_ROWS) == 0;
		if(wasEven) evenParityCellColumnAmount--;
		else evenParityCellColumnAmount++;
		
		playedMoves[filledCellAmount] = moveCellX;
		filledCellAmount++;
		
		activeBitboard ^= maskBitboard;
		maskBitboard |= b;
		ceilingBitboard += b;
		bitboard = activeBitboard | ceilingBitboard;
		
		long hash = bitboard;
		
		long mirroredBitboard = Long.reverseBytes(hash) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
		if(mirroredBitboard < hash) hash = mirroredBitboard;
		
		mixedHash = mixedHash(hash);
	}
	
	public void undoMove() {
		filledCellAmount--;
		int moveCellX = playedMoves[filledCellAmount];
		
		long b = ceilingBitboard & Bitboards.COLUMNS[moveCellX];
		b >>>= 1;
		
		boolean isEven = (b & Bitboards.ODD_BOARD_ROWS) == 0;
		if(isEven) evenParityCellColumnAmount++;
		else evenParityCellColumnAmount--;
		
		maskBitboard ^= b;
		activeBitboard ^= maskBitboard;
		ceilingBitboard -= b;
		bitboard = activeBitboard | ceilingBitboard;
		
		long hash = bitboard;
		
		long mirroredBitboard = Long.reverseBytes(hash) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
		if(mirroredBitboard < hash) hash = mirroredBitboard;
		
		mixedHash = mixedHash(hash);
	}
	
	public int cellColumnHeight(int cellColumnIndex) {
		return Long.bitCount(maskBitboard & Bitboards.COLUMNS[cellColumnIndex]);
	}
	
	public boolean cellFilled(int cellX, int cellY) {
		int cellPosition = BITBOARD_HEIGHT * cellX + cellY;
		long board = 1L << cellPosition;
		
		return (board & maskBitboard) != 0;
	}
	
	public BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		int cellPosition = BITBOARD_HEIGHT * cellX + cellY;
		long board = 1L << cellPosition;
		
		if((board & maskBitboard) == 0) return null;
		
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		return (activeBitboard & board) == 0 ? (redAtTurn ? BoardPlayerColor.YELLOW : BoardPlayerColor.RED) : (redAtTurn ? BoardPlayerColor.RED : BoardPlayerColor.YELLOW);
	}
	
	public boolean canPlayMove() {
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) return false;
		
		return filledCellAmount != FULL_CELL_AMOUNT;
	}
	
	public void resetEvaluationMetrics() {
		evaluationAmount = 0;
		nodeEvaluationAmount = 0;
	}
	
	public int getEvaluationAmount() {
		return evaluationAmount;
	}
	
	public int getNodeEvaluationAmount() {
		return nodeEvaluationAmount;
	}
	
	public boolean over() {
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) return true;
		
		return filledCellAmount == FULL_CELL_AMOUNT;
	}
	
	public BoardOutcome outcome() {
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) {
			
			boolean redAtTurn = (filledCellAmount & 1) == 0;
			return redAtTurn ? BoardOutcome.YELLOW_WIN : BoardOutcome.RED_WIN;
		}
		
		return filledCellAmount == FULL_CELL_AMOUNT ? BoardOutcome.DRAW : BoardOutcome.UNDECIDED;
	}
	
	private static long mixedHash(long hash) {
		hash ^= hash >>> HASH_MIX_SHIFT_AMOUNT;
		
		hash *= HASH_MIX_FIRST_MAGIC;
		hash ^= hash >>> HASH_MIX_SHIFT_AMOUNT;
		
		hash *= HASH_MIX_SECOND_MAGIC;
		hash ^= hash >>> HASH_MIX_SHIFT_AMOUNT;
		
		return hash;
	}
	
	private static boolean canRedWinInClaimEven(long redCells, long yellowCells, long currentYellowCells, long currentMask) {
		long wins = redCells;
		
		wins &= wins << RIGHT_BITBOARD_DIRECTION;
		wins &= wins << (RIGHT_BITBOARD_DIRECTION << 1);
		
		while(wins != 0) {
			
			int winPosition = Long.numberOfTrailingZeros(wins);
			
			long winBitboard = 1L << winPosition;
			wins ^= winBitboard;
			
			long redBuilds = Bitboards.RIGHT_CELLS_BELOW_LINE_BITBOARDS[winPosition];
			
			long cells = currentYellowCells | (~currentMask & yellowCells & redBuilds);
			if(bitboardContainsConnection(cells)) continue;
			
			return true;
		}
		
		wins = redCells;
		
		wins &= wins << DOWN_RIGHT_BITBOARD_DIRECTION;
		wins &= wins << (DOWN_RIGHT_BITBOARD_DIRECTION << 1);
		
		while(wins != 0) {
			
			int winPosition = Long.numberOfTrailingZeros(wins);
			
			long winBitboard = 1L << winPosition;
			wins ^= winBitboard;
			
			long redBuilds = Bitboards.DOWN_RIGHT_CELLS_BELOW_LINE_BITBOARDS[winPosition];
			
			long cells = currentYellowCells | (~currentMask & yellowCells & redBuilds);
			if(bitboardContainsConnection(cells)) continue;
			
			return true;
		}
		
		wins = redCells;
		
		wins &= wins << UP_RIGHT_BITBOARD_DIRECTION;
		wins &= wins << (UP_RIGHT_BITBOARD_DIRECTION << 1);
		
		while(wins != 0) {
			
			int winPosition = Long.numberOfTrailingZeros(wins);
			
			long winBitboard = 1L << winPosition;
			wins ^= winBitboard;
			
			long redBuilds = Bitboards.UP_RIGHT_CELLS_BELOW_LINE_BITBOARDS[winPosition];
			
			long cells = currentYellowCells | (~currentMask & yellowCells & redBuilds);
			if(bitboardContainsConnection(cells)) continue;
			
			return true;
		}
		
		return false;
	}
	
	private static long nonVerticalWinCellsBitboard(long bitboard) {
		long b = bitboard;
		int doubleDirection = RIGHT_BITBOARD_DIRECTION << 1;
		
		b &= b >>> RIGHT_BITBOARD_DIRECTION;
		b &= b >>> doubleDirection;
		
		b |= b << RIGHT_BITBOARD_DIRECTION;
		b |= b << doubleDirection;
		
		long result = b;
		
		b = bitboard;
		doubleDirection = DOWN_RIGHT_BITBOARD_DIRECTION << 1;
		
		b &= b >>> DOWN_RIGHT_BITBOARD_DIRECTION;
		b &= b >>> doubleDirection;
		
		b |= b << DOWN_RIGHT_BITBOARD_DIRECTION;
		b |= b << doubleDirection;
		
		result |= b;
		
		b = bitboard;
		doubleDirection = UP_RIGHT_BITBOARD_DIRECTION << 1;
		
		b &= b >>> UP_RIGHT_BITBOARD_DIRECTION;
		b &= b >>> doubleDirection;
		
		b |= b << UP_RIGHT_BITBOARD_DIRECTION;
		b |= b << doubleDirection;
		
		result |= b;
		
		return result;
	}
	
	private static long winAnchorCellsBitboard(long bitboard, int direction) {
		int doubleDirection = direction << 1;
		
		bitboard &= bitboard << direction;
		bitboard &= bitboard << doubleDirection;
		
		return bitboard;
	}
	
	private static long bitboardConnectionOpportunities(long bitboard) {
		long doubles = bitboard;
		doubles &= doubles << UP_BITBOARD_DIRECTION;
		
		long triples = doubles;
		triples &= triples << UP_BITBOARD_DIRECTION;
		
		long result = triples << UP_BITBOARD_DIRECTION;
		
		doubles = bitboard;
		doubles &= doubles << RIGHT_BITBOARD_DIRECTION;
		
		triples = doubles;
		triples &= triples << RIGHT_BITBOARD_DIRECTION;
		
		result |= triples << RIGHT_BITBOARD_DIRECTION;
		result |= triples >>> (RIGHT_BITBOARD_DIRECTION * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
		
		result |= (doubles >>> (RIGHT_BITBOARD_DIRECTION << 1)) & (bitboard << RIGHT_BITBOARD_DIRECTION);
		result |= (doubles << RIGHT_BITBOARD_DIRECTION) & (bitboard >>> RIGHT_BITBOARD_DIRECTION);
		
		doubles = bitboard;
		doubles &= doubles << DOWN_RIGHT_BITBOARD_DIRECTION;
		
		triples = doubles;
		triples &= triples << DOWN_RIGHT_BITBOARD_DIRECTION;
		
		result |= triples << DOWN_RIGHT_BITBOARD_DIRECTION;
		result |= triples >>> (DOWN_RIGHT_BITBOARD_DIRECTION * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
		
		result |= (doubles >>> (DOWN_RIGHT_BITBOARD_DIRECTION << 1)) & (bitboard << DOWN_RIGHT_BITBOARD_DIRECTION);
		result |= (doubles << DOWN_RIGHT_BITBOARD_DIRECTION) & (bitboard >>> DOWN_RIGHT_BITBOARD_DIRECTION);
		
		doubles = bitboard;
		doubles &= doubles << UP_RIGHT_BITBOARD_DIRECTION;
		
		triples = doubles;
		triples &= triples << UP_RIGHT_BITBOARD_DIRECTION;
		
		result |= triples << UP_RIGHT_BITBOARD_DIRECTION;
		result |= triples >>> (UP_RIGHT_BITBOARD_DIRECTION * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
		
		result |= (doubles >>> (UP_RIGHT_BITBOARD_DIRECTION << 1)) & (bitboard << UP_RIGHT_BITBOARD_DIRECTION);
		result |= (doubles << UP_RIGHT_BITBOARD_DIRECTION) & (bitboard >>> UP_RIGHT_BITBOARD_DIRECTION);
		
		result &= Bitboards.FULL_BOARD;
		return result;
	}
	
	private static boolean bitboardContainsVerticalConnection(long bitboard) {
		bitboard &= bitboard << 1;
		bitboard &= bitboard << 2;
		
		return bitboard != 0;
	}
	
	private static boolean bitboardContainsNonVerticalConnection(long bitboard) {
		long board = bitboard;
		board &= board << RIGHT_BITBOARD_DIRECTION;
		board &= board << (RIGHT_BITBOARD_DIRECTION << 1);
		
		if(board != 0) return true;
		
		board = bitboard;
		board &= board << DOWN_RIGHT_BITBOARD_DIRECTION;
		board &= board << (DOWN_RIGHT_BITBOARD_DIRECTION << 1);
		
		if(board != 0) return true;
		
		board = bitboard;
		board &= board << UP_RIGHT_BITBOARD_DIRECTION;
		board &= board << (UP_RIGHT_BITBOARD_DIRECTION << 1);
		
		return board != 0;
	}
	
	private static boolean bitboardContainsConnection(long bitboard) {
		long board = bitboard;
		board &= board << RIGHT_BITBOARD_DIRECTION;
		board &= board << (RIGHT_BITBOARD_DIRECTION << 1);
		
		if(board != 0) return true;
		
		board = bitboard;
		board &= board << DOWN_RIGHT_BITBOARD_DIRECTION;
		board &= board << (DOWN_RIGHT_BITBOARD_DIRECTION << 1);
		
		if(board != 0) return true;
		
		board = bitboard;
		board &= board << UP_RIGHT_BITBOARD_DIRECTION;
		board &= board << (UP_RIGHT_BITBOARD_DIRECTION << 1);
		
		if(board != 0) return true;
		
		board = bitboard;
		board &= board << UP_BITBOARD_DIRECTION;
		board &= board << (UP_BITBOARD_DIRECTION << 1);
		
		return board != 0;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	private static float approximateElo(float averageScoreLoss) {
		averageScoreLoss /= ELO_APPROXIMATION_FIRST_COEFFICIENT;
		averageScoreLoss += ELO_APPROXIMATION_SECOND_COEFFICIENT;
		
		averageScoreLoss = (float) Math.log(averageScoreLoss);
		
		averageScoreLoss *= ELO_APPROXIMATION_THIRD_COEFFICIENT;
		
		return Math.min(averageScoreLoss, PERFECT_ELO_APPROXIMATION);
	}
	
}
