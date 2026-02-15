package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class OverCommand extends Command {
	
	public OverCommand() {
		super("over", "over");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		System.out.println(solver.gameOver());
		
		return false;
	}
	
}
