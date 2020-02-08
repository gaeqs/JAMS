package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.utils.LabelUtils;

public class ParameterMatcherLabel implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		if (!match(value, registerSet))
			throw new ParameterParseException("Bad parameter format: " + value + ".");
		return new ParameterParseResult.Builder().label(value).build();
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		return LabelUtils.isLabelLegal(value);
	}
}
