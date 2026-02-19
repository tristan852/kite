package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class HeightCommand extends Command {
	
	public HeightCommand() {
		super("height", "Show the height of column <x>", "height <x>");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 1) {
			
			errorStream.println(AnsiUtil.redAnsi("Incorrect number of arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", 1, 7, errorStream, exitOnError);
		if(x < 0) return false;
		
		System.out.println(solver.cellColumnHeight(x));
		
		return false;
	}
	
}
