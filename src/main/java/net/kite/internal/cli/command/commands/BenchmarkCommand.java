package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class BenchmarkCommand extends Command {
	
	public BenchmarkCommand() {
		super("benchmark", "bench", "Run benchmarks (metrics printed by default)", "benchmark [print-metrics]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		if(arguments.length == 0) {
			
			Kite.runBenchmark();
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean printMetrics;
			if(s.equals("true")) printMetrics = true;
			else if(s.equals("false")) printMetrics = false;
			else {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown boolean value for argument 'print-metrics': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
			Kite.runBenchmark(printMetrics);
			
		} else {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
		}
		
		return false;
	}
	
}
