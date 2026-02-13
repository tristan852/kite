package net.kite.internal.util.time;

import java.util.Locale;

public class TimeUtil {
	
	private static final String[] UNIT_NOTATIONS = new String[] {
			"ns",
			"us",
			"ms",
			"s"
	};
	
	private static final double UNIT_SCALE = 1000.0;
	
	public static String formatDuration(double duration) {
		int unit = 0;
		
		while(duration >= UNIT_SCALE) {
			
			duration /= UNIT_SCALE;
			unit++;
		}
		
		String notation = UNIT_NOTATIONS[unit];
		return String.format(Locale.US, "%.2f %s", duration, notation);
	}
	
}
