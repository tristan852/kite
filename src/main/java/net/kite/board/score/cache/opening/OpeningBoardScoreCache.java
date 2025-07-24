package net.kite.board.score.cache.opening;

import net.kite.board.Board;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCache;

import java.io.IOException;
import java.io.InputStream;

public class OpeningBoardScoreCache {
	
	private static final int DEFAULT_CAPACITY = 16777259;
	private static final int DEFAULT_MAXIMAL_DEPTH = 14;
	
	private static final long BOARD_PARTIAL_PASCAL_HASH_MASK = 0x00000000000000FFL;
	
	private final byte[] boardPartialPascalHashes;
	private final byte[] boardScores;
	
	private final int maximalDepth;
	
	public OpeningBoardScoreCache() {
		this(DEFAULT_CAPACITY, DEFAULT_MAXIMAL_DEPTH);
	}
	
	public OpeningBoardScoreCache(int capacity, int maximalDepth) {
		this.boardPartialPascalHashes = new byte[capacity];
		this.boardScores = new byte[capacity];
		
		this.maximalDepth = maximalDepth;
	}
	
	public void loadFromResources(String resourcePath) {
		InputStream inputStream = BoardScoreCache.class.getResourceAsStream(resourcePath);
		if(inputStream == null) {
			
			System.err.println("The opening score cache could not be found in resources!");
			return;
		}
		
		try(inputStream) {
			
			int c = boardPartialPascalHashes.length;
			
			inputStream.readNBytes(boardPartialPascalHashes, 0, c);
			inputStream.readNBytes(boardScores, 0, c);
			
			for(int i = 0; i < c; i++) {
				
				boardScores[i] += BoardScore.INVALID;
			}
			
		} catch(IOException exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache from resources: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public int boardScore(Board board) {
		int n = board.playedMoveAmount();
		if(n > maximalDepth) return Integer.MIN_VALUE;
		
		int capacity = boardPartialPascalHashes.length;
		
		long pascalHash = board.pascalHash();
		int index = (int) Long.remainderUnsigned(pascalHash, capacity);
		
		byte boardScore = boardScores[index];
		if(boardScore == BoardScore.INVALID) return Integer.MIN_VALUE;
		
		long partialPascalHash = boardPartialPascalHashes[index];
		
		pascalHash &= BOARD_PARTIAL_PASCAL_HASH_MASK;
		partialPascalHash &= BOARD_PARTIAL_PASCAL_HASH_MASK;
		
		if(pascalHash != partialPascalHash) return Integer.MIN_VALUE;
		
		return boardScore;
	}
	
}
