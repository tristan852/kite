package net.kite.internal.wasm;

import net.kite.api.Kite;
import org.teavm.jso.JSExport;

public class KiteWasmApi {
	
	private static Kite solver;
	
	public static void initialize() {
		System.out.println("main method called!");
		System.out.println("Should load everything here!");
	}
	
	@JSExport
	public static int debug() {
		return 1234;
	}
	
	// @Export(name = "create")
	//@Export(name = "solve")
	//@Export(name = "getBestMove")
	//@Export(name = "evaluate")
	//@Export(name = "getLegalMoves")
	//@Export(name = "isGameOver")
	//@Export(name = "analyze")
	
}
