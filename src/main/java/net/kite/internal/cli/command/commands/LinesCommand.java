package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.line.BoardLine;
import net.kite.internal.cli.command.Command;

import java.io.PrintStream;

public class LinesCommand extends Command {
	
	public LinesCommand() {
		super("lines", "lines");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError) {
		if(arguments.length != 0) {
			
			errorStream.println("Too many arguments!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		BoardLine[] lines = solver.winLines();
		if(lines == null) {
			
			errorStream.println("The game has not ended yet or has ended in a draw!");
			if(exitOnError) System.exit(1);
			return false;
		}
		
		for(BoardLine line : lines) System.out.println(line);
		
		return false;
	}
	
}
