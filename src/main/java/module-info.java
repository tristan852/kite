module kite.main {
	exports net.kite.api;
	exports net.kite.api.board.analysis.game;
	exports net.kite.api.board.analysis.move;
	exports net.kite.api.board.line;
	exports net.kite.api.board.outcome;
	exports net.kite.api.board.player.color;
	exports net.kite.api.board.evaluation;
	exports net.kite.api.skill.level;
	
	requires it.unimi.dsi.sux4j;
	requires it.unimi.dsi.fastutil;
	requires it.unimi.dsi.dsiutils;
	requires org.fusesource.jansi;
	requires org.teavm.jso.apis;
}
