package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class SkilledCommand extends Command {
	
	public SkilledCommand() {
		super("skilled", "skilled <level>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream) {
		if(arguments.length != 1) {
			
			errorStream.println("Incorrect number of arguments!");
			return false;
		}
		
		String s = arguments[0];
		SkillLevel level;
		
		try {
			
			level = SkillLevel.level(s);
			
		} catch(IllegalArgumentException exception) {
			
			errorStream.printf("Unknown skill level for argument 'level': %s%n", s);
			return false;
		}
		
		int move = solver.skilledMove(level);
		if(move == 0) {
			
			errorStream.println("The game is over!");
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
