package net.kite.board;

import net.kite.board.bit.Bitboard;
import net.kite.board.bit.Bitboards;
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
			  4,   8,  24, 139,  34, 450,   0,   0,
			  0,  85,  25, 140, 137, 249,   0,   0,
			121, 158, 461, 347, 175, 212,   0,   0,
			319, 264, 347, 267, 386, 439,   0,   0,
			121, 158, 461, 347, 175, 212,   0,   0,
			  0,  85,  25, 140, 137, 249,   0,   0,
			  4,   8,  24, 139,  34, 450
	};
	
	private static final int[] YELLOW_MOVE_CELL_SCORES = new int[] {
			  8, 122,  29, 193, 105, 394,   0,   0,
			  2, 164, 138, 205,  33, 424,   0,   0,
			 87, 305, 195, 384, 174, 384,   0,   0,
			127, 403, 472, 486, 434, 425,   0,   0,
			 87, 305, 195, 384, 174, 384,   0,   0,
			  2, 164, 138, 205,  33, 424,   0,   0,
			  8, 122,  29, 193, 105, 394
	};
	
	private static final int MISSING_MOVE_SCORE = Integer.MIN_VALUE;
	
	private static final int CELLS_ABOVE_CELLS_MAXIMAL_ITERATION_AMOUNT = 5;
	
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
	private static final char TO_STRING_EMPTY_CELL_CHARACTER = '.';
	private static final String TO_STRING_MOVES_PREFIX_STRING = "moves: ";
	private static final String TO_STRING_MOVE_SCORES_PREFIX_STRING = "\nmove scores: ";
	private static final String TO_STRING_MOVE_SCORE_SEPARATOR_STRING = ", ";
	private static final String TO_STRING_OUTCOME_PREFIX_STRING = "\noutcome: ";
	private static final String TO_STRING_ILLEGAL_MOVE_STRING = "-";
	
	private static final String WINNING_MOVE_FORMAT_PREFIX = "+";
	
	private int filledCellAmount;
	private int evenParityCellColumnAmount = WIDTH;
	
	private int evaluationAmount;
	private int nodeEvaluationAmount;
	
	private long bitboard = Bitboards.EMPTY_CEILING;
	private long activeBitboard = Bitboards.EMPTY;
	private long maskBitboard = Bitboards.EMPTY;
	private long ceilingBitboard = Bitboards.EMPTY_CEILING;
	
	private long mixedHash = EMPTY_MIXED_HASH;
	
	private BoardOutcome outcome = BoardOutcome.UNDECIDED;
	
	private final int[] moves;
	private final int[] moveScores;
	
	private final int[] playedMoves;
	private final int[] undoneMoves;
	
	private final BoardScoreCache scoreCache;
	
	private final BoardLine[] lines = new BoardLine[MAXIMAL_LINE_AMOUNT];
	
	public Board() {
		this.scoreCache = new BoardScoreCache();
		
		this.moves = new int[MOVES_LENGTH];
		this.moveScores = new int[MOVES_LENGTH];
		this.playedMoves = new int[FULL_CELL_AMOUNT];
		this.undoneMoves = new int[FULL_CELL_AMOUNT];
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
		
		long opponentWinBitboard = bitboardConnectionOpportunities(activeBitboard ^ maskBitboard) & (~maskBitboard);
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
		
		int moveIndex = movesBaseIndex;
		
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
	
	public boolean moveLegal(int moveCellX) {
		if(outcome != BoardOutcome.UNDECIDED) return false;
		
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
		
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) {
			
			boolean redAtTurn = (filledCellAmount & 1) == 0;
			outcome = redAtTurn ? BoardOutcome.YELLOW_WIN : BoardOutcome.RED_WIN;
			
			return;
		}
		
		if(filledCellAmount == FULL_CELL_AMOUNT) outcome = BoardOutcome.DRAW;
		
		long hash = bitboard;
		
		long mirroredBitboard = Long.reverseBytes(hash) >>> MIRRORED_BITBOARD_SHIFT_AMOUNT;
		if(mirroredBitboard < hash) hash = mirroredBitboard;
		
		mixedHash = mixedHash(hash);
	}
	
	public void undoMove() {
		outcome = BoardOutcome.UNDECIDED;
		
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
	
	public int playedMove(int moveIndex) {
		return playedMoves[moveIndex];
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
	
	public boolean canUndoMove() {
		return filledCellAmount > 0;
	}
	
	public boolean canPlayMove() {
		return outcome == BoardOutcome.UNDECIDED;
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
		return outcome != BoardOutcome.UNDECIDED;
	}
	
	public BoardOutcome getOutcome() {
		return outcome;
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
			
			long redBuilds = Bitboards.CELLS_BELOW_LINE_BITBOARDS[RIGHT_BITBOARD_DIRECTION][winPosition];
			
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
			
			long redBuilds = Bitboards.CELLS_BELOW_LINE_BITBOARDS[DOWN_RIGHT_BITBOARD_DIRECTION][winPosition];
			
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
			
			long redBuilds = Bitboards.CELLS_BELOW_LINE_BITBOARDS[UP_RIGHT_BITBOARD_DIRECTION][winPosition];
			
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
	
	private static String formatMoveScore(int moveScore) {
		if(moveScore > 0) return WINNING_MOVE_FORMAT_PREFIX + moveScore;
		
		return String.valueOf(moveScore);
	}
	
}
