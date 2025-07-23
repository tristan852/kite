package net.kite.board.score.cache;

import net.kite.board.bit.Bitboards;

public class BoardScoreCache {
	
	private static final byte MAXIMAL_ENTRY_AGE = 10;
	private static final int MAXIMAL_ENTRY_LOW_EFFORT_NODES_VISITED = 100;
	
	private static final int DEFAULT_CAPACITY = 8388608;
	
	private static final int MISSING_ENTRY_KEY = -1;
	
	private final long[] entryHashes;
	
	private final byte[] entryMinimalScores;
	private final byte[] entryMaximalScores;
	
	private final int[] entryNodesVisited;
	private final byte[] entryAges;
	
	private final long keyMask;
	
	public BoardScoreCache() {
		this(DEFAULT_CAPACITY);
	}
	
	public BoardScoreCache(int capacity) {
		this.entryHashes = new long[capacity];
		this.entryMinimalScores = new byte[capacity];
		this.entryMaximalScores = new byte[capacity];
		this.entryNodesVisited = new int[capacity];
		this.entryAges = new byte[capacity];
		
		this.keyMask = capacity - 1;
	}
	
	public void updateEntry(long hash, long mixedHash, int minimalScore, int maximalScore, int nodesVisited) {
		int key = (int) (mixedHash & keyMask);
		
		long h = entryHashes[key];
		if(h == hash) {
			
			int min = entryMinimalScores[key];
			int max = entryMaximalScores[key];
			
			if(minimalScore > min) entryMinimalScores[key] = (byte) minimalScore;
			if(maximalScore < max) entryMaximalScores[key] = (byte) maximalScore;
			
			entryNodesVisited[key] += nodesVisited;
			
			return;
		}
		
		int nodes = entryNodesVisited[key];
		if(nodes > MAXIMAL_ENTRY_LOW_EFFORT_NODES_VISITED && nodesVisited <= nodes) {
			
			entryAges[key]++;
			if(entryAges[key] <= MAXIMAL_ENTRY_AGE) return;
		}
		
		entryHashes[key] = hash;
		entryMinimalScores[key] = (byte) minimalScore;
		entryMaximalScores[key] = (byte) maximalScore;
		entryNodesVisited[key] = nodesVisited;
		entryAges[key] = 0;
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
	
	public int entryNodesVisited(int entryKey) {
		return entryNodesVisited[entryKey];
	}
	
	public int entryAge(int entryKey) {
		return entryAges[entryKey];
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
