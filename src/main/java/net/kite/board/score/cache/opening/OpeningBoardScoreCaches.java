package net.kite.board.score.cache.opening;

import java.io.InputStream;

public class OpeningBoardScoreCaches {
	
	public static final OpeningBoardScoreCache DEFAULT = new OpeningBoardScoreCache();
	
	private static final String DEFAULT_RESOURCE_PATH = "/board_score_caches/opening.cfc";
	
	private static boolean defaultLoaded;
	
	public static void ensureDefaultIsLoaded(InputStream inputStream) {
		if(defaultLoaded) return;
		
		synchronized(OpeningBoardScoreCaches.class) {
			
			if(defaultLoaded) return;
			defaultLoaded = true;
			
			if(inputStream == null) DEFAULT.loadFromResources(DEFAULT_RESOURCE_PATH);
			else DEFAULT.loadFromInputStream(inputStream);
		}
	}
	
}
