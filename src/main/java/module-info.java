module kite.main {
	exports net.kite.api;
	exports net.kite.api.board.line;
	exports net.kite.api.board.outcome;
	exports net.kite.api.board.player.color;
	exports net.kite.api.board.score;
	exports net.kite.api.skill.level;
	
	requires org.fusesource.jansi;
	requires org.teavm.jso.apis;
}
