package net.jamsimulator.jams.utils;

public class NumericUtils {

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}


	public static boolean isLong(String string) {
		try {
			Long.parseLong(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isFloat(String string) {
		try {
			Float.parseFloat(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isShort(String string) {
		try {
			Short.parseShort(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isByte(String string) {
		try {
			Byte.parseByte(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}


	public static double intsToDouble(int low, int high) {
		return Double.longBitsToDouble((((long) high) << 32) + low);
	}

	public static int[] doubleToInts(double d) {
		int[] array = new int[2];
		long l = Double.doubleToLongBits(d);
		array[0] = (int) l;
		array[1] = (int) (l >> 32);
		return array;
	}
}
