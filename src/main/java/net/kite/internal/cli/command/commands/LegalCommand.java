package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class LegalCommand extends Command {
	
	public LegalCommand() {
		super("legal", "Check if a move is legal", "legal <move>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 1) {
			
			errorStream.println(AnsiUtil.redAnsi("Incorrect number of arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "move", 1, 7, errorStream, exitOnError);
		if(x < 0) return false;
		
		System.out.println(solver.moveLegal(x));
		return false;
	}
	
}
