package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.List;

public class ParameterMatcherLabel implements ParameterMatcher {

	private static List<String> illegalCharacters = List.of(",", "/", "\\", ";", "!", "|", "\"", "@",
			"·", "#", "~", "½", "&", "¬", "(", ")", "[", "]", "{", "}", "=", "'", "=", "?", "¿", "^", "*",
			"+", "´", "¨");

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		if (!match(value, registerSet))
			throw new ParameterParseException("Bad parameter format: " + value + ".");
		return new ParameterParseResult.Builder().label(value).build();
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		char firstChar = value.charAt(0);
		if (!(firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A' && firstChar <= 'Z')) return false;
		return illegalCharacters.stream().noneMatch(value::contains);
	}
}
