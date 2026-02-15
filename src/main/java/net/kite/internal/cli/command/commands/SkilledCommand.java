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
		SkillLevel level = SkillLevel.level(arguments[0]);
		
		System.out.println(solver.skilledMove(level));
		
		return false;
	}
	
}
