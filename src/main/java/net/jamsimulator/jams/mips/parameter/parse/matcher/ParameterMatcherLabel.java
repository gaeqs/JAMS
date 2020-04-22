package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.LabelUtils;

public class ParameterMatcherLabel implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		if (!match(value, registerSet))
			throw new ParameterParseException("Bad parameter format: " + value + ".");
		return new ParameterParseResult.Builder().label(value).build();
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		return LabelUtils.isLabelLegal(value);
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		return LabelUtils.isLabelLegal(value);
	}
}
