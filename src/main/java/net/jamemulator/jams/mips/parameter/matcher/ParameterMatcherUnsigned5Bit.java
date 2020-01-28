package net.jamemulator.jams.mips.parameter.matcher;

import java.util.function.Predicate;

public class ParameterMatcherUnsigned5Bit implements Predicate<String> {

	@Override
	public boolean test(String s) {
		try {
			int i = Integer.parseInt(s);
			return i >= 0 && i < 32;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
