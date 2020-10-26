package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherLong implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isLong(value);
	}
}
