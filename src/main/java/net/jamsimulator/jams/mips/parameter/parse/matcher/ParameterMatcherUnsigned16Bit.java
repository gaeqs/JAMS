package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.NumericUtils;

public class ParameterMatcherUnsigned16Bit implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		try {
			int i = NumericUtils.decodeInteger(value);
			if (i < 0 || i >= 65535) throw new ParameterParseException("Number " + i + " out of bounds.");
			return new ParameterParseResult.Builder().immediate(i).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		try {
			int i = NumericUtils.decodeInteger(value);
			return i >= 0 && i < 65535;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		try {
			int i = NumericUtils.decodeInteger(value);
			return i >= 0 && i < 65535;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
