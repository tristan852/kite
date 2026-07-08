package net.kite.internal.util.random;

import java.util.concurrent.ThreadLocalRandom;

public final class Random {
	
	private static final long LCG_MULTIPLIER = 0x5851F42D4C957F2DL;
	private static final long LCG_ADDEND = 0x14057B7EF767814FL;
	
	private static final long INVERSE_LCG_MULTIPLIER = 0xC097EF87329E28A5L;
	
	private static final int SCRAMBLE_SEED_FIRST_SHIFT_AMOUNT = 30;
	private static final int SCRAMBLE_SEED_SECOND_SHIFT_AMOUNT = 27;
	private static final int SCRAMBLE_SEED_THIRD_SHIFT_AMOUNT = 31;
	
	private static final long SCRAMBLE_SEED_FIRST_MULTIPLIER = 0xBF58476D1CE4E5B9L;
	private static final long SCRAMBLE_SEED_SECOND_MULTIPLIER = 0x94D049BB133111EBL;
	
	private static final long SCRAMBLE_SEED_FIRST_INVERSE_MULTIPLIER = 0x96DE1B173F119089L;
	private static final long SCRAMBLE_SEED_SECOND_INVERSE_MULTIPLIER = 0x319642B2D24D8EC3L;
	
	private static final long START_SEED_THREAD_ID_MULTIPLIER = 0x9E3779B97F4A7C15L;
	
	private static final int NEXT_FLOAT_OFFSET  = 40;
	private static final int NEXT_DOUBLE_OFFSET = 11;
	
	private static final float  NEXT_FLOAT_MULTIPLIER  = 0x1.0p-24F;
	private static final double NEXT_DOUBLE_MULTIPLIER = 0x1.0p-53;
	
	private long currentSeed;
	
	public Random() {
		setRandomSeed();
	}
	
	public long getSeed() {
		long seed = currentSeed;
		
		seed -= LCG_ADDEND;
		seed *= INVERSE_LCG_MULTIPLIER;
		seed -= LCG_ADDEND;
		
		return unscrambleSeed(seed);
	}
	
	public long setRandomSeed() {
		Thread currentThread = Thread.currentThread();
		ThreadLocalRandom currentThreadRandom = ThreadLocalRandom.current();
		
		long l1 = System.nanoTime();
		@SuppressWarnings("deprecation")
		long l2 = currentThread.getId() * START_SEED_THREAD_ID_MULTIPLIER;
		long l3 = currentThreadRandom.nextLong();
		
		long startSeed = l1 ^ l2 ^ l3;
		setSeed(startSeed);
		
		return startSeed;
	}
	
	public void setSeed(long seed) {
		currentSeed = scrambleSeed(seed) + LCG_ADDEND;
		
		randomLong();
	}
	
	public boolean nextBoolean() {
		return (randomLong() & 1L) == 0;
	}
	
	public double nextDouble() {
		return (randomLong() >>> NEXT_DOUBLE_OFFSET) * NEXT_DOUBLE_MULTIPLIER;
	}
	
	public float nextFloat() {
		return (randomLong() >>> NEXT_FLOAT_OFFSET) * NEXT_FLOAT_MULTIPLIER;
	}
	
	public int randomInteger(int maximalValue) {
		return (int) randomLong(maximalValue);
	}
	
	public long randomLong(long maximalValue) {
		while(true) {
			
			long randomLong = randomLong();
			
			long upperBits = Math.unsignedMultiplyHigh(randomLong, maximalValue);
			long lowerBits = randomLong * maximalValue;
			
			long threshold = Long.remainderUnsigned(-maximalValue, maximalValue);
			if(Long.compareUnsigned(lowerBits, threshold) >= 0) return upperBits;
		}
	}
	
	public long randomLong() {
		currentSeed = currentSeed * LCG_MULTIPLIER + LCG_ADDEND;
		return currentSeed;
	}
	
	private static long unXORRightShift(long x, int shiftAmount) {
		long result = 0;
		
		for(int i = 63; i >= 0; i--) {
			
			long b = (x >>> i) & 1;
			
			if(i + shiftAmount <= 63) {
				
				b ^= (result >>> (i + shiftAmount)) & 1;
			}
			
			result |= b << i;
		}
		
		return result;
	}
	
	private static long unscrambleSeed(long scrambledSeed) {
		scrambledSeed = unXORRightShift(scrambledSeed, SCRAMBLE_SEED_THIRD_SHIFT_AMOUNT);
		
		scrambledSeed *= SCRAMBLE_SEED_SECOND_INVERSE_MULTIPLIER;
		scrambledSeed = unXORRightShift(scrambledSeed, SCRAMBLE_SEED_SECOND_SHIFT_AMOUNT);
		
		scrambledSeed *= SCRAMBLE_SEED_FIRST_INVERSE_MULTIPLIER;
		scrambledSeed = unXORRightShift(scrambledSeed, SCRAMBLE_SEED_FIRST_SHIFT_AMOUNT);
		
		return scrambledSeed;
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
