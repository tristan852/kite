package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class SeedCommand extends Command {
	
	public SeedCommand() {
		super("seed", "Set the random seed", "seed [seed]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			solver.seedRandomness();
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			long seed;
			
			try {
				
				seed = Long.parseLong(s);
				
			} catch(NumberFormatException exception) {
				
				errorStream.printf("Unknown long value for argument 'seed': \"%s\"%n", s);
				if(exitOnError) System.exit(1);
				return false;
			}
			
			solver.seedRandomness(seed);
			
		} else {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
