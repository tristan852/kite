package net.kite.board;

import net.kite.board.bit.Bitboard;
import net.kite.board.bit.Bitboards;
import net.kite.board.history.BoardHistory;
import net.kite.board.history.entry.BoardHistoryEntry;
import net.kite.board.line.BoardLine;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.player.color.BoardPlayerColor;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCache;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;

public class Board {
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private static final int FULL_CELL_AMOUNT = 42;
	
	private static final int UP_BITBOARD_DIRECTION = 1;
	private static final int RIGHT_BITBOARD_DIRECTION = 8;
	private static final int UP_RIGHT_BITBOARD_DIRECTION = 9;
	private static final int DOWN_RIGHT_BITBOARD_DIRECTION = 7;
	
	private static final int[] NON_VERTICAL_BITBOARD_CONNECTION_DIRECTIONS = new int[] {
			RIGHT_BITBOARD_DIRECTION,
			DOWN_RIGHT_BITBOARD_DIRECTION,
			UP_RIGHT_BITBOARD_DIRECTION
	};
	
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
	
	private static final int MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT = 218;
	private static final int MOVE_SCORE_COLUMN_FORK_WEIGHT = 233;
	private static final int MOVE_SCORE_IMMEDIATE_THREAT_WEIGHT = 465;
	private static final int MOVE_SCORE_SOON_THREAT_WEIGHT = 602;
	
	private static final int[] RED_MOVE_CELL_SCORES = new int[] {
			 15,  44,  23, 175,  68, 479,   0,   0,
			 93, 125,  97, 179, 209, 309,   0,   0,
			123, 204, 501, 389, 197, 421,   0,   0,
			337, 385, 351, 289, 428, 450,   0,   0,
			123, 204, 501, 389, 197, 421,   0,   0,
			 93, 125,  97, 179, 209, 309,   0,   0,
			 15,  44,  23, 175,  68, 479
	};
	
	private static final int[] YELLOW_MOVE_CELL_SCORES = new int[] {
			 66, 149,  10, 196, 141, 463,   0,   0,
			 33, 176, 242, 268,  31, 469,   0,   0,
			132, 285, 257, 419, 219, 375,   0,   0,
			177, 414, 599, 583, 516, 513,   0,   0,
			132, 285, 257, 419, 219, 375,   0,   0,
			 33, 176, 242, 268,  31, 469,   0,   0,
			 66, 149,  10, 196, 141, 463
	};
	
	private static final int MISSING_MOVE_SCORE = Integer.MIN_VALUE;
	
	private static final int BITBOARD_CONNECTION_OPPORTUNITY_LENGTH = 3;
	
	private static final int MIRRORED_BITBOARD_SHIFT_AMOUNT = 8;
	
	private static final int LOGARITHMIC_BITBOARD_LENGTH = 3;
	private static final int LARGEST_BITBOARD_Y = 7;
	
	private static final int LARGEST_MOVE_CELL_X = 6;
	private static final int LARGEST_MOVE_CELL_Y = 5;
	
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	private static final long[] HASH_MIX_MAGICS = new long[] {
			0xFF51AFD7ED558CCDL,
			0xC4CEB9FE1A85EC53L
	};
	
	private static final long EMPTY_MIXED_HASH = 0x2373BFB0BD385EEAL;
	
	private static final int HASH_MIX_SHIFT_AMOUNT = 33;
	
	private static final int COLUMN_HASH_BASE = 3;
	
	private static final int MINIMAL_CHILD_CACHE_LOOKUP_DEPTH = 13;
	
	private static final int BITBOARD_HEIGHT = 8;
	
	private static final int MAXIMAL_DISABLED_ITERATIVE_DEEPENING_SCORE_RANGE = 11;
	
	private static final float ELO_APPROXIMATION_FIRST_COEFFICIENT = 53.167f;
	private static final float ELO_APPROXIMATION_SECOND_COEFFICIENT = 0.000661f;
	private static final float ELO_APPROXIMATION_THIRD_COEFFICIENT = -414.261f;
	private static final float PERFECT_ELO_APPROXIMATION = 3000.0f;
	
	private static final int ELO_APPROXIMATION_RED_MIN_MOVE_AMOUNT = 1;
	private static final int ELO_APPROXIMATION_YELLOW_MIN_MOVE_AMOUNT = 2;
	
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	private static final char TO_STRING_EMPTY_CELL_CHARACTER = '.';
	private static final String TO_STRING_MOVES_PREFIX_STRING = "moves: ";
	private static final String TO_STRING_MOVE_SCORES_PREFIX_STRING = "\nmove scores: ";
	private static final String TO_STRING_MOVE_SCORE_SEPARATOR_STRING = ", ";
	private static final String TO_STRING_OUTCOME_PREFIX_STRING = "\noutcome: ";
	private static final String TO_STRING_ILLEGAL_MOVE_STRING = "-";
	
	private static final String WINNING_MOVE_FORMAT_PREFIX = "+";
	
	private static final int MAXIMAL_LINE_AMOUNT = 4;
	
	private final int[] cellColumnHeights = new int[WIDTH];
	
	private boolean symmetrical = true;
	
	private int filledCellAmount;
	private int evenParityCellColumnAmount = WIDTH;
	
	private long bitboard = Bitboards.EMPTY_CEILING;
	private long activeBitboard = Bitboards.EMPTY;
	private long maskBitboard = Bitboards.EMPTY;
	private long ceilingBitboard = Bitboards.EMPTY_CEILING;
	
	private BoardOutcome outcome = BoardOutcome.UNDECIDED;
	
	private final int[][] moves;
	private final int[][] moveScores;
	
	private final int[] playedMoves;
	private final int[] undoneMoves;
	
	private long hash = Bitboards.EMPTY_CEILING;
	private long mixedHash = EMPTY_MIXED_HASH;
	
	private final BoardHistory history;
	private final BoardScoreCache scoreCache;
	
	private final BoardLine[] lines = new BoardLine[MAXIMAL_LINE_AMOUNT];
	
	public Board(BoardScoreCache scoreCache) {
		this.scoreCache = scoreCache;
		
		this.moves = new int[FULL_CELL_AMOUNT][WIDTH];
		this.moveScores = new int[FULL_CELL_AMOUNT][WIDTH];
		this.playedMoves = new int[FULL_CELL_AMOUNT];
		this.undoneMoves = new int[FULL_CELL_AMOUNT];
		
		this.history = new BoardHistory();
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int y = HEIGHT - 1; y >= 0; y--) {
			for(int x = 0; x < WIDTH; x++) {
				
				BoardPlayerColor cellPlayerColor = cellPlayerColor(x, y);
				char cellCharacter = cellPlayerColor == null ? TO_STRING_EMPTY_CELL_CHARACTER : cellPlayerColor.getCharacter();
				
				stringBuilder.append(cellCharacter);
			}
			
			stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
		}
		
		stringBuilder.append(TO_STRING_CELL_ROW_SEPARATOR_STRING);
		stringBuilder.append(TO_STRING_MOVES_PREFIX_STRING);
		
		for(int i = 0; i < filledCellAmount; i++) {
			
			int move = playedMoves[i];
			char moveCharacter = (char) (SMALLEST_MOVE_CHARACTER + move);
			
			stringBuilder.append(moveCharacter);
		}
		
		stringBuilder.append(TO_STRING_MOVE_SCORES_PREFIX_STRING);
		
		int[] moveScores = this.moveScores[0];
		
		for(int x : ORDERED_MOVE_COLUMN_INDICES) {
			
			if(moveLegal(x)) moveScores[x] = evaluateMove(x);
		}
		
		for(int x = 0; x < WIDTH; x++) {
			
			if(x != 0) stringBuilder.append(TO_STRING_MOVE_SCORE_SEPARATOR_STRING);
			
			if(moveLegal(x)) {
				
				int score = moveScores[x];
				String s = formatMoveScore(score);
				
				stringBuilder.append(s);
				
			} else stringBuilder.append(TO_STRING_ILLEGAL_MOVE_STRING);
		}
		
		stringBuilder.append(TO_STRING_OUTCOME_PREFIX_STRING);
		stringBuilder.append(outcome);
		
		return stringBuilder.toString();
	}
	
	public BoardLine[] winningPlayerLines() {
		boolean won = outcome.isWin();
		if(!won) return null;
		
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
		return Long.divideUnsigned(h1, 3);
	}
	
	private long partialColumnHash(long columnHash, int x) {
		int height = cellColumnHeights[x];
		long board = Bitboards.BOTTOM_CELL_BITBOARDS[x];
		
		for(int y = 0; y < height; y++) {
			
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
		if(outcome != BoardOutcome.UNDECIDED) {
			
			if(outcome == BoardOutcome.DRAW) return BoardScore.DRAW;
			return BoardScore.loss(filledCellAmount);
		}
		
		if(activePlayerHasImmediateWin()) {
			
			return BoardScore.win(filledCellAmount + 1);
		}
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		int minimalScore = BoardScore.minimal(filledCellAmount);
		int maximalScore = BoardScore.maximal(filledCellAmount);
		
		if(maximalScore > maxScore) maximalScore = maxScore;
		
		if(filledCellAmount > 0) {
			
			undoMove();
			
			int lastMove = playedMoves[filledCellAmount];
			
			openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this);
			if(openingBoardScore != Integer.MIN_VALUE) {
				
				int s = -openingBoardScore;
				if(minimalScore < s) minimalScore = s;
			}
			
			playMove(lastMove);
		}
		
		int entryKey = scoreCache.entryKey(hash, mixedHash);
		if(entryKey >= 0) {
			
			int entryMinScore = scoreCache.entryMinimalScore(entryKey);
			int entryMaxScore = scoreCache.entryMaximalScore(entryKey);
			
			if(entryMinScore > minimalScore) minimalScore = entryMinScore;
			if(entryMaxScore < maximalScore) maximalScore = entryMaxScore;
		}
		
		while(minimalScore < maximalScore) {
			
			int s1 = (minimalScore + maximalScore) >> 1;
			
			int range = maximalScore - minimalScore;
			if(range > MAXIMAL_DISABLED_ITERATIVE_DEEPENING_SCORE_RANGE) {
				
				int s2 = minimalScore >> 1;
				int s3 = maximalScore >> 1;
				
				if(s1 >= 0 && s1 < s3) {
					
					s1 = s3;
					
				} else if(s1 <= 0 && s1 > s2) {
					
					s1 = s2;
				}
			}
			
			int evaluationResult = evaluateWithNoImmediateWin(s1, s1 + 1);
			if(evaluationResult <= s1) {
				
				maximalScore = evaluationResult;
				
			} else {
				
				minimalScore = evaluationResult;
			}
		}
		
		return minimalScore;
	}
	
	// only interested in scores in between min and max (excluding min and max tho)
	private int evaluateWithNoImmediateWin(int minimalScore, int maximalScore) {
		int minScore = BoardScore.minimal(filledCellAmount);
		int maxScore = BoardScore.maximalWithNoImmediateWin(filledCellAmount);
		
		if(maximalScore > 0) {
			
			long emptyCells = Bitboards.FULL_BOARD ^ maskBitboard;
			
			boolean canNoLongerWin = !bitboardContainsNonVerticalConnection(activeBitboard | emptyCells);
			if(canNoLongerWin) maxScore = 0;
		}
		
		if(evenParityCellColumnAmount == WIDTH) {
			
			long redCells = activeBitboard | (Bitboards.ODD_BOARD_ROWS & ~maskBitboard);
			long yellowCells = Bitboards.FULL_BOARD ^ redCells;
			
			long currentYellowCells = activeBitboard ^ maskBitboard;
			
			if(!canRedWinInClaimEven(redCells, yellowCells, currentYellowCells, maskBitboard)) {
				
				if(maxScore > BoardScore.DRAW) maxScore = BoardScore.DRAW;
				
				boolean yellowWon = bitboardContainsNonVerticalConnection(yellowCells);
				if(yellowWon) {
					
					long yellowWinningStones = nonVerticalWinCellsBitboard(yellowCells);
					
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
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		int entryKey = scoreCache.entryKey(hash, mixedHash);
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
				
				entryKey = scoreCache.entryKey(h1, mixedHash);
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
		
		long opponentWinBitboard = opponentWinBitboard();
		long immediateThreats = opponentWinBitboard & ceilingBitboard;
		
		if(immediateThreats != Bitboards.EMPTY) {
			
			int p = Long.numberOfTrailingZeros(immediateThreats);
			long b = 1L << p;
			
			immediateThreats ^= b;
			if(immediateThreats != Bitboards.EMPTY) {
				
				return minScore;
			}
			
			int forcedX = p >>> LOGARITHMIC_BITBOARD_LENGTH;
			
			long upperCellBitboard = b << 1;
			if((upperCellBitboard & opponentWinBitboard) != 0) {
				
				return minScore;
			}
			
			playMove(forcedX);
			
			int s = -evaluateWithNoImmediateWin(-maximalScore, -minimalScore);
			
			undoMove();
			
			if(s >= maximalScore) {
				
				scoreCache.updateEntry(hash, mixedHash, s, maxScore);
				return s;
			}
			
			if(s > minimalScore) minimalScore = s;
			
			scoreCache.updateEntry(hash, mixedHash, minScore, minimalScore);
			
			return minimalScore;
		}
		
		int[] moves = this.moves[filledCellAmount];
		int[] moveScores = this.moveScores[filledCellAmount];
		int moveAmount = 0;
		
		long movesBitboard = ceilingBitboard & Bitboards.FULL_BOARD;
		if(symmetrical) movesBitboard &= Bitboards.SYMMETRY_PRUNE_BITBOARD;
		
		int bestMoveIndex = 0;
		int bestMoveScore = Integer.MIN_VALUE;
		
		while(movesBitboard != 0) {
			
			int movePosition = Long.numberOfTrailingZeros(movesBitboard);
			int moveCellX = movePosition >>> LOGARITHMIC_BITBOARD_LENGTH;
			
			long moveBitboard = 1L << movePosition;
			movesBitboard ^= moveBitboard;
			
			long upperCellBitboard = moveBitboard << 1;
			if((upperCellBitboard & opponentWinBitboard) != 0) {
				
				continue;
			}
			
			int moveScore = moveScore(movePosition, moveBitboard, opponentWinBitboard);
			int moveIndex = moveAmount;
			
			moves[moveIndex] = moveCellX;
			moveScores[moveIndex] = moveScore;
			
			moveAmount++;
			
			if(moveScore > bestMoveScore) {
				
				bestMoveIndex = moveIndex;
				bestMoveScore = moveScore;
			}
		}
		
		if(moveAmount == 0) {
			
			scoreCache.updateEntry(hash, mixedHash, minScore, minScore);
			
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
				
				scoreCache.updateEntry(hash, mixedHash, s, maxScore);
				return s;
			}
			
			if(s > minimalScore) {
				
				minimalScore = s;
			}
			
			i++;
			if(i == moveAmount) break;
			
			bestMoveIndex = 0;
			bestMoveScore = moveScores[0];
			
			for(int j = 1; j < moveAmount; j++) {
				
				int moveScore = moveScores[j];
				if(moveScore > bestMoveScore) {
					
					bestMoveIndex = j;
					bestMoveScore = moveScore;
				}
			}
		}
		
		scoreCache.updateEntry(hash, mixedHash, minScore, minimalScore);
		
		return minimalScore;
	}
	
	private int moveScore(int moveCellPosition, long moveBitboard, long opponentOpenThreats) {
		long board = activeBitboard;
		long mask = maskBitboard;
		long ceiling = ceilingBitboard;
		
		board |= moveBitboard;
		mask |= moveBitboard;
		
		ceiling ^= moveBitboard;
		
		moveBitboard <<= 1;
		
		ceiling |= moveBitboard;
		
		long result = bitboardConnectionOpportunities(board);
		
		result &= ~mask;
		result &= ~(opponentOpenThreats << 1);
		
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		int[] moveCellScores = redAtTurn ? RED_MOVE_CELL_SCORES : YELLOW_MOVE_CELL_SCORES;
		
		int moveScore = moveCellScores[moveCellPosition];
		
		moveScore += Long.bitCount(result) * MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT;
		
		long responseMoves = ceiling & Bitboards.FULL_BOARD;
		long l1 = result & responseMoves;
		
		moveScore += Long.bitCount(l1) * MOVE_SCORE_IMMEDIATE_THREAT_WEIGHT;
		
		responseMoves <<= 1;
		long l2 = result & responseMoves;
		
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
	
	private long opponentWinBitboard() {
		long board = activeBitboard;
		long mask = maskBitboard;
		
		board ^= mask;
		
		long result = bitboardConnectionOpportunities(board);
		return result & ~mask;
	}
	
	public boolean moveLegal(int moveCellX) {
		if(outcome != BoardOutcome.UNDECIDED) return false;
		
		return cellColumnHeights[moveCellX] < HEIGHT;
	}
	
	public void playMove(int moveCellX) {
		BoardHistoryEntry entry = history.entry(filledCellAmount);
		entry.fill(
				symmetrical,
				bitboard, activeBitboard, maskBitboard, ceilingBitboard,
				hash, mixedHash
		);
		
		int moveCellY = cellColumnHeights[moveCellX];
		
		cellColumnHeights[moveCellX]++;
		
		boolean wasEven = (moveCellY & 1) == 0;
		if(wasEven) evenParityCellColumnAmount--;
		else evenParityCellColumnAmount++;
		
		playedMoves[filledCellAmount] = moveCellX;
		filledCellAmount++;
		
		int p = BITBOARD_HEIGHT * moveCellX + moveCellY;
		
		long b1 = 1L << p;
		
		activeBitboard = activeBitboard ^ maskBitboard;
		maskBitboard |= b1;
		ceilingBitboard ^= b1;
		
		b1 <<= 1;
		
		ceilingBitboard |= b1;
		bitboard = activeBitboard | ceilingBitboard;
		
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) {
			
			boolean redAtTurn = (filledCellAmount & 1) == 0;
			outcome = redAtTurn ? BoardOutcome.YELLOW_WIN : BoardOutcome.RED_WIN;
			
			return;
		}
		
		if(filledCellAmount == FULL_CELL_AMOUNT) outcome = BoardOutcome.DRAW;
		
		hash = bitboard;
		
		long mirroredBitboard = Long.reverseBytes(bitboard) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
		if(mirroredBitboard < hash) hash = mirroredBitboard;
		
		mixedHash = mixedHash(hash);
		symmetrical = bitboard == mirroredBitboard;
	}
	
	public void undoMove() {
		outcome = BoardOutcome.UNDECIDED;
		
		filledCellAmount--;
		int moveCellX = playedMoves[filledCellAmount];
		
		BoardHistoryEntry entry = history.entry(filledCellAmount);
		
		cellColumnHeights[moveCellX]--;
		
		int moveCellY = cellColumnHeights[moveCellX];
		
		boolean isEven = (moveCellY & 1) == 0;
		if(isEven) evenParityCellColumnAmount++;
		else evenParityCellColumnAmount--;
		
		symmetrical = entry.isSymmetrical();
		
		bitboard = entry.getBitboard();
		activeBitboard = entry.getActiveBitboard();
		maskBitboard = entry.getMaskBitboard();
		ceilingBitboard = entry.getCeilingBitboard();
		
		hash = entry.getHash();
		mixedHash = entry.getMixedHash();
	}
	
	public int cellColumnHeight(int cellColumnIndex) {
		return cellColumnHeights[cellColumnIndex];
	}
	
	public int playedMove(int moveIndex) {
		return playedMoves[moveIndex];
	}
	
	public boolean cellFilled(int cellX, int cellY) {
		int height = cellColumnHeights[cellX];
		
		return cellY < height;
	}
	
	public BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		int height = cellColumnHeights[cellX];
		
		if(cellY >= height) return null;
		
		int cellPosition = BITBOARD_HEIGHT * cellX + cellY;
		
		long board = 1L << cellPosition;
		BoardPlayerColor activePlayerColor = activePlayerColor();
		
		return (activeBitboard & board) == 0 ? activePlayerColor.opposite() : activePlayerColor;
	}
	
	public BoardPlayerColor activePlayerColor() {
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		
		return redAtTurn ? BoardPlayerColor.RED : BoardPlayerColor.YELLOW;
	}
	
	public int playedMoveAmount() {
		return filledCellAmount;
	}
	
	public boolean canPlayMoves() {
		return outcome == BoardOutcome.UNDECIDED;
	}
	
	public boolean over() {
		return outcome != BoardOutcome.UNDECIDED;
	}
	
	public BoardOutcome getOutcome() {
		return outcome;
	}
	
	private static long mixedHash(long hash) {
		hash ^= hash >>> HASH_MIX_SHIFT_AMOUNT;
		
		for(long m : HASH_MIX_MAGICS) {
			
			hash *= m;
			hash ^= hash >>> HASH_MIX_SHIFT_AMOUNT;
		}
		
		return hash;
	}
	
	private static boolean canRedWinInClaimEven(long redCells, long yellowCells, long currentYellowCells, long currentMask) {
		for(int direction : NON_VERTICAL_BITBOARD_CONNECTION_DIRECTIONS) {
			
			long wins = redCells;
			
			wins &= wins << direction;
			wins &= wins << (direction << 1);
			
			while(wins != 0) {
				
				int winPosition = Long.numberOfTrailingZeros(wins);
				
				long winBitboard = 1L << winPosition;
				wins ^= winBitboard;
				
				long redBuilds = Bitboards.CELLS_BELOW_LINE_BITBOARDS[direction][winPosition];
				
				long cells = currentYellowCells | (~currentMask & yellowCells & redBuilds);
				if(bitboardContainsConnection(cells)) continue;
				
				return true;
			}
		}
		
		return false;
	}
	
	private static long nonVerticalWinCellsBitboard(long bitboard) {
		long result = 0;
		
		for(int direction : NON_VERTICAL_BITBOARD_CONNECTION_DIRECTIONS) {
			
			int doubleDirection = direction << 1;
			long b = bitboard;
			
			b &= b >>> direction;
			b &= b >>> doubleDirection;
			
			b |= b << direction;
			b |= b << doubleDirection;
			
			result |= b;
		}
		
		return result;
	}
	
	private static long winAnchorCellsBitboard(long bitboard, int direction) {
		int doubleDirection = direction << 1;
		
		bitboard &= bitboard << direction;
		bitboard &= bitboard << doubleDirection;
		
		return bitboard;
	}
	
	private static long winCellsBitboard(long bitboard) {
		long result = 0;
		
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			int doubleDirection = direction << 1;
			long b = bitboard;
			
			b &= b >>> direction;
			b &= b >>> doubleDirection;
			
			b |= b << direction;
			b |= b << doubleDirection;
			
			result |= b;
		}
		
		return result;
	}
	
	private static long bitboardConnectionOpportunities(long bitboard) {
		long result = 0;
		
		long verticalDoubles = bitboard;
		verticalDoubles &= verticalDoubles << UP_BITBOARD_DIRECTION;
		
		long verticalTriples = verticalDoubles;
		verticalTriples &= verticalTriples << UP_BITBOARD_DIRECTION;
		
		result |= verticalTriples << UP_BITBOARD_DIRECTION;
		
		for(int direction : NON_VERTICAL_BITBOARD_CONNECTION_DIRECTIONS) {
			
			long doubles = bitboard;
			doubles &= doubles << direction;
			
			long triples = doubles;
			triples &= triples << direction;
			
			result |= triples << direction;
			result |= triples >>> (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
			
			result |= (doubles >>> (direction << 1)) & (bitboard << direction);
			result |= (doubles << direction) & (bitboard >>> direction);
		}
		
		result &= Bitboards.FULL_BOARD;
		return result;
	}
	
	private static boolean bitboardContainsVerticalConnection(long bitboard) {
		bitboard &= bitboard << 1;
		bitboard &= bitboard << 2;
		
		return bitboard != 0;
	}
	
	private static boolean bitboardContainsNonVerticalConnection(long bitboard) {
		for(int direction : NON_VERTICAL_BITBOARD_CONNECTION_DIRECTIONS) {
			
			long board = bitboard;
			board &= board << direction;
			board &= board << (direction << 1);
			
			if(board != 0) return true;
		}
		
		return false;
	}
	
	private static boolean bitboardContainsConnection(long bitboard) {
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			long board = bitboard;
			board &= board << direction;
			board &= board << (direction << 1);
			
			if(board != 0) return true;
		}
		
		return false;
	}
	
	public static Board boardWithMoves(String moves, BoardScoreCache scoreCache) {
		Board board = new Board(scoreCache);
		
		int l = moves.length();
		for(int i = 0; i < l; i++) {
			
			char move = moves.charAt(i);
			int x = move - SMALLEST_MOVE_CHARACTER;
			
			board.playMove(x);
		}
		
		return board;
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
		
		if(averageScoreLoss > PERFECT_ELO_APPROXIMATION) return PERFECT_ELO_APPROXIMATION;
		return averageScoreLoss;
	}
	
	private static String formatMoveScore(int moveScore) {
		if(moveScore > 0) return WINNING_MOVE_FORMAT_PREFIX + moveScore;
		
		return String.valueOf(moveScore);
	}
	
}
