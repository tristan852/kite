package net.kite.internal.cli;

public final class Main {
	
	public static void main(String[] programArguments) {
		KiteCli kiteCli = new KiteCli();
		kiteCli.onStart(programArguments);
	}
	
}
