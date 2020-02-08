package net.jamsimulator.jams.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

	public static List<String> multiSplitIgnoreInsideString(String string, String... separators) {
		List<String> list = new ArrayList<>();
		int length = string.length(), from = 0;

		boolean insideString = false;
		boolean escape = false;
		for (int i = 0; i < length; i++) {
			char c = string.charAt(i);
			if(c == '"' && !escape)
				insideString = !insideString;
			escape = !escape && c == '\\';

			if(insideString) continue;
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


	public static String removeComments(String string) {
		char c;
		boolean insideString = false;
		boolean escape = true;
		for (int i = 0; i < string.length(); i++) {
			c = string.charAt(i);
			if (c == '"' && !escape) insideString = !insideString;
			if (c == '#' && !insideString) return string.substring(0, i);
			escape = !escape && c == '\\';
		}
		return string;
	}
}
