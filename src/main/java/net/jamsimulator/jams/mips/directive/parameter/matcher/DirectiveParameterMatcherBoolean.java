package net.jamsimulator.jams.mips.directive.parameter.matcher;

public class DirectiveParameterMatcherBoolean implements DirectiveParameterMatcher {


	@Override
	public boolean matches(String value) {
		return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
	}
}
