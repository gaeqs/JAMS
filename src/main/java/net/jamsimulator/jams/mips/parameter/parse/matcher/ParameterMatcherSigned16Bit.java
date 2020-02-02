package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;

public class ParameterMatcherSigned16Bit implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		try {
			int i = Integer.parseInt(value);
			if (i < -32768 || i >= 32768) throw new ParameterParseException("Number " + i + " out of bounds.");
			return new ParameterParseResult.Builder().immediate(i).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		try {
			int i = Integer.parseInt(value);
			return i >= -32768 && i < 32768;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
