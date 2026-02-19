package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public class CanUndoCommand extends Command {
	
	public CanUndoCommand() {
		super("can-undo", "Check if moves can be undone", "can-undo");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println(solver.canUndoMove());
		
		return false;
	}
	
}
