package net.jamsimulator.jams.mips.directive.parameter.matcher;

public class DirectiveParameterMatcherString implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"';
	}
}
