package net.kite.board.score.cache;

import net.kite.board.score.cache.entry.BoardScoreCacheEntry;

public class BoardScoreCache {
	
	private static final int DEFAULT_CAPACITY = 8388608;
	
	private final BoardScoreCacheEntry[] entries;
	
	private final long keyMask;
	
	public BoardScoreCache() {
		this(DEFAULT_CAPACITY);
	}
	
	public BoardScoreCache(int capacity) {
		this.entries = new BoardScoreCacheEntry[capacity];
		this.keyMask = capacity - 1;
		
		for(int i = 0; i < capacity; i++) {
			
			entries[i] = new BoardScoreCacheEntry();
		}
	}
	
	public void updateEntry(long hash, long mixedHash, int minimalScore, int maximalScore, int nodesVisited) {
		int key = (int) (mixedHash & keyMask);
		
		BoardScoreCacheEntry entry = entries[key];
		
		entry.update(hash, minimalScore, maximalScore, nodesVisited);
	}
	
	public BoardScoreCacheEntry entry(long hash, long mixedHash) {
		int key = (int) (mixedHash & keyMask);
		
		BoardScoreCacheEntry entry = entries[key];
		
		long h = entry.getHash();
		return h == hash ? entry : null;
	}
	
	public BoardScoreCacheEntry entry(int entryIndex) {
		return entries[entryIndex];
	}
	
	public int capacity() {
		return entries.length;
	}
	
}
