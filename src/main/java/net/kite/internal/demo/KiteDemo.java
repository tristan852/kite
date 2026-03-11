package net.kite.internal.demo;

import net.kite.api.Kite;
import net.kite.api.board.analysis.move.MoveAnalysis;
import net.kite.api.board.line.BoardLine;
import net.kite.api.board.outcome.BoardOutcome;
import net.kite.api.skill.level.SkillLevel;
import net.kite.internal.board.score.cache.opening.OpeningBoardScoreCaches;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.*;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.css.ElementCSSInlineStyle;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.svg.SVGElement;
import org.teavm.jso.dom.xml.Element;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class KiteDemo {
	
	private static final String APP_TITLE = "Kite - Connect four solver";
	
	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 6;
	
	private static final int MAXIMAL_BOARD_Y = 5;
	
	private static final long AI_MOVE_TIME_DELAY = 1000;
	
	private static final String LOCATION_SEARCH_PREFIX = "?";
	private static final String LOCATION_SEARCH_ITEM_SEPARATOR = "&";
	private static final char LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR = '=';
	private static final String LOCATION_SEARCH_MOVES_KEY = "moves";
	private static final String LOCATION_SEARCH_UNDONE_MOVES_KEY = "undone-moves";
	private static final String LOCATION_SEARCH_AI_COLOR_KEY = "ai-color";
	private static final String LOCATION_SEARCH_AI_LEVEL_KEY = "ai-level";
	private static final String LOCATION_SEARCH_MULTIPLAYER_KEY = "multiplayer";
	
	private static final char SMALLEST_LOCATION_SEARCH_MOVE = '1';
	private static final String RED_LOCATION_SEARCH_AI_COLOR = "red";
	private static final String YELLOW_LOCATION_SEARCH_AI_COLOR = "yellow";
	
	private static final String AUDIO_SETTING_LOCAL_STORAGE_ITEM_NAME = "volume";
	
	private static final float[] VOLUME_LEVELS = new float[] {
			1.0f, 0.0f, 0.3f
	};
	
	private static final SkillLevel[] ORDERED_AI_SKILL_LEVELS = SkillLevel.values();
	
	private static final String POSITIVE_MOVE_SCORE_FORMAT_PREFIX = "+";
	
	private static final int OPENING_SCORE_CACHE_SIZE_IN_BYTES = 33554518;
	private static final float OPENING_SCORE_CACHE_SIZE_IN_MEGABYTES = 33.6f;
	private static final float MAXIMAL_LOADING_PROGRESS = 100.0f;
	
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
	private static final String AUDIO_ELEMENT_TYPE = "audio";
	
	private static final String LOADING_MESSAGE_ELEMENT_ID = "loading-message";
	private static final String LOADING_BAR_ELEMENT_ID = "loading-bar";
	private static final String LOADING_BAR_PROGRESS_ELEMENT_ID = "loading-bar-progress";
	
	private static final String SVG_ELEMENT_NAMESPACE = "http://www.w3.org/2000/svg";
	
	private static final String KEY_DOWN_EVENT_TYPE = "keydown";
	private static final String ELEMENT_CHANGE_EVENT_TYPE = "change";
	
	private static final String ENTER_KEY_NAME = "Enter";
	private static final String BACKSPACE_KEY_NAME = "Backspace";
	
	private static final String ARROW_UP_KEY_NAME = "ArrowUp";
	private static final String ARROW_DOWN_KEY_NAME = "ArrowDown";
	private static final String ARROW_LEFT_KEY_NAME = "ArrowLeft";
	private static final String ARROW_RIGHT_KEY_NAME = "ArrowRight";
	
	private static final String ONE_KEY_NAME = "1";
	private static final String TWO_KEY_NAME = "2";
	private static final String THREE_KEY_NAME = "3";
	private static final String FOUR_KEY_NAME = "4";
	private static final String FIVE_KEY_NAME = "5";
	private static final String SIX_KEY_NAME = "6";
	private static final String SEVEN_KEY_NAME = "7";
	
	private static final String NUMPAD_ONE_KEY_NAME = "Numpad1";
	private static final String NUMPAD_TWO_KEY_NAME = "Numpad2";
	private static final String NUMPAD_THREE_KEY_NAME = "Numpad3";
	private static final String NUMPAD_FOUR_KEY_NAME = "Numpad4";
	private static final String NUMPAD_FIVE_KEY_NAME = "Numpad5";
	private static final String NUMPAD_SIX_KEY_NAME = "Numpad6";
	private static final String NUMPAD_SEVEN_KEY_NAME = "Numpad7";
	
	private static final String ELEMENT_HEIGHT_STYLE_KEY = "height";
	private static final String ELEMENT_HEIGHT_STYLE_VALUE_FORMAT = "%spx";
	private static final String ELEMENT_COLOR_STYLE_KEY = "color";
	private static final String ELEMENT_BACKGROUND_COLOR_STYLE_KEY = "background-color";
	private static final String ELEMENT_DISPLAY_STYLE_KEY = "display";
	
	private static final String VISIBLE_ELEMENT_DISPLAY_STYLE_VALUE = "block";
	private static final String INVISIBLE_ELEMENT_DISPLAY_STYLE_VALUE = "none";
	private static final String FLEXBOX_ELEMENT_DISPLAY_STYLE_VALUE = "flex";
	
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
	
	private static final String[] SELECT_ELEMENT_STYLES = new String[] {
			"text-align", "center"
	};
	
	private static final String[] WINNER_LABEL_ELEMENT_STYLES = new String[] {
			"text-align", "center",
			"font-size", "calc(min(80dvw / 436 * 24, 24px))",
			"font-style", "italic",
			"font-weight", "bold"
	};
	
	private static final String[] CELL_ELEMENT_STYLES = new String[] {
			"width", "100%",
			"aspect-ratio", "1",
			"position", "relative",
			"margin-top", "calc(min(80dvw / 436 * 3, 3px))",
			"margin-bottom", "calc(min(80dvw / 436 * 3, 3px))",
			"background-color", "#09090B",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_MARKER_ELEMENT_STYLES = new String[] {
			"width", "16%",
			"aspect-ratio", "1",
			"position", "absolute",
			"top", "50%",
			"left", "50%",
			"transform", "translate(-50%, -50%)",
			"background-color", "#09090B",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_HIGHLIGHT_ELEMENT_STYLES = new String[] {
			"width", "100%",
			"aspect-ratio", "1",
			"position", "absolute",
			"top", "50%",
			"left", "50%",
			"transform", "translate(-50%, -50%)",
			"display", "none",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_HIGHLIGHT_FOREGROUND_ELEMENT_STYLES = new String[] {
			"width", "82%",
			"aspect-ratio", "1",
			"position", "absolute",
			"top", "50%",
			"left", "50%",
			"transform", "translate(-50%, -50%)",
			"display", "none",
			"background-color", "#09090B",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_EVALUATION_ELEMENT_STYLES = new String[] {
			"width", "48%",
			"aspect-ratio", "1",
			"position", "absolute",
			"top", "0%",
			"right", "0%",
			"transform", "translate(20%, -20%)",
			"display", "none",
			"border-radius", "50%"
	};
	
	private static final String[] CELL_EVALUATION_TEXT_ELEMENT_STYLES = new String[] {
			"line-height", "0",
			"color", "#FFFFFF",
			"font-size", "calc(min(80dvw / 436 * 16, 16px))",
			"font-weight", "bold"
	};
	
	private static final String[] CELL_EVALUATION_BACKGROUND_ELEMENT_STYLES = new String[] {
			"width", "48%",
			"aspect-ratio", "1",
			"position", "absolute",
			"top", "0%",
			"right", "0%",
			"transform", "translate(20%, -20%) scale(1.25)",
			"display", "none",
			"background-color", "#27272A",
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
			"font-size", "calc(min(80dvw / 436 * 16, 16px))",
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
	
	private static final String[] AUDIO_SETTING_ELEMENT_STYLES = new String[] {
			"position", "fixed",
			"bottom", "0",
			"right", "0",
			"margin", "2.5dvh 2.5dvw",
			"padding", "6px"
	};
	
	private static final String[] LOADING_BAR_ERROR_ELEMENT_STYLES = new String[] {
			"background-color", "#9F0712"
	};
	
	private static final String[] LOADING_BAR_PROGRESS_ERROR_ELEMENT_STYLES = new String[] {
			"background-color", "#FFA2A2"
	};
	
	private static final String WIDTH_ELEMENT_STYLE_KEY = "width";
	private static final String WIDTH_ELEMENT_STYLE_VALUE_FORMAT = "%.6f%%";
	
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
	
	private static final String GITHUB_LOGO_ELEMENT_SOURCE_PATH = "images/icons/github.svg";
	private static final String GITHUB_LOGO_ELEMENT_ALTERNATIVE_TEXT = "The GitHub logo";
	private static final int GITHUB_LOGO_ELEMENT_SIZE = 50;
	private static final String GITHUB_LOGO_ELEMENT_TARGET_PATH = "https://github.com/tristan852/kite";
	
	private static final String[] AUDIO_SETTING_ELEMENT_SOURCE_PATHS = new String[] {
			"images/icons/volume_full.svg",
			"images/icons/volume_mute.svg",
			"images/icons/volume_low.svg"
	};
	
	private static final String AUDIO_SETTING_ELEMENT_ALTERNATIVE_TEXT = "Volume control";
	private static final int AUDIO_SETTING_ELEMENT_SIZE = 30;
	
	private static final String VERSION_ELEMENT_TEXT_FORMAT = "v%s";
	private static final String FIRST_MODE_BUTTON_ELEMENT_TEXT = "Analysis";
	private static final String SECOND_MODE_BUTTON_ELEMENT_TEXT = "Play vs. AI";
	private static final String THIRD_MODE_BUTTON_ELEMENT_TEXT = "Local Multiplayer";
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
	private static final String DRAW_WINNER_LABEL_ELEMENT_BACKGROUND_COLOR = "#9F9FA9";
	
	private static final String CELL_HIGHLIGHT_BACKGROUND_COLOR = "#7CCF00";
	
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
	
	private static final String RED_STROKE_ATTRIBUTE_VALUE = "#C10007";
	private static final String YELLOW_STROKE_ATTRIBUTE_VALUE = "#A65F00";
	private static final String STRAIGHT_STROKE_WIDTH_ATTRIBUTE_VALUE = "30";
	private static final String DIAGONAL_STROKE_WIDTH_ATTRIBUTE_VALUE = "21.21";
	private static final String DEFAULT_STROKE_LINE_CAPE_ATTRIBUTE_VALUE = "round";
	
	private static final String XMLNS_ATTRIBUTE_NAME = "xmlns";
	private static final String VIEW_BOX_ATTRIBUTE_NAME = "viewBox";
	
	private static final String DEFAULT_XMLNS_ATTRIBUTE_VALUE = "http://www.w3.org/2000/svg";
	private static final String DEFAULT_VIEW_BOX_ATTRIBUTE_VALUE = "0 0 436 380";
	
	private static final String REQUEST_METHOD = "GET";
	private static final String REQUEST_RESPONSE_TYPE = "arraybuffer";
	private static final String REQUEST_URL = "kite_resources/board_score_caches/opening.cfc";
	
	private static final String MOVE_SOUND_URL = "sounds/move.mp3";
	
	private static final int SUCCESSFUL_REQUEST_STATUS = 200;
	
	private static final Window WINDOW = Window.current();
	private static final HTMLDocument DOCUMENT = HTMLDocument.current();
	
	private boolean aiModeSelected;
	private boolean multiplayerModeSelected;
	private SkillLevel aiSkillLevel;
	
	private boolean aiPlaysRed;
	private boolean redAtTurn;
	
	private final int[] movesScores = new int[BOARD_WIDTH];
	private MoveAnalysis lastMoveAnalysis;
	
	private HTMLElement loadingMessageElement;
	private HTMLElement loadingBarElement;
	private HTMLElement loadingBarProgressElement;
	
	private HTMLButtonElement undoButtonElement;
	private HTMLButtonElement redoButtonElement;
	
	private HTMLSelectElement modeSelectElement;
	private HTMLSelectElement aiSkillLevelSelectElement;
	
	private HTMLElement boardLinesElement;
	
	private final HTMLElement[][] cellElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellMarkerElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellHighlightElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellHighlightForegroundElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellEvaluationElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[][] cellEvaluationBackgroundElements = new HTMLElement[BOARD_WIDTH][BOARD_HEIGHT];
	private final HTMLElement[] cellColumnElements = new HTMLElement[BOARD_WIDTH];
	
	private final HTMLElement[] cellLabelElements = new HTMLElement[BOARD_WIDTH];
	
	private HTMLElement winnerLabelElement;
	
	private HTMLAudioElement moveAudioElement;
	
	private String currentLoadingMessage;
	private String currentLoadingProgress;
	
	private Kite solver;
	
	private int eventID;
	
	private final int[] highlightedCellXs = new int[BOARD_WIDTH];
	private final int[] highlightedCellYs = new int[BOARD_WIDTH];
	
	private int highlightedCellAmount;
	
	private int cellAnalysisX = Integer.MIN_VALUE;
	private int cellAnalysisY;
	
	private int volumeLevel;
	
	public KiteDemo() {
		this.redAtTurn = true;
	}
	
	public void onStart() {
		loadingMessageElement = DOCUMENT.getElementById(LOADING_MESSAGE_ELEMENT_ID);
		loadingBarElement = DOCUMENT.getElementById(LOADING_BAR_ELEMENT_ID);
		loadingBarProgressElement = DOCUMENT.getElementById(LOADING_BAR_PROGRESS_ELEMENT_ID);
		
		XMLHttpRequest request = new XMLHttpRequest();
		
		request.open(REQUEST_METHOD, REQUEST_URL);
		request.setResponseType(REQUEST_RESPONSE_TYPE);
		
		request.onLoad((progressEvent) -> {
			
			int requestStatus = request.getStatus();
			if(requestStatus == SUCCESSFUL_REQUEST_STATUS) {
				
				updateLoadingProgress(MAXIMAL_LOADING_PROGRESS, OPENING_SCORE_CACHE_SIZE_IN_BYTES);
				
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
		updateLoadingProgress("Error while loading Kite solver!", -1);
		
		setElementStyles(loadingBarElement, LOADING_BAR_ERROR_ELEMENT_STYLES);
		setElementStyles(loadingBarProgressElement, LOADING_BAR_PROGRESS_ERROR_ELEMENT_STYLES);
	}
	
	private void computeAndUpdateLoadProgress(int loadedBytes) {
		if(loadedBytes < 0) loadedBytes = 0;
		else if(loadedBytes > OPENING_SCORE_CACHE_SIZE_IN_BYTES) loadedBytes = OPENING_SCORE_CACHE_SIZE_IN_BYTES;
		
		float progress = (float) loadedBytes / OPENING_SCORE_CACHE_SIZE_IN_BYTES;
		updateLoadingProgress(progress, loadedBytes);
	}
	
	private void updateLoadingProgress(float progress, int loadedBytes) {
		float loadedMB = loadedBytes / MEGABYTE_IN_BYTES;
		loadedMB = Math.round(loadedMB * MEGABYTE_FORMAT_PRECISION) / MEGABYTE_FORMAT_PRECISION;
		
		String message = "Loading Kite solver... (" + loadedMB + " MB / " + OPENING_SCORE_CACHE_SIZE_IN_MEGABYTES + " MB)";
		updateLoadingProgress(message, progress);
	}
	
	private void updateLoadingProgress(String message, float progress) {
		if(!message.equals(currentLoadingMessage)) {
			
			currentLoadingMessage = message;
			loadingMessageElement.setInnerText(message);
		}
		
		if(progress < 0) return;
		
		String s = String.format(Locale.ROOT, WIDTH_ELEMENT_STYLE_VALUE_FORMAT, progress);
		if(!s.equals(currentLoadingProgress)) {
			
			currentLoadingProgress = s;
			setElementStyles(loadingBarProgressElement, WIDTH_ELEMENT_STYLE_KEY, s);
		}
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
		
		modeSelectElement = (HTMLSelectElement) createControlElement(SELECT_ELEMENT_TYPE);
		undoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		redoButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		HTMLButtonElement newGameButtonElement = (HTMLButtonElement) createControlElement(BUTTON_ELEMENT_TYPE);
		
		aiSkillLevelSelectElement = (HTMLSelectElement) createControlElement(SELECT_ELEMENT_TYPE);
		
		undoButtonElement.setTextContent(UNDO_BUTTON_ELEMENT_TEXT);
		redoButtonElement.setTextContent(REDO_BUTTON_ELEMENT_TEXT);
		
		newGameButtonElement.setTextContent(NEW_GAME_BUTTON_ELEMENT_TEXT);
		
		HTMLOptionElement optionElement = createOptionElement(FIRST_MODE_BUTTON_ELEMENT_TEXT);
		modeSelectElement.appendChild(optionElement);
		
		optionElement = createOptionElement(SECOND_MODE_BUTTON_ELEMENT_TEXT);
		modeSelectElement.appendChild(optionElement);
		
		optionElement = createOptionElement(THIRD_MODE_BUTTON_ELEMENT_TEXT);
		modeSelectElement.appendChild(optionElement);
		
		for(SkillLevel skillLevel : ORDERED_AI_SKILL_LEVELS) {
			
			String skillLevelDisplayName = skillLevel.getDisplayName();
			
			optionElement = createOptionElement(skillLevelDisplayName);
			aiSkillLevelSelectElement.appendChild(optionElement);
		}
		
		setElementStyles(modeSelectElement, SELECT_ELEMENT_STYLES);
		setElementStyles(aiSkillLevelSelectElement, SELECT_ELEMENT_STYLES);
		
		modeSelectElement.addEventListener(ELEMENT_CHANGE_EVENT_TYPE, (event) -> {
			
			eventID++;
			
			int i = modeSelectElement.getSelectedIndex();
			changeMode(i);
		});
		
		newGameButtonElement.onClick((mouseEvent) -> {
			
			eventID++;
			
			setupNewGame(aiModeSelected || multiplayerModeSelected);
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
			
			eventID++;
			
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
					
					if(!modeSelectElement.isDisabled()) {
						
						eventID++;
						
						int i = modeSelectElement.getSelectedIndex();
						i = i == 0 ? 1 : 0;
						
						modeSelectElement.setSelectedIndex(i);
						changeMode(i);
					}
					
					keyboardEvent.preventDefault();
				}
				case BACKSPACE_KEY_NAME -> {
					
					if(keyboardEvent.isRepeat()) return;
					
					if(!newGameButtonElement.isDisabled()) newGameButtonElement.click();
					keyboardEvent.preventDefault();
				}
				case ARROW_UP_KEY_NAME -> {
					
					if(!aiSkillLevelSelectElement.isDisabled()) {
						
						eventID++;
						
						int i = aiSkillLevelSelectElement.getSelectedIndex();
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
						
						eventID++;
						
						int i = aiSkillLevelSelectElement.getSelectedIndex();
						int n = ORDERED_AI_SKILL_LEVELS.length;
						
						i++;
						if(i < n) {
							
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
				case ONE_KEY_NAME, NUMPAD_ONE_KEY_NAME -> {
					
					cellColumnElements[0].click();
					keyboardEvent.preventDefault();
				}
				case TWO_KEY_NAME, NUMPAD_TWO_KEY_NAME -> {
					
					cellColumnElements[1].click();
					keyboardEvent.preventDefault();
				}
				case THREE_KEY_NAME, NUMPAD_THREE_KEY_NAME -> {
					
					cellColumnElements[2].click();
					keyboardEvent.preventDefault();
				}
				case FOUR_KEY_NAME, NUMPAD_FOUR_KEY_NAME -> {
					
					cellColumnElements[3].click();
					keyboardEvent.preventDefault();
				}
				case FIVE_KEY_NAME, NUMPAD_FIVE_KEY_NAME -> {
					
					cellColumnElements[4].click();
					keyboardEvent.preventDefault();
				}
				case SIX_KEY_NAME, NUMPAD_SIX_KEY_NAME -> {
					
					cellColumnElements[5].click();
					keyboardEvent.preventDefault();
				}
				case SEVEN_KEY_NAME, NUMPAD_SEVEN_KEY_NAME -> {
					
					cellColumnElements[6].click();
					keyboardEvent.preventDefault();
				}
			}
		});
		
		controlsElement.appendChild(modeSelectElement);
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
		
		boardLinesElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
		setElementStyles(boardLinesElement, BOARD_LINES_ELEMENT_STYLES);
		
		boardElement.appendChild(boardLinesElement);
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			HTMLElement cellColumnElement = createColumnFlexboxElement();
			cellColumnElements[x] = cellColumnElement;
			
			int maxY = BOARD_HEIGHT - 1;
			for(int y = maxY; y >= 0; y--) {
				
				HTMLElement cellElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellElement, CELL_ELEMENT_STYLES);
				
				HTMLElement cellMarkerElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellMarkerElement, CELL_MARKER_ELEMENT_STYLES);
				
				HTMLElement cellHighlightElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellHighlightElement, CELL_HIGHLIGHT_ELEMENT_STYLES);
				
				HTMLElement cellHighlightForegroundElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellHighlightForegroundElement, CELL_HIGHLIGHT_FOREGROUND_ELEMENT_STYLES);
				
				HTMLElement cellEvaluationElement = createColumnFlexboxElement();
				setElementStyles(cellEvaluationElement, CELL_EVALUATION_ELEMENT_STYLES);
				
				HTMLElement cellEvaluationBackgroundElement = DOCUMENT.createElement(DEFAULT_ELEMENT_TYPE);
				setElementStyles(cellEvaluationBackgroundElement, CELL_EVALUATION_BACKGROUND_ELEMENT_STYLES);
				
				HTMLElement cellEvaluationTextElement = createSpanElement(0);
				setElementStyles(cellEvaluationTextElement, CELL_EVALUATION_TEXT_ELEMENT_STYLES);
				cellEvaluationElement.appendChild(cellEvaluationTextElement);
				
				cellElement.appendChild(cellMarkerElement);
				cellElement.appendChild(cellHighlightElement);
				cellElement.appendChild(cellHighlightForegroundElement);
				cellElement.appendChild(cellEvaluationBackgroundElement);
				cellElement.appendChild(cellEvaluationElement);
				
				cellElements[x][y] = cellElement;
				cellMarkerElements[x][y] = cellMarkerElement;
				cellHighlightElements[x][y] = cellHighlightElement;
				cellHighlightForegroundElements[x][y] = cellHighlightForegroundElement;
				cellEvaluationElements[x][y] = cellEvaluationElement;
				cellEvaluationBackgroundElements[x][y] = cellEvaluationBackgroundElement;
				
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
		
		Storage localStorage = WINDOW.getLocalStorage();
		
		String s = localStorage.getItem(AUDIO_SETTING_LOCAL_STORAGE_ITEM_NAME);
		if(s != null) {
			
			volumeLevel = Integer.parseInt(s);
			moveAudioElement.setVolume(VOLUME_LEVELS[volumeLevel]);
		}
		
		HTMLElement audioSettingElement = createImageElement(AUDIO_SETTING_ELEMENT_SOURCE_PATHS[volumeLevel], AUDIO_SETTING_ELEMENT_ALTERNATIVE_TEXT, AUDIO_SETTING_ELEMENT_SIZE);
		setElementStyles(audioSettingElement, AUDIO_SETTING_ELEMENT_STYLES);
		
		audioSettingElement.onClick((mouseEvent) -> {
			
			volumeLevel++;
			if(volumeLevel == VOLUME_LEVELS.length) volumeLevel = 0;
			
			moveAudioElement.setVolume(VOLUME_LEVELS[volumeLevel]);
			
			((HTMLImageElement) audioSettingElement).setSrc(AUDIO_SETTING_ELEMENT_SOURCE_PATHS[volumeLevel]);
			
			if(volumeLevel == 0) localStorage.removeItem(AUDIO_SETTING_LOCAL_STORAGE_ITEM_NAME);
			else localStorage.setItem(AUDIO_SETTING_LOCAL_STORAGE_ITEM_NAME, String.valueOf(volumeLevel));
		});
		
		appElement.appendChild(audioSettingElement);
		
		int perfectIndex = SkillLevel.PERFECT.ordinal();
		
		aiSkillLevel = SkillLevel.PERFECT;
		aiSkillLevelSelectElement.setSelectedIndex(perfectIndex);
		
		Location location = WINDOW.getLocation();
		String locationSearch = location.getSearch();
		
		if(!locationSearch.isBlank()) {
			
			locationSearch = locationSearch.substring(1);
			
			String[] items = locationSearch.split(LOCATION_SEARCH_ITEM_SEPARATOR);
			for(String item : items) {
				
				int splitIndex = item.indexOf(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				
				String itemKey;
				String itemValue;
				
				if(splitIndex < 0) {
					
					itemKey = item;
					itemValue = null;
					
				} else {
					
					itemKey = item.substring(0, splitIndex);
					itemValue = item.substring(splitIndex + 1);
				}
				
				switch(itemKey) {
					case LOCATION_SEARCH_MOVES_KEY -> {
						
						if(itemValue == null) return;
						
						int l = itemValue.length();
						for(int i = 0; i < l; i++) {
							
							int moveX = itemValue.charAt(i) - SMALLEST_LOCATION_SEARCH_MOVE;
							playMove(moveX, true);
						}
					}
					case LOCATION_SEARCH_UNDONE_MOVES_KEY -> {
						
						if(itemValue == null) return;
						
						int l = itemValue.length();
						for(int i = 0; i < l; i++) {
							
							int moveX = itemValue.charAt(i) - SMALLEST_LOCATION_SEARCH_MOVE + 1;
							solver.playMove(moveX);
						}
						
						for(int i = 0; i < l; i++) solver.undoMove();
					}
					case LOCATION_SEARCH_AI_COLOR_KEY -> {
						
						if(itemValue == null) return;
						
						aiModeSelected = true;
						
						modeSelectElement.setSelectedIndex(1);
						
						disableButtonElement(undoButtonElement);
						disableButtonElement(redoButtonElement);
						
						aiPlaysRed = itemValue.equals(RED_LOCATION_SEARCH_AI_COLOR);
					}
					case LOCATION_SEARCH_AI_LEVEL_KEY -> {
						
						if(itemValue == null) return;
						
						int i = Integer.parseInt(itemValue);
						
						aiSkillLevel = ORDERED_AI_SKILL_LEVELS[i];
						aiSkillLevelSelectElement.setSelectedIndex(i);
					}
					default -> {
						
						multiplayerModeSelected = true;
						
						modeSelectElement.setSelectedIndex(2);
						
						disableButtonElement(undoButtonElement);
						disableButtonElement(redoButtonElement);
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
			
			if(!multiplayerModeSelected) {
				
				if(solver.canUndoMove()) {
					
					int x = solver.lastMove();
					
					solver.undoMove();
					lastMoveAnalysis = solver.analyseMove(x);
					solver.redoMove();
					
				} else {
					
					disableButtonElement(undoButtonElement);
				}
				
				if(!solver.canRedoMove()) disableButtonElement(redoButtonElement);
				
				updateCellLabelElements();
			}
		}
		
		moveAudioElement = (HTMLAudioElement) DOCUMENT.createElement(AUDIO_ELEMENT_TYPE);
		moveAudioElement.setSrc(MOVE_SOUND_URL);
		
		bodyElement.appendChild(appElement);
	}
	
	private void changeAISkillLevel(int aiSkillLevelIndex) {
		int index = aiSkillLevel.ordinal();
		if(index == aiSkillLevelIndex) return;
		
		aiSkillLevel = ORDERED_AI_SKILL_LEVELS[aiSkillLevelIndex];
		
		updateLocationSearch();
		setupNewGame(true);
	}
	
	private void changeMode(int mode) {
		switch(mode) {
			case 0 -> {
				
				aiModeSelected = false;
				multiplayerModeSelected = false;
				
				modeSelectElement.setSelectedIndex(0);
				
				disableSelectElement(aiSkillLevelSelectElement);
				
				if(solver.canUndoMove()) {
					
					enableButtonElement(undoButtonElement);
					
					int x = solver.lastMove();
					
					solver.undoMove();
					lastMoveAnalysis = solver.analyseMove(x);
					solver.redoMove();
					
				} else {
					
					lastMoveAnalysis = null;
				}
			}
			case 1 -> {
				
				aiModeSelected = true;
				multiplayerModeSelected = false;
				
				modeSelectElement.setSelectedIndex(1);
				
				disableButtonElement(undoButtonElement);
				disableButtonElement(redoButtonElement);
				enableSelectElement(aiSkillLevelSelectElement);
				
				setupNewGame(true);
			}
			case 2 -> {
				
				aiModeSelected = false;
				multiplayerModeSelected = true;
				
				modeSelectElement.setSelectedIndex(2);
				
				disableButtonElement(undoButtonElement);
				disableButtonElement(redoButtonElement);
				disableSelectElement(aiSkillLevelSelectElement);
				
				setupNewGame(true);
			}
		}
		
		updateCellLabelElements();
		updateLocationSearch();
	}
	
	private void setupNewGame(boolean clearRedoHistory) {
		int playedMoveAmount = solver.playedMoveAmount();
		if(playedMoveAmount != 0) {
			
			for(int i = 0; i < playedMoveAmount; i++) {
				
				int moveX = solver.playedMove(i) - 1;
				int moveY = solver.playedMoveRow(i) - 1;
				
				setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX, false);
			}
			
			solver.clearBoard();
			if(clearRedoHistory) solver.clearRedoHistory();
			lastMoveAnalysis = null;
			
			redAtTurn = true;
			
			if(!(aiModeSelected || multiplayerModeSelected)) updateCellLabelElements();
			updateWinnerLabelElement();
			updateLocationSearch();
			
		} else if(clearRedoHistory && solver.canRedoMove()) {
			
			solver.clearRedoHistory();
			updateLocationSearch();
		}
		
		if(aiModeSelected) {
			
			Random random = ThreadLocalRandom.current();
			
			aiPlaysRed = random.nextBoolean();
			if(aiPlaysRed) playAIMove();
			else updateLocationSearch();
			
		} else if(!multiplayerModeSelected) {
			
			disableButtonElement(undoButtonElement);
			if(solver.canRedoMove()) enableButtonElement(redoButtonElement);
			else disableButtonElement(redoButtonElement);
		}
		
		hideWinLines();
	}
	
	private void undoMove() {
		if(solver.boardEmpty()) return;
		
		redAtTurn = !redAtTurn;
		
		int moveX = solver.lastMove() - 1;
		int moveY = solver.lastMoveRow() - 1;
		
		solver.undoMove();
		
		if(!(aiModeSelected || multiplayerModeSelected)) {
			
			if(solver.canUndoMove()) {
				
				int x = solver.lastMove();
				
				solver.undoMove();
				lastMoveAnalysis = solver.analyseMove(x);
				solver.redoMove();
				
			} else {
				
				lastMoveAnalysis = null;
			}
		}
		
		setCellElementBackgroundColor(moveX, moveY, EMPTY_CELL_ELEMENT_BACKGROUND_COLOR_INDEX, false);
		
		if(!solver.boardEmpty()) {
			
			moveX = solver.lastMove() - 1;
			moveY = solver.lastMoveRow() - 1;
			
			int i = redAtTurn ? YELLOW_CELL_ELEMENT_BACKGROUND_COLOR_INDEX : RED_CELL_ELEMENT_BACKGROUND_COLOR_INDEX;
			
			setCellElementBackgroundColor(moveX, moveY, i, true);
		}
		
		hideWinLines();
		
		if(!(aiModeSelected || multiplayerModeSelected)) updateCellLabelElements();
		updateWinnerLabelElement();
		updateLocationSearch();
		
		enableButtonElement(redoButtonElement);
		if(solver.canUndoMove()) enableButtonElement(undoButtonElement);
		else disableButtonElement(undoButtonElement);
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
		
		if(!initial && !(aiModeSelected || multiplayerModeSelected)) {
			
			lastMoveAnalysis = solver.analyseMove(moveX + 1);
		}
		
		solver.playMove(moveX + 1);
		setCellElementBackgroundColor(moveX, moveY, i, true);
		
		if(!initial) moveAudioElement.play();
		
		BoardOutcome outcome = solver.gameOutcome();
		if(outcome.isWin()) showWinLines();
		
		if(!initial) {
			
			updateLocationSearch();
			
			if(aiModeSelected) {
				
				if(aiPlaysRed == redAtTurn && outcome == BoardOutcome.UNDECIDED) {
					
					scheduleAIMove();
					return;
				}
				
			} else if(!multiplayerModeSelected) {
				
				enableButtonElement(undoButtonElement);
				if(!solver.canRedoMove()) disableButtonElement(redoButtonElement);
				
				updateCellLabelElements();
			}
		}
		
		updateWinnerLabelElement();
	}
	
	private void showWinLines() {
		String strokeAttributeValue = redAtTurn ? YELLOW_STROKE_ATTRIBUTE_VALUE : RED_STROKE_ATTRIBUTE_VALUE;
		
		BoardLine[] lines = solver.winLines();
		for(BoardLine line : lines) {
			
			int x1 = line.getStartCellX();
			int y1 = line.getStartCellY();
			
			int x2 = line.getEndCellX();
			int y2 = line.getEndCellY();
			
			int dx = line.getDirectionX();
			int dy = line.getDirectionY();
			
			boolean diagonal = (Math.abs(dx) + Math.abs(dy)) != 1;
			
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
			
			lineElementLine.setAttribute(STROKE_ATTRIBUTE_NAME, strokeAttributeValue);
			lineElementLine.setAttribute(STROKE_WIDTH_ATTRIBUTE_NAME, diagonal ? DIAGONAL_STROKE_WIDTH_ATTRIBUTE_VALUE : STRAIGHT_STROKE_WIDTH_ATTRIBUTE_VALUE);
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
	
	private void highlightCell(int cellX, int cellY) {
		highlightedCellXs[highlightedCellAmount] = cellX;
		highlightedCellYs[highlightedCellAmount] = cellY;
		
		highlightedCellAmount++;
		
		HTMLElement e = cellHighlightElements[cellX][cellY];
		setElementStyles(e, ELEMENT_BACKGROUND_COLOR_STYLE_KEY, CELL_HIGHLIGHT_BACKGROUND_COLOR);
		
		showElement(e);
		showElement(cellHighlightForegroundElements[cellX][cellY]);
	}
	
	private void removeCellHighlights() {
		for(int i = 0; i < highlightedCellAmount; i++) {
			
			int x = highlightedCellXs[i];
			int y = highlightedCellYs[i];
			
			hideElement(cellHighlightElements[x][y]);
			hideElement(cellHighlightForegroundElements[x][y]);
		}
		
		highlightedCellAmount = 0;
	}
	
	private void addCellAnalysis(int cellX, int cellY, MoveAnalysis moveAnalysis) {
		removeCellAnalyses();
		
		cellAnalysisX = cellX;
		cellAnalysisY = cellY;
		
		String color;
		String text;
		
		switch(moveAnalysis.getMoveQuality()) {
			case BEST -> {
				
				color = "#00C951";
				text = "★";
			}
			case GOOD -> {
				
				color = "#7CCF00";
				text = "✓";
			}
			case INACCURACY -> {
				
				color = "#F0B100";
				text = "?!";
			}
			case MISTAKE -> {
				
				color = "#FF6900";
				text = "?";
			}
			case BLUNDER -> {
				
				color = "#FB2C36";
				text = "??";
			}
			case MISSED_WIN -> {
				
				color = "#FE9A00";
				text = "━";
			}
			case FORCED -> {
				
				color = "#00B8DB";
				text = "➔";
			}
			default -> {
				
				color = null;
				text = null;
			}
		}
		
		HTMLElement e = cellEvaluationElements[cellX][cellY];
		
		e.getFirstChild().setTextContent(text);
		setElementStyles(e, ELEMENT_BACKGROUND_COLOR_STYLE_KEY, color);
		
		showFlexboxElement(e);
		showElement(cellEvaluationBackgroundElements[cellX][cellY]);
	}
	
	private void removeCellAnalyses() {
		if(cellAnalysisX == Integer.MIN_VALUE) return;
		
		hideElement(cellEvaluationElements[cellAnalysisX][cellAnalysisY]);
		hideElement(cellEvaluationBackgroundElements[cellAnalysisX][cellAnalysisY]);
		
		cellAnalysisX = Integer.MIN_VALUE;
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
		if(aiModeSelected || multiplayerModeSelected) {
			
			for(int x = 0; x < BOARD_WIDTH; x++) {
				
				HTMLElement cellLabelElement = cellLabelElements[x];
				cellLabelElement.setTextContent(EMPTY_CELL_LABEL_ELEMENT_TEXT);
			}
			
			removeCellHighlights();
			removeCellAnalyses();
			
			return;
		}
		
		solver.evaluateAllMoves(movesScores);
		
		if(lastMoveAnalysis == null) {
			
			removeCellAnalyses();
			
		} else {
			
			int x = solver.lastMove() - 1;
			int y = solver.lastMoveRow() - 1;
			
			addCellAnalysis(x, y, lastMoveAnalysis);
		}
		
		int bestMoveScore = Integer.MIN_VALUE;
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			int moveScore = movesScores[x];
			String moveScoreString = formatMoveScore(moveScore);
			
			if(moveScore > bestMoveScore) bestMoveScore = moveScore;
			
			HTMLElement cellLabelElement = cellLabelElements[x];
			cellLabelElement.setTextContent(moveScoreString);
		}
		
		removeCellHighlights();
		if(bestMoveScore == Integer.MIN_VALUE) return;
		
		for(int x = 0; x < BOARD_WIDTH; x++) {
			
			int moveScore = movesScores[x];
			if(moveScore == bestMoveScore) {
				
				int y = solver.cellColumnHeight(x + 1);
				highlightCell(x, y);
			}
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
		int undoneMoveAmount = solver.undoneMoveAmount();
		boolean movesWerePlayed = playedMoveAmount != 0;
		boolean movesWereUndone = undoneMoveAmount != 0;
		boolean aiLevelNotPerfect = aiSkillLevel != SkillLevel.PERFECT;
		boolean searchNotEmpty = movesWerePlayed || movesWereUndone || aiModeSelected || multiplayerModeSelected || aiLevelNotPerfect;
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
			
			if(movesWereUndone) {
				
				if(b) stringBuilder.append(LOCATION_SEARCH_ITEM_SEPARATOR);
				b = true;
				
				stringBuilder.append(LOCATION_SEARCH_UNDONE_MOVES_KEY);
				stringBuilder.append(LOCATION_SEARCH_ITEM_KEY_AND_VALUE_SEPARATOR);
				
				int n = playedMoveAmount + undoneMoveAmount;
				for(int i = playedMoveAmount; i < n; i++) {
					
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
				
			} else if(multiplayerModeSelected) {
				
				if(b) stringBuilder.append(LOCATION_SEARCH_ITEM_SEPARATOR);
				b = true;
				
				stringBuilder.append(LOCATION_SEARCH_MULTIPLAYER_KEY);
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
	
	private static void hideElement(ElementCSSInlineStyle element) {
		CSSStyleDeclaration elementStyle = element.getStyle();
		elementStyle.setProperty(ELEMENT_DISPLAY_STYLE_KEY, INVISIBLE_ELEMENT_DISPLAY_STYLE_VALUE);
	}
	
	private static void showFlexboxElement(ElementCSSInlineStyle element) {
		CSSStyleDeclaration elementStyle = element.getStyle();
		elementStyle.setProperty(ELEMENT_DISPLAY_STYLE_KEY, FLEXBOX_ELEMENT_DISPLAY_STYLE_VALUE);
	}
	
	private static void showElement(ElementCSSInlineStyle element) {
		CSSStyleDeclaration elementStyle = element.getStyle();
		elementStyle.setProperty(ELEMENT_DISPLAY_STYLE_KEY, VISIBLE_ELEMENT_DISPLAY_STYLE_VALUE);
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
