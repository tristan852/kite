package net.kite;

import net.kite.board.Board;
import net.kite.command.Command;
import net.kite.command.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Kite {
	
	private static Kite kite;
	
	private static final String NAME = "Kite";
	private static final String VERSION = "1.0.0";
	private static final String AUTHOR = "tristan852";
	
	private static final String WELCOME_MESSAGE;
	private static final String WELCOME_MESSAGE_TEMPLATE = "%s v%s by %s\n";
	
	private static final String COMMAND_PART_SEPARATOR = " ";
	
	static {
		WELCOME_MESSAGE = WELCOME_MESSAGE_TEMPLATE.formatted(NAME, VERSION, AUTHOR);
	}
	
	private Board board;
	
	public Kite() {
		this.board = new Board();
		
		kite = this;
	}
	
	public void onStart(String[] programArguments) {
		board.evaluate();
		
		System.out.println(WELCOME_MESSAGE);
		
		try {
			
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			while(true) {
				
				String line = bufferedReader.readLine();
				if(line == null) break;
				
				String[] commandParts = line.split(COMMAND_PART_SEPARATOR);
				
				int l = commandParts.length;
				if(l == 0) continue;
				
				String commandName = commandParts[0];
				Command command = Commands.command(commandName);
				if(command == null) {
					
					String errorMessage = String.format("Could not find a command with the name: %s", commandName);
					System.err.println(errorMessage);
					continue;
				}
				
				l--;
				
				String[] commandArguments = new String[l];
				System.arraycopy(commandParts, 1, commandArguments, 0, l);
				
				boolean quit = command.execute(commandArguments);
				if(quit) break;
			}
			
			bufferedReader.close();
			
		} catch(IOException exception) {
			
			String errorMessage = String.format("An exception occurred while handling a command: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public static String getName() {
		return NAME;
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public static String getAuthor() {
		return AUTHOR;
	}
	
	public static Kite getKite() {
		return kite;
	}
	
}
