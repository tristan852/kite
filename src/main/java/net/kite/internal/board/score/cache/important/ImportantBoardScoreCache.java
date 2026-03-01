package net.kite.internal.board.score.cache.important;

import java.util.Arrays;

public class ImportantBoardScoreCache {
	
	private static final int CAPACITY  = 4096;
	private static final long KEY_MASK = 4095;
	
	private static final long ENTRY_DATA_PARTIAL_HASH_MASK  = 0xFFFFFFFFFFFFF000L;
	private static final long ENTRY_DATA_SCORE_MASK = 0x00000000000000FFL;
	
	private static final int ENTRY_DATA_SCORE_UNPACK_OFFSET = 56;
	
	private static final int MISSING_ENTRY_KEY = -1;
	private static final long MISSING_ENTRY_DATA = 0x0000000000000080L;
	
	private final long[] entryData;
	
	public ImportantBoardScoreCache() {
		this.entryData = new long[CAPACITY];
		
		Arrays.fill(entryData, MISSING_ENTRY_DATA);
	}
	
	public void updateEntry(long mixedHash, int score) {
		int key = (int) (mixedHash & KEY_MASK);
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		mixedHash |= score & ENTRY_DATA_SCORE_MASK;
		
		entryData[key] = mixedHash;
	}
	
	public boolean entryFilled(int entryKey) {
		return entryData[entryKey] != MISSING_ENTRY_DATA;
	}
	
	public long entryMixedHash(int entryKey) {
		long mixedHash = entryData[entryKey];
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		mixedHash |= entryKey;
		
		return mixedHash;
	}
	
	public int entryScore(int entryKey) {
		long minimalScore = entryData[entryKey];
		
		minimalScore <<= ENTRY_DATA_SCORE_UNPACK_OFFSET;
		minimalScore >>= ENTRY_DATA_SCORE_UNPACK_OFFSET;
		
		return (int) minimalScore;
	}
	
	public int entryKey(long mixedHash) {
		int key = (int) (mixedHash & KEY_MASK);
		
		long data = entryData[key];
		if(data == MISSING_ENTRY_DATA) return MISSING_ENTRY_KEY;
		
		long h = data & ENTRY_DATA_PARTIAL_HASH_MASK;
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		
		return h == mixedHash ? key : MISSING_ENTRY_KEY;
	}
	
	public static int getCapacity() {
		return CAPACITY;
	}
	
}
