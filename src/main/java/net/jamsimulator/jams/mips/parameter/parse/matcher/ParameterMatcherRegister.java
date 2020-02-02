package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ParameterMatcherRegister implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		try {
			Optional<Register> register = registerSet.getRegister(value.substring(1));
			if (!register.isPresent())
				throw new ParameterParseException("No register found");
			return new ParameterParseResult.Builder().register(register.get().getIdentifier()).build();
		} catch (Exception ex) {
			throw new ParameterParseException("Error while parsing parameter " + value + ".", ex);
		}
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		return value.length() >= 2 && registerSet.getRegister(value.substring(1)).isPresent();
	}
}
