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
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	
	// TODO keep solver in sync after each play/undo
	
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	private Kite solver;
	
	private final int[] columnHeights = new int[BOARD_WIDTH];
	private boolean redAtTurn = true;
	
	private final HTMLElement[][] cells = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	
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
			
			solver = Kite.createInstance();
			
			HTMLBodyElement body = DOCUMENT.getBody();
			
			while(true) {
				
				Node node = body.getFirstChild();
				if(node == null) break;
				
				body.removeChild(node);
			}
			
			HTMLElement container = createFlexBox("column", 60);
			
			HTMLImageElement imageElement = (HTMLImageElement) DOCUMENT.createElement("img");
			
			// use raw.github instead
			imageElement.setSrc("https://github.com/tristan852/kite/blob/main/assets/images/brand/small_logo.png?raw=true");
			
			container.appendChild(imageElement);
			
			container.appendChild(createBoard());
			
			imageElement = (HTMLImageElement) DOCUMENT.createElement("img");
			
			imageElement.setSrc("https://github.com/tristan852/kite/blob/main/assets/images/socials/github.png?raw=true");
			container.appendChild(imageElement);
			
			body.appendChild(container);
			
			playMove(3);
			playMove(3);
			playMove(3);
		});
		
		xhr.send();
	}
	
	// TODO synchronize these
	
	private void playMove(int moveX) {
		int moveY = columnHeights[moveX];
		columnHeights[moveX]++;
		
		HTMLElement cell = cells[moveX][moveY];
		
		cell.getStyle().setProperty("background-color", redAtTurn ? "#FB2C36" : "#F0B100");
		
		solver.playMove(moveX);
		redAtTurn = !redAtTurn;
	}
	
	private void undoMove(int moveX, int moveY) {
		HTMLElement cell = cells[moveX][moveY];
		
		cell.getStyle().setProperty("background-color", "#09090B");
		
		solver.undoMove();
		redAtTurn = !redAtTurn;
	}
	
	private HTMLElement createBoard() {
		HTMLElement cellBoard = createFlexBox("row", 6);
		
		for(int x = 0; x < 7; x++) {
			
			HTMLElement column = createBoardColumn(x);
			
			cellBoard.appendChild(column);
		}
		
		cellBoard.getStyle().setProperty("background-color", "#18181B");
		cellBoard.getStyle().setProperty("padding", "25px");
		
		return cellBoard;
	}
	
	private HTMLElement createBoardColumn(int x) {
		HTMLElement cellColumn = createFlexBox("column", 6);
		
		for(int y = 0; y < 6; y++) {
			
			HTMLElement cell = DOCUMENT.createElement("div");
			
			cell.getStyle().setProperty("width", "50px");
			cell.getStyle().setProperty("height", "50px");
			cell.getStyle().setProperty("border-radius", "50%");
			cell.getStyle().setProperty("background-color", "#09090B");
			
			cellColumn.appendChild(cell);
			
			cells[x][y] = cell;
		}
		
		cellColumn.onClick(mouseEvent -> {
			
			System.out.println("clicked: " + x);
		});
		
		return cellColumn;
	}
	
	/**
	 * <href="https://tristan852.github.io/kite/"></>
	 * @param direction
	 * @param gap
	 * @return
	 */
	
	private HTMLElement createFlexBox(String direction, int gap) {
		HTMLElement flexBox = DOCUMENT.createElement("div");
		
		flexBox.getStyle().setProperty("display", "flex");
		flexBox.getStyle().setProperty("flex-direction", direction);
		flexBox.getStyle().setProperty("gap", gap + "px"); // Optional: spacing between items
		
		return flexBox;
	}
	
}
