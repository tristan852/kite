package net.kite.demo;

import net.kite.Kite;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.skill.level.SkillLevel;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.dom.html.*;
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
			
			OpeningBoardScoreCaches.ensureDefaultIsLoaded(bytes);
			
			solver = Kite.createInstance();
			
			HTMLBodyElement body = DOCUMENT.getBody();
			
			while(true) {
				
				Node node = body.getFirstChild();
				if(node == null) break;
				
				body.removeChild(node);
			}
			
			HTMLElement container = createFlexBox("column", 60);
			
			container.appendChild(createImage("https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/brand/small_logo.png", "", 120));
			
			HTMLElement version = DOCUMENT.createElement("span");
			
			version.setTextContent("v" + Kite.getVersion());
			
			container.appendChild(version);
			
			HTMLButtonElement button = (HTMLButtonElement) DOCUMENT.createElement("button");
			
			button.setTextContent("Analyze / Play vs AI");
			
			HTMLSelectElement select = (HTMLSelectElement) DOCUMENT.createElement("select");
			
			select.getOptions().add((HTMLOptionElement) DOCUMENT.createElement("option"));
			
			container.appendChild(button);
			container.appendChild(select);
			
			container.appendChild(createBoard());
			
			container.appendChild(wrapWithLink(createImage("https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/socials/github.png", "", 60), "https://github.com/tristan852/kite"));
			
			body.appendChild(container);
			
			playAIMove();
			playAIMove();
			playAIMove();
		});
		
		xhr.send();
	}
	
	// TODO synchronize these
	
	private void playHumanMove(int moveX) {
		int height = columnHeights[moveX];
		if(height == BOARD_HEIGHT) return;
		
		playMove(moveX);
	}
	
	private void playAIMove() {
		int moveX = solver.skilledMove(SkillLevel.PERFECT);
		
		playMove(moveX);
	}
	
	private void playMove(int moveX) {
		int moveY = columnHeights[moveX];
		columnHeights[moveX]++;
		
		HTMLElement cell = cells[moveX - 1][moveY];
		
		cell.getStyle().setProperty("background-color", redAtTurn ? "#FB2C36" : "#F0B100");
		
		solver.playMove(moveX);
		redAtTurn = !redAtTurn;
	}
	
	private void undoMove(int moveX, int moveY) {
		HTMLElement cell = cells[moveX - 1][moveY];
		
		cell.getStyle().setProperty("background-color", "#09090B");
		
		solver.undoMove();
		redAtTurn = !redAtTurn;
	}
	
	private HTMLElement createBoard() {
		HTMLElement cellBoard = createFlexBox("row", 0);
		
		for(int x = 0; x < 7; x++) {
			
			HTMLElement column = createBoardColumn(x);
			
			cellBoard.appendChild(column);
		}
		
		cellBoard.getStyle().setProperty("background-color", "#27272A");
		cellBoard.getStyle().setProperty("padding", "25px 19px");
		cellBoard.getStyle().setProperty("padding", "25px");
		
		return cellBoard;
	}
	
	private HTMLElement createBoardColumn(int x) {
		HTMLElement cellColumn = createFlexBox("column", 6);
		
		for(int y = BOARD_HEIGHT - 1; y >= 0; y--) {
			
			HTMLElement cell = DOCUMENT.createElement("div");
			
			cell.getStyle().setProperty("width", "50px");
			cell.getStyle().setProperty("height", "50px");
			cell.getStyle().setProperty("border-radius", "50%");
			cell.getStyle().setProperty("background-color", "#09090B");
			
			cellColumn.appendChild(cell);
			
			cells[x][y] = cell;
		}
		
		cellColumn.onClick(mouseEvent -> {
			
			int moveX = x + 1;
			playHumanMove(moveX);
		});
		
		cellColumn.getStyle().setProperty("cursor", "pointer");
		cellColumn.getStyle().setProperty("padding-left", "3px");
		cellColumn.getStyle().setProperty("padding-right", "3px");
		
		return cellColumn;
	}
	
	private HTMLElement wrapWithLink(HTMLElement element, String target) {
		HTMLLinkElement link = (HTMLLinkElement) DOCUMENT.createElement("a");
		
		link.setHref(target);
		link.appendChild(element);
		
		return link;
	}
	
	private HTMLElement createImage(String source, String altText, int size) {
		HTMLImageElement image = (HTMLImageElement) DOCUMENT.createElement("img");
		
		image.setSrc(source);
		image.setAlt(altText);
		image.setWidth(size);
		image.setHeight(size);
		
		return image;
	}
	
	private HTMLElement createFlexBox(String direction, int gap) {
		HTMLElement flexBox = DOCUMENT.createElement("div");
		
		flexBox.getStyle().setProperty("display", "flex");
		flexBox.getStyle().setProperty("flex-direction", direction);
		flexBox.getStyle().setProperty("justify-content", "center");
		flexBox.getStyle().setProperty("align-items", "center");
		flexBox.getStyle().setProperty("gap", gap + "px"); // Optional: spacing between items
		
		return flexBox;
	}
	
}
