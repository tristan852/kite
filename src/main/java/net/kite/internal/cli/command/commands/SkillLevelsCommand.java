package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class SkillLevelsCommand extends Command {
	
	private static final SkillLevel[][] CATEGORIZED_SKILL_LEVELS = new SkillLevel[][] {
			{
					SkillLevel.RANDOM,
					SkillLevel.PERFECT,
					SkillLevel.ADAPTIVE
			},
			{
					SkillLevel.BEGINNER,
					SkillLevel.NOVICE,
					SkillLevel.AMATEUR,
					SkillLevel.INTERMEDIATE,
					SkillLevel.SKILLED,
					SkillLevel.ADVANCED,
					SkillLevel.EXPERT,
					SkillLevel.MASTER,
					SkillLevel.GRANDMASTER,
					SkillLevel.SUPER_GRANDMASTER
			}
	};
	
	public SkillLevelsCommand() {
		super("skill-levels", "Show all available skill levels", "skill-levels");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println("Available skill levels:");
		
		for(SkillLevel[] levels : CATEGORIZED_SKILL_LEVELS) {
			
			System.out.println();
			
			for(SkillLevel level : levels) {
				
				String name = level.getName();
				name = AnsiUtil.brightMagentaAnsi(name);
				
				System.out.printf("  - %s%n", name);
			}
		}
		
		System.out.println();
		return false;
	}
	
}
