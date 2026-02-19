package net.kite.internal.cli;

public final class Main {
	
	public static void main(String[] programArguments) {
		KiteCLI kiteCLI = new KiteCLI();
		kiteCLI.onStart(programArguments);
	}
	
}
