package net.kite.demo;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.typedarrays.ArrayBuffer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class KiteDemo {
	
	public void onStart() {
		HTMLDocument doc = HTMLDocument.current();
		
		int a;
		
		XMLHttpRequest xhr = new XMLHttpRequest();
		xhr.open("GET", "/WEB-INF/classes/score_caches/opening.cfc");
		xhr.setResponseType("arraybuffer");
		xhr.onLoad(() -> {
			if (xhr.getStatus() == 200) {
				System.out.println("GOT IT");
			} else {
				
			}
		});
		xhr.send();
		
		var div = doc.createElement("div");
		div.appendChild(doc.createTextNode("Hello world!"));
		doc.getBody().appendChild(div);
		
		var div2 = doc.createElement("div");
		div2.appendChild(doc.createTextNode("TeaVM generated element3; solver move: " + a));
		doc.getBody().appendChild(div2);
	}
	
}
