package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;
import java.util.Scanner;

public class LegalCommand extends Command {
	
	public LegalCommand() {
		super("legal", "legal <move>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 1) {
			
			errorStream.println("Incorrect number of arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "move", true, errorStream, exitOnError);
		if(x < 0) return false;
		
		System.out.println(solver.moveLegal(x));
		return false;
	}
	
}
