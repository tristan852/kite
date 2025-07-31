package net.kite.demo;

import net.kite.Kite;
import net.kite.board.outcome.BoardOutcome;
import net.kite.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.skill.level.SkillLevel;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import java.util.concurrent.ThreadLocalRandom;

public class KiteDemo {
	
	private static final String APP_TITLE = "Kite - Connect four solver";
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	private static final int BOARD_SIZE = 42;
	
	private static final String LOCATION_SEARCH_PREFIX = "?";
	private static final String LOCATION_SEARCH_ITEM_SEPARATOR = "&";
	private static final String LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR = "=";
	private static final String LOCATION_SEARCH_MOVES_KEY = "moves";
	private static final String LOCATION_SEARCH_AI_COLOR_KEY = "ai-color";
	private static final String LOCATION_SEARCH_AI_LEVEL_KEY = "ai-level";
	
	private static final char SMALLEST_LOCATION_SEARCH_MOVE = '1';
	private static final String RED_LOCATION_SEARCH_AI_COLOR = "red";
	private static final String YELLOW_LOCATION_SEARCH_AI_COLOR = "yellow";
	
	private static final SkillLevel[] ORDERED_AI_SKILL_LEVELS = SkillLevel.values();
	
	private static final String POSITIVE_MOVE_SCORE_FORMAT_PREFIX = "+";
	
	private static final String DEFAULT_ELEMENT_TYPE = "div";
	private static final String ANCHOR_ELEMENT_TYPE = "a";
	private static final String SPAN_ELEMENT_TYPE = "span";
	private static final String IMAGE_ELEMENT_TYPE = "img";
	private static final String BUTTON_ELEMENT_TYPE = "button";
	private static final String SELECT_ELEMENT_TYPE = "select";
	private static final String OPTION_ELEMENT_TYPE = "option";
	
	private static final String ELEMENT_CHANGE_EVENT_TYPE = "change";
	
	private static final String ELEMENT_WIDTH_STYLE_KEY = "width";
	private static final String ELEMENT_WIDTH_STYLE_VALUE_FORMAT = "%spx";
	private static final String ELEMENT_HEIGHT_STYLE_KEY = "height";
	private static final String ELEMENT_HEIGHT_STYLE_VALUE_FORMAT = "%spx";
	private static final String ELEMENT_COLOR_STYLE_KEY = "color";
	private static final String ELEMENT_BACKGROUND_COLOR_STYLE_KEY = "background-color";
	
	private static final String[] FLEXBOX_ELEMENT_STYLES = new String[] {
			"display", "flex",
			"justify-content", "center",
			"align-items", "center",
			"flex-wrap", "wrap"
	};
	
	private static final String FLEXBOX_ELEMENT_DIRECTION_STYLE_KEY = "flex-direction";
	private static final String FLEXBOX_ELEMENT_GAP_STYLE_KEY = "gap";
	private static final String FLEXBOX_ELEMENT_GAP_STYLE_VALUE_FORMAT = "%spx";
	
	private static final String FLEXBOX_ELEMENT_ROW_DIRECTION = "row";
	private static final String FLEXBOX_ELEMENT_COLUMN_DIRECTION = "column";
	
	private static final String[] CONTROL_ELEMENT_STYLES = new String[] {
			"width", "150px",
			"height", "30px",
			"color", "#F4F4F5",
			"background-color", "#27272A",
			"border", "none",
			"border-radius", "6px",
			"cursor", "pointer"
	};
	
	private static final String[] ENABLED_CONTROL_ELEMENT_STYLES = new String[] {
			"color", "#F4F4F5",
			"background-color", "#27272A",
			"cursor", "pointer"
	};
	
	private static final String[] DISABLED_CONTROL_ELEMENT_STYLES = new String[] {
			"color", "#9F9FA9",
			"background-color", "#18181B",
			"cursor", "default"
	};
	
	private static final String[] OPTION_ELEMENT_STYLES = new String[] {
			"cursor", "pointer"
	};
	
	private static final String[] BRAND_ELEMENT_STYLES = new String[] {
			"margin", "6px"
	};
	
	private static final String[] GITHUB_LOGO_ELEMENT_STYLES = new String[] {
			"padding", "6px"
	};
	
	private static final String[] VERSION_ELEMENT_STYLES = new String[] {
			"font-style", "italic"
	};
	
	private static final String[] AI_SKILL_LEVEL_SELECT_ELEMENT_STYLES = new String[] {
			"text-align", "center"
	};
	
	private static final String[] WINNER_LABEL_ELEMENT_STYLES = new String[] {
			"text-align", "center",
			"font-style", "italic",
			"font-weight", "bold",
			"font-size", "24px"
	};
	
	private static final String[] CELL_ELEMENT_STYLES = new String[] {
			"width", "100%",
			"aspect-ratio", "1",
			"border-radius", "50%",
			"background-color", "#09090B"
	};
	
	private static final String[] CELL_COLUMN_ELEMENT_STYLES = new String[] {
			"width", "calc(100% / 430 * 50)",
			"padding-left", "calc(100% / 430 * 3)",
			"padding-right", "calc(100% / 430 * 3)",
			"cursor", "pointer"
	};
	
	private static final String[] CELL_LABEL_ELEMENT_STYLES = new String[] {
			"text-align", "center",
			"font-weight", "bold"
	};
	
	private static final String[] CELL_LABELS_ELEMENT_STYLES = new String[] {
			"margin-bottom", "8px"
	};
	
	// 430 x 380
	private static final String[] BOARD_ELEMENT_STYLES = new String[] {
			"aspect-ratio", "43 / 38",
			"background-color", "#27272A",
			"border-radius", "25px"
	};
	
	private static final int APP_ELEMENT_GAP = 80;
	private static final int SIDEBAR_ELEMENT_GAP = 54;
	private static final int CONTROLS_ELEMENT_GAP = 10;
	private static final int BRAND_ELEMENT_GAP = 40;
	private static final int BOARD_AND_LABELS_ELEMENT_GAP = 30;
	private static final int CELL_COLUMN_ELEMENT_GAP = 6;
	
	private static final String LOGO_ELEMENT_SOURCE_PATH = "https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/brand/small_logo.png";
	private static final String LOGO_ELEMENT_ALTERNATIVE_TEXT = "The Kite logo";
	private static final int LOGO_ELEMENT_SIZE = 120;
	
	private static final String GITHUB_LOGO_ELEMENT_SOURCE_PATH = "https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/socials/github.png";
	private static final String GITHUB_LOGO_ELEMENT_ALTERNATIVE_TEXT = "The GitHub logo";
	private static final int GITHUB_LOGO_ELEMENT_SIZE = 50;
	private static final String GITHUB_LOGO_ELEMENT_TARGET_PATH = "https://github.com/tristan852/kite";
	
	private static final String VERSION_ELEMENT_TEXT_FORMAT = "v%s";
	private static final String FIRST_MODE_BUTTON_ELEMENT_TEXT = "Mode: Analyze";
	private static final String SECOND_MODE_BUTTON_ELEMENT_TEXT = "Mode: Play vs. AI";
	private static final String NEW_GAME_BUTTON_ELEMENT_TEXT = "New Game";
	private static final String UNDO_BUTTON_ELEMENT_TEXT = "Undo Move";
	private static final String REDO_BUTTON_ELEMENT_TEXT = "Redo Move";
	
	private static final int WINNER_LABEL_ELEMENT_HEIGHT = 28;
	
	private static final String RED_WINNER_LABEL_ELEMENT_TEXT = "Red wins!";
	private static final String YELLOW_WINNER_LABEL_ELEMENT_TEXT = "Yellow wins!";
	private static final String DRAW_WINNER_LABEL_ELEMENT_TEXT = "Draw!";
	private static final String EMPTY_WINNER_LABEL_ELEMENT_TEXT = "";
	
	private static final String RED_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR = "#FB2C36";
	private static final String YELLOW_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR = "#F0B100";
	private static final String DRAW_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR = "#71717B";
	
	private static final String[] CELL_ELEMENT_BACKGROUND_COLORS = new String[] {
			"#FB2C36",
			"#F0B100",
			"#09090B"
	};
	
	private static final int RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 0;
	private static final int YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 1;
	private static final int EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 2;
	
	private static final int CELL_LABEL_ELEMENT_WIDTH = 56;
	private static final int CELL_LABEL_ELEMENT_HEIGHT = 20;
	private static final String EMPTY_CELL_LABEL_ELEMENT_TEXT = "";
	
	private static final String REQUEST_METHOD = "GET";
	private static final String REQUEST_RESPONSE_TYPE = "arraybuffer";
	private static final String REQUEST_URL = "WEB-INF/classes/board_score_caches/opening.cfc";
	private static final int REQUEST_RETRY_TIME_DELAY = 1000;
	
	private static final int SUCCESSFUL_REQUEST_STATUS = 200;
	
	private static final Window WINDOW = Window.current();
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	private boolean aiModeSelected;
	private SkillLevel aiSkillLevel;
	
	private boolean aiPlaysRed;
	private boolean redAtTurn;
	
	private final int[] playedMoves = new int[BOARD_SIZE];
	private final int[] columnPlayedMoveAmounts = new int[BOARD_WIDTH];
	
	private int playedMoveAmount;
	private int undoneMoveAmount;
	
	private final int[] movesScores = new int[BOARD_WIDTH];
	
	private HTMLButtonElement modeButtonElement;
	private HTMLButtonElement undoButtonElement;
	private HTMLButtonElement redoButtonElement;
	
	private HTMLSelectElement aiSkillLevelSelectElement;
	
	private final HTMLElement[][] cellElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[] cellLabelElements = new HTMLElement[BOARD_WIDTH];
	
	private HTMLElement winnerLabelElement;
	
	private Kite solver;
	
	public KiteDemo() {
		this.redAtTurn = true;
	}
	
	public void onStart() {
		XMLHttpRequest request = new XMLHttpRequest();
		
		request.open(REQUEST_METHOD, REQUEST_URL);
		request.setResponseType(REQUEST_RESPONSE_TYPE);
		
		request.onLoad((progressEvent) -> {
			
			int requestStatus = request.getStatus();
			if(requestStatus == SUCCESSFUL_REQUEST_STATUS) {
				
				ArrayBuffer arrayBuffer = (ArrayBuffer) request.getResponse();
				Int8Array array = new Int8Array(arrayBuffer);
				
				byte[] bytes = array.copyToJavaArray();
				OpeningBoardScoreCaches.ensureDefaultIsLoaded(bytes);
				
				buildApp();
				
			} else {
				
				onLoadError();
			}
		});
		
		request.onError((progressEvent) -> {
			
			onLoadError();
		});
		
		request.onTimeout((progressEvent) -> {
			
			onLoadError();
		});
		
		request.onAbort((progressEvent) -> {
			
			onLoadError();
		});
		
		request.send();
	}
	
	private void onLoadError() {
		System.err.println("An error occurred while loading the opening score cache!");
		
		Window.setTimeout(this::onStart, REQUEST_RETRY_TIME_DELAY);
	}
	
	private void buildApp() {
		solver = Kite.createInstance();
		
		HTMLBodyElement bodyElement = DOCUMENT.getBody();
		
		while(true) {
			
			Node node = bodyElement.getFirstChild();
			if(node == null) break;
			
			bodyElement.removeChild(node);
		}
		
		HTMLElement appElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, APP_ELEMENT_GAP);
		HTMLElement sidebarElement = createFlexboxElement(FLEXBOX_ELEMENT_COLUMN_DIRECTION, SIDEBAR_ELEMENT_GAP);
		HTMLElement controlsElement = createFlexboxElement(FLEXBOX_ELEMENT_COLUMN_DIRECTION, CONTROLS_ELEMENT_GAP);
		HTMLElement brandElement = createFlexboxElement(FLEXBOX_ELEMENT_COLUMN_DIRECTION, BRAND_ELEMENT_GAP);
		
		setElementStyles(brandElement, BRAND_ELEMENT_STYLES);
		
		HTMLElement logoImageElement = createImageElement(LOGO_ELEMENT_SOURCE_PATH, LOGO_ELEMENT_ALTERNATIVE_TEXT, LOGO_ELEMENT_SIZE);
		HTMLElement versionElement = createSpanElement(0, 0);
		
		String version = Kite.getVersion();
		String versionElementText = VERSION_ELEMENT_TEXT_FORMAT.formatted(version);
		
		versionElement.setTextContent(versionElementText);
		
		setElementStyles(versionElement, VERSION_ELEMENT_STYLES);
		
		brandElement.appendChild(logoImageElement);
		brandElement.appendChild(versionElement);
		
		modeButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		undoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		redoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		HTMLButtonElement newGameButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		aiSkillLevelSelectElement = (HTMLSelectElement) createControlElement(SELECT_ELEMENT_TYPE);
		
		modeButtonElement.setTextContent(FIRST_MODE_BUTTON_ELEMENT_TEXT);
		undoButtonElement.setTextContent(UNDO_BUTTON_ELEMENT_TEXT);
		redoButtonElement.setTextContent(REDO_BUTTON_ELEMENT_TEXT);
		
		newGameButtonElement.setTextContent(NEW_GAME_BUTTON_ELEMENT_TEXT);
		
		modeButtonElement.onClick((mouseEvent) -> {
			
			changeMode();
		});
		
		newGameButtonElement.onClick((mouseEvent) -> {
			
			setupNewGame();
		});
		
		undoButtonElement.onClick((mouseEvent) -> {
			
			undoMove();
		});
		
		redoButtonElement.onClick((mouseEvent) -> {
			
			redoMove();
		});
		
		aiSkillLevelSelectElement.addEventListener(ELEMENT_CHANGE_EVENT_TYPE, (event) -> {
			
			int i = aiSkillLevelSelectElement.getSelectedIndex();
			changeAISkillLevel(i);
		});
		
		for(SkillLevel skillLevel : ORDERED_AI_SKILL_LEVELS) {
			
			String skillLevelName = skillLevel.name();
			skillLevelName = formatSkillLevelName(skillLevelName);
			
			HTMLOptionElement optionElement = createOptionElement(skillLevelName);
			aiSkillLevelSelectElement.appendChild(optionElement);
		}
		
		setElementStyles(aiSkillLevelSelectElement, AI_SKILL_LEVEL_SELECT_ELEMENT_STYLES);
		
		controlsElement.appendChild(modeButtonElement);
		controlsElement.appendChild(aiSkillLevelSelectElement);
		controlsElement.appendChild(newGameButtonElement);
		controlsElement.appendChild(undoButtonElement);
		controlsElement.appendChild(redoButtonElement);
		
		HTMLElement githubLogoElement = createImageElement(GITHUB_LOGO_ELEMENT_SOURCE_PATH, GITHUB_LOGO_ELEMENT_ALTERNATIVE_TEXT, GITHUB_LOGO_ELEMENT_SIZE);
		setElementStyles(githubLogoElement, GITHUB_LOGO_ELEMENT_STYLES);
		
		githubLogoElement = createAnchorElement(GITHUB_LOGO_ELEMENT_TARGET_PATH, githubLogoElement);
		
		sidebarElement.appendChild(brandElement);
		sidebarElement.appendChild(controlsElement);
		sidebarElement.appendChild(githubLogoElement);
		
		HTMLElement boardElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, 0);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			HTMLElement cellColumnElement = createFlexboxElement(FLEXBOX_ELEMENT_COLUMN_DIRECTION, CELL_COLUMN_ELEMENT_GAP);
			
			int maxY = BOARD_HEIGHT - 1;
			for(int y = maxY; y >= 0; y--) {
				
				HTMLElement cellElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellElement, CELL_ELEMENT_STYLES);
				
				cellElements[x][y] = cellElement;
				
				cellColumnElement.appendChild(cellElement);
			}
			
			int moveX = x;
			
			cellColumnElement.onClick((mouseEvent) -> {
				
				playMove(moveX, false, false);
			});
			
			setElementStyles(cellColumnElement, CELL_COLUMN_ELEMENT_STYLES);
			
			boardElement.appendChild(cellColumnElement);
		}
		
		setElementStyles(boardElement, BOARD_ELEMENT_STYLES);
		
		HTMLElement cellLabelsElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, 0);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			HTMLElement cellLabelElement = createSpanElement(CELL_LABEL_ELEMENT_WIDTH, CELL_LABEL_ELEMENT_HEIGHT);
			setElementStyles(cellLabelElement, CELL_LABEL_ELEMENT_STYLES);
			
			cellLabelElements[x] = cellLabelElement;
			
			cellLabelsElement.appendChild(cellLabelElement);
		}
		
		setElementStyles(cellLabelsElement, CELL_LABELS_ELEMENT_STYLES);
		
		winnerLabelElement = createSpanElement(0, WINNER_LABEL_ELEMENT_HEIGHT);
		setElementStyles(winnerLabelElement, WINNER_LABEL_ELEMENT_STYLES);
		
		HTMLElement boardAndLabelsElement = createFlexboxElement(FLEXBOX_ELEMENT_COLUMN_DIRECTION, BOARD_AND_LABELS_ELEMENT_GAP);
		
		boardAndLabelsElement.appendChild(winnerLabelElement);
		boardAndLabelsElement.appendChild(boardElement);
		boardAndLabelsElement.appendChild(cellLabelsElement);
		
		appElement.appendChild(sidebarElement);
		appElement.appendChild(boardAndLabelsElement);
		
		int perfectIndex = SkillLevel.PERFECT.ordinal();
		
		aiSkillLevel = SkillLevel.PERFECT;
		aiSkillLevelSelectElement.setSelectedIndex(perfectIndex);
		
		Location location = WINDOW.getLocation();
		String locationSearch = location.getSearch();
		
		if(!locationSearch.isBlank()) {
			
			locationSearch = locationSearch.substring(1);
			
			String[] items = locationSearch.split(LOCATION_SEARCH_ITEM_SEPARATOR);
			for(String item : items) {
				
				String[] itemKeyAndValue = item.split(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				
				String itemKey = itemKeyAndValue[0];
				String itemValue = itemKeyAndValue[1];
				
				switch(itemKey) {
					case LOCATION_SEARCH_MOVES_KEY -> {
						
						int l = itemValue.length();
						for(int i = 0; i < l; i++) {
							
							int moveX = itemValue.charAt(i) - SMALLEST_LOCATION_SEARCH_MOVE;
							playMove(moveX, false, true);
						}
					}
					case LOCATION_SEARCH_AI_COLOR_KEY -> {
						
						aiModeSelected = true;
						
						modeButtonElement.setTextContent(SECOND_MODE_BUTTON_ELEMENT_TEXT);
						
						disableButtonElement(undoButtonElement);
						disableButtonElement(redoButtonElement);
						
						aiPlaysRed = itemValue.equals(RED_LOCATION_SEARCH_AI_COLOR);
					}
					default -> {
						
						int i = Integer.parseInt(itemValue);
						
						aiSkillLevel = ORDERED_AI_SKILL_LEVELS[i];
						aiSkillLevelSelectElement.setSelectedIndex(i);
					}
				}
			}
		}
		
		if(aiModeSelected) {
			
			if(aiPlaysRed == redAtTurn) {
				
				boolean gameNotOver = !solver.gameOver();
				if(gameNotOver) playAIMove();
			}
			
		} else {
			
			disableSelectElement(aiSkillLevelSelectElement);
			
			if(playedMoveAmount == 0) {
				
				updateCellLabelElements();
			}
		}
		
		bodyElement.appendChild(appElement);
	}
	
	private void changeAISkillLevel(int aiSkillLevelIndex) {
		int index = aiSkillLevel.ordinal();
		if(index == aiSkillLevelIndex) return;
		
		aiSkillLevel = ORDERED_AI_SKILL_LEVELS[aiSkillLevelIndex];
		
		updateLocationSearch();
		setupNewGame();
	}
	
	private void changeMode() {
		aiModeSelected = !aiModeSelected;
		if(aiModeSelected) {
			
			modeButtonElement.setTextContent(SECOND_MODE_BUTTON_ELEMENT_TEXT);
			
			disableButtonElement(undoButtonElement);
			disableButtonElement(redoButtonElement);
			enableSelectElement(aiSkillLevelSelectElement);
			
			setupNewGame();
			
		} else {
			
			modeButtonElement.setTextContent(FIRST_MODE_BUTTON_ELEMENT_TEXT);
			
			enableButtonElement(undoButtonElement);
			enableButtonElement(redoButtonElement);
			disableSelectElement(aiSkillLevelSelectElement);
		}
		
		updateCellLabelElements();
		updateLocationSearch();
	}
	
	private void setupNewGame() {
		if(playedMoveAmount != 0) {
			
			solver.clearBoard();
			
			for(int i = playedMoveAmount - 1; i >= 0; i--) {
				
				int moveX = playedMoves[i];
				
				columnPlayedMoveAmounts[moveX]--;
				
				int moveY = columnPlayedMoveAmounts[moveX];
				
				setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX);
			}
			
			playedMoveAmount = 0;
			undoneMoveAmount = 0;
			
			redAtTurn = true;
			
			if(!aiModeSelected) updateCellLabelElements();
			updateWinnerLabelElement();
			updateLocationSearch();
		}
		
		if(aiModeSelected) {
			
			ThreadLocalRandom random = ThreadLocalRandom.current();
			
			aiPlaysRed = random.nextBoolean();
			if(aiPlaysRed) playAIMove();
			else updateLocationSearch();
		}
	}
	
	private void undoMove() {
		if(playedMoveAmount == 0) return;
		
		playedMoveAmount--;
		undoneMoveAmount++;
		
		redAtTurn = !redAtTurn;
		
		int moveX = playedMoves[playedMoveAmount];
		
		columnPlayedMoveAmounts[moveX]--;
		
		int moveY = columnPlayedMoveAmounts[moveX];
		
		solver.undoMove();
		setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX);
		
		if(!aiModeSelected) updateCellLabelElements();
		updateWinnerLabelElement();
		updateLocationSearch();
	}
	
	private void redoMove() {
		if(undoneMoveAmount == 0) return;
		
		int moveX = playedMoves[playedMoveAmount];
		playMove(moveX, true, false);
	}
	
	private void playAIMove() {
		int moveX = solver.skilledMove(aiSkillLevel);
		moveX--;
		
		playMove(moveX, false, false);
	}
	
	private void playMove(int moveX, boolean redo, boolean initial) {
		if(solver.gameOver()) return;
		
		int moveY = columnPlayedMoveAmounts[moveX];
		if(moveY == BOARD_HEIGHT) return;
		
		int i = redAtTurn ? RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
		
		playedMoves[playedMoveAmount] = moveX;
		playedMoveAmount++;
		
		redAtTurn = !redAtTurn;
		
		if(redo) undoneMoveAmount--;
		else undoneMoveAmount = 0;
		
		columnPlayedMoveAmounts[moveX]++;
		
		solver.playMove(moveX + 1);
		setCellElementBackgroundColor(moveX, moveY, i);
		
		if(aiModeSelected) {
			
			if(!solver.gameOver() && aiPlaysRed == redAtTurn) {
				
				playAIMove();
				return;
			}
		}
		
		updateWinnerLabelElement();
		if(!initial) {
			
			updateLocationSearch();
			if(!aiModeSelected) updateCellLabelElements();
		}
	}
	
	private void setCellElementBackgroundColor(int cellElementX, int cellElementY, int cellElementBackgroundColorIndex) {
		String s = CELL_ELEMENT_BACKGROUND_COLORS[cellElementBackgroundColorIndex];
		
		HTMLElement cellElement = cellElements[cellElementX][cellElementY];
		setElementStyles(cellElement, ELEMENT_BACKGROUND_COLOR_STYLE_KEY, s);
	}
	
	private void updateCellLabelElements() {
		if(aiModeSelected) {
			
			for(int x = 0; x < BOARD_WIDTH; x++) {
				
				HTMLElement cellLabelElement = cellLabelElements[x];
				cellLabelElement.setTextContent(EMPTY_CELL_LABEL_ELEMENT_TEXT);
			}
			
			return;
		}
		
		solver.evaluateAllMoves(movesScores);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			int moveScore = movesScores[x];
			String moveScoreString = formatMoveScore(moveScore);
			
			HTMLElement cellLabelElement = cellLabelElements[x];
			cellLabelElement.setTextContent(moveScoreString);
		}
	}
	
	private void updateWinnerLabelElement() {
		BoardOutcome gameOutcome = solver.gameOutcome();
		switch(gameOutcome) {
			case UNDECIDED -> {
				
				winnerLabelElement.setTextContent(EMPTY_WINNER_LABEL_ELEMENT_TEXT);
			}
			case RED_WIN -> {
				
				winnerLabelElement.setTextContent(RED_WINNER_LABEL_ELEMENT_TEXT);
				setElementStyles(winnerLabelElement, ELEMENT_COLOR_STYLE_KEY, RED_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR);
			}
			case YELLOW_WIN -> {
				
				winnerLabelElement.setTextContent(YELLOW_WINNER_LABEL_ELEMENT_TEXT);
				setElementStyles(winnerLabelElement, ELEMENT_COLOR_STYLE_KEY, YELLOW_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR);
			}
			case DRAW -> {
				
				winnerLabelElement.setTextContent(DRAW_WINNER_LABEL_ELEMENT_TEXT);
				setElementStyles(winnerLabelElement, ELEMENT_COLOR_STYLE_KEY, DRAW_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR);
			}
		}
	}
	
	private void updateLocationSearch() {
		Location location = WINDOW.getLocation();
		String locationPath = location.getPathName();
		
		boolean movesWerePlayed = playedMoveAmount != 0;
		boolean aiLevelNotPerfect = aiSkillLevel != SkillLevel.PERFECT;
		boolean searchNotEmpty = movesWerePlayed || aiModeSelected || aiLevelNotPerfect;
		if(searchNotEmpty) {
			
			StringBuilder stringBuilder = new StringBuilder(locationPath);
			stringBuilder.append(LOCATION_SEARCH_PREFIX);
			
			boolean b = false;
			
			if(movesWerePlayed) {
				
				b = true;
				
				stringBuilder.append(LOCATION_SEARCH_MOVES_KEY);
				stringBuilder.append(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				
				for(int i = 0; i < playedMoveAmount; i++) {
					
					char c = (char) (SMALLEST_LOCATION_SEARCH_MOVE + playedMoves[i]);
					stringBuilder.append(c);
				}
			}
			
			if(aiModeSelected) {
				
				if(b) stringBuilder.append(LOCATION_SEARCH_ITEM_SEPARATOR);
				b = true;
				
				String s = aiPlaysRed ? RED_LOCATION_SEARCH_AI_COLOR : YELLOW_LOCATION_SEARCH_AI_COLOR;
				
				stringBuilder.append(LOCATION_SEARCH_AI_COLOR_KEY);
				stringBuilder.append(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				stringBuilder.append(s);
			}
			
			if(aiLevelNotPerfect) {
				
				if(b) stringBuilder.append(LOCATION_SEARCH_ITEM_SEPARATOR);
				
				int index = aiSkillLevelSelectElement.getSelectedIndex();
				
				stringBuilder.append(LOCATION_SEARCH_AI_LEVEL_KEY);
				stringBuilder.append(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				stringBuilder.append(index);
			}
			
			locationPath = stringBuilder.toString();
		}
		
		History history = WINDOW.getHistory();
		history.replaceState(null, APP_TITLE, locationPath);
	}
	
	private static void enableButtonElement(HTMLButtonElement buttonElement) {
		buttonElement.setDisabled(false);
		
		setElementStyles(buttonElement, ENABLED_CONTROL_ELEMENT_STYLES);
	}
	
	private static void enableSelectElement(HTMLSelectElement selectElement) {
		selectElement.setDisabled(false);
		
		setElementStyles(selectElement, ENABLED_CONTROL_ELEMENT_STYLES);
	}
	
	private static void disableButtonElement(HTMLButtonElement buttonElement) {
		buttonElement.setDisabled(true);
		
		setElementStyles(buttonElement, DISABLED_CONTROL_ELEMENT_STYLES);
	}
	
	private static void disableSelectElement(HTMLSelectElement selectElement) {
		selectElement.setDisabled(true);
		
		setElementStyles(selectElement, DISABLED_CONTROL_ELEMENT_STYLES);
	}
	
	private static HTMLOptionElement createOptionElement(String optionLabel) {
		HTMLOptionElement optionElement = (HTMLOptionElement) DOCUMENT.createElement(OPTION_ELEMENT_TYPE);
		
		optionElement.setLabel(optionLabel);
		
		setElementStyles(optionElement, OPTION_ELEMENT_STYLES);
		
		return optionElement;
	}
	
	private static HTMLElement createControlElement(String controlElementType) {
		HTMLElement controlElement = DOCUMENT.createElement(controlElementType);
		
		setElementStyles(controlElement, CONTROL_ELEMENT_STYLES);
		
		return controlElement;
	}
	
	private static HTMLElement createImageElement(String imageSourcePath, String imageAlternativeText, int imageSize) {
		HTMLImageElement imageElement = (HTMLImageElement) DOCUMENT.createElement(IMAGE_ELEMENT_TYPE);
		
		imageElement.setSrc(imageSourcePath);
		imageElement.setAlt(imageAlternativeText);
		
		imageElement.setWidth(imageSize);
		imageElement.setHeight(imageSize);
		
		return imageElement;
	}
	
	private static HTMLElement createSpanElement(int spanWidth, int spanHeight) {
		HTMLElement spanElement = DOCUMENT.createElement(SPAN_ELEMENT_TYPE);
		
		if(spanWidth != 0) {
			
			String s = ELEMENT_WIDTH_STYLE_VALUE_FORMAT.formatted(spanWidth);
			setElementStyles(spanElement, ELEMENT_WIDTH_STYLE_KEY, s);
		}
		
		if(spanHeight != 0) {
			
			String s = ELEMENT_HEIGHT_STYLE_VALUE_FORMAT.formatted(spanHeight);
			setElementStyles(spanElement, ELEMENT_HEIGHT_STYLE_KEY, s);
		}
		
		return spanElement;
	}
	
	private static HTMLElement createAnchorElement(String anchorTargetPath, HTMLElement anchorChildElement) {
		HTMLAnchorElement anchorElement = (HTMLAnchorElement) DOCUMENT.createElement(ANCHOR_ELEMENT_TYPE);
		
		anchorElement.setHref(anchorTargetPath);
		anchorElement.appendChild(anchorChildElement);
		
		return anchorElement;
	}
	
	private static HTMLElement createFlexboxElement(String flexboxDirection, int flexboxGap) {
		HTMLElement flexboxElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		
		String s = FLEXBOX_ELEMENT_GAP_STYLE_VALUE_FORMAT.formatted(flexboxGap);
		
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_STYLES);
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_DIRECTION_STYLE_KEY, flexboxDirection, FLEXBOX_ELEMENT_GAP_STYLE_KEY, s);
		
		return flexboxElement;
	}
	
	private static void setElementStyles(HTMLElement element, String... styles) {
		CSSStyleDeclaration elementStyle = element.getStyle();
		
		int n = styles.length >>> 1;
		for(int i = 0; i < n; i++) {
			
			int j = i << 1;
			int k = j + 1;
			
			String s1 = styles[j];
			String s2 = styles[k];
			
			elementStyle.setProperty(s1, s2);
		}
	}
	
	private static String formatMoveScore(int moveScore) {
		if(moveScore == Integer.MIN_VALUE) return EMPTY_CELL_LABEL_ELEMENT_TEXT;
		if(moveScore > 0) return POSITIVE_MOVE_SCORE_FORMAT_PREFIX + moveScore;
		
		return String.valueOf(moveScore);
	}
	
	private static String formatSkillLevelName(String skillLevelName) {
		char c = skillLevelName.charAt(0);
		
		skillLevelName = skillLevelName.substring(1);
		skillLevelName = skillLevelName.toLowerCase();
		
		return c + skillLevelName;
	}
	
}
