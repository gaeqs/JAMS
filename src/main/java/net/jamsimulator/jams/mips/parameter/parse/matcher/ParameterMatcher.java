package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

public interface ParameterMatcher {

	ParameterParseResult parse(String value, Registers registerSet);

	boolean match(String value, Registers registerSet);


	boolean match(String value, RegistersBuilder builder);

}
