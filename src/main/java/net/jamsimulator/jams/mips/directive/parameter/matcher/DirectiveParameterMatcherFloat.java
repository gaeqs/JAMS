package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherFloat implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isFloat(value);
	}
}
