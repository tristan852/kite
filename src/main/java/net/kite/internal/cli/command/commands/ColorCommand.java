package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.player.color.BoardPlayerColor;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class ColorCommand extends Command {
	
	public ColorCommand() {
		super("color", "Show the player of a disc", "color [x y]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			System.out.println(solver.activePlayerColor());
			return false;
		}
		
		if(arguments.length != 2) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Incorrect number of arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int x = parseCoordinateArgument(arguments[0], "x", 0, 6, errorStream, exitOnError);
		if(x < 0) return false;
		
		int y = parseCoordinateArgument(arguments[1], "y", 0, 5, errorStream, exitOnError);
		if(y < 0) return false;
		
		BoardPlayerColor color = solver.cellPlayerColor(x, y);
		String s = color == null ? AnsiUtil.brightCyanAnsi("empty") : (color == BoardPlayerColor.RED ? AnsiUtil.brightRedAnsi(color.getName()) : AnsiUtil.brightYellowAnsi(color.getName()));
		
		System.out.println(s);
		
		return false;
	}
	
}
