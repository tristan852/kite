package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class OccupiedCommand extends Command {
	
	public OccupiedCommand() {
		super("occupied", "Check if position <x> <y> is occupied", "occupied <x> <y>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 2) {
			
			errorStream.println(AnsiUtil.redAnsi("Incorrect number of arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", 0, 6, errorStream, exitOnError);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", 0, 5, errorStream, exitOnError);
		if(y < 0) return false;
		
		System.out.println(solver.cellOccupied(x, y));
		
		return false;
	}
	
}
