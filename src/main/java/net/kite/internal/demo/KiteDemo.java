package net.kite.internal.demo;

import net.kite.api.Kite;
import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.internal.board.score.cache.opening.OpeningBoardScoreCaches;
import net.kite.api.skill.level.SkillLevel;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.css.ElementCSSInlineStyle;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.svg.SVGElement;
import org.teavm.jso.dom.xml.Element;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import java.util.concurrent.ThreadLocalRandom;

public final class KiteDemo {
	
	private static final String APP_TITLE = "Kite - Connect four solver";
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	private static final int BOARD_SIZE = 42;
	
	private static final int MAXIMAL_BOARD_Y = 5;
	
	private static final long AI_MOVE_TIME_DELAY = 1000;
	
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
	
	private static final int OPENING_SCORE_CACHE_SIZE_IN_BYTES = 33554518;
	private static final int OPENING_SCORE_CACHE_HALF_SIZE_IN_BYTES = 16777259;
	private static final float OPENING_SCORE_CACHE_SIZE_IN_MEGABYTES = 33.6f;
	private static final int MAXIMAL_LOADING_PROGRESS = 100;
	
	private static final float MEGABYTE_IN_BYTES = 1000000.0f;
	private static final float MEGABYTE_FORMAT_PRECISION = 10.0f;
	
	private static final String DEFAULT_ELEMENT_TYPE = "div";
	private static final String ANCHOR_ELEMENT_TYPE = "a";
	private static final String SPAN_ELEMENT_TYPE = "span";
	private static final String IMAGE_ELEMENT_TYPE = "img";
	private static final String SVG_ELEMENT_TYPE = "svg";
	private static final String LINE_ELEMENT_TYPE = "line";
	private static final String BUTTON_ELEMENT_TYPE = "button";
	private static final String SELECT_ELEMENT_TYPE = "select";
	private static final String OPTION_ELEMENT_TYPE = "option";
	
	private static final String LOADING_MESSAGE_ELEMENT_ID = "loading-message";
	
	private static final String SVG_ELEMENT_NAMESPACE = "http://www.w3.org/2000/svg";
	
	private static final String KEY_DOWN_EVENT_TYPE = "keydown";
	private static final String ELEMENT_CHANGE_EVENT_TYPE = "change";
	
	private static final String ENTER_KEY_NAME = "Enter";
	private static final String BACKSPACE_KEY_NAME = "Backspace";
	private static final String ARROW_UP_KEY_NAME = "ArrowUp";
	private static final String ARROW_DOWN_KEY_NAME = "ArrowDown";
	private static final String ARROW_LEFT_KEY_NAME = "ArrowLeft";
	private static final String ARROW_RIGHT_KEY_NAME = "ArrowRight";
	
	private static final String ELEMENT_HEIGHT_STYLE_KEY = "height";
	private static final String ELEMENT_HEIGHT_STYLE_VALUE_FORMAT = "%spx";
	private static final String ELEMENT_COLOR_STYLE_KEY = "color";
	private static final String ELEMENT_BACKGROUND_COLOR_STYLE_KEY = "background-color";
	
	private static final String[] FLEXBOX_ELEMENT_STYLES = new String[] {
			"display", "flex",
			"justify-content", "center",
			"align-items", "center"
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
	
	private static final String[] LOGO_AND_VERSION_ELEMENT_STYLES = new String[] {
			"margin", "6px"
	};
	
	private static final String[] GITHUB_LOGO_ELEMENT_STYLES = new String[] {
			"padding", "6px"
	};
	
	private static final String[] GITHUB_LOGO_CONTAINER_ELEMENT_STYLES = new String[] {
			"width", "92px",
			"height", "92px"
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
			"margin-top", "calc(min(80dvw / 436 * 3, 3px))",
			"margin-bottom", "calc(min(80dvw / 436 * 3, 3px))",
			"background-color", "#09090B",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_MARKER_ELEMENT_STYLES = new String[] {
			"width", "16%",
			"aspect-ratio", "1",
			"background-color", "#09090B",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_COLUMN_ELEMENT_STYLES = new String[] {
			"width", "calc(min(80dvw / 436 * 50, 50px))",
			"padding-left", "calc(min(80dvw / 436 * 3, 3px))",
			"padding-right", "calc(min(80dvw / 436 * 3, 3px))",
			"cursor", "pointer"
	};
	
	private static final String[] CELL_LABEL_ELEMENT_STYLES = new String[] {
			"width", "calc(min(80dvw / 436 * 56, 56px))",
			"text-align", "center",
			"font-weight", "bold"
	};
	
	private static final String[] CELL_LABELS_ELEMENT_STYLES = new String[] {
			"margin-bottom", "8px"
	};
	
	private static final String[] BOARD_ELEMENT_STYLES = new String[] {
			"width", "80dvw",
			"max-width", "436px",
			"aspect-ratio", "109 / 95",
			"position", "relative",
			"background-color", "#27272A",
			"border-radius", "calc(min(80dvw / 436 * 25, 25px))"
	};
	
	private static final String[] BOARD_LINES_ELEMENT_STYLES = new String[] {
			"position", "absolute",
			"top", "0",
			"bottom", "0",
			"left", "0",
			"right", "0",
			"pointer-events", "none"
	};
	
	private static final String[] BOARD_LINE_ELEMENT_STYLES = new String[] {
			"position", "absolute",
			"top", "0",
			"bottom", "0",
			"left", "0",
			"right", "0"
	};
	
	private static final String APP_ELEMENT_CLASS_NAME = "app";
	private static final String SIDEBAR_ELEMENT_CLASS_NAME = "sidebar";
	private static final String CONTROLS_ELEMENT_CLASS_NAME = "controls";
	private static final String LOGO_IMAGE_ELEMENT_CLASS_NAME = "logo-image";
	private static final String LOGO_AND_VERSION_ELEMENT_CLASS_NAME = "logo-and-version";
	private static final String TOP_GITHUB_LOGO_ELEMENT_CLASS_NAME = "top-github-logo";
	private static final String BOTTOM_GITHUB_LOGO_ELEMENT_CLASS_NAME = "bottom-github-logo";
	
	private static final int BRAND_ELEMENT_GAP = 38;
	private static final int BOARD_AND_LABELS_ELEMENT_GAP = 30;
	
	private static final String LOGO_ELEMENT_SOURCE_PATH = "https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/brand/logo.svg";
	private static final String LOGO_ELEMENT_ALTERNATIVE_TEXT = "The Kite logo";
	private static final int LOGO_ELEMENT_SIZE = 120;
	
	private static final String GITHUB_LOGO_ELEMENT_SOURCE_PATH = "https://raw.githubusercontent.com/tristan852/kite/refs/heads/main/assets/images/socials/github.svg";
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
	
	private static final String[] CELL_MARKER_ELEMENT_BACKGROUND_COLORS = new String[] {
			"#FFA2A2",
			"#FFDF20",
			"#09090B"
	};
	
	private static final int RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 0;
	private static final int YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 1;
	private static final int EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX = 2;
	
	private static final int BOARD_ELEMENT_GRID_SIZE = 56;
	private static final int BOARD_ELEMENT_GRID_OFFSET = 50;
	
	private static final int CELL_LABEL_ELEMENT_HEIGHT = 20;
	private static final String EMPTY_CELL_LABEL_ELEMENT_TEXT = "";
	
	private static final String X1_ATTRIBUTE_NAME = "x1";
	private static final String X2_ATTRIBUTE_NAME = "x2";
	private static final String Y1_ATTRIBUTE_NAME = "y1";
	private static final String Y2_ATTRIBUTE_NAME = "y2";
	
	private static final String STROKE_ATTRIBUTE_NAME = "stroke";
	private static final String STROKE_WIDTH_ATTRIBUTE_NAME = "stroke-width";
	private static final String STROKE_LINE_CAPE_ATTRIBUTE_NAME = "stroke-linecap";
	
	private static final String DEFAULT_STROKE_ATTRIBUTE_VALUE = "#F4F4F5";
	private static final String DEFAULT_STROKE_WIDTH_ATTRIBUTE_VALUE = "8";
	private static final String DEFAULT_STROKE_LINE_CAPE_ATTRIBUTE_VALUE = "square";
	
	private static final String XMLNS_ATTRIBUTE_NAME = "xmlns";
	private static final String VIEW_BOX_ATTRIBUTE_NAME = "viewBox";
	
	private static final String DEFAULT_XMLNS_ATTRIBUTE_VALUE = "http://www.w3.org/2000/svg";
	private static final String DEFAULT_VIEW_BOX_ATTRIBUTE_VALUE = "0 0 436 380";
	
	private static final String REQUEST_METHOD = "GET";
	private static final String REQUEST_RESPONSE_TYPE = "arraybuffer";
	private static final String REQUEST_URL = "kite_resources/board_score_caches/opening.cfc";
	
	private static final int SUCCESSFUL_REQUEST_STATUS = 200;
	
	private static final Window WINDOW = Window.current();
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	private boolean aiModeSelected;
	private SkillLevel aiSkillLevel;
	
	private boolean aiPlaysRed;
	private boolean redAtTurn;
	
	private final int[] movesScores = new int[BOARD_WIDTH];
	
	private HTMLElement loadingMessageElement;
	
	private HTMLButtonElement modeButtonElement;
	private HTMLButtonElement undoButtonElement;
	private HTMLButtonElement redoButtonElement;
	
	private HTMLSelectElement aiSkillLevelSelectElement;
	
	private HTMLElement boardLinesElement;
	
	private final HTMLElement[][] cellElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellMarkerElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	
	private final HTMLElement[] cellLabelElements = new HTMLElement[BOARD_WIDTH];
	
	private HTMLElement winnerLabelElement;
	
	private String currentLoadingMessage;
	
	private Kite solver;
	
	private int eventID;
	
	public KiteDemo() {
		this.redAtTurn = true;
	}
	
	public void onStart() {
		loadingMessageElement = DOCUMENT.getElementById(LOADING_MESSAGE_ELEMENT_ID);
		
		XMLHttpRequest request = new XMLHttpRequest();
		
		request.open(REQUEST_METHOD, REQUEST_URL);
		request.setResponseType(REQUEST_RESPONSE_TYPE);
		
		request.onLoad((progressEvent) -> {
			
			int requestStatus = request.getStatus();
			if(requestStatus == SUCCESSFUL_REQUEST_STATUS) {
				
				updateLoadProgress(MAXIMAL_LOADING_PROGRESS, OPENING_SCORE_CACHE_SIZE_IN_BYTES);
				
				ArrayBuffer arrayBuffer = (ArrayBuffer) request.getResponse();
				Int8Array array = new Int8Array(arrayBuffer);
				
				byte[] bytes = array.copyToJavaArray();
				OpeningBoardScoreCaches.ensureDefaultIsLoaded(bytes);
				
				buildApp();
				
			} else {
				
				onLoadError();
			}
		});
		
		request.onProgress((progressEvent) -> {
			
			int loadedBytes = progressEvent.getLoaded();
			computeAndUpdateLoadProgress(loadedBytes);
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
		updateLoadingMessage("Error while loading Kite solver!");
	}
	
	private void computeAndUpdateLoadProgress(int loadedBytes) {
		if(loadedBytes < 0) loadedBytes = 0;
		else if(loadedBytes > OPENING_SCORE_CACHE_SIZE_IN_BYTES) loadedBytes = OPENING_SCORE_CACHE_SIZE_IN_BYTES;
		
		int progress = (int) (((long) loadedBytes * MAXIMAL_LOADING_PROGRESS + OPENING_SCORE_CACHE_HALF_SIZE_IN_BYTES) / OPENING_SCORE_CACHE_SIZE_IN_BYTES);
		updateLoadProgress(progress, loadedBytes);
	}
	
	private void updateLoadProgress(int progress, int loadedBytes) {
		float loadedMB = loadedBytes / MEGABYTE_IN_BYTES;
		loadedMB = Math.round(loadedMB * MEGABYTE_FORMAT_PRECISION) / MEGABYTE_FORMAT_PRECISION;
		
		String message = "Loading Kite solver... " + progress + "% (" + loadedMB + " MB / " + OPENING_SCORE_CACHE_SIZE_IN_MEGABYTES + " MB)";
		
		updateLoadingMessage(message);
	}
	
	private void updateLoadingMessage(String message) {
		if(message.equals(currentLoadingMessage)) return;
		
		currentLoadingMessage = message;
		loadingMessageElement.setInnerText(message);
	}
	
	private void buildApp() {
		solver = Kite.createInstance();
		
		HTMLBodyElement bodyElement = DOCUMENT.getBody();
		
		while(true) {
			
			Node node = bodyElement.getFirstChild();
			if(node == null) break;
			
			bodyElement.removeChild(node);
		}
		
		HTMLElement appElement = createFlexboxElementWithClass(APP_ELEMENT_CLASS_NAME);
		HTMLElement sidebarElement = createFlexboxElementWithClass(SIDEBAR_ELEMENT_CLASS_NAME);
		HTMLElement controlsElement = createFlexboxElementWithClass(CONTROLS_ELEMENT_CLASS_NAME);
		HTMLElement brandElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, BRAND_ELEMENT_GAP);
		HTMLElement logoAndVersionElement = createFlexboxElementWithClass(LOGO_AND_VERSION_ELEMENT_CLASS_NAME);
		
		setElementStyles(logoAndVersionElement, LOGO_AND_VERSION_ELEMENT_STYLES);
		
		HTMLElement logoImageElement = createImageElement(LOGO_ELEMENT_SOURCE_PATH, LOGO_ELEMENT_ALTERNATIVE_TEXT, LOGO_ELEMENT_SIZE);
		HTMLElement versionElement = createSpanElement(0);
		
		logoImageElement.setClassName(LOGO_IMAGE_ELEMENT_CLASS_NAME);
		
		String version = Kite.getVersion();
		String versionElementText = VERSION_ELEMENT_TEXT_FORMAT.formatted(version);
		
		versionElement.setTextContent(versionElementText);
		
		setElementStyles(versionElement, VERSION_ELEMENT_STYLES);
		
		logoAndVersionElement.appendChild(logoImageElement);
		logoAndVersionElement.appendChild(versionElement);
		
		HTMLElement githubLogoElement = createImageElement(GITHUB_LOGO_ELEMENT_SOURCE_PATH, GITHUB_LOGO_ELEMENT_ALTERNATIVE_TEXT, GITHUB_LOGO_ELEMENT_SIZE);
		setElementStyles(githubLogoElement, GITHUB_LOGO_ELEMENT_STYLES);
		
		githubLogoElement = createAnchorElement(GITHUB_LOGO_ELEMENT_TARGET_PATH, githubLogoElement);
		
		HTMLElement githubLogoContainerElement = createColumnFlexboxElement();
		
		setElementStyles(githubLogoContainerElement, GITHUB_LOGO_CONTAINER_ELEMENT_STYLES);
		
		githubLogoContainerElement.appendChild(githubLogoElement);
		
		HTMLElement githubLogoContainerContainerElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		githubLogoContainerContainerElement.setClassName(TOP_GITHUB_LOGO_ELEMENT_CLASS_NAME);
		
		githubLogoContainerContainerElement.appendChild(githubLogoContainerElement);
		
		brandElement.appendChild(logoAndVersionElement);
		brandElement.appendChild(githubLogoContainerContainerElement);
		
		modeButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		undoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		redoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		HTMLButtonElement newGameButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		aiSkillLevelSelectElement = (HTMLSelectElement) createControlElement(SELECT_ELEMENT_TYPE);
		
		modeButtonElement.setTextContent(FIRST_MODE_BUTTON_ELEMENT_TEXT);
		undoButtonElement.setTextContent(UNDO_BUTTON_ELEMENT_TEXT);
		redoButtonElement.setTextContent(REDO_BUTTON_ELEMENT_TEXT);
		
		newGameButtonElement.setTextContent(NEW_GAME_BUTTON_ELEMENT_TEXT);
		
		for(SkillLevel skillLevel : ORDERED_AI_SKILL_LEVELS) {
			
			String skillLevelDisplayName = skillLevel.getDisplayName();
			
			HTMLOptionElement optionElement = createOptionElement(skillLevelDisplayName);
			aiSkillLevelSelectElement.appendChild(optionElement);
		}
		
		setElementStyles(aiSkillLevelSelectElement, AI_SKILL_LEVEL_SELECT_ELEMENT_STYLES);
		
		modeButtonElement.onClick((mouseEvent) -> {
			
			eventID++;
			
			changeMode();
		});
		
		newGameButtonElement.onClick((mouseEvent) -> {
			
			eventID++;
			
			setupNewGame();
		});
		
		undoButtonElement.onClick((mouseEvent) -> {
			
			eventID++;
			
			undoMove();
		});
		
		redoButtonElement.onClick((mouseEvent) -> {
			
			eventID++;
			
			redoMove();
		});
		
		aiSkillLevelSelectElement.addEventListener(ELEMENT_CHANGE_EVENT_TYPE, (event) -> {
			
			int i = aiSkillLevelSelectElement.getSelectedIndex();
			changeAISkillLevel(i);
		});
		
		WINDOW.addEventListener(KEY_DOWN_EVENT_TYPE, (event) -> {
			
			KeyboardEvent keyboardEvent = (KeyboardEvent) event;
			if(keyboardEvent.isCtrlKey()) return;
			if(keyboardEvent.isShiftKey()) return;
			if(keyboardEvent.isAltKey()) return;
			if(keyboardEvent.isMetaKey()) return;
			if(keyboardEvent.isComposing()) return;
			
			switch(keyboardEvent.getKey()) {
				case ENTER_KEY_NAME -> {
					
					if(keyboardEvent.isRepeat()) return;
					
					if(!modeButtonElement.isDisabled()) modeButtonElement.click();
					keyboardEvent.preventDefault();
				}
				case BACKSPACE_KEY_NAME -> {
					
					if(keyboardEvent.isRepeat()) return;
					
					if(!newGameButtonElement.isDisabled()) newGameButtonElement.click();
					keyboardEvent.preventDefault();
				}
				case ARROW_UP_KEY_NAME -> {
					
					if(!aiSkillLevelSelectElement.isDisabled()) {
						
						System.out.println("down");
						int i = aiSkillLevelSelectElement.getSelectedIndex();
						System.out.println(i);
						if(i > 0) {
							
							i--;
							
							aiSkillLevelSelectElement.setSelectedIndex(i);
							changeAISkillLevel(i);
						}
					}
					
					keyboardEvent.preventDefault();
				}
				case ARROW_DOWN_KEY_NAME -> {
					
					if(!aiSkillLevelSelectElement.isDisabled()) {
						
						int i = aiSkillLevelSelectElement.getSelectedIndex();
						int n = ORDERED_AI_SKILL_LEVELS.length;
						System.out.println("up");
						System.out.println(i);
						System.out.println(n);
						
						i++;
						if(i < n) {
							
							System.out.println((i - 1) + " -> " + (i));
							
							aiSkillLevelSelectElement.setSelectedIndex(i);
							changeAISkillLevel(i);
						}
					}
					
					keyboardEvent.preventDefault();
				}
				case ARROW_LEFT_KEY_NAME -> {
					
					if(!undoButtonElement.isDisabled()) undoButtonElement.click();
					keyboardEvent.preventDefault();
				}
				case ARROW_RIGHT_KEY_NAME -> {
					
					if(!redoButtonElement.isDisabled()) redoButtonElement.click();
					keyboardEvent.preventDefault();
				}
			}
		});
		
		controlsElement.appendChild(modeButtonElement);
		controlsElement.appendChild(aiSkillLevelSelectElement);
		controlsElement.appendChild(newGameButtonElement);
		controlsElement.appendChild(undoButtonElement);
		controlsElement.appendChild(redoButtonElement);
		
		HTMLElement githubLogoElement2 = createImageElement(GITHUB_LOGO_ELEMENT_SOURCE_PATH, GITHUB_LOGO_ELEMENT_ALTERNATIVE_TEXT, GITHUB_LOGO_ELEMENT_SIZE);
		setElementStyles(githubLogoElement2, GITHUB_LOGO_ELEMENT_STYLES);
		
		githubLogoElement2 = createAnchorElement(GITHUB_LOGO_ELEMENT_TARGET_PATH, githubLogoElement2);
		githubLogoElement2.setClassName(BOTTOM_GITHUB_LOGO_ELEMENT_CLASS_NAME);
		
		sidebarElement.appendChild(brandElement);
		sidebarElement.appendChild(controlsElement);
		sidebarElement.appendChild(githubLogoElement2);
		
		sidebarElement.setClassName(SIDEBAR_ELEMENT_CLASS_NAME);
		
		HTMLElement boardElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, 0);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			HTMLElement cellColumnElement = createColumnFlexboxElement();
			
			int maxY = BOARD_HEIGHT - 1;
			for(int y = maxY; y >= 0; y--) {
				
				HTMLElement cellElement = createColumnFlexboxElement();
				setElementStyles(cellElement, CELL_ELEMENT_STYLES);
				
				HTMLElement cellMarkerElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellMarkerElement, CELL_MARKER_ELEMENT_STYLES);
				
				cellElement.appendChild(cellMarkerElement);
				
				cellElements[x][y] = cellElement;
				cellMarkerElements[x][y] = cellMarkerElement;
				
				cellColumnElement.appendChild(cellElement);
			}
			
			int moveX = x;
			
			cellColumnElement.onClick((mouseEvent) -> {
				
				if(aiModeSelected && aiPlaysRed == redAtTurn) return;
				
				playMove(moveX, false);
			});
			
			setElementStyles(cellColumnElement, CELL_COLUMN_ELEMENT_STYLES);
			
			boardElement.appendChild(cellColumnElement);
		}
		
		boardLinesElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		setElementStyles(boardLinesElement, BOARD_LINES_ELEMENT_STYLES);
		
		boardElement.appendChild(boardLinesElement);
		
		setElementStyles(boardElement, BOARD_ELEMENT_STYLES);
		
		HTMLElement cellLabelsElement = createFlexboxElement(FLEXBOX_ELEMENT_ROW_DIRECTION, 0);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			HTMLElement cellLabelElement = createSpanElement(CELL_LABEL_ELEMENT_HEIGHT);
			setElementStyles(cellLabelElement, CELL_LABEL_ELEMENT_STYLES);
			
			cellLabelElements[x] = cellLabelElement;
			
			cellLabelsElement.appendChild(cellLabelElement);
		}
		
		setElementStyles(cellLabelsElement, CELL_LABELS_ELEMENT_STYLES);
		
		winnerLabelElement = createSpanElement(WINNER_LABEL_ELEMENT_HEIGHT);
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
							playMove(moveX, true);
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
				if(gameNotOver) {
					
					boolean boardEmpty = solver.boardEmpty();
					if(boardEmpty) playAIMove();
					else scheduleAIMove();
				}
			}
			
		} else {
			
			disableSelectElement(aiSkillLevelSelectElement);
			updateCellLabelElements();
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
		int playedMoveAmount = solver.playedMoveAmount();
		if(playedMoveAmount != 0) {
			
			for(int i = 0; i < playedMoveAmount; i++) {
				
				int moveX = solver.playedMove(i) - 1;
				int moveY = solver.playedMoveRow(i) - 1;
				
				setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX, false);
			}
			
			solver.clearBoard();
			
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
		
		hideWinLines();
	}
	
	private void undoMove() {
		if(solver.boardEmpty()) return;
		
		redAtTurn = !redAtTurn;
		
		int moveX = solver.lastMove() - 1;
		int moveY = solver.lastMoveRow() - 1;
		
		solver.undoMove();
		
		setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX, false);
		
		if(!solver.boardEmpty()) {
			
			moveX = solver.lastMove() - 1;
			moveY = solver.lastMoveRow() - 1;
			
			int i = redAtTurn ? YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
			
			setCellElementBackgroundColor(moveX, moveY, i, true);
		}
		
		hideWinLines();
		
		if(!aiModeSelected) updateCellLabelElements();
		updateWinnerLabelElement();
		updateLocationSearch();
	}
	
	private void redoMove() {
		if(!solver.canRedoMove()) return;
		
		int playedMoveAmount = solver.playedMoveAmount();
		int moveX = solver.playedMove(playedMoveAmount) - 1;
		playMove(moveX, false);
	}
	
	private void scheduleAIMove() {
		int id = eventID;
		
		TimerHandler timerHandler = () -> {
			
			if(eventID != id) return;
			
			playAIMove();
		};
		
		Window.setTimeout(timerHandler, AI_MOVE_TIME_DELAY);
	}
	
	private void playAIMove() {
		int moveX = solver.skilledMove(aiSkillLevel);
		moveX--;
		
		playMove(moveX, false);
	}
	
	private void playMove(int moveX, boolean initial) {
		if(solver.gameOver()) return;
		
		int moveY = solver.cellColumnHeight(moveX + 1);
		if(moveY == BOARD_HEIGHT) return;
		
		eventID++;
		
		int i = redAtTurn ? RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
		
		if(!solver.boardEmpty()) {
			
			int lastMoveX = solver.lastMove() - 1;
			int lastMoveY = solver.lastMoveRow() - 1;
			
			int j = redAtTurn ? YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
			
			setCellElementBackgroundColor(lastMoveX, lastMoveY, j, false);
		}
		
		redAtTurn = !redAtTurn;
		
		solver.playMove(moveX + 1);
		setCellElementBackgroundColor(moveX, moveY, i, true);
		
		BoardOutcome outcome = solver.gameOutcome();
		if(outcome.isWin()) showWinLines();
		
		if(aiModeSelected && !initial) {
			
			if(aiPlaysRed == redAtTurn && outcome == BoardOutcome.UNDECIDED) {
				
				scheduleAIMove();
				return;
			}
		}
		
		updateWinnerLabelElement();
		if(!initial) {
			
			updateLocationSearch();
			if(!aiModeSelected) updateCellLabelElements();
		}
	}
	
	private void showWinLines() {
		BoardLine[] lines = solver.winLines();
		for(BoardLine line : lines) {
			
			int x1 = line.getStartCellX();
			int y1 = line.getStartCellY();
			
			int x2 = line.getEndCellX();
			int y2 = line.getEndCellY();
			
			y1 = MAXIMAL_BOARD_Y - y1;
			y2 = MAXIMAL_BOARD_Y - y2;
			
			x1 *= BOARD_ELEMENT_GRID_SIZE;
			y1 *= BOARD_ELEMENT_GRID_SIZE;
			
			x2 *= BOARD_ELEMENT_GRID_SIZE;
			y2 *= BOARD_ELEMENT_GRID_SIZE;
			
			x1 += BOARD_ELEMENT_GRID_OFFSET;
			y1 += BOARD_ELEMENT_GRID_OFFSET;
			
			x2 += BOARD_ELEMENT_GRID_OFFSET;
			y2 += BOARD_ELEMENT_GRID_OFFSET;
			
			SVGElement lineElement = (SVGElement) DOCUMENT.createElementNS(SVG_ELEMENT_NAMESPACE, SVG_ELEMENT_TYPE);
			Element lineElementLine = DOCUMENT.createElementNS(SVG_ELEMENT_NAMESPACE, LINE_ELEMENT_TYPE);
			
			setElementStyles(lineElement, BOARD_LINE_ELEMENT_STYLES);
			
			lineElement.setAttribute(XMLNS_ATTRIBUTE_NAME, DEFAULT_XMLNS_ATTRIBUTE_VALUE);
			lineElement.setAttribute(VIEW_BOX_ATTRIBUTE_NAME, DEFAULT_VIEW_BOX_ATTRIBUTE_VALUE);
			
			String s1 = String.valueOf(x1);
			String s2 = String.valueOf(y1);
			String s3 = String.valueOf(x2);
			String s4 = String.valueOf(y2);
			
			lineElementLine.setAttribute(X1_ATTRIBUTE_NAME, s1);
			lineElementLine.setAttribute(Y1_ATTRIBUTE_NAME, s2);
			lineElementLine.setAttribute(X2_ATTRIBUTE_NAME, s3);
			lineElementLine.setAttribute(Y2_ATTRIBUTE_NAME, s4);
			
			lineElementLine.setAttribute(STROKE_ATTRIBUTE_NAME, DEFAULT_STROKE_ATTRIBUTE_VALUE);
			lineElementLine.setAttribute(STROKE_WIDTH_ATTRIBUTE_NAME, DEFAULT_STROKE_WIDTH_ATTRIBUTE_VALUE);
			lineElementLine.setAttribute(STROKE_LINE_CAPE_ATTRIBUTE_NAME, DEFAULT_STROKE_LINE_CAPE_ATTRIBUTE_VALUE);
			
			lineElement.appendChild(lineElementLine);
			
			boardLinesElement.appendChild(lineElement);
		}
		
		int moveX = solver.lastMove() - 1;
		int moveY = solver.lastMoveRow() - 1;
		
		int i = redAtTurn ? YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
		
		setCellElementBackgroundColor(moveX, moveY, i, false);
	}
	
	private void hideWinLines() {
		boardLinesElement.clear();
	}
	
	private void setCellElementBackgroundColor(int cellElementX, int cellElementY, int cellElementBackgroundColorIndex, boolean marked) {
		String s = CELL_ELEMENT_BACKGROUND_COLORS[cellElementBackgroundColorIndex];
		
		HTMLElement cellElement = cellElements[cellElementX][cellElementY];
		setElementStyles(cellElement, ELEMENT_BACKGROUND_COLOR_STYLE_KEY, s);
		
		if(marked) s = CELL_MARKER_ELEMENT_BACKGROUND_COLORS[cellElementBackgroundColorIndex];
		
		cellElement = cellMarkerElements[cellElementX][cellElementY];
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
		
		int playedMoveAmount = solver.playedMoveAmount();
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
					
					int playedMove = solver.playedMove(i) - 1;
					char c = (char) (SMALLEST_LOCATION_SEARCH_MOVE + playedMove);
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
	
	private static HTMLElement createSpanElement(int spanHeight) {
		HTMLElement spanElement = DOCUMENT.createElement(SPAN_ELEMENT_TYPE);
		
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
	
	private static HTMLElement createFlexboxElementWithClass(String flexboxClassName) {
		HTMLElement flexboxElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		
		flexboxElement.setClassName(flexboxClassName);
		
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_STYLES);
		
		return flexboxElement;
	}
	
	private static HTMLElement createColumnFlexboxElement() {
		HTMLElement flexboxElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_STYLES);
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_DIRECTION_STYLE_KEY, FLEXBOX_ELEMENT_COLUMN_DIRECTION);
		
		return flexboxElement;
	}
	
	private static HTMLElement createFlexboxElement(String flexboxDirection, int flexboxGap) {
		HTMLElement flexboxElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		
		String s = FLEXBOX_ELEMENT_GAP_STYLE_VALUE_FORMAT.formatted(flexboxGap);
		
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_STYLES);
		setElementStyles(flexboxElement, FLEXBOX_ELEMENT_DIRECTION_STYLE_KEY, flexboxDirection, FLEXBOX_ELEMENT_GAP_STYLE_KEY, s);
		
		return flexboxElement;
	}
	
	private static void setElementStyles(ElementCSSInlineStyle element, String... styles) {
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
	
}
