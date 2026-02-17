package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.line.BoardLine;
import net.kite.internal.cli.command.Command;

public class LinesCommand extends Command {
	
	public LinesCommand() {
		super("lines", "lines");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length != 0) {
			
			System.err.println("Too many arguments!");
			return false;
		}
		
		BoardLine[] lines = solver.winLines();
		if(lines == null) {
			
			System.err.println("The game has not ended yet or has ended in a draw!");
			return false;
		}
		
		for(BoardLine line : lines) System.out.println(line);
		
		return false;
	}
	
}
