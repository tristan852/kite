package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.analysis.game.GameAnalysis;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class AnalysisCommand extends Command {
	
	private static final int PLAYER_AMOUNT = 2;
	
	private final GameAnalysis[] playerGameAnalyses;
	
	public AnalysisCommand() {
		super("analysis", "a", "Show game analysis for a player or both players", "analysis [color] [include-moves]");
		
		this.playerGameAnalyses = new GameAnalysis[PLAYER_AMOUNT];
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			solver.analyseGame(playerGameAnalyses);
			
			boolean ansiEnabled = !AnsiUtil.areAnsiCodesDisabled();
			
			String a1 = playerGameAnalyses[0].toString(false, ansiEnabled);
			String a2 = playerGameAnalyses[1].toString(false, ansiEnabled);
			
			if(ansiEnabled) {
				
				String s1 = AnsiUtil.brightRedAnsi("red");
				String s2 = AnsiUtil.brightYellowAnsi("yellow");
				System.out.printf("%s player's game analysis:%n%n%s%n%n%s player's performance:%n%n%s%n%n", s1, a1, s2, a2);
				
			} else {
				
				System.out.printf("red player's game analysis:%n%n%s%n%nyellow player's performance:%n%n%s%n%n", a1, a2);
			}
			
			return false;
		}
		
		if(arguments.length > 2) {
			
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
		
		boolean includeMoves = false;
		if(arguments.length == 2) {
			
			s = arguments[1];
			
			if(s.equals("true") || s.equals("include-moves")) includeMoves = true;
			else if(!s.equals("false")) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown boolean value for argument 'include-moves': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
		}
		
		GameAnalysis gameAnalysis = solver.analyseGame(color);
		
		boolean ansiDisabled = AnsiUtil.areAnsiCodesDisabled();
		
		s = gameAnalysis.toString(includeMoves, !ansiDisabled);
		System.out.println(s);
		System.out.println();
		
		return false;
	}
	
}
