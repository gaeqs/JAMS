package net.jamsimulator.jams.mips.parameter.matcher;

import java.util.function.Predicate;

public class ParameterMatcherUnsigned16Bit implements Predicate<String> {

	@Override
	public boolean test(String s) {
		try {
			int i = Integer.parseInt(s);
			return i >= 0 && i < 65535;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
