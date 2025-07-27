package net.kite.demo;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8Array;

import java.io.ByteArrayInputStream;

public class KiteDemo {
	
	public void onStart() {
		HTMLDocument doc = HTMLDocument.current();
		
		// call ensure... inside the onload
		
		XMLHttpRequest xhr = new XMLHttpRequest();
		xhr.open("GET", "WEB-INF/classes/board_score_caches/opening.cfc");
		xhr.setResponseType("arraybuffer");
		xhr.onLoad((x) -> {
			if (xhr.getStatus() == 200) {
				System.out.println("GOT IT");
			} else {
				System.out.println("DID NOT GET IT");
			}
			
			ArrayBuffer arrayBuffer = (ArrayBuffer) xhr.getResponse();
			Uint8Array array = new Uint8Array(arrayBuffer);
			
			int n = array.getByteLength();
			byte[] bytes = new byte[n];
			
			for(int i = 0; i < n; i++) {
				
				bytes[i] = (byte) array.get(i);
			}
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			
			System.out.println(bytes.length);
		});
		xhr.send();
		
		var div = doc.createElement("div");
		div.appendChild(doc.createTextNode("Hello world!"));
		doc.getBody().appendChild(div);
		
		var div2 = doc.createElement("div");
		div2.appendChild(doc.createTextNode("TeaVM generated element3; solver move: "));
		doc.getBody().appendChild(div2);
	}
	
}
