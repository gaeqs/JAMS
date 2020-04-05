package net.jamsimulator.jams.utils;

import java.math.BigInteger;
import java.util.Optional;

public class NumericUtils {

	public static boolean isInteger(String string) {
		try {
			decodeInteger(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isLong(String string) {
		try {
			Long.decode(string);
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

	public static int decodeInteger(String string) {
		int multiplier = 1;
		if (string.startsWith("+"))
			string = string.substring(1);
		else if (string.startsWith("-")) {
			multiplier = -1;
			string = string.substring(1);
		}

		if (string.startsWith("0x") || string.startsWith("0X") || string.startsWith("#")) {
			return multiplier * new BigInteger(string.substring(2), 16).intValue();
		}
		if (string.startsWith("0o") || string.startsWith("0O")) {
			return multiplier * new BigInteger(string.substring(2), 8).intValue();
		}
		if (string.startsWith("0b") || string.startsWith("0B")) {
			return multiplier * new BigInteger(string.substring(2), 2).intValue();
		}
		return Integer.parseInt(string);
	}

	public static Optional<Integer> decodeIntegerSafe(String string) {
		try {
			return Optional.of(decodeInteger(string));
		} catch (NumberFormatException ex) {
			return Optional.empty();
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
