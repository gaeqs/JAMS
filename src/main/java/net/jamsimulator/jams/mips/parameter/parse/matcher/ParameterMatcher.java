package net.jamsimulator.jams.mips.parameter.parse.matcher;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.RegisterSet;

public interface ParameterMatcher {

	ParameterParseResult parse(String value, RegisterSet registerSet);

	boolean match(String value, RegisterSet registerSet);

}
