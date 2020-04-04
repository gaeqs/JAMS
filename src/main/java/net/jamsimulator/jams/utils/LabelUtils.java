package net.jamsimulator.jams.utils;

import java.util.Arrays;
import java.util.List;

public class LabelUtils {

	private static List<String> illegalCharacters = Arrays.asList(",", "/", "\\", ";", "!", "|", "\"", "@",
			"·", "#", "~", "½", "&", "¬", "(", ")", "[", "]", "{", "}", "=", "'", "=", "?", "¿", "^", "*",
			"+", "´", "¨");

	public static boolean isLabelLegal(String label) {
		char firstChar = label.charAt(0);
		return illegalCharacters.stream().noneMatch(label::contains);
	}

}
