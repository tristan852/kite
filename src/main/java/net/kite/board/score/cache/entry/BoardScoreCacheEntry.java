package net.kite.board.score.cache.entry;

import net.kite.board.bit.Bitboards;

public class BoardScoreCacheEntry {
	
	private static final int MAXIMAL_AGE = 10;
	private static final int MAXIMAL_LOW_EFFORT_NODES_VISITED = 100;
	
	private long hash;
	
	private int minimalScore;
	private int maximalScore;
	
	private int nodesVisited;
	private int age;
	
	public void update(long hash, int minimalScore, int maximalScore, int nodesVisited) {
		if(this.hash == hash) {
			
			if(minimalScore > this.minimalScore) this.minimalScore = minimalScore;
			if(maximalScore < this.maximalScore) this.maximalScore = maximalScore;
			
			this.nodesVisited += nodesVisited;
			
			return;
		}
		
		if(this.nodesVisited > MAXIMAL_LOW_EFFORT_NODES_VISITED && nodesVisited <= this.nodesVisited) {
			
			age++;
			if(age <= MAXIMAL_AGE) return;
		}
		
		this.hash = hash;
		this.minimalScore = minimalScore;
		this.maximalScore = maximalScore;
		this.nodesVisited = nodesVisited;
		this.age = 0;
	}
	
	public boolean filled() {
		return hash != Bitboards.EMPTY;
	}
	
	public long getHash() {
		return hash;
	}
	
	public int getMinimalScore() {
		return minimalScore;
	}
	
	public int getMaximalScore() {
		return maximalScore;
	}
	
	public int getNodesVisited() {
		return nodesVisited;
	}
	
	public int getAge() {
		return age;
	}
	
}
