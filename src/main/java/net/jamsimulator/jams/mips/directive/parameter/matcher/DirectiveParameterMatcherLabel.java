package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.LabelUtils;

public class DirectiveParameterMatcherLabel implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return LabelUtils.isLabelLegal(value);
	}
}
