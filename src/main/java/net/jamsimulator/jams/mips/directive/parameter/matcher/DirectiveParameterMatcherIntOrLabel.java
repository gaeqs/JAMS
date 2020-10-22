package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherIntOrLabel implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isInteger(value);
	}
}
