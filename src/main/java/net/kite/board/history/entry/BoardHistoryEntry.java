package net.kite.board.history.entry;

public class BoardHistoryEntry {
	
	private boolean symmetrical;
	
	private long bitboard;
	private long activeBitboard;
	private long maskBitboard;
	private long ceilingBitboard;
	
	private long hash;
	private long mixedHash;
	
	public void fill(
			boolean symmetrical,
			long bitboard, long activeBitboard, long maskBitboard, long ceilingBitboard,
			long hash, long mixedHash
	) {
		this.symmetrical = symmetrical;
		
		this.bitboard = bitboard;
		this.activeBitboard = activeBitboard;
		this.maskBitboard = maskBitboard;
		this.ceilingBitboard = ceilingBitboard;
		
		this.hash = hash;
		this.mixedHash = mixedHash;
	}
	
	public boolean isSymmetrical() {
		return symmetrical;
	}
	
	public long getBitboard() {
		return bitboard;
	}
	
	public long getActiveBitboard() {
		return activeBitboard;
	}
	
	public long getMaskBitboard() {
		return maskBitboard;
	}
	
	public long getCeilingBitboard() {
		return ceilingBitboard;
	}
	
	public long getHash() {
		return hash;
	}
	
	public long getMixedHash() {
		return mixedHash;
	}
	
}
