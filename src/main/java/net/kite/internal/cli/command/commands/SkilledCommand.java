package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class SkilledCommand extends Command {
	
	public SkilledCommand() {
		super("skilled", "skilled <skill-level>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 1) {
			
			errorStream.println("Incorrect number of arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		String s = arguments[0];
		SkillLevel level;
		
		try {
			
			level = SkillLevel.level(s);
			
		} catch(IllegalArgumentException exception) {
			
			errorStream.printf("Unknown skill level for argument 'skill-level': \"%s\"%n", s);
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int move = solver.skilledMove(level);
		if(move == 0) {
			
			errorStream.println("The game is over!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
