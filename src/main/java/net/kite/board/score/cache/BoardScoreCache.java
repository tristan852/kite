package net.kite.board.score.cache;

import net.kite.board.bit.Bitboards;

public class BoardScoreCache {
	
	private static final int CAPACITY = 1048576;
	private static final long KEY_MASK = 1048575;
	
	private static final int MISSING_ENTRY_KEY = -1;
	
	private final long[] entryHashes;
	
	private final byte[] entryMinimalScores;
	private final byte[] entryMaximalScores;
	
	public BoardScoreCache() {
		this.entryHashes = new long[CAPACITY];
		this.entryMinimalScores = new byte[CAPACITY];
		this.entryMaximalScores = new byte[CAPACITY];
	}
	
	public void updateEntry(long hash, long mixedHash, int minimalScore, int maximalScore) {
		int key = (int) (mixedHash & KEY_MASK);
		
		long h = entryHashes[key];
		if(h == hash) {
			
			int min = entryMinimalScores[key];
			int max = entryMaximalScores[key];
			
			if(minimalScore > min) entryMinimalScores[key] = (byte) minimalScore;
			if(maximalScore < max) entryMaximalScores[key] = (byte) maximalScore;
			
			return;
		}
		
		entryHashes[key] = hash;
		
		entryMinimalScores[key] = (byte) minimalScore;
		entryMaximalScores[key] = (byte) maximalScore;
	}
	
	public boolean entryFilled(int entryKey) {
		return entryHashes[entryKey] != Bitboards.EMPTY;
	}
	
	public long entryHash(int entryKey) {
		return entryHashes[entryKey];
	}
	
	public int entryMinimalScore(int entryKey) {
		return entryMinimalScores[entryKey];
	}
	
	public int entryMaximalScore(int entryKey) {
		return entryMaximalScores[entryKey];
	}
	
	public int entryKey(long hash, long mixedHash) {
		int key = (int) (mixedHash & KEY_MASK);
		
		long h = entryHashes[key];
		return h == hash ? key : MISSING_ENTRY_KEY;
	}
	
	public static int getCapacity() {
		return CAPACITY;
	}
	
}
