package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class BenchmarkCommand extends Command {
	
	public BenchmarkCommand() {
		super("benchmark", "benchmark [print-metrics]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		if(arguments.length == 0) {
			
			boolean successful = Kite.runBenchmark();
			
			String message = successful ? "Benchmark completed successfully!" : "Benchmark was not completed successfully!";
			System.out.println(message);
			
		} else if(arguments.length == 1) {
			
			String s = arguments[0];
			
			boolean printMetrics;
			if(s.equalsIgnoreCase("true")) printMetrics = true;
			else if(s.equalsIgnoreCase("false")) printMetrics = false;
			else {
				
				System.err.printf("Unknown boolean value for argument 'printMetrics': %s%n", s);
				return false;
			}
			
			boolean successful = Kite.runBenchmark(printMetrics);
			
			String message = successful ? "Benchmark completed successfully!" : "Benchmark was not completed successfully!";
			System.out.println(message);
			
		} else {
			
			System.err.println("Too many arguments!");
		}
		
		return false;
	}
	
}
