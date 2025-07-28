package net.kite.demo;

import net.kite.Kite;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.skill.level.SkillLevel;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

// TODO optimize (especially the use of the solver; clean up)
public class KiteDemo {
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	private static final int BOARD_SIZE = 42;
	
	private static final Window WINDOW = Window.current();
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	private boolean aiPlay;
	private SkillLevel aiLevel = SkillLevel.PERFECT;
	
	private boolean aiPlaysRed;
	
	private Kite solver;
	
	private final int[] movesScores = new int[BOARD_WIDTH];
	
	private final int[] columnHeights = new int[BOARD_WIDTH];
	private boolean redAtTurn = true;
	
	private final int[] playedMoves = new int[BOARD_SIZE];
	
	private int playedMoveAmount;
	private int movesToRedoAmount;
	
	private final HTMLElement[][] cells = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[] cellLabels = new HTMLElement[BOARD_WIDTH];
	
	private HTMLElement winnerLabel;
	
	private HTMLButtonElement modeButton;
	private HTMLButtonElement undoButton;
	private HTMLButtonElement redoButton;
	
	private HTMLSelectElement levelSelect;
	
	public void onStart() {
		XMLHttpRequest xhr = new XMLHttpRequest();
		xhr.open("GET", "WEB-INF/classes/board_score_caches/opening.cfc");
		xhr.setResponseType("arraybuffer");
		xhr.onLoad((x) -> {
			
			if(xhr.getStatus() == 200) {
				
				ArrayBuffer arrayBuffer = (ArrayBuffer) xhr.getResponse();
				Int8Array array = new Int8Array(arrayBuffer);
				
				byte[] bytes = array.copyToJavaArray();
				
				buildApp(bytes);
				
			} else {
				
				System.err.println("error message");
			}
		});
		
		xhr.send();
	}
	
	private void buildApp(byte[] bytes) {
		OpeningBoardScoreCaches.ensureDefaultIsLoaded(bytes);
		
		solver = Kite.createInstance();
		
		HTMLBodyElement body = DOCUMENT.getBody();
		
		while(true) {
			
			Node node = body.getFirstChild();
			if(node == null) break;
			
			body.removeChild(node);
		}
		
		HTMLElement container = createFlexBox("row", 80);
		HTMLElement sidebarContainer = createFlexBox("column", 54);
		HTMLElement controlsContainer = createFlexBox("column", 10);
		HTMLElement brandContainer = createFlexBox("column", 40);
		
		brandContainer.getStyle().setProperty("margin", "6px");
		brandContainer.appendChild(createImage("https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/brand/small_logo.png", "", 120));
		
		HTMLElement version = DOCUMENT.createElement("span");
		
		version.setTextContent("v" + Kite.getVersion());
		version.getStyle().setProperty("font-style", "italic");
		
		brandContainer.appendChild(version);
		sidebarContainer.appendChild(brandContainer);
		
		modeButton = (HTMLButtonElement) createControl("button", (mouseEvent) -> {
			
			toggleMode();
		});
		
		HTMLElement button2 = createControl("button", (mouseEvent) -> {
			
			clearBoard();
		});
		
		button2.setTextContent("New Game");
		
		undoButton = (HTMLButtonElement) createControl("button", (mouseEvent) -> {
			
			if(!aiPlay) undoMove();
		});
		
		undoButton.setTextContent("Undo Move");
		
		redoButton = (HTMLButtonElement) createControl("button", (mouseEvent) -> {
			
			redoMove();
		});
		
		redoButton.setTextContent("Redo Move");
		
		levelSelect = (HTMLSelectElement) createControl("select", (mouseEvent) -> {
			
			// toggleMode();
		});
		
		levelSelect.addEventListener("change", (event) -> {
			
			setAILevel(levelSelect.getSelectedIndex());
		});
		
		for(SkillLevel level : SkillLevel.values()) {
			
			HTMLOptionElement optionElement = (HTMLOptionElement) DOCUMENT.createElement("option");
			
			optionElement.setLabel(level.name());
			
			levelSelect.getOptions().add(optionElement);
			
			// optionElement.setDefaultSelected();
		}
		
		levelSelect.getStyle().setProperty("text-align", "center");
		
		controlsContainer.appendChild(modeButton);
		controlsContainer.appendChild(levelSelect);
		controlsContainer.appendChild(button2);
		controlsContainer.appendChild(undoButton);
		controlsContainer.appendChild(redoButton);
		
		redoButton.setDisabled(true);
		
		sidebarContainer.appendChild(controlsContainer);
		
		HTMLElement githubLink = createImage("https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/socials/github.png", "", 50);
		
		githubLink.getStyle().setProperty("padding", "6px");
		
		githubLink = wrapWithLink(githubLink, "https://github.com/tristan852/kite");
		
		sidebarContainer.appendChild(githubLink);
		
		System.out.println("db1");
		
		container.appendChild(sidebarContainer);
		container.appendChild(createBoard());
		
		String search = WINDOW.getLocation().getSearch();
		if(!search.isBlank()) {
			
			search = search.substring(1);
			
			String[] items = search.split("&");
			System.out.println(Arrays.toString(items));
			for(String item : items) {
				
				String key = item.split("=")[0];
				String value = item.split("=")[1];
				
				if(key.equals("moves")) {
					
					for(char c : value.toCharArray()) {
						
						playMove(c - '0');
					}
					
				} else if(key.equals("ai-color")) {
					
					aiPlaysRed = value.equals("red");
					setMode(true);
					// bug: this clears the loaded moves
					
				} else {
					
					setAILevel(Integer.parseInt(value));
				}
			}
		}
		
		body.appendChild(container);
		
		if(solver.gameOver()) return;
		if(aiPlay && aiPlaysRed == redAtTurn) playAIMove();
	}
	
	// TODO synchronize (keyword) these
	
	private void toggleMode() {
		setMode(!aiPlay);
	}
	
	private void setMode(boolean aiPlay) {
		this.aiPlay = aiPlay;
		
		modeButton.setTextContent(aiPlay ? "Mode: Play vs. AI" : "Mode: Analyze");
		
		if(aiPlay) clearBoard();
		
		updateLabels();
	}
	
	private void setAILevel(int level) {
		aiLevel = SkillLevel.values()[level]; // TODO array as constant -> orderred ai levels
		
		System.out.println(aiLevel);
		
		levelSelect.setSelectedIndex(level);
		
		if(aiPlay) clearBoard();
	}
	
	private void setWindowSearch() {
		String s = "moves=" + solver.boardMovesString();
		
		if(aiPlay) {
			
			if(!s.isBlank()) s += "&";
			
			s += "ai-color=" + (aiPlaysRed ? "red" : "yellow");
			s += "&ai-level=" + aiLevel.ordinal();
		}
		
		if(!s.isBlank()) s = "?" + s;
		
		Window.current().getHistory().pushState(null, "", s);
	}
	
	private void clearBoard() {
		while(playedMoveAmount > 0) undoMove();
		
		movesToRedoAmount = 0;
		
		aiPlaysRed = ThreadLocalRandom.current().nextBoolean();
		if(aiPlaysRed && aiPlay) playAIMove();
	}
	
	private void playHumanMove(int moveX) {
		if(solver.gameOver()) return;
		
		int columnIndex = moveX - 1;
		
		int height = columnHeights[columnIndex];
		if(height == BOARD_HEIGHT) return;
		
		playMove(moveX);
		movesToRedoAmount = 0;
		
		if(solver.gameOver()) return;
		System.out.println(aiPlay + ", " + aiPlaysRed + ", " + redAtTurn);
		if(aiPlay && aiPlaysRed == redAtTurn) playAIMove();
	}
	
	private void playAIMove() {
		int moveX = solver.skilledMove(aiLevel);
		
		playMove(moveX);
		movesToRedoAmount = 0;
	}
	
	private void playMove(int moveX) {
		int columnIndex = moveX - 1;
		
		int moveY = columnHeights[columnIndex];
		columnHeights[columnIndex]++;
		
		HTMLElement cell = cells[columnIndex][moveY];
		
		cell.getStyle().setProperty("background-color", redAtTurn ? "#FB2C36" : "#F0B100");
		
		solver.playMove(moveX);
		redAtTurn = !redAtTurn;
		
		playedMoves[playedMoveAmount] = moveX;
		playedMoveAmount++;
		
		updateLabels();
		updateWinnerLabel();
	}
	
	private void redoMove() {
		if(movesToRedoAmount == 0) return;
		
		int moveX = playedMoves[playedMoveAmount];
		
		playMove(moveX);
		movesToRedoAmount--;
	}
	
	private void undoMove() {
		if(playedMoveAmount == 0) return;
		
		playedMoveAmount--;
		movesToRedoAmount++;
		
		int moveX = playedMoves[playedMoveAmount];
		int columnIndex = moveX - 1;
		
		columnHeights[columnIndex]--;
		
		int moveY = columnHeights[columnIndex];
		
		HTMLElement cell = cells[columnIndex][moveY];
		
		cell.getStyle().setProperty("background-color", "#09090B");
		
		solver.undoMove();
		redAtTurn = !redAtTurn;
		
		updateLabels();
		updateWinnerLabel();
	}
	
	private void updateWinnerLabel() {
		if(solver.gameOver()) {
			
			BoardOutcome outcome = solver.gameOutcome();
			
			String text = outcome == BoardOutcome.DRAW ? "Draw!" : outcome == BoardOutcome.RED_WIN ? "Red wins!" : "Yellow wins!";
			String textColor = outcome == BoardOutcome.DRAW ? "#71717B" : outcome == BoardOutcome.RED_WIN ? "#FB2C36" : "#F0B100";
			
			winnerLabel.setTextContent(text);
			winnerLabel.getStyle().setProperty("color", textColor);
			
		} else {
			
			winnerLabel.setTextContent("");
		}
	}
	
	private void updateLabels() {
		boolean gameOver = solver.gameOver();
		
		if(aiPlay || gameOver) hideLabels();
		else showLabels();
	}
	
	private void showLabels() {
		solver.evaluateAllMoves(movesScores);
		System.out.println(Arrays.toString(movesScores));
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			String s = movesScores[x] == Integer.MIN_VALUE ? "" : formatScore(movesScores[x]);
			cellLabels[x].setTextContent(s);
		}
	}
	
	private void hideLabels() {
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			cellLabels[x].setTextContent("");
		}
	}
	
	private HTMLElement createBoard() {
		HTMLElement cellBoard = createFlexBox("row", 0);
		
		for(int x = 0; x < 7; x++) {
			
			HTMLElement column = createBoardColumn(x);
			
			cellBoard.appendChild(column);
		}
		
		cellBoard.getStyle().setProperty("background-color", "#27272A");
		cellBoard.getStyle().setProperty("padding", "25px 19px");
		cellBoard.getStyle().setProperty("border-radius", "25px");
		
		HTMLElement labels = createFlexBox("row", 0);
		labels.getStyle().setProperty("margin-bottom", "8px");
		
		for(int x = 0; x < 7; x++) {
			
			HTMLElement label = DOCUMENT.createElement("span");
			
			label.getStyle().setProperty("width", "56px");
			label.getStyle().setProperty("height", "20px");
			label.getStyle().setProperty("text-align", "center");
			label.getStyle().setProperty("font-weight", "bold");
			
			labels.appendChild(label);
			
			cellLabels[x] = label;
		}
		
		winnerLabel = DOCUMENT.createElement("span");
		
		winnerLabel.getStyle().setProperty("height", "28px");
		winnerLabel.getStyle().setProperty("text-align", "center");
		winnerLabel.getStyle().setProperty("font-style", "italic");
		winnerLabel.getStyle().setProperty("font-weight", "bold");
		winnerLabel.getStyle().setProperty("font-size", "24px");
		
		HTMLElement container = createFlexBox("column", 30);
		
		container.appendChild(winnerLabel);
		container.appendChild(cellBoard);
		container.appendChild(labels);
		
		return container;
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
		HTMLAnchorElement link = (HTMLAnchorElement) DOCUMENT.createElement("a");
		
		link.setHref(target);
		link.appendChild(element);
		
		return link;
	}
	
	private HTMLElement createControl(String type, EventListener<MouseEvent> clickListener) {
		HTMLElement element = DOCUMENT.createElement(type);
		
		element.getStyle().setProperty("width", "150px");
		element.getStyle().setProperty("height", "30px");
		element.getStyle().setProperty("color", "#F4F4F5");
		element.getStyle().setProperty("background-color", "#27272A");
		element.getStyle().setProperty("cursor", "pointer");
		element.getStyle().setProperty("border", "none");
		element.getStyle().setProperty("border-radius", "6px");
		
		element.onClick(clickListener);
		
		return element;
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
	
	private static String formatScore(int score) {
		if(score > 0) return "+" + score;
		
		return String.valueOf(score);
	}
	
}
