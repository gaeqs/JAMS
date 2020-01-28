package net.jamemulator.jams.mips.parameter.matcher;

import java.util.List;
import java.util.function.Predicate;

public class ParameterMatcherLabel implements Predicate<String> {

	private static List<String> illegalCharacters = List.of(",", "/", "\\", ";", "!", "|", "\"", "@",
			"·", "#", "~", "½", "&", "¬", "(", ")", "[", "]", "{", "}", "=", "'", "=", "?", "¿", "^", "*",
			"+", "´", "¨");

	@Override
	public boolean test(String s) {
		char firstChar = s.charAt(0);
		if (!(firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A' && firstChar <= 'Z')) return false;
		return illegalCharacters.stream().noneMatch(s::contains);
	}
}
