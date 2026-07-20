package net.kite.internal.wasm;

import net.kite.api.Kite;
import org.teavm.jso.JSExport;

public class KiteWasmApi {
	
	private static Kite solver;
	
	public static void initialize() {
		
	}
	
	@JSExport
	public static int debug() {
		return 1234;
	}
	
}
