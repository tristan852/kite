package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class GameCommand extends Command {
	
	public GameCommand() {
		super("game", "Start a new interactive game", "game [skill-level]");
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
				
				errorStream.printf("Unknown skill level for argument 'skill-level': \"%s\"%n", s);
				if(exitOnError) System.exit(1);
				return false;
			}
			
		} else {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(scanner == null) {
			
			errorStream.println("Command not available in script mode.");
			System.exit(1);
		}
		
		System.out.printf("Started new game against skill level: %s%n%nPlay moves by entering the column number.%nExit with 'exit'.%n%n", level);
		
		solver = Kite.createInstance();
		
		Random random = ThreadLocalRandom.current();
		if(random.nextBoolean()) solver.playMove(solver.skilledMove(level));
		
		System.out.println(solver.boardString());
		
		while(true) {
			
			if(!quiet) {
				
				System.out.print("> ");
				System.out.flush();
			}
			
			String message;
			
			try {
				
				message = scanner.nextLine();
				
			} catch(NoSuchElementException exception) {
				
				return true;
			}
			
			message = message.trim();
			if(message.isBlank()) continue;
			
			message = message.toLowerCase(Locale.ROOT);
			if(message.equals("exit")) return false;
			
			int x = parseCoordinateArgument(message, "move", true, errorStream, exitOnError);
			if(x < 0) continue;
			
			if(!solver.moveLegal(x)) {
				
				errorStream.println("Illegal move!");
				continue;
			}
			
			solver.playMove(x);
			
			if(solver.gameOver()) {
				
				System.out.println(solver.boardString());
				System.out.println(solver.gameOutcome() == BoardOutcome.DRAW ? "You drew." : "You won!");
				return false;
			}
			
			solver.playMove(solver.skilledMove(level));
			System.out.println(solver.boardString());
			
			if(solver.gameOver()) {
				
				System.out.println(solver.gameOutcome() == BoardOutcome.DRAW ? "You drew." : "You lost!");
				return false;
			}
		}
	}
	
}
