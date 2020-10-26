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

import java.util.Arrays;
import java.util.List;

public class LabelUtils {

	private static final List<String> illegalCharacters = Arrays.asList("\\", ";", "\"", "#", "'");

	/**
	 * Returns whether this label is valid.
	 * The given string must not contain the final character ':'.
	 *
	 * @param label the string.
	 * @return whether the label represented by the given string is valid.
	 */
	public static boolean isLabelLegal(String label) {
		if (label.isEmpty()) return false;

		//Special case: ':' is not allowed, but "::" is.
		int colon = -2;

		do {
			colon = label.indexOf(':', colon + 2);
			if (colon == -1) break;
			if (label.length() <= colon + 1 || label.charAt(colon + 1) != ':') {
				return false;
			}
		} while (label.length() > colon + 2);

		return illegalCharacters.stream().noneMatch(label::contains);
	}

	/**
	 * Returns the final position of the label inside the given line.
	 * If no labels are found, this method returns -1.
	 * <p>
	 * The returned label may be illegal. Use {@link #isLabelLegal(String)} to check if the returned label is legal.
	 *
	 * @param line the line containing the label.
	 * @return the last position of the label, inclusive, or -1 if not found. (The last position will be the ':' character.)
	 */
	public static int getLabelFinishIndex(String line) {
		var insideString = false;
		var insideChar = false;

		var chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case '"' -> {
					if (insideChar) continue;
					insideString = !insideString;
				}
				case '\'' -> {
					if (insideString) continue;
					insideChar = !insideChar;
				}
				case ':' -> {
					if (insideChar || insideString) continue;
					if (chars.length == i + 1 || chars[i + 1] != ':') return i;
					i++;
				}
			}
		}

		return -1;
	}
}
