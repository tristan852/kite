package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class PerformanceCommand extends Command {
	
	private static final int PLAYER_AMOUNT = 2;
	
	private final float[] playerPerformances;
	
	public PerformanceCommand() {
		super("performance <color>", "performance <color>");
		
		this.playerPerformances = new float[PLAYER_AMOUNT];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			solver.evaluatePlayerPerformances(playerPerformances);
			
			System.out.printf("red player performance: %.2f%nyellow player performance: %.2f%n", playerPerformances[0], playerPerformances[1]);
			
			return false;
		}
		
		if(arguments.length != 1) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		String s = arguments[0];
		
		BoardPlayerColor color;
		if(s.equals("red")) color = BoardPlayerColor.RED;
		else if(s.equals("yellow")) color = BoardPlayerColor.YELLOW;
		else {
			
			errorStream.printf("Unknown color value for argument 'color': \"%s\"%n", s);
			if(exitOnError) System.exit(1);
			return false;
		}
		
		float f = solver.evaluatePlayerPerformance(color);
		System.out.printf("%.2f%n", f);
		
		return false;
	}
	
}
