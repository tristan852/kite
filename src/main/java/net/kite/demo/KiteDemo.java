package net.kite.demo;

import net.kite.Kite;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.HTMLBodyElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

public class KiteDemo {
	
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	public void onStart() {
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
			
			System.out.println("db1");
			System.out.println(bytes.length);
			
			OpeningBoardScoreCaches.ensureDefaultIsLoaded(bytes);
			
			System.out.println("db2");
			
			Kite kite = Kite.createInstance();
			kite.playMoves(2,6,6,5,4,4,4,3,2,2,1,2);
			System.out.println(kite.evaluateBoard());
			
			HTMLBodyElement body = DOCUMENT.getBody();
			
			while(true) {
				
				Node node = body.getFirstChild();
				if(node == null) break;
				
				body.removeChild(node);
			}
			
			HTMLElement container = createFlexBox("column");
			
			HTMLImageElement imageElement = (HTMLImageElement) DOCUMENT.createElement("img");
			
			imageElement.setSrc("https://github.com/tristan852/kite/blob/main/assets/images/brand/small_logo.png?raw=true");
			
			container.appendChild(imageElement);
			
			container.appendChild(createBoard());
			
			body.appendChild(container);
		});
		
		xhr.send();
	}
	
	private HTMLElement createBoard() {
		HTMLElement cellBoard = createFlexBox("row");
		
		for(int x = 0; x < 7; x++) {
			
			HTMLElement column = createBoardColumn();
			
			cellBoard.appendChild(column);
		}
		
		return cellBoard;
	}
	
	private HTMLElement createBoardColumn() {
		HTMLElement cellColumn = createFlexBox("column");
		
		for(int y = 0; y < 6; y++) {
			
			HTMLElement cell = DOCUMENT.createElement("div");
			
			cell.getStyle().setProperty("width", "50px");
			cell.getStyle().setProperty("height", "50px");
			cell.getStyle().setProperty("height", "50px");
			cell.getStyle().setProperty("borderRadius", "50%");
			
			cellColumn.appendChild(cell);
		}
		
		return cellColumn;
	}
	
	private HTMLElement createFlexBox(String direction) {
		HTMLElement flexBox = DOCUMENT.createElement("div");
		
		flexBox.getStyle().setProperty("display", "flex");
		flexBox.getStyle().setProperty("flexDirection", direction);
		flexBox.getStyle().setProperty("gap", "10px"); // Optional: spacing between items
		
		return flexBox;
	}
	
}
