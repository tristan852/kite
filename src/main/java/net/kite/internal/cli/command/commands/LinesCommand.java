package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.api.board.line.BoardLine;
import net.kite.internal.cli.command.Command;

public class LinesCommand extends Command {
	
	private static final int COORDINATE_OFFSET = 48;
	
	public LinesCommand() {
		super("lines", "lines");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		BoardLine[] lines = solver.winLines();
		for(BoardLine line : lines) System.out.println(line);
		
		return false;
	}
	
}
