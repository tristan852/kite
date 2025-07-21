package net.kite.board.history;

import net.kite.board.history.entry.BoardHistoryEntry;

public class BoardHistory {
	
	private static final int ENTRY_AMOUNT = 42;
	
	private final BoardHistoryEntry[] entries = new BoardHistoryEntry[ENTRY_AMOUNT];
	
	public BoardHistory() {
		for(int i = 0; i < ENTRY_AMOUNT; i++) entries[i] = new BoardHistoryEntry();
	}
	
	public BoardHistoryEntry entry(int entryIndex) {
		return entries[entryIndex];
	}
	
}
