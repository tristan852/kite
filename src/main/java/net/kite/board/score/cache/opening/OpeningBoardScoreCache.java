package net.kite.board.score.cache.opening;

import net.kite.board.Board;
import net.kite.board.score.BoardScore;
import net.kite.board.score.cache.BoardScoreCache;

import java.io.IOException;
import java.io.InputStream;

public class OpeningBoardScoreCache {
	
	private static final int CAPACITY = 16777259;
	private static final int MAXIMAL_DEPTH = 14;
	
	private static final long BOARD_PARTIAL_COLUMN_HASH_MASK = 0x00000000000000FFL;
	
	private final byte[] boardPartialColumnHashes;
	private final byte[] boardScores;
	
	public OpeningBoardScoreCache() {
		this.boardPartialColumnHashes = new byte[CAPACITY];
		this.boardScores = new byte[CAPACITY];
	}
	
	public void loadFromResources(String resourcePath) {
		InputStream inputStream = BoardScoreCache.class.getResourceAsStream(resourcePath);
		
		if(inputStream == null) {
			
			System.err.println("The opening score cache could not be found in resources!");
			return;
		}
		
		try(inputStream) {
			
			inputStream.readNBytes(boardPartialColumnHashes, 0, CAPACITY);
			inputStream.readNBytes(boardScores, 0, CAPACITY);
			
			for(int i = 0; i < CAPACITY; i++) {
				
				boardScores[i] += BoardScore.INVALID;
			}
			
		} catch(IOException exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache from resources: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public void loadFromBytes(byte[] bytes) {
		if(bytes == null) {
			
			System.err.println("The opening score cache could not be found in web resources!");
			return;
		}
		
		try {
			
			System.arraycopy(bytes, 0, boardPartialColumnHashes, 0, CAPACITY);
			System.arraycopy(bytes, CAPACITY, boardScores, 0, CAPACITY);
			
			for(int i = 0; i < CAPACITY; i++) {
				
				boardScores[i] += BoardScore.INVALID;
			}
			
		} catch(Exception exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache from bytes: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public int boardScore(Board board) {
		int n = board.playedMoveAmount();
		if(n > MAXIMAL_DEPTH) return Integer.MIN_VALUE;
		
		long columnHash = board.columnHash();
		int index = (int) Long.remainderUnsigned(columnHash, CAPACITY);
		
		byte boardScore = boardScores[index];
		if(boardScore == BoardScore.INVALID) return Integer.MIN_VALUE;
		
		long partialColumnHash = boardPartialColumnHashes[index];
		
		columnHash &= BOARD_PARTIAL_COLUMN_HASH_MASK;
		partialColumnHash &= BOARD_PARTIAL_COLUMN_HASH_MASK;
		
		if(columnHash == partialColumnHash) return boardScore;
		
		return Integer.MIN_VALUE;
	}
	
}
