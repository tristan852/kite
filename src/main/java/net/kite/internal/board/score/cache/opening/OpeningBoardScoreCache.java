package net.kite.internal.board.score.cache.opening;

import net.kite.internal.board.score.BoardScore;
import net.kite.internal.board.score.cache.BoardScoreCache;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class OpeningBoardScoreCache {
	
	private static final int CAPACITY = 16777259;
	
	private static final long BOARD_PARTIAL_COLUMN_HASH_MASK = 0x00000000000000FFL;
	
	private static final int SAVE_TO_FILE_BUFFER_SIZE = 131072;
	
	private final byte[] boardPartialColumnHashes;
	private final byte[] boardScores;
	
	public OpeningBoardScoreCache() {
		this.boardPartialColumnHashes = new byte[CAPACITY];
		this.boardScores = new byte[CAPACITY];
	}
	
	public void loadFromResources(String resourcePath) {
		InputStream inputStream = BoardScoreCache.class.getResourceAsStream(resourcePath);
		
		if(inputStream == null) {
			
			System.err.println("The opening score cache could not be found in resources!");
			return;
		}
		
		inputStream = new BufferedInputStream(inputStream);
		loadFromStream(inputStream);
	}
	
	public void loadFromBytes(byte[] bytes) {
		if(bytes == null) {
			
			System.err.println("The opening score cache could not be found in web resources!");
			return;
		}
		
		InputStream inputStream = new ByteArrayInputStream(bytes);
		loadFromStream(inputStream);
	}
	
	private void loadFromStream(InputStream inputStream) {
		try(
				inputStream;
				InputStream inflatedInputStream = new InflaterInputStream(inputStream)
		) {
			
			readFromInputStream(inflatedInputStream, boardPartialColumnHashes);
			readFromInputStream(inflatedInputStream, boardScores);
			
			for(int i = 0; i < CAPACITY; i++) {
				
				boardScores[i] += BoardScore.INVALID;
			}
			
		} catch(Exception exception) {
			
			String errorMessage = String.format("An exception occurred while loading opening score cache: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public void saveToFile(String filePath) {
		try(
				OutputStream outputStream = new FileOutputStream(filePath);
				OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, SAVE_TO_FILE_BUFFER_SIZE);
				OutputStream deflatedOutputStream = new DeflaterOutputStream(bufferedOutputStream, new Deflater(Deflater.BEST_COMPRESSION))
		) {
			
			for(int i = 0; i < CAPACITY; i++) {
				
				boardScores[i] -= BoardScore.INVALID;
			}
			
			deflatedOutputStream.write(boardPartialColumnHashes);
			deflatedOutputStream.write(boardScores);
			
			for(int i = 0; i < CAPACITY; i++) {
				
				boardScores[i] += BoardScore.INVALID;
			}
			
		} catch(Exception exception) {
			
			String errorMessage = String.format("An exception occurred while saving opening score cache to file: %s", exception);
			System.err.println(errorMessage);
		}
	}
	
	public int boardScore(long boardColumnHash) {
		int index = (int) Long.remainderUnsigned(boardColumnHash, CAPACITY);
		
		long partialColumnHash = boardPartialColumnHashes[index];
		
		boardColumnHash &= BOARD_PARTIAL_COLUMN_HASH_MASK;
		partialColumnHash &= BOARD_PARTIAL_COLUMN_HASH_MASK;
		
		return boardColumnHash == partialColumnHash ? boardScores[index] : Integer.MIN_VALUE;
	}
	
	private static void readFromInputStream(InputStream inputStream, byte[] array) throws IOException {
		int bytesRead = 0;
		int l = array.length;
		
		while(bytesRead < l) {
			
			int n = inputStream.read(array, bytesRead, l - bytesRead);
			if(n < 0) throw new IOException("Reached end of stream while reading into byte array!");
			
			bytesRead += n;
		}
	}
	
}
