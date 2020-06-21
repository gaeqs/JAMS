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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {

	public static List<String> multiSplit(String string, String... separators) {
		List<String> list = new ArrayList<>();
		int length = string.length(), from = 0;

		for (int i = 0; i < length; i++) {
			for (String separator : separators) {
				int sepLen = separator.length();
				if (string.regionMatches(i, separator, 0, sepLen)) {
					list.add(string.substring(from, i));
					from = i + sepLen;
					i = from - 1;
					break;
				}
			}
		}

		if (from < length) list.add(string.substring(from));
		return list;
	}

	public static List<String> multiSplitIgnoreInsideString(String string, boolean addEmpty, String... separators) {
		List<String> list = new ArrayList<>();
		int length = string.length(), from = 0;

		boolean insideString = false;
		boolean insideChar = false;
		boolean escape = false;
		String result;
		for (int i = 0; i < length; i++) {
			char c = string.charAt(i);
			if (c == '"' && !escape)
				insideString = !insideString;
			if (c == '\'' && !escape)
				insideChar = !insideChar;
			escape = !escape && c == '\\';

			if (insideString || insideChar) continue;
			for (String separator : separators) {
				int sepLen = separator.length();
				if (string.regionMatches(i, separator, 0, sepLen)) {
					result = string.substring(from, i);
					if (addEmpty || !result.isEmpty())
						list.add(result);
					from = i + sepLen;
					i = from - 1;
					break;
				}
			}
		}

		if (from < length) list.add(string.substring(from));
		return list;
	}

	public static Map<Integer, String> multiSplitIgnoreInsideStringWithIndex(String string, boolean addEmpty, String... separators) {
		Map<Integer, String> map = new HashMap<>();
		int length = string.length(), from = 0;

		boolean insideString = false;
		boolean insideChar = false;
		boolean escape = false;
		String result;
		for (int i = 0; i < length; i++) {
			char c = string.charAt(i);
			if (c == '"' && !escape)
				insideString = !insideString;
			if (c == '\'' && !escape)
				insideChar = !insideChar;
			escape = !escape && c == '\\';

			if (insideString || insideChar) continue;
			for (String separator : separators) {
				int sepLen = separator.length();
				if (string.regionMatches(i, separator, 0, sepLen)) {
					result = string.substring(from, i);
					if (addEmpty || !result.isEmpty())
						map.put(from, result);
					from = i + sepLen;
					i = from - 1;
					break;
				}
			}
		}

		if (from < length) map.put(from, string.substring(from));
		return map;
	}


	public static int getCommentIndex(String string) {
		char c;
		boolean insideString = false;
		boolean insideChar = false;
		boolean escape = true;
		for (int i = 0; i < string.length(); i++) {
			c = string.charAt(i);
			if (c == '"' && !escape) insideString = !insideString;
			if (c == '\'' && !escape) insideChar = !insideChar;
			if ((c == '#' || c == ';') && !insideString && !insideChar) return i;
			escape = !escape && c == '\\';
		}
		return -1;
	}

	public static String removeComments(String string) {
		char c;
		boolean insideString = false;
		boolean insideChar = false;
		boolean escape = true;
		for (int i = 0; i < string.length(); i++) {
			c = string.charAt(i);
			if (c == '"' && !escape) insideString = !insideString;
			if (c == '\'' && !escape) insideChar = !insideChar;
			if ((c == '#' || c == ';') && !insideString && !insideChar) return string.substring(0, i);
			escape = !escape && c == '\\';
		}
		return string;
	}

	public static boolean isStringOrChar(String string) {
		return string.startsWith("\"") && string.endsWith("\"") ||
				string.startsWith("'") && string.endsWith("'");
	}

	public static int charCount(String string, char... chars) {
		char[] array = string.toCharArray();
		int amount = 0;
		for (char current : array) {
			for (char target : chars) {
				if (target == current) {
					amount++;
					break;
				}
			}
		}
		return amount;
	}


	public static String addExtraSpaces(String string) {
		StringBuilder builder = new StringBuilder(string);
		while (builder.length() < 10) builder.append(" ");
		return builder.toString();
	}

	public static String addZeros(String s, int to) {
		StringBuilder builder = new StringBuilder();
		int max = Math.max(0, to - s.length());

		for (int i = 0; i < max; i++) {
			builder.append("0");
		}

		return builder + s;
	}

}
