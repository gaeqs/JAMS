package net.jamsimulator.jams.mips.parameter.matcher;

import java.util.function.Predicate;

public class ParameterMatcherSigned32Bit implements Predicate<String> {

	@Override
	public boolean test(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
