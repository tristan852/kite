package net.kite.internal.demo;

import org.fusesource.jansi.Ansi;

public final class Main {
	
	public static void main(String[] programArguments) {
		System.out.println(Ansi.ansi().fgGreen().a("Hello").reset().toString());
		
		KiteDemo kiteDemo = new KiteDemo();
		kiteDemo.onStart();
	}
	
}
