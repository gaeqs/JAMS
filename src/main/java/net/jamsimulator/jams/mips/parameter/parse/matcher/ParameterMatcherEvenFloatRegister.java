package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.Optional;

public class ParameterMatcherEvenFloatRegister implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		try {
			Optional<Register> register = registerSet.getCoprocessor1Register(value.substring(1));
			if (!register.isPresent())
				throw new ParameterParseException("No register found");
			if ((register.get().getIdentifier() & 1) != 0) {
				throw new ParameterParseException("Found register is not even.");
			}
			return new ParameterParseResult.Builder().register(register.get().getIdentifier()).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		if (value.length() < 2) return false;
		Optional<Register> register = registerSet.getCoprocessor1Register(value.substring(1));
		return register.isPresent() && (register.get().getIdentifier() & 1) == 0;
	}
}
