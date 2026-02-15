package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.cli.command.Command;

public class PerformanceCommand extends Command {
	
	private static final int PLAYER_AMOUNT = 2;
	
	private final float[] playerPerformances;
	
	public PerformanceCommand() {
		super("performance <color>", "performance <color>");
		
		this.playerPerformances = new float[PLAYER_AMOUNT];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			solver.evaluatePlayerPerformances(playerPerformances);
			
			System.out.printf("red player performance: %.2f%nyellow player performance: %.2f%n", playerPerformances[0], playerPerformances[1]);
			
			return false;
		}
		
		String s = arguments[0];
		BoardPlayerColor color = BoardPlayerColor.color(s);
		
		float f = solver.evaluatePlayerPerformance(color);
		System.out.printf("%.2f%n", f);
		
		return false;
	}
	
}
