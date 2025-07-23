package net.kite.board;

import net.kite.board.bit.Bitboards;
import net.kite.board.history.BoardHistory;
import net.kite.board.history.entry.BoardHistoryEntry;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.player.color.BoardPlayerColor;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCache;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;

public class Board {
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private static final int FULL_CELL_AMOUNT = 42;
	
	private static final int[] BITBOARD_CONNECTION_DIRECTIONS = new int[] {
			8,
			7,
			9,
			1
	};
	
	private static final int[] ORDERED_MOVE_COLUMN_INDICES = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT = 12;
	private static final int MOVE_SCORE_COLUMN_FORK_WEIGHT = 16;
	
	private static final int[] RED_MOVE_CELL_SCORES = new int[] {
			 5,  1, 10,  3,  7,  0,  0,  0,
			 8,  6, 17, 11, 13,  2,  0,  0,
			12, 12, 21, 15, 18,  4,  0,  0,
			16, 14, 22, 19, 20,  9,  0,  0,
			12, 12, 21, 15, 18,  4,  0,  0,
			 8,  6, 17, 11, 13,  2,  0,  0,
			 5,  1, 10,  3,  7,  0
	};
	
	private static final int[] YELLOW_MOVE_CELL_SCORES = new int[] {
			 0,  7,  3, 10,  1,  5,  0,  0,
			 2, 13, 11, 17,  6,  8,  0,  0,
			 4, 18, 15, 21, 12, 12,  0,  0,
			 9, 20, 19, 22, 14, 16,  0,  0,
			 4, 18, 15, 21, 12, 12,  0,  0,
			 2, 13, 11, 17,  6,  8,  0,  0,
			 0,  7,  3, 10,  1,  5
	};
	
	private static final int BITBOARD_CONNECTION_OPPORTUNITY_LENGTH = 3;
	
	private static final int LOGARITHMIC_BITBOARD_LENGTH = 3;
	
	private static final int LARGEST_MOVE_CELL_X = 6;
	
	private static final int MISSING_MOVE_SCORE = Integer.MIN_VALUE;
	
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	private static final long[] HASH_MIX_MAGICS = new long[] {
			0xFF51AFD7ED558CCDL,
			0xC4CEB9FE1A85EC53L
	};
	
	private static final int HASH_MIX_SHIFT_AMOUNT = 33;
	
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	private static final String TO_STRING_EMPTY_CELL_STRING = ".";
	private static final String TO_STRING_MOVES_PREFIX_STRING = "moves: ";
	private static final String TO_STRING_MOVE_SCORES_PREFIX_STRING = "\nmove scores: ";
	private static final String TO_STRING_MOVE_SCORE_SEPARATOR_STRING = ", ";
	private static final String TO_STRING_OUTCOME_PREFIX_STRING = "\noutcome: ";
	private static final String TO_STRING_ILLEGAL_MOVE_STRING = "-";
	
	private static final String WINNING_MOVE_FORMAT_PREFIX = "+";
	
	private final int[] cellColumnHeights = new int[WIDTH];
	private int filledCellAmount;
	
	private long bitboard = Bitboards.EMPTY_CEILING;
	private long activeBitboard = Bitboards.EMPTY;
	private long maskBitboard = Bitboards.EMPTY;
	private long ceilingBitboard = Bitboards.EMPTY_CEILING;
	
	private long mirroredBitboard = Bitboards.EMPTY_CEILING;
	private long mirroredActiveBitboard = Bitboards.EMPTY;
	private long mirroredMaskBitboard = Bitboards.EMPTY;
	private long mirroredCeilingBitboard = Bitboards.EMPTY_CEILING;
	
	private BoardOutcome outcome = BoardOutcome.UNDECIDED;
	
	private final int[][] moves;
	private final int[][] moveScores;
	
	private final int[] playedMoves;
	private final int[] nodesVisited;
	
	private long hash = Bitboards.EMPTY_CEILING;
	private long mixedHash = 0x2373BFB0BD385EEAL;
	
	private final BoardHistory history;
	private final BoardScoreCache scoreCache;
	
	public Board(BoardScoreCache scoreCache) {
		this.scoreCache = scoreCache;
		
		this.moves = new int[FULL_CELL_AMOUNT][WIDTH];
		this.moveScores = new int[FULL_CELL_AMOUNT][WIDTH];
		this.playedMoves = new int[FULL_CELL_AMOUNT];
		
		int l = FULL_CELL_AMOUNT;
		l++;
		
		this.nodesVisited = new int[l];
		
		this.history = new BoardHistory();
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int y = HEIGHT - 1; y >= 0; y--) {
			for(int x = 0; x < WIDTH; x++) {
				
				BoardPlayerColor cellPlayerColor = cellPlayerColor(x, y);
				String cellString = cellPlayerColor == null ? TO_STRING_EMPTY_CELL_STRING : cellPlayerColor.getString();
				
				stringBuilder.append(cellString);
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
			
			if(moveLegal(x)) {
				
				int score = moveScores[x];
				String s = formatMoveScore(score);
				
				if(x != 0) stringBuilder.append(TO_STRING_MOVE_SCORE_SEPARATOR_STRING);
				stringBuilder.append(s);
				
			} else stringBuilder.append(TO_STRING_ILLEGAL_MOVE_STRING);
		}
		
		stringBuilder.append(TO_STRING_OUTCOME_PREFIX_STRING);
		stringBuilder.append(outcome);
		
		return stringBuilder.toString();
	}
	
	public long pascalHash() {
		long h1 = 0;
		long h2 = 0;
		
		for(int x = 0; x < WIDTH; x++) h1 = partialPascalHash(h1, x);
		for(int x = LARGEST_MOVE_CELL_X; x >= 0; x--) h2 = partialPascalHash(h2, x);
		
		if(h2 < h1) h1 = h2;
		return Long.divideUnsigned(h1, 3);
	}
	
	private long partialPascalHash(long pascalHash, int x) {
		int height = cellColumnHeights[x];
		long board = Bitboards.bottomCellBitboard(x);
		
		for(int y = 0; y < height; y++) {
			
			pascalHash *= 3;
			
			boolean activeCell = (activeBitboard & board) != 0;
			
			if(activeCell) pascalHash++;
			else pascalHash += 2;
			
			board <<= 1;
		}
		
		pascalHash *= 3;
		return pascalHash;
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
			int s2 = minimalScore >> 1;
			int s3 = maximalScore >> 1;
			
			if(s1 >= 0 && s1 < s3) {
				
				s1 = s3;
				
			} else if(s1 <= 0 && s1 > s2) {
				
				s1 = s2;
			}
			
			int evaluationResult = evaluateWithNoImmediateWin(s1, s1 + 1);
			if(evaluationResult <= s1) {
				
				maximalScore = s1;
				
			} else {
				
				minimalScore = s1 + 1;
			}
		}
		
		return minimalScore;
	}
	
	// only interested in scores in between min and max (excluding min and max tho)
	private int evaluateWithNoImmediateWin(int minimalScore, int maximalScore) {
		int depth = filledCellAmount;
		int nextDepth = depth + 1;
		nodesVisited[depth] = 1;
		
		if(outcome != BoardOutcome.UNDECIDED) {
			
			if(outcome == BoardOutcome.DRAW) return BoardScore.DRAW;
			return BoardScore.loss(filledCellAmount);
		}
		
		int minScore = BoardScore.minimal(filledCellAmount);
		int maxScore = BoardScore.maximal(filledCellAmount);
		
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
		
		long opponentWinBitboard = opponentWinBitboard();
		long immediateThreats = opponentWinBitboard & ceilingBitboard;
		
		if(immediateThreats != Bitboards.EMPTY) {
			
			int p = Long.numberOfTrailingZeros(immediateThreats);
			long b = Bitboards.cellBitboard(p);
			
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
			nodesVisited[depth] += nodesVisited[nextDepth];
			
			undoMove();
			
			if(s >= maximalScore) {
				
				int nodes = nodesVisited[depth];
				
				scoreCache.updateEntry(hash, mixedHash, s, maxScore, nodes);
				return s;
			}
			
			if(s > minimalScore) minimalScore = s;
			
			int nodes = nodesVisited[depth];
			
			scoreCache.updateEntry(hash, mixedHash, minScore, minimalScore, nodes);
			
			return minimalScore;
		}
		
		int[] moves = this.moves[filledCellAmount];
		int[] moveScores = this.moveScores[filledCellAmount];
		int moveAmount = 0;
		
		long movesBitboard = ceilingBitboard & Bitboards.FULL_BOARD;
		
		boolean symmetric = bitboard == mirroredBitboard;
		if(symmetric) movesBitboard &= Bitboards.SYMMETRY_PRUNE_BITBOARD;
		
		while(movesBitboard != 0) {
			
			int movePosition = Long.numberOfTrailingZeros(movesBitboard);
			int moveCellX = movePosition >>> LOGARITHMIC_BITBOARD_LENGTH;
			
			long moveBitboard = Bitboards.cellBitboard(movePosition);
			movesBitboard ^= moveBitboard;
			
			long upperCellBitboard = moveBitboard << 1;
			if((upperCellBitboard & opponentWinBitboard) != 0) {
				
				continue;
			}
			
			int moveScore = moveScore(movePosition, moveBitboard);
			int moveIndex = moveAmount;
			
			moves[moveIndex] = moveCellX;
			moveScores[moveIndex] = moveScore;
			
			moveAmount++;
		}
		
		for(int i = 0; i < moveAmount; i++) {
			
			int bestMoveIndex = 0;
			int bestMoveScore = moveScores[0];
			
			for(int j = 1; j < moveAmount; j++) {
				
				int s = moveScores[j];
				if(s > bestMoveScore) {
					
					bestMoveIndex = j;
					bestMoveScore = s;
				}
			}
			
			moveScores[bestMoveIndex] = MISSING_MOVE_SCORE;
			
			int moveCellX = moves[bestMoveIndex];
			
			playMove(moveCellX);
			
			int s = -evaluateWithNoImmediateWin(-maximalScore, -minimalScore);
			nodesVisited[depth] += nodesVisited[nextDepth];
			
			undoMove();
			
			if(s >= maximalScore) {
				
				int nodes = nodesVisited[depth];
				
				scoreCache.updateEntry(hash, mixedHash, s, maxScore, nodes);
				return s;
			}
			
			if(s > minimalScore) {
				
				minimalScore = s;
			}
		}
		
		int nodes = nodesVisited[depth];
		
		scoreCache.updateEntry(hash, mixedHash, minScore, minimalScore, nodes);
		
		return minimalScore;
	}
	
	private int moveScore(int moveCellPosition, long moveBitboard) {
		long board = activeBitboard;
		long mask = maskBitboard;
		
		board ^= moveBitboard;
		mask ^= moveBitboard;
		
		long result = 0;
		
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			long b2 = board;
			
			b2 &= b2 >>> direction;
			
			long b3 = b2;
			b3 &= b3 >>> direction;
			
			result |= b3 >>> direction;
			result |= b3 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
			
			long b4 = (b2 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH)) & board;
			long b5 = (b2 >>> (direction << 1)) & board;
			
			result |= b4 >>> direction;
			result |= b5 << direction;
		}
		
		result &= ~mask;
		result &= Bitboards.FULL_BOARD;
		
		int openScore = Long.bitCount(result) * MOVE_SCORE_CONNECTION_OPPORTUNITY_WEIGHT;
		
		result &= result << 1;
		
		openScore += Long.bitCount(result) * MOVE_SCORE_COLUMN_FORK_WEIGHT;
		
		boolean redAtTurn = (filledCellAmount & 1) == 0;
		int[] moveCellScores = redAtTurn ? RED_MOVE_CELL_SCORES : YELLOW_MOVE_CELL_SCORES;
		
		return openScore + moveCellScores[moveCellPosition];
	}
	
	private boolean activePlayerHasImmediateWin() {
		long result = 0;
		
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			long b1 = activeBitboard;
			long b2 = b1;
			
			b2 &= b2 >>> direction;
			
			long b3 = b2;
			b3 &= b3 >>> direction;
			
			result |= b3 >>> direction;
			result |= b3 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
			
			long b4 = (b2 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH)) & b1;
			long b5 = (b2 >>> (direction << 1)) & b1;
			
			result |= b4 >>> direction;
			result |= b5 << direction;
		}
		
		result &= Bitboards.FULL_BOARD;
		result &= ceilingBitboard;
		
		return result != 0;
	}
	
	private long opponentWinBitboard() {
		long board = activeBitboard;
		long mask = maskBitboard;
		
		board ^= mask;
		
		long result = 0;
		
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			long b2 = board;
			
			b2 &= b2 >>> direction;
			
			long b3 = b2;
			b3 &= b3 >>> direction;
			
			result |= b3 >>> direction;
			result |= b3 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH);
			
			long b4 = (b2 << (direction * BITBOARD_CONNECTION_OPPORTUNITY_LENGTH)) & board;
			long b5 = (b2 >>> (direction << 1)) & board;
			
			result |= b4 >>> direction;
			result |= b5 << direction;
		}
		
		result &= Bitboards.FULL_BOARD;
		return result & ~mask;
	}
	
	public boolean moveLegal(int moveCellX) {
		if(outcome != BoardOutcome.UNDECIDED) return false;
		
		return cellColumnHeights[moveCellX] < HEIGHT;
	}
	
	public void playMove(int moveCellX) {
		BoardHistoryEntry entry = history.entry(filledCellAmount);
		entry.fill(
				bitboard, activeBitboard, maskBitboard, ceilingBitboard,
				mirroredBitboard, mirroredActiveBitboard, mirroredMaskBitboard, mirroredCeilingBitboard,
				hash, mixedHash
		);
		
		int moveCellY = cellColumnHeights[moveCellX];
		cellColumnHeights[moveCellX]++;
		
		playedMoves[filledCellAmount] = moveCellX;
		filledCellAmount++;
		
		int mirroredMoveCellX = LARGEST_MOVE_CELL_X - moveCellX;
		
		long b1 = Bitboards.cellBitboard(moveCellX, moveCellY);
		long b2 = Bitboards.cellBitboard(mirroredMoveCellX, moveCellY);
		
		activeBitboard = activeBitboard ^ maskBitboard;
		maskBitboard ^= b1;
		ceilingBitboard ^= b1;
		
		mirroredActiveBitboard = mirroredActiveBitboard ^ mirroredMaskBitboard;
		mirroredMaskBitboard ^= b2;
		mirroredCeilingBitboard ^= b2;
		
		b1 <<= 1;
		b2 <<= 1;
		
		ceilingBitboard ^= b1;
		bitboard = activeBitboard | ceilingBitboard;
		
		mirroredCeilingBitboard ^= b2;
		mirroredBitboard = mirroredActiveBitboard | mirroredCeilingBitboard;
		
		long board = activeBitboard ^ maskBitboard;
		if(bitboardContainsConnection(board)) {
			
			boolean redAtTurn = (filledCellAmount & 1) == 0;
			outcome = redAtTurn ? BoardOutcome.YELLOW_WIN : BoardOutcome.RED_WIN;
			
			return;
		}
		
		if(filledCellAmount == FULL_CELL_AMOUNT) outcome = BoardOutcome.DRAW;
		
		updateHash();
	}
	
	public void undoMove() {
		outcome = BoardOutcome.UNDECIDED;
		
		filledCellAmount--;
		int moveCellX = playedMoves[filledCellAmount];
		
		BoardHistoryEntry entry = history.entry(filledCellAmount);
		
		cellColumnHeights[moveCellX]--;
		
		bitboard = entry.getBitboard();
		activeBitboard = entry.getActiveBitboard();
		maskBitboard = entry.getMaskBitboard();
		ceilingBitboard = entry.getCeilingBitboard();
		
		mirroredBitboard = entry.getMirroredBitboard();
		mirroredActiveBitboard = entry.getMirroredActiveBitboard();
		mirroredMaskBitboard = entry.getMirroredMaskBitboard();
		mirroredCeilingBitboard = entry.getMirroredCeilingBitboard();
		
		hash = entry.getHash();
		mixedHash = entry.getMixedHash();
	}
	
	private void updateHash() {
		hash = bitboard;
		if(hash < mirroredBitboard) hash = mirroredBitboard;
		
		mixedHash = hash;
		mixedHash ^= mixedHash >>> HASH_MIX_SHIFT_AMOUNT;
		
		for(long m : HASH_MIX_MAGICS) {
			
			mixedHash *= m;
			mixedHash ^= mixedHash >>> HASH_MIX_SHIFT_AMOUNT;
		}
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
		
		long board = Bitboards.cellBitboard(cellX, cellY);
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
	
	private static boolean bitboardContainsConnection(long bitboard) {
		for(int direction : BITBOARD_CONNECTION_DIRECTIONS) {
			
			long board = bitboard;
			board &= board >>> direction;
			board &= board >>> (direction << 1);
			
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
	
	private static String formatMoveScore(int moveScore) {
		if(moveScore > 0) return WINNING_MOVE_FORMAT_PREFIX + moveScore;
		
		return String.valueOf(moveScore);
	}
	
}
