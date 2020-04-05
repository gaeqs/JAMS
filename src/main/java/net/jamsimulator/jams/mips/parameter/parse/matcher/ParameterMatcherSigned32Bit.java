package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.utils.NumericUtils;

public class ParameterMatcherSigned32Bit implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		try {
			return new ParameterParseResult.Builder().immediate(NumericUtils.decodeInteger(value)).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		try {
			NumericUtils.decodeInteger(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
