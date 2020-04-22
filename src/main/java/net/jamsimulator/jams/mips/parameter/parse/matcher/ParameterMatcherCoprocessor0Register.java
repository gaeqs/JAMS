package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Optional;

public class ParameterMatcherCoprocessor0Register implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		try {
			Optional<Register> register = registerSet.getCoprocessor0Register(value.substring(1));
			if (!register.isPresent())
				throw new ParameterParseException("No register found");
			return new ParameterParseResult.Builder().register(register.get().getIdentifier()).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}


	@Override
	public boolean match(String value, Registers registerSet) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& registerSet.getValidRegistersStarts().contains(c)
				&& registerSet.getCoprocessor0Register(value.substring(1)).isPresent();
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& builder.getValidRegistersStarts().contains(c)
				&& builder.getCoprocessor0RegistersNames().contains(value.substring(1));
	}
}
