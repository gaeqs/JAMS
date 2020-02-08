package net.jamsimulator.jams.utils;

import java.util.ArrayList;
import java.util.List;

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

}
