package net.jamsimulator.jams.utils;

public class MathUtils {

	public static double clamp(double min, double val, double max) {
		if (val < min) {
			return min;
		} else {
			return Math.min(val, max);
		}
	}

}
