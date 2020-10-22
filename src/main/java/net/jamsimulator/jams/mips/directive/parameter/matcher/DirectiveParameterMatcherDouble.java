package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherDouble implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isDouble(value);
	}
}
