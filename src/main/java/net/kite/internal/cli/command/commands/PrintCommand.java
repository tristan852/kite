package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public class PrintCommand extends Command {
	
	public PrintCommand() {
		super("print", "pr", "Print the board (analysis included by default)", "print [include-analysis]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			System.out.println(solver.boardAnalysisString(true));
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean includeAnalysis;
			if(s.equals("true")) includeAnalysis = true;
			else if(s.equals("false")) includeAnalysis = false;
			else {
				
				errorStream.println(AnsiUtil.redAnsi(String.format("Unknown boolean value for argument 'include-analysis': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			if(includeAnalysis) System.out.println(solver.boardAnalysisString(true));
			else System.out.println(solver.boardString(true));
			
		} else {
			
			errorStream.println(AnsiUtil.redAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
