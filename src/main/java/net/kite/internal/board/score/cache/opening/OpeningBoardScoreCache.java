package net.kite.internal.board.score.cache.opening;

import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;
import net.kite.internal.board.score.cache.BoardScoreCache;

import java.io.*;

public final class OpeningBoardScoreCache {
	
	private static final int PACKED_BOARD_SCORES_SIZE_IN_BYTES = 72397224;
	private static final int HASH_FUNCTION_SIZE_IN_BYTES       = 27134232;
	
	private static final int BOARD_SCORE_SIZE_IN_BITS = 6;
	private static final int BOARD_SCORE_MASK = 0x3F;
	private static final int BOARD_SCORE_OFFSET = -18;
	
	private static final int BYTE_MASK = 0xFF;
	
	private final byte[] packedBoardScores;
	
	private GOVMinimalPerfectHashFunction<Long> hashFunction;
	
	public OpeningBoardScoreCache() {
		this.packedBoardScores = new byte[PACKED_BOARD_SCORES_SIZE_IN_BYTES];
	}
	
	public void loadFromResources(String resourcePath) {
		InputStream inputStream = BoardScoreCache.class.getResourceAsStream(resourcePath);
		
		if(inputStream == null) {
			
			System.err.println("The opening score cache could not be found in resources!");
			return;
		}
		
		try(inputStream) {
			
			inputStream.readNBytes(packedBoardScores, 0, PACKED_BOARD_SCORES_SIZE_IN_BYTES);
			
			@SuppressWarnings("unchecked")
			GOVMinimalPerfectHashFunction<Long> hf = (GOVMinimalPerfectHashFunction<Long>) BinIO.loadObject(inputStream);
			
			hashFunction = hf;
			
		} catch(IOException | ClassNotFoundException exception) {
			
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
			
			System.arraycopy(bytes, 0, packedBoardScores, 0, PACKED_BOARD_SCORES_SIZE_IN_BYTES);
			InputStream stream = new ByteArrayInputStream(bytes, PACKED_BOARD_SCORES_SIZE_IN_BYTES, HASH_FUNCTION_SIZE_IN_BYTES);
			
			@SuppressWarnings("unchecked")
			GOVMinimalPerfectHashFunction<Long> hf = (GOVMinimalPerfectHashFunction<Long>) BinIO.loadObject(stream);
			
			hashFunction = hf;
			
		} catch(Exception exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache from bytes: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public int boardScore(long mixedHash) {
		int index = (int) hashFunction.getLong(mixedHash);
		index *= BOARD_SCORE_SIZE_IN_BITS;
		
		int byteIndex = index >> 3;
		int bitIndex = index - (byteIndex << 3);
		
		int i = (packedBoardScores[byteIndex + 1] << 8) | (packedBoardScores[byteIndex] & BYTE_MASK);
		
		i >>>= bitIndex;
		i &= BOARD_SCORE_MASK;
		
		return i + BOARD_SCORE_OFFSET;
	}
	
}
