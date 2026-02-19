package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class SkillLevelsCommand extends Command {
	
	public SkillLevelsCommand() {
		super("skill-levels", "Show all available skill levels", "skill-levels");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println("Available skill levels:\n");
		
		for(SkillLevel level : SkillLevel.values()) {
			
			String name = level.getName();
			name = AnsiUtil.magentaAnsi(name);
			
			System.out.printf("  - %s%n", name);
		}
		
		System.out.println();
		return false;
	}
	
}
