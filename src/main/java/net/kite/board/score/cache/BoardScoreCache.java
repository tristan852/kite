package net.kite.board.score.cache;

import net.kite.board.bit.Bitboards;

public class BoardScoreCache {
	
	private static final int DEFAULT_CAPACITY = 8388608;
	
	private static final int MISSING_ENTRY_KEY = -1;
	
	private final long[] entryHashes;
	
	private final byte[] entryMinimalScores;
	private final byte[] entryMaximalScores;
	
	private final long keyMask;
	
	public BoardScoreCache() {
		this(DEFAULT_CAPACITY);
	}
	
	public BoardScoreCache(int capacity) {
		this.entryHashes = new long[capacity];
		this.entryMinimalScores = new byte[capacity];
		this.entryMaximalScores = new byte[capacity];
		
		this.keyMask = capacity - 1;
	}
	
	public void updateEntry(long hash, long mixedHash, int minimalScore, int maximalScore) {
		int key = (int) (mixedHash & keyMask);
		
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
		int key = (int) (mixedHash & keyMask);
		
		long h = entryHashes[key];
		return h == hash ? key : MISSING_ENTRY_KEY;
	}
	
	public int capacity() {
		return entryHashes.length;
	}
	
}
