package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;
import net.kite.internal.util.ansi.AnsiUtil;

import java.io.PrintStream;
import java.util.Scanner;

public final class MetricsCommand extends Command {
	
	public MetricsCommand() {
		super("metrics", "record", "Start and stop recording metrics", "metrics [reset]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver, PrintStream errorStream, boolean exitOnError, boolean quiet, Scanner scanner) {
		boolean reset = false;
		
		if(arguments.length == 1) {
			
			String s = arguments[0];
			
			if(s.equals("true") || s.equals("reset")) reset = true;
			else if(!s.equals("false")) {
				
				errorStream.println(AnsiUtil.brightRedAnsi(String.format("Unknown boolean value for argument 'reset': \"%s\"", s)));
				if(exitOnError) System.exit(1);
				return false;
			}
			
		} else if(arguments.length > 1) {
			
			errorStream.println(AnsiUtil.brightRedAnsi("Too many arguments!"));
			if(exitOnError) System.exit(1);
			return false;
		}
		
		if(reset) {
			
			System.out.println();
			solver.printAndResetPerformanceMetrics();
			System.out.println();
			
			return false;
		}
		
		if(solver.isRecordingMetrics()) {
			
			solver.stopRecordingPerformanceMetrics();
			System.out.println("Recording stopped...");
			
		} else {
			
			solver.startRecordingPerformanceMetrics();
			System.out.println("Recording started...");
		}
		
		return false;
	}
	
}
