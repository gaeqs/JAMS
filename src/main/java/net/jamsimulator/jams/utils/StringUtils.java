package net.jamsimulator.jams.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {

	public static List<String> multiSplitIgnoreInsideString(String string, String... separators) {
		List<String> list = new ArrayList<>();
		int length = string.length(), from = 0;

		boolean insideString = false;
		boolean insideChar = false;
		boolean escape = false;
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
}
