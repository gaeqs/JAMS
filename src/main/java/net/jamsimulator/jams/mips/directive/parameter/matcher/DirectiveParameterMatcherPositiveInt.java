package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherPositiveInt implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.decodeIntegerSafe(value).map(target -> target > 0).orElse(false);
	}
}
