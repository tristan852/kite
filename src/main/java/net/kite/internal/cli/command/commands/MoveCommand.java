package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class MoveCommand extends Command {
	
	public MoveCommand() {
		super("move", new String[] { "Shows the last move or the move at the given index", "(including undone moves)" }, "move [index]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			if(solver.boardEmpty()) {
				
				errorStream.println(AnsiUtil.brightRedAnsi("No moves have been played yet!"));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			System.out.println(solver.lastMove());
			return false;
		}
		
		if(arguments.length != 1) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int n = solver.playedMoveAmount() + solver.undoneMoveAmount();
		if(n == 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("No moves have been played or undone yet!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		int index = parseCoordinateArgument(arguments[0], "index", 0, n - 1, errorStream, exitOnError);
		if(index < 0) return false;
		
		System.out.println(solver.playedMove(index));
		
		return false;
	}
	
}
