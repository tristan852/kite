package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class OverCommand extends Command {
	
	public OverCommand() {
		super("over", "Check if the game is over", "over");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println(solver.gameOver());
		
		return false;
	}
	
}
