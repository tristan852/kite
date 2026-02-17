package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;

public class SkilledCommand extends Command {
	
	public SkilledCommand() {
		super("skilled", "skilled <level>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 1) {
			
			System.err.println("Incorrect number of arguments!");
			return false;
		}
		
		String s = arguments[0];
		SkillLevel level;
		
		try {
			
			level = SkillLevel.level(s);
			
		} catch(IllegalArgumentException exception) {
			
			System.err.printf("Unknown skill level for argument 'level': %s%n", s);
			return false;
		}
		
		int move = solver.skilledMove(level);
		if(move == 0) {
			
			System.err.println("The game is over!");
			return false;
		}
		
		System.out.println(move);
		
		return false;
	}
	
}
