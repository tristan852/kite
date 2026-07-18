package net.kite.internal.board.score.cache.opening;

import net.kite.internal.board.score.cache.BoardScoreCache;

import java.io.IOException;
import java.io.InputStream;

public final class OpeningBoardScoreCache {
	
	private static final int PACKED_BOARD_SCORES_SIZE_IN_BYTES = 61737771;
	private static final int BOARD_SCORES_SIZE                 = 82317028;
	private static final int BUCKET_SEEDS_SIZE_IN_BYTES        = 33554432;
	private static final int BUCKET_SEEDS_INDEX_MASK           = 33554431;
	
	private static final int BOARD_SCORE_MASK = 0x3F;
	private static final int BOARD_SCORE_OFFSET = -18;
	
	private static final long MIXED_LONG_ADDEND            = 0x9E3779B97F4A7C15L;
	private static final long MIXED_LONG_FIRST_MULTIPLIER  = 0xBF58476D1CE4E5B9L;
	private static final long MIXED_LONG_SECOND_MULTIPLIER = 0x94D049BB133111EBL;
	
	private static final int MIXED_LONG_FIRST_SHIFT_AMOUNT  = 30;
	private static final int MIXED_LONG_SECOND_SHIFT_AMOUNT = 27;
	private static final int MIXED_LONG_THIRD_SHIFT_AMOUNT  = 31;
	
	private static final int BYTE_MASK = 0xFF;
	
	private final byte[] packedBoardScores;
	private final byte[] bucketSeeds;
	
	public OpeningBoardScoreCache() {
		this.packedBoardScores = new byte[PACKED_BOARD_SCORES_SIZE_IN_BYTES];
		this.bucketSeeds = new byte[BUCKET_SEEDS_SIZE_IN_BYTES];
	}
	
	public void loadFromResources(String resourcePath) {
		InputStream inputStream = BoardScoreCache.class.getResourceAsStream(resourcePath);
		
		if(inputStream == null) {
			
			System.err.println("The opening score cache could not be found in resources!");
			return;
		}
		
		try(inputStream) {
			
			inputStream.readNBytes(bucketSeeds, 0, BUCKET_SEEDS_SIZE_IN_BYTES);
			inputStream.readNBytes(packedBoardScores, 0, PACKED_BOARD_SCORES_SIZE_IN_BYTES);
			
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
			
			System.arraycopy(bytes, 0, bucketSeeds, 0, BUCKET_SEEDS_SIZE_IN_BYTES);
			System.arraycopy(bytes, BUCKET_SEEDS_SIZE_IN_BYTES, packedBoardScores, 0, PACKED_BOARD_SCORES_SIZE_IN_BYTES);
			
		} catch(Exception exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache from bytes: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public int boardScore(long mixedHash) {
		int bucketIndex = ((int) mixedHash) & BUCKET_SEEDS_INDEX_MASK;
		byte bucketSeed = bucketSeeds[bucketIndex];
		
		int index = (int) Long.remainderUnsigned(mixLong(mixedHash, bucketSeed), BOARD_SCORES_SIZE);
		
		int byteIndex = index >> 3;
		int bitIndex = index - (byteIndex << 3);
		
		int i = (packedBoardScores[byteIndex + 1] << 8) | (packedBoardScores[byteIndex] & BYTE_MASK);
		
		i >>>= bitIndex;
		i &= BOARD_SCORE_MASK;
		
		return i + BOARD_SCORE_OFFSET;
	}
	
	private static long mixLong(long l, byte seed) {
		long s = ((long) seed) & BYTE_MASK;
		
		s = mixLong(s);
		l ^= s;
		
		return mixLong(l);
	}
	
	private static long mixLong(long l) {
		l += MIXED_LONG_ADDEND;
		
		l = (l ^ (l >>> MIXED_LONG_FIRST_SHIFT_AMOUNT)) * MIXED_LONG_FIRST_MULTIPLIER;
		l = (l ^ (l >>> MIXED_LONG_SECOND_SHIFT_AMOUNT)) * MIXED_LONG_SECOND_MULTIPLIER;
		
		return l ^ (l >>> MIXED_LONG_THIRD_SHIFT_AMOUNT);
	}
	
}
