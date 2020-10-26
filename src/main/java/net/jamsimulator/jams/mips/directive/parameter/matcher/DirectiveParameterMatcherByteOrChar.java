package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherByteOrChar implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isByte(value)
				|| value.length() > 1 && value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'';
	}
}
