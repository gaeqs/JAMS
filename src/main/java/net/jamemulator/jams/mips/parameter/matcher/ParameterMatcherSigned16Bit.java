package net.jamemulator.jams.mips.parameter.matcher;

import java.util.function.Predicate;

public class ParameterMatcherSigned16Bit implements Predicate<String> {

	@Override
	public boolean test(String s) {
		try {
			int i = Integer.parseInt(s);
			return i >= -32768 && i < 32768;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
