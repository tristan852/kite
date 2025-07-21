package net.kite.board.history.entry;

public class BoardHistoryEntry {
	
	private long bitboard;
	private long activeBitboard;
	private long maskBitboard;
	private long ceilingBitboard;
	
	private long mirroredBitboard;
	private long mirroredActiveBitboard;
	private long mirroredMaskBitboard;
	private long mirroredCeilingBitboard;
	
	private long hash;
	private long mixedHash;
	
	public void fill(
			long bitboard, long activeBitboard, long maskBitboard, long ceilingBitboard,
			long mirroredBitboard, long mirroredActiveBitboard, long mirroredMaskBitboard, long mirroredCeilingBitboard,
			long hash, long mixedHash
	) {
		this.bitboard = bitboard;
		this.activeBitboard = activeBitboard;
		this.maskBitboard = maskBitboard;
		this.ceilingBitboard = ceilingBitboard;
		
		this.mirroredBitboard = mirroredBitboard;
		this.mirroredActiveBitboard = mirroredActiveBitboard;
		this.mirroredMaskBitboard = mirroredMaskBitboard;
		this.mirroredCeilingBitboard = mirroredCeilingBitboard;
		
		this.hash = hash;
		this.mixedHash = mixedHash;
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
	
	public long getMirroredBitboard() {
		return mirroredBitboard;
	}
	
	public long getMirroredActiveBitboard() {
		return mirroredActiveBitboard;
	}
	
	public long getMirroredMaskBitboard() {
		return mirroredMaskBitboard;
	}
	
	public long getMirroredCeilingBitboard() {
		return mirroredCeilingBitboard;
	}
	
	public long getHash() {
		return hash;
	}
	
	public long getMixedHash() {
		return mixedHash;
	}
	
}
