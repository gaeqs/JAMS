package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.function.Predicate;

public class ParameterMatcherUnsigned5Bit implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		try {
			int i = Integer.parseInt(value);
			if (i < 0 || i >= 32) throw new ParameterParseException("Number " + i + " out of bounds.");
			return new ParameterParseResult.Builder().immediate(i).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		try {
			int i = Integer.parseInt(value);
			return i >= 0 && i < 32;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
