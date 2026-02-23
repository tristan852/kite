package net.kite.internal.demo;

import org.fusesource.jansi.Ansi;

public final class Main {
	
	public static void main(String[] programArguments) {
		System.out.println(Ansi.ansi().getClass());
		System.out.println(Main.class.getResourceAsStream("/benchmarks/endgame_easy.txt"));
		
		KiteDemo kiteDemo = new KiteDemo();
		kiteDemo.onStart();
	}
	
}
