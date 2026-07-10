package net.kite.internal.board.score.cache;

public final class BoardScoreCache {
	
	// Warning: do not change, due to removed safety check
	// for probing
	private static final int  CAPACITY = 65536;
	private static final long KEY_MASK = 65535;
	
	private static final long ENTRY_DATA_PARTIAL_HASH_MASK  = 0xFFFFFFFFFFFF0000L;
	private static final long ENTRY_DATA_MINIMAL_SCORE_MASK = 0x00000000000000FFL;
	private static final long ENTRY_DATA_MAXIMAL_SCORE_MASK = 0x000000000000FF00L;
	
	private static final int ENTRY_DATA_MAXIMAL_SCORE_PACK_OFFSET = 8;
	
	private static final int ENTRY_DATA_MAXIMAL_SCORE_UNPACK_OFFSET = 48;
	private static final int ENTRY_DATA_SCORE_UNPACK_OFFSET = 56;
	
	private static final int MISSING_ENTRY_KEY = -1;
	
	private final long[] entryData;
	
	public BoardScoreCache() {
		this.entryData = new long[CAPACITY];
	}
	
	public void updateEntry(long mixedHash, int minimalScore, int maximalScore) {
		int key = (int) (mixedHash & KEY_MASK);
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		
		long data = entryData[key];
		if((data & ENTRY_DATA_PARTIAL_HASH_MASK) == mixedHash) {
			
			int min = (int) ((data << ENTRY_DATA_SCORE_UNPACK_OFFSET) >> ENTRY_DATA_SCORE_UNPACK_OFFSET);
			int max = (int) ((data << ENTRY_DATA_MAXIMAL_SCORE_UNPACK_OFFSET) >> ENTRY_DATA_SCORE_UNPACK_OFFSET);
			
			if(min > minimalScore) minimalScore = min;
			if(max < maximalScore) maximalScore = max;
		}
		
		mixedHash |= minimalScore & ENTRY_DATA_MINIMAL_SCORE_MASK;
		mixedHash |= (maximalScore << ENTRY_DATA_MAXIMAL_SCORE_PACK_OFFSET) & ENTRY_DATA_MAXIMAL_SCORE_MASK;
		
		entryData[key] = mixedHash;
	}
	
	public boolean entryFilled(int entryKey) {
		return entryData[entryKey] != 0;
	}
	
	public long entryMixedHash(int entryKey) {
		long mixedHash = entryData[entryKey];
		
		mixedHash &= ENTRY_DATA_PARTIAL_HASH_MASK;
		mixedHash |= entryKey;
		
		return mixedHash;
	}
	
	public int entryMinimalScore(int entryKey) {
		long minimalScore = entryData[entryKey];
		
		minimalScore <<= ENTRY_DATA_SCORE_UNPACK_OFFSET;
		minimalScore >>= ENTRY_DATA_SCORE_UNPACK_OFFSET;
		
		return (int) minimalScore;
	}
	
	public int entryMaximalScore(int entryKey) {
		long maximalScore = entryData[entryKey];
		
		maximalScore <<= ENTRY_DATA_MAXIMAL_SCORE_UNPACK_OFFSET;
		maximalScore >>= ENTRY_DATA_SCORE_UNPACK_OFFSET;
		
		return (int) maximalScore;
	}
	
	public int entryKey(long mixedHash) {
		int key = (int) (mixedHash & KEY_MASK);
		
		return (entryData[key] & ENTRY_DATA_PARTIAL_HASH_MASK) == (mixedHash & ENTRY_DATA_PARTIAL_HASH_MASK) ? key : MISSING_ENTRY_KEY;
	}
	
	public static int getCapacity() {
		return CAPACITY;
	}
	
}
