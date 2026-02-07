package net.kite.util.random;

import net.kite.util.math.MathUtil;

import java.util.concurrent.ThreadLocalRandom;

public class Random {
	
	private static final long LCG_MULTIPLIER = 0x5851F42D4C957F2DL;
	private static final long LCG_ADDEND = 0x14057B7EF767814FL;
	
	private static final long SCRAMBLE_SEED_FIRST_SHIFT_AMOUNT = 30;
	private static final long SCRAMBLE_SEED_SECOND_SHIFT_AMOUNT = 27;
	private static final long SCRAMBLE_SEED_THIRD_SHIFT_AMOUNT = 31;
	
	private static final long SCRAMBLE_SEED_FIRST_MULTIPLIER = 0xBF58476D1CE4E5B9L;
	private static final long SCRAMBLE_SEED_SECOND_MULTIPLIER = 0x94D049BB133111EBL;
	
	private static final long START_SEED_THREAD_ID_MULTIPLIER = 0x9E3779B97F4A7C15L;
	
	private long currentSeed;
	
	public Random() {
		Thread currentThread = Thread.currentThread();
		ThreadLocalRandom currentThreadRandom = ThreadLocalRandom.current();
		
		long l1 = System.nanoTime();
		@SuppressWarnings("deprecation")
		long l2 = currentThread.getId() * START_SEED_THREAD_ID_MULTIPLIER;
		long l3 = currentThreadRandom.nextLong();
		
		long startSeed = l1 ^ l2 ^ l3;
		setSeed(startSeed);
	}
	
	public void setSeed(long seed) {
		currentSeed = scrambleSeed(seed) + LCG_ADDEND;
		
		randomLong();
	}
	
	public int randomInteger(int maximalValue) {
		while(true) {
			
			long randomLong = randomLong();
			
			long upperBits = MathUtil.unsignedMultiplyHigh(randomLong, maximalValue);
			long lowerBits = randomLong * maximalValue;
			
			long threshold = Long.remainderUnsigned(-maximalValue, maximalValue);
			if(Long.compareUnsigned(lowerBits, threshold) >= 0) return (int) upperBits;
		}
	}
	
	public long randomLong() {
		currentSeed = currentSeed * LCG_MULTIPLIER + LCG_ADDEND;
		return currentSeed;
	}
	
	private static long scrambleSeed(long seed) {
		seed ^= (seed >>> SCRAMBLE_SEED_FIRST_SHIFT_AMOUNT);
		seed *= SCRAMBLE_SEED_FIRST_MULTIPLIER;
		
		seed ^= (seed >>> SCRAMBLE_SEED_SECOND_SHIFT_AMOUNT);
		seed *= SCRAMBLE_SEED_SECOND_MULTIPLIER;
		
		seed ^= (seed >>> SCRAMBLE_SEED_THIRD_SHIFT_AMOUNT);
		return seed;
	}
	
}
