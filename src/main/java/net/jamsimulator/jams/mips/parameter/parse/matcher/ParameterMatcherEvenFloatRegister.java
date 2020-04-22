package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ParameterMatcherEvenFloatRegister implements ParameterMatcher {

	private static final List<String> ENDS = Arrays.asList("2", "4", "6", "8", "0");

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
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
	public boolean match(String value, Registers registerSet) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& registerSet.getValidRegistersStarts().contains(c)
				&& ENDS.contains(value.substring(value.length() - 1))
				&& registerSet.getCoprocessor1Register(value.substring(1)).isPresent();
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		if (value.isEmpty()) return false;
		char c = value.charAt(0);
		return value.length() >= 2
				&& builder.getValidRegistersStarts().contains(c)
				&& ENDS.contains(value.substring(value.length() - 1))
				&& builder.getCoprocessor1RegistersNames().contains(value.substring(1));
	}
}
