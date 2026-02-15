package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class EvaluateCommand extends Command {
	
	private static final int MOVE_CHARACTER_OFFSET = 48;
	
	public EvaluateCommand() {
		super("evaluate", "evaluate [move]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			System.out.println(solver.evaluateBoard());
			
		} else {
			
			char c = arguments[0].charAt(0);
			int i = c - MOVE_CHARACTER_OFFSET;
			
			System.out.println(solver.evaluateMove(i));
		}
		
		return false;
	}
	
}
