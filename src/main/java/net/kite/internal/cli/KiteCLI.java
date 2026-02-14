package net.kite.internal.cli;

import net.kite.api.Kite;

import java.util.Scanner;

public class KiteCLI {
	
	public void onStart() {
		Kite solver = Kite.createInstance();
		Scanner scanner = new Scanner(System.in);
		
		String name = Kite.getName();
		String version = Kite.getVersion();
		String author = Kite.getAuthor();
		
		String message = String.format("%s v%s by %s", name, version, author);
		System.out.println(message);
		System.out.println(solver);
		
		Kite.runBenchmark();
		
//		while(true) {
//			
//			message = scanner.nextLine();
//		}
		
		// TODO run help to get help message
		
		// commands for all solver methods and run benchmark (with and without metrics)
		// exit command and help command
	}
	
}
