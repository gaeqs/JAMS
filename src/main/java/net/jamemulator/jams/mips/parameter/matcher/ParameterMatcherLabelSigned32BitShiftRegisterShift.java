package net.jamemulator.jams.mips.parameter.matcher;

import net.jamemulator.jams.mips.parameter.ParameterType;

import java.util.function.Predicate;

public class ParameterMatcherLabelSigned32BitShiftRegisterShift implements Predicate<String> {

	@Override
	public boolean test(String s) {
		int plusIndex = s.indexOf('+');
		if (plusIndex == -1) return false;
		//Checks the label and the number
		String label = s.substring(0, plusIndex);
		return ParameterType.LABEL.match(label) && ParameterType.SIGNED_32_BIT_REGISTER_SHIFT
				.match(s.substring(plusIndex + 1));
	}
}
