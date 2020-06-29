/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
		char c = '+';
		if (string.startsWith("+"))
			string = string.substring(1);
		else if (string.startsWith("-")) {
			c = '-';
			string = string.substring(1);
		}

		int radix = 10;
		int substring = 0;
		if (string.startsWith("0x") || string.startsWith("0X") || string.startsWith("#")) {
			radix = 16;
			substring = 2;
		}
		if (string.startsWith("0o") || string.startsWith("0O")) {
			radix = 8;
			substring = 2;
		}
		if (string.startsWith("0b") || string.startsWith("0B")) {
			radix = 2;
			substring = 2;
		}

		return new BigInteger(c + string.substring(substring), radix).intValue();
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

	public static int[] longToInts(long l) {
		int[] array = new int[2];
		array[0] = (int) l;
		array[1] = (int) (l >> 32);
		return array;
	}
}
