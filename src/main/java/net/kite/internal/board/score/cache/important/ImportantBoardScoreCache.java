package net.kite.internal.board.score.cache.important;

import java.util.Arrays;

public class ImportantBoardScoreCache {
	
	private static final int CAPACITY  = 4096;
	private static final long KEY_MASK = 4095;
	
	private static final long ENTRY_DATA_PARTIAL_HASH_MASK  = 0xFFFFFFFFFFFFF000L;
	private static final long ENTRY_DATA_SCORE_MASK = 0x00000000000000FFL;
	private static final long ENTRY_DATA_EXACT_MASK = 0x0000000000000100L;
	
	private static final int ENTRY_DATA_SCORE_UNPACK_OFFSET = 56;
	
	private static final long MISSING_ENTRY_DATA = 0x0000000000000080L;
	
	private final long[] entryData;
	
	public ImportantBoardScoreCache() {
		this.entryData = new long[CAPACITY];
		
		Arrays.fill(entryData, MISSING_ENTRY_DATA);
	}
	
	public void updateEntry(long mixedHash, int score, boolean exact) {
		int key = (int) (mixedHash & KEY_MASK);
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		
		long data = entryData[key];
		if(data != MISSING_ENTRY_DATA) {
			
			long h = data & ENTRY_DATA_PARTIAL_HASH_MASK;
			if(h == mixedHash) {
				
				boolean e = (data & ENTRY_DATA_EXACT_MASK) != 0;
				if(e) return;
				
				int s = (int) ((data << ENTRY_DATA_SCORE_UNPACK_OFFSET) >> ENTRY_DATA_SCORE_UNPACK_OFFSET);
				if(!exact && score <= s) return;
				
			} else if(!exact) {
				
				boolean e = (data & ENTRY_DATA_EXACT_MASK) != 0;
				if(e) return;
			}
		}
		
		mixedHash |= score & ENTRY_DATA_SCORE_MASK;
		if(exact) mixedHash |= ENTRY_DATA_EXACT_MASK;
		
		entryData[key] = mixedHash;
	}
	
	public long entry(long mixedHash) {
		int key = (int) (mixedHash & KEY_MASK);
		
		long data = entryData[key];
		if(data == MISSING_ENTRY_DATA) return Long.MIN_VALUE;
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		
		return (data & ENTRY_DATA_PARTIAL_HASH_MASK) == mixedHash ? data ^ mixedHash : Long.MIN_VALUE;
	}
	
	public static int getCapacity() {
		return CAPACITY;
	}
	
}
