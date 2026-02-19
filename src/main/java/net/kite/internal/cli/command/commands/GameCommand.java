package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public final class GameCommand extends Command {
	
	private Kite gameSolver;
	
	public GameCommand() {
		super("game", "g", "Start a new interactive game", "game [skill-level]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		SkillLevel level;
		
		if(arguments.length == 0) {
			
			level = SkillLevel.PERFECT;
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			try {
				
				level = SkillLevel.level(s);
				
			} catch(IllegalArgumentException exception) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown skill level for argument 'skill-level': \"%s\"%nEnter 'skill-levels' for a list of skill levels.", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(scanner == null) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Command not available in script mode."));
			System.exit(1);
		}
		
		AnsiUtil.switchToAlternateScreenBuffer();
		
		String s1 = AnsiUtil.brightMagentaAnsi(level.getName());
		String s2 = AnsiUtil.brightYellowAnsi("exit");
		System.out.printf("Started new game against skill level: %s%n%nPlay moves by entering the column number.%nExit with '%s'.%n%n", s1, s2);
		
		AnsiUtil.createCheckpoint();
		
		if(gameSolver == null) gameSolver = Kite.createInstance();
		else gameSolver.clearBoard();
		
		Random random = ThreadLocalRandom.current();
		if(random.nextBoolean()) gameSolver.playMove(gameSolver.skilledMove(level));
		
		System.out.println(gameSolver.boardString(true));
		System.out.println();
		
		boolean gameOver = false;
		while(true) {
			
			if(!quiet) {
				
				System.out.print("> ");
				System.out.flush();
			}
			
			String message;
			
			try {
				
				message = scanner.nextLine();
				
			} catch(NoSuchElementException exception) {
				
				AnsiUtil.switchToNormalScreenBuffer();
				return true;
			}
			
			message = message.trim();
			if(message.isBlank()) continue;
			
			message = message.toLowerCase(Locale.ROOT);
			if(message.equals("exit")) {
				
				AnsiUtil.switchToNormalScreenBuffer();
				return false;
			}
			
			if(gameOver) {
				
				System.out.printf("The game is over! Please exit the game using '%s'.%n", s2);
				continue;
			}
			
			int x = parseCoordinateArgument(message, "move", 1, 7, errorStream, exitOnError);
			if(x < 0) continue;
			
			if(!gameSolver.moveLegal(x)) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("Illegal move!"));
				continue;
			}
			
			gameSolver.playMove(x);
			if(gameSolver.gameOver()) {
				
				String s = gameSolver.boardString(true);
				
				AnsiUtil.restoreCheckpoint();
				System.out.println(s);
				System.out.println();
				System.out.println(gameSolver.gameOutcome() == BoardOutcome.DRAW ? "It's a draw!" : "You win!");
				
				gameOver = true;
				continue;
			}
			
			gameSolver.playMove(gameSolver.skilledMove(level));
			String s = gameSolver.boardString(true);
			
			AnsiUtil.restoreCheckpoint();
			System.out.println(s);
			System.out.println();
			
			if(gameSolver.gameOver()) {
				
				// TODO better 
				System.out.println(gameSolver.gameOutcome() == BoardOutcome.DRAW ? "It's a draw!" : "You lose.");
				
				gameOver = true;
			}
		}
	}
	
}
