package net.kite.demo;

import org.teavm.jso.dom.html.HTMLDocument;

public class KiteDemo {
	
	public void onStart() {
		HTMLDocument doc = HTMLDocument.current();
		
		var div = doc.createElement("div");
		div.appendChild(doc.createTextNode("TeaVM generated element"));
		doc.getBody().appendChild(div);
	}
	
}
