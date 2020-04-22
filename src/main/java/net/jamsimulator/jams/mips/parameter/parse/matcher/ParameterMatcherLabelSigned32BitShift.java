package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.parameter.parse.exception.ParameterParseException;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

public class ParameterMatcherLabelSigned32BitShift implements ParameterMatcher {

	@Override
	public ParameterParseResult parse(String value, Registers registerSet) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) throw new ParameterParseException("Bad parameter format: " + value + ".");
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		ParameterParseResult result = ParameterType.LABEL.parse(label, registerSet);
		return result.and(ParameterType.SIGNED_32_BIT.parse(value.substring(plusIndex + 1), registerSet));
	}

	@Override
	public boolean match(String value, Registers registerSet) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) return false;
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		return ParameterType.LABEL.match(label, registerSet) && ParameterType.SIGNED_32_BIT
				.match(value.substring(plusIndex + 1), registerSet);
	}

	@Override
	public boolean match(String value, RegistersBuilder builder) {
		int plusIndex = value.indexOf('+');
		if (plusIndex == -1) return false;
		//Checks the label and the number
		String label = value.substring(0, plusIndex);
		return ParameterType.LABEL.match(label, builder) && ParameterType.SIGNED_32_BIT
				.match(value.substring(plusIndex + 1), builder);
	}
}
