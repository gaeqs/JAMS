package net.jamsimulator.jams.mips.parameter.matcher;

import java.util.function.Predicate;

public class ParameterMatcherCoprocessor0Register implements Predicate<String> {

	@Override
	public boolean test(String s) {
		return s.equals("$8") || s.equals("$12") || s.equals("$13") || s.equals("t14");
	}
}
