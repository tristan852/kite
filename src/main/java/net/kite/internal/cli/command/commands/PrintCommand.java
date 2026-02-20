package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class PrintCommand extends Command {
	
	public PrintCommand() {
		super("print", "pr", "Print the board (analysis included by default)", "print [include-analysis]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			boolean ansiDisabled = AnsiUtil.areAnsiCodesDisabled();
			if(quiet) System.out.println(solver.compactBoardAnalysisString(!ansiDisabled));
			else System.out.println(ansiDisabled ? solver.boardAnalysisString(false) : solver.fancyBoardAnalysisString(true));
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean includeAnalysis;
			if(s.equals("true")) includeAnalysis = true;
			else if(s.equals("false")) includeAnalysis = false;
			else {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown boolean value for argument 'include-analysis': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			boolean ansiDisabled = AnsiUtil.areAnsiCodesDisabled();
			if(includeAnalysis) {
				
				if(quiet) System.out.println(solver.compactBoardAnalysisString(!ansiDisabled));
				else System.out.println(ansiDisabled ? solver.boardAnalysisString(false) : solver.fancyBoardAnalysisString(true));
				
			} else {
				
				if(quiet) System.out.println(solver.compactBoardAnalysisString(!ansiDisabled));
				else System.out.println(ansiDisabled ? solver.boardString(false) : solver.fancyBoardString(true));
			}
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
