package net.kite.board.score.cache.opening;

public class OpeningBoardScoreCaches {
	
	public static final OpeningBoardScoreCache DEFAULT = new OpeningBoardScoreCache();
	
	private static final String DEFAULT_RESOURCE_PATH = "/board_score_caches/opening.cfc";
	
	private static boolean defaultLoaded;
	
	public static void ensureDefaultIsLoaded(byte[] prefetchedCacheBytes) {
		if(defaultLoaded) return;
		
		synchronized(OpeningBoardScoreCaches.class) {
			
			if(defaultLoaded) return;
			defaultLoaded = true;
			
			if(prefetchedCacheBytes == null) DEFAULT.loadFromResources(DEFAULT_RESOURCE_PATH);
			else DEFAULT.loadFromBytes(prefetchedCacheBytes);
		}
	}
	
}
