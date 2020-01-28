package net.jamemulator.jams.mips.parameter.matcher;

import net.jamemulator.jams.mips.parameter.ParameterType;

import java.util.function.Predicate;

public class ParameterMatcherSigned16BitRegisterShift implements Predicate<String> {

	@Override
	public boolean test(String s) {
		//Gets the number and the register
		if (s.length() < 5 || !s.contains("($") || !s.endsWith(")")) return false;
		int parenthesisIndex = s.indexOf('(');
		String register = s.substring(parenthesisIndex + 1, s.length() - 1);
		//If the register is not valid, return false.
		if (!ParameterType.REGISTER.match(register)) return false;
		//Checks the number
		return ParameterType.SIGNED_16_BIT.match(s.substring(0, parenthesisIndex));
	}
}
