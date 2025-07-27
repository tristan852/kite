package net.kite.demo;

import net.kite.Kite;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import java.io.ByteArrayInputStream;

public class KiteDemo {
	
	public void onStart() {
		HTMLDocument doc = HTMLDocument.current();
		
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
			Int8Array array = new Int8Array(arrayBuffer);
			
			byte[] bytes = array.copyToJavaArray();
			
			System.out.println("its bytes now");
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			
			System.out.println("db1");
			System.out.println(bytes.length);
			
			OpeningBoardScoreCaches.ensureDefaultIsLoaded(inputStream);
			
			System.out.println("db2");
			
			System.out.println(Kite.createInstance().optimalMove());
			System.out.println(bytes.length);
			
			var div = doc.createElement("div");
			div.appendChild(doc.createTextNode("Hello world!"));
			doc.getBody().appendChild(div);
			
			var div2 = doc.createElement("div");
			div2.appendChild(doc.createTextNode("TeaVM generated element3; solver move: "));
			doc.getBody().appendChild(div2);
		});
		
		xhr.send();
	}
	
}
