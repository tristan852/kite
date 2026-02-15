package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class PlayCommand extends Command {
	
	public PlayCommand() {
		super("play", "play [moves]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(String s : arguments) stringBuilder.append(s);
		
		solver.playMoves(stringBuilder.toString());
		
		return false;
	}
	
}
