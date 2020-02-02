package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.RegisterSet;

public class ParameterMatcherUnsigned16BitRegisterShift implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, RegisterSet registerSet) {
		//Gets the number and the register
		if (value.length() < 5 || !value.contains("($") || !value.endsWith(")"))
			throw new ParameterParseException("Bad parameter format: " + value + ".");
		int parenthesisIndex = value.indexOf('(');
		//Parses
		String register = value.substring(parenthesisIndex + 1, value.length() - 1);
		ParameterParseResult result = ParameterType.REGISTER.parse(register, registerSet);
		return result.and(ParameterType.UNSIGNED_16_BIT.parse(value.substring(0, parenthesisIndex), registerSet));
	}

	@Override
	public boolean match(String value, RegisterSet registerSet) {
		//Gets the number and the register
		if (value.length() < 5 || !value.contains("($") || !value.endsWith(")")) return false;
		int parenthesisIndex = value.indexOf('(');
		String register = value.substring(parenthesisIndex + 1, value.length() - 1);
		//If the register is not valid, return false.
		if (!ParameterType.REGISTER.match(register, registerSet)) return false;
		//Checks the label
		return ParameterType.UNSIGNED_16_BIT.match(value.substring(0, parenthesisIndex), registerSet);
	}
}
