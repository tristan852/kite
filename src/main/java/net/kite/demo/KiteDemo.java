package net.kite.demo;

import net.kite.Kite;
import org.teavm.jso.dom.html.HTMLDocument;

public class KiteDemo {
	
	public void onStart() {
		HTMLDocument doc = HTMLDocument.current();
		
		Kite solver = Kite.createInstance();
		
		var div = doc.createElement("div");
		div.appendChild(doc.createTextNode("TeaVM generated element" + solver.toString()));
		doc.getBody().appendChild(div);
	}
	
}
