package net.kite.internal.cli.command.commands;

import net.kite.api.Kite;
import net.kite.internal.cli.command.Command;

public class BenchmarkCommand extends Command {
	
	public BenchmarkCommand() {
		super("benchmark", "benchmark [print-metrics]");
	}
	
	@Override
	public boolean execute(String[] arguments, Kite solver) {
		boolean successful;
		
		if(arguments.length == 0) {
			
			successful = Kite.runBenchmark();
			
		} else {
			
			boolean printMetrics = Boolean.parseBoolean(arguments[0]);
			successful = Kite.runBenchmark(printMetrics);
		}
		
		String message = successful ? "Benchmark completed successfully!" : "Benchmark was not completed successfully!";
		System.out.println(message);
		
		return false;
	}
	
}
