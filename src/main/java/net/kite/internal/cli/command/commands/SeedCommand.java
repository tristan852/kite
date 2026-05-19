package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public final class SeedCommand extends Command {
	
	public SeedCommand() {
		super("seed", "se", "Retrieve or set the random seed", "seed [seed/random]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			long seed = solver.randomSeed();
			
			System.out.printf(Locale.ROOT, "seed: %,d\n", seed);
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			long seed;
			
			if(s.equals("random")) {
				
				seed = solver.seedRandomness();
				
			} else {
				
				try {
					
					seed = Long.parseLong(s);
					
				} catch(NumberFormatException exception) {
					
					errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown long value for argument 'seed': \"%s\"", s)));
					if(exitOnError) System.exit(1);
					return false;
				}
				
				solver.seedRandomness(seed);
			}
			
			System.out.printf(Locale.ROOT, "new seed: %,d\n", seed);
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
