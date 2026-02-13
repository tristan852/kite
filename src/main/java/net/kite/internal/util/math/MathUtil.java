package net.kite.internal.util.math;

public class MathUtil {
	
	private static final long UNSIGNED_MULTIPLY_HIGH_MASK = 0x00000000FFFFFFFFL;
	private static final long UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT = 32;
	private static final long UNSIGNED_MULTIPLY_HIGH_SECOND_SHIFT_AMOUNT = 63;
	
	public static long unsignedMultiplyHigh(long l1, long l2) {
		long l3 = l1 >> UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT;
		long l4 = l1 & UNSIGNED_MULTIPLY_HIGH_MASK;
		
		long l5 = l2 >> UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT;
		long l6 = l2 & UNSIGNED_MULTIPLY_HIGH_MASK;
		
		long l7 = l4 * l6;
		long l8 = l3 * l6 + (l7 >>> UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT);
		
		long l9 = l8 & UNSIGNED_MULTIPLY_HIGH_MASK;
		long l10 = l8 >> UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT;
		
		l9 += l4 * l5;
		
		long result = l3 * l5 + l10 + (l9 >> UNSIGNED_MULTIPLY_HIGH_FIRST_SHIFT_AMOUNT);
		
		result += (l2 & (l1 >> UNSIGNED_MULTIPLY_HIGH_SECOND_SHIFT_AMOUNT));
		result += (l1 & (l2 >> UNSIGNED_MULTIPLY_HIGH_SECOND_SHIFT_AMOUNT));
		
		return result;
	}
	
}
