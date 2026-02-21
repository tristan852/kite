package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;
import net.kite.internal.cli.command.Commands;
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
		SkillLevel level = null;
		
		if(arguments.length == 1) {
			
			String s = arguments[0];
			
			try {
				
				level = SkillLevel.level(s);
				
			} catch(IllegalArgumentException exception) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown skill level for argument 'skill-level': \"%s\"%nEnter 'skill-levels' for a list of skill levels.", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
		} else if(arguments.length > 1) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(scanner == null) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Command not available in script mode."));
			System.exit(1);
		}
		
		if(AnsiUtil.areAnsiCodesDisabled()) {
			
			if(!quiet) System.out.println();
			
		} else {
			
			AnsiUtil.switchToAlternateScreenBuffer();
		}
		
		String s2 = null;
		
		if(level == null) {
			
			if(!quiet) {
				
				String s1 = AnsiUtil.brightYellowAnsi("skill-levels");
				s2 = AnsiUtil.brightYellowAnsi("exit");
				System.out.printf("Please enter a skill level.%nEnter '%s' to list available levels or '%s' to cancel.%n%n", s1, s2);
			}
			
			AnsiUtil.saveCursorPosition();
			
			while(true) {
				
				AnsiUtil.restoreCursorPosition();
				AnsiUtil.clearScreenCursorLine();
				
				if(!quiet) {
					
					System.out.print("skill-level> ");
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
				
				AnsiUtil.clearScreenFromCursorPosition();
				
				message = message.toLowerCase(Locale.ROOT);
				if(message.equals("exit")) {
					
					AnsiUtil.switchToNormalScreenBuffer();
					return false;
				}
				
				if(message.equals("skill-levels")) {
					
					Commands.SKILL_LEVELS.execute(new String[] {}, null, errorStream, false, quiet, null);
					continue;
				}
				
				try {
					
					level = SkillLevel.level(message);
					break;
					
				} catch(IllegalArgumentException exception) {
					
					errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown skill level for argument 'skill-level': \"%s\"%nEnter 'skill-levels' for a list of skill levels.", message)));
				}
			}
			
			if(AnsiUtil.areAnsiCodesDisabled()) {
				
				if(!quiet) System.out.println();
				
			} else {
				
				AnsiUtil.clearScreen();
			}
		}
		
		if(!quiet) {
			
			String s1 = AnsiUtil.brightMagentaAnsi(level.getDisplayName());
			if(s2 == null) s2 = AnsiUtil.brightYellowAnsi("exit");
			System.out.printf("You are playing against the AI at skill level %s.%n%nPlay moves by entering the column number.%nExit with '%s'.%n", s1, s2);
		}
		
		if(gameSolver == null) gameSolver = Kite.createInstance();
		else gameSolver.clearBoard();
		
		Random random = ThreadLocalRandom.current();
		if(random.nextBoolean()) gameSolver.playMove(gameSolver.skilledMove(level));
		
		System.out.println();
		boolean ansiDisabled = AnsiUtil.areAnsiCodesDisabled();
		if(quiet) System.out.println(gameSolver.compactBoardString(!ansiDisabled));
		else System.out.println(ansiDisabled ? gameSolver.boardString(false) : gameSolver.fancyBoardString(true));
		System.out.flush();
		System.out.println();
		
		AnsiUtil.saveCursorPosition();
		
		boolean gameOver = false;
		while(true) {
			
			AnsiUtil.restoreCursorPosition();
			AnsiUtil.clearScreenCursorLine();
			
			if(!quiet) {
				
				System.out.print("play> ");
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
			
			AnsiUtil.clearScreenFromCursorPosition();
			
			message = message.toLowerCase(Locale.ROOT);
			if(message.equals("exit")) {
				
				AnsiUtil.switchToNormalScreenBuffer();
				return false;
			}
			
			if(gameOver) {
				
				if(!quiet) System.out.printf("The game is over! Please exit the game using '%s'.%n", s2);
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
				
				String s;
				if(quiet) s = gameSolver.compactBoardString(!ansiDisabled);
				else s = ansiDisabled ? gameSolver.boardString(false) : gameSolver.fancyBoardString(true);
				
				if(!AnsiUtil.areAnsiCodesDisabled()) {
					AnsiUtil.moveCursorToTopLeft();
					
					if(!quiet) {
						
						String s1 = AnsiUtil.brightMagentaAnsi(level.getDisplayName());
						System.out.printf("You are playing against the AI at skill level %s.%n%nPlay moves by entering the column number.%nExit with '%s'.%n", s1, s2);
					}
				}
				
				System.out.println();
				System.out.println(s);
				System.out.flush();
				System.out.println();
				
				if(!quiet) {
					
					System.out.println(gameSolver.gameOutcome() == BoardOutcome.DRAW ? "It's a draw!" : "You win!");
					System.out.printf("Moves: %s%n%n", gameSolver.boardMovesString());
				}
				
				if(AnsiUtil.areAnsiCodesDisabled()) return false;
				
				AnsiUtil.clearScreenFromCursorPosition();
				AnsiUtil.saveCursorPosition();
				
				gameOver = true;
				continue;
			}
			
			gameSolver.playMove(gameSolver.skilledMove(level));
			
			String s;
			if(quiet) s = gameSolver.compactBoardString(!ansiDisabled);
			else s = ansiDisabled ? gameSolver.boardString(false) : gameSolver.fancyBoardString(true);
			
			AnsiUtil.moveCursorToTopLeft();
			
			if(!AnsiUtil.areAnsiCodesDisabled()) {
				AnsiUtil.moveCursorToTopLeft();
				
				if(!quiet) {
					
					String s1 = AnsiUtil.brightMagentaAnsi(level.getDisplayName());
					System.out.printf("You are playing against the AI at skill level %s.%n%nPlay moves by entering the column number.%nExit with '%s'.%n", s1, s2);
				}
			}
			
			System.out.println();
			System.out.println(s);
			System.out.flush();
			System.out.println();
			
			if(gameSolver.gameOver()) {
				
				if(!quiet) {
					
					System.out.println(gameSolver.gameOutcome() == BoardOutcome.DRAW ? "It's a draw!" : "You lose.");
					System.out.printf("Moves: %s%n%n", gameSolver.boardMovesString());
				}
				
				if(AnsiUtil.areAnsiCodesDisabled()) return false;
				
				gameOver = true;
			}
			
			AnsiUtil.clearScreenFromCursorPosition();
			AnsiUtil.saveCursorPosition();
		}
	}
	
}
