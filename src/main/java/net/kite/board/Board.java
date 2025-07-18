package net.kite.board;

import net.kite.board.bit.Bitboard;
import net.kite.board.bit.Bitboards;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.player.color.BoardPlayerColor;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCaches;
import net.kite.board.score.cache.entry.BoardScoreCacheEntry;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;

public class Board {
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private static final int FULL_CELL_AMOUNT = 42;
	
	private static final int[] ORDERED_MOVE_CELL_XS = new int[] {
			3, 2, 4, 1, 5, 0, 6
	};
	
	private static final int[] SYMMETRIC_ORDERED_MOVE_CELL_XS = new int[] {
			3, 2, 1, 0
	};
	
	private static final String TO_STRING_CELL_ROW_SEPARATOR_STRING = "\n";
	private static final String TO_STRING_EMPTY_CELL_STRING = ".";
	private static final String TO_STRING_MOVES_PREFIX_STRING = "moves: ";
	
	private static final int[] BITBOARD_CONNECTION_DIRECTIONS = new int[] {
			1,
			7,
			8,
			9
	};
	
	private static final int BITBOARD_CONNECTION_OPPORTUNITY_LENGTH = 3;
	
	private static final int LOGARITHMIC_BITBOARD_LENGTH = 3;
	private static final int BITBOARD_LENGTH_MASK = 7;
	
	private static final int LARGEST_MOVE_CELL_X = 6;
	
	private static final int MISSING_MOVE_SCORE = Integer.MIN_VALUE;
	
	private static final char SMALLEST_MOVE_CHARACTER = '1';
	
	private static final long[] HASH_MIX_MAGICS = new long[] {
			0xFF51AFD7ED558CCDL,
			0xC4CEB9FE1A85EC53L
	};
	
	private static final int HASH_MIX_SHIFT_AMOUNT = 33;
	
	private final BoardPlayerColor[][] cellPlayerColors;
	
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
	
	private BoardPlayerColor activePlayerColor = BoardPlayerColor.RED;
	private BoardOutcome outcome = BoardOutcome.UNDECIDED;
	
	private final int[][] moves;
	private final int[][] moveScores;
	
	private final int[] playedMoves;
	private final int[] nodesVisited;
	
	private long hash = Bitboards.EMPTY_CEILING;
	private long mixedHash = 0x2373BFB0BD385EEAL;
	
	public Board() {
		this.cellPlayerColors = new BoardPlayerColor[WIDTH][HEIGHT];
		this.moves = new int[FULL_CELL_AMOUNT][WIDTH];
		this.moveScores = new int[FULL_CELL_AMOUNT][WIDTH];
		this.playedMoves = new int[FULL_CELL_AMOUNT];
		
		int l = FULL_CELL_AMOUNT;
		l++;
		
		this.nodesVisited = new int[l];
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
		
		for(int y = 0; y < height; y++) {
			
			pascalHash *= 3;
			
			boolean activeCell = cellPlayerColors[x][y] == activePlayerColor;
			
			if(activeCell) pascalHash++;
			else pascalHash += 2;
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
	
	public int evaluate() {
		if(outcome != BoardOutcome.UNDECIDED) {
			
			if(outcome == BoardOutcome.DRAW) return BoardScore.DRAW;
			return BoardScore.lose(filledCellAmount);
		}
		
		if(activePlayerHasImmediateWin()) {
			
			return BoardScore.win(filledCellAmount + 1);
		}
		
		int minimalScore = BoardScore.minimal(filledCellAmount);
		int maximalScore = BoardScore.maximal(filledCellAmount);
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		BoardScoreCacheEntry entry = BoardScoreCaches.DEFAULT.entry(hash, mixedHash);
		if(entry != null) {
			
			int entryMinScore = entry.getMinimalScore();
			int entryMaxScore = entry.getMaximalScore();
			
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
			return BoardScore.lose(filledCellAmount);
		}
		
		int minScore = BoardScore.minimal(filledCellAmount);
		int maxScore = BoardScore.maximal(filledCellAmount);
		
		if(minScore > minimalScore) minimalScore = minScore;
		if(maxScore < maximalScore) maximalScore = maxScore;
		
		if(minimalScore >= maximalScore) return minimalScore;
		
		int openingBoardScore = OpeningBoardScoreCaches.DEFAULT.boardScore(this);
		if(openingBoardScore != Integer.MIN_VALUE) return openingBoardScore;
		
		BoardScoreCacheEntry entry = BoardScoreCaches.DEFAULT.entry(hash, mixedHash);
		if(entry != null) {
			
			int entryMinScore = entry.getMinimalScore();
			int entryMaxScore = entry.getMaximalScore();
			
			if(entryMinScore > minimalScore) minimalScore = entryMinScore;
			if(entryMaxScore < maximalScore) maximalScore = entryMaxScore;
			
			if(minimalScore >= maximalScore) return minimalScore;
		}
		
		long opponentWinBitboard = opponentWinBitboard();
		long immediateThreats = opponentWinBitboard & ceilingBitboard;
		
		if(immediateThreats != Bitboards.EMPTY) {
			
			int p = Bitboard.firstCellPosition(immediateThreats);
			int forcedX = p >>> LOGARITHMIC_BITBOARD_LENGTH;
			int forcedY = p & BITBOARD_LENGTH_MASK;
			long b = Bitboards.cellBitboard(forcedX, forcedY);
			
			immediateThreats ^= b;
			if(immediateThreats != Bitboards.EMPTY) {
				
				return minScore;
			}
			
			long upperCellBitboard = Bitboards.cellBitboard(forcedX, forcedY + 1);
			if((upperCellBitboard & opponentWinBitboard) != 0) {
				
				return minScore;
			}
			
			playMove(forcedX);
			
			int s = -evaluateWithNoImmediateWin(-maximalScore, -minimalScore);
			nodesVisited[depth] += nodesVisited[nextDepth];
			
			undoMove();
			
			if(s >= maximalScore) {
				
				int nodes = nodesVisited[depth];
				
				saveToCache(s, maxScore, nodes);
				return s;
			}
			
			if(s > minimalScore) minimalScore = s;
			
			int nodes = nodesVisited[depth];
			
			saveToCache(minScore, minimalScore, nodes);
			
			return minimalScore;
		}
		
		int[] moves = this.moves[filledCellAmount];
		int[] moveScores = this.moveScores[filledCellAmount];
		int moveAmount = 0;
		
		boolean symmetric = bitboard == mirroredBitboard;
		int[] orderedMoves = symmetric ? SYMMETRIC_ORDERED_MOVE_CELL_XS : ORDERED_MOVE_CELL_XS;
		
		for(int moveCellX : orderedMoves) {
			
			int cellColumnHeight = cellColumnHeights[moveCellX];
			if(cellColumnHeight == HEIGHT) continue;
			
			long upperCellBitboard = Bitboards.cellBitboard(moveCellX, cellColumnHeight + 1);
			if((upperCellBitboard & opponentWinBitboard) != 0) {
				
				continue;
			}
			
			int moveScore = moveScore(moveCellX);
			int moveIndex = moveAmount;
			
			moves[moveIndex] = moveCellX;
			moveScores[moveIndex] = moveScore;
			
			moveAmount++;
		}
		
		for(int i = 0; i < moveAmount; i++) {
			
			int bestMoveIndex = 0;
			int bestMoveScore = MISSING_MOVE_SCORE;
			
			for(int j = 0; j < moveAmount; j++) {
				
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
				
				saveToCache(s, maxScore, nodes);
				return s;
			}
			
			if(s > minimalScore) minimalScore = s;
		}
		
		int nodes = nodesVisited[depth];
		
		saveToCache(minScore, minimalScore, nodes);
		
		return minimalScore;
	}
	
	private void saveToCache(int minimalScore, int maximalScore, int nodesVisited) {
		BoardScoreCaches.DEFAULT.updateEntry(hash, mixedHash, minimalScore, maximalScore, nodesVisited);
	}
	
	private int moveScore(int moveCellX) {
		int moveCellY = cellColumnHeights[moveCellX];
		
		long board = activeBitboard;
		long mask = maskBitboard;
		long b = Bitboards.cellBitboard(moveCellX, moveCellY);
		
		board ^= b;
		mask ^= b;
		
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
		
		return Long.bitCount(result);
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
		int moveCellY = cellColumnHeights[moveCellX];
		cellColumnHeights[moveCellX]++;
		
		cellPlayerColors[moveCellX][moveCellY] = activePlayerColor;
		activePlayerColor = activePlayerColor.opposite();
		
		playedMoves[filledCellAmount] = moveCellX;
		filledCellAmount++;
		
		int mirroredMoveCellX = LARGEST_MOVE_CELL_X - moveCellX;
		
		long b1 = Bitboards.cellBitboard(moveCellX, moveCellY);
		long b2 = Bitboards.cellBitboard(mirroredMoveCellX, moveCellY);
		
		maskBitboard ^= b1;
		ceilingBitboard ^= b1;
		
		mirroredMaskBitboard ^= b2;
		mirroredCeilingBitboard ^= b2;
		
		moveCellY++;
		b1 = Bitboards.cellBitboard(moveCellX, moveCellY);
		b2 = Bitboards.cellBitboard(mirroredMoveCellX, moveCellY);
		
		ceilingBitboard ^= b1;
		activeBitboard = (~bitboard) & maskBitboard;
		bitboard = activeBitboard | ceilingBitboard;
		
		mirroredCeilingBitboard ^= b2;
		mirroredActiveBitboard = (~mirroredBitboard) & mirroredMaskBitboard;
		mirroredBitboard = mirroredActiveBitboard | mirroredCeilingBitboard;
		
		long board = (~activeBitboard) & maskBitboard;
		if(bitboardContainsConnection(board)) {
			
			BoardPlayerColor winnerPlayerColor = activePlayerColor.opposite();
			outcome = BoardOutcome.winOfPlayerColor(winnerPlayerColor);
			
			return;
		}
		
		if(filledCellAmount == FULL_CELL_AMOUNT) outcome = BoardOutcome.DRAW;
		
		updateHash();
	}
	
	public void undoMove() {
		outcome = BoardOutcome.UNDECIDED;
		
		filledCellAmount--;
		int moveCellX = playedMoves[filledCellAmount];
		
		cellColumnHeights[moveCellX]--;
		int moveCellY = cellColumnHeights[moveCellX];
		
		activePlayerColor = activePlayerColor.opposite();
		cellPlayerColors[moveCellX][moveCellY] = null;
		
		int mirroredMoveCellX = LARGEST_MOVE_CELL_X - moveCellX;
		
		long b1 = Bitboards.cellBitboard(moveCellX, moveCellY);
		long b2 = Bitboards.cellBitboard(mirroredMoveCellX, moveCellY);
		
		maskBitboard ^= b1;
		ceilingBitboard ^= b1;
		
		mirroredMaskBitboard ^= b2;
		mirroredCeilingBitboard ^= b2;
		
		moveCellY++;
		b1 = Bitboards.cellBitboard(moveCellX, moveCellY);
		b2 = Bitboards.cellBitboard(mirroredMoveCellX, moveCellY);
		
		ceilingBitboard ^= b1;
		activeBitboard = (~bitboard) & maskBitboard;
		bitboard = activeBitboard | ceilingBitboard;
		
		mirroredCeilingBitboard ^= b2;
		mirroredActiveBitboard = (~mirroredBitboard) & mirroredMaskBitboard;
		mirroredBitboard = mirroredActiveBitboard | mirroredCeilingBitboard;
		
		updateHash();
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
	
	public boolean cellFilled(int cellX, int cellY) {
		return cellPlayerColors[cellX][cellY] != null;
	}
	
	public BoardPlayerColor cellPlayerColor(int cellX, int cellY) {
		return cellPlayerColors[cellX][cellY];
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
	
	public static Board boardWithMoves(String moves) {
		Board board = new Board();
		
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
	
}
