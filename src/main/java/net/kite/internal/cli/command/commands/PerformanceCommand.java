package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public final class PerformanceCommand extends Command {
	
	private static final int PLAYER_AMOUNT = 2;
	
	private final float[] playerPerformances;
	
	public PerformanceCommand() {
		super("performance", "Show Elo performance for a player or both players", "performance <color>");
		
		this.playerPerformances = new float[PLAYER_AMOUNT];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			solver.evaluatePlayerPerformances(playerPerformances);
			
			System.out.printf(Locale.ROOT, "red player's performance: %.2f%nyellow player's performance: %.2f%n", playerPerformances[0], playerPerformances[1]);
			
			return false;
		}
		
		if(arguments.length != 1) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		String s = arguments[0];
		
		BoardPlayerColor color;
		if(s.equals("red")) color = BoardPlayerColor.RED;
		else if(s.equals("yellow")) color = BoardPlayerColor.YELLOW;
		else {
			
			errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown color value for argument 'color': \"%s\"", s)));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		float f = solver.evaluatePlayerPerformance(color);
		System.out.printf(Locale.ROOT, "%.2f%n", f);
		
		return false;
	}
	
}
