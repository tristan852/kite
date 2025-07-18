package net.kite.board.score.cache.opening;

public class OpeningBoardScoreCaches {
	
	public static final OpeningBoardScoreCache DEFAULT = new OpeningBoardScoreCache();
	
	private static final String DEFAULT_RESOURCE_PATH = "/caches/opening.cfc";
	
	static {
		DEFAULT.loadFromResources(DEFAULT_RESOURCE_PATH);
	}
	
}
