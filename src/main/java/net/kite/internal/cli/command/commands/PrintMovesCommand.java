package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class PrintMovesCommand extends Command {
	
	public PrintMovesCommand() {
		super("print-moves", "pm", "Show the sequence of played moves", "print-moves");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length != 0) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		System.out.println(solver.boardMovesString());
		
		return false;
	}
	
}
