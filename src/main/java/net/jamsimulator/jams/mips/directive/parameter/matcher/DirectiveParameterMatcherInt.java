package net.jamsimulator.jams.mips.directive.parameter.matcher;

import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveParameterMatcherInt implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return NumericUtils.isInteger(value) || LabelUtils.isLabelLegal(value);
	}
}
