package net.jamsimulator.jams.mips.directive.parameter;

import net.jamsimulator.jams.mips.directive.parameter.matcher.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum DirectiveParameterType {

	ANY(v -> true),
	STRING(new DirectiveParameterMatcherString()),
	BOOLEAN(new DirectiveParameterMatcherBoolean()),
	BYTE(new DirectiveParameterMatcherByte()),
	BYTE_OR_CHAR(new DirectiveParameterMatcherByteOrChar()),
	SHORT(new DirectiveParameterMatcherShort()),
	INT(new DirectiveParameterMatcherInt()),
	INT_OR_LABEL(new DirectiveParameterMatcherIntOrLabel()),
	POSITIVE_INT(new DirectiveParameterMatcherPositiveInt()),
	LONG(new DirectiveParameterMatcherLong()),
	FLOAT(new DirectiveParameterMatcherFloat()),
	DOUBLE(new DirectiveParameterMatcherDouble()),

	LABEL(new DirectiveParameterMatcherLabel()),
	NUMBER_2_BITS(new DirectiveParameterMatcher2BitsNumber());


	private final DirectiveParameterMatcher matcher;

	DirectiveParameterType(DirectiveParameterMatcher matcher) {
		this.matcher = matcher;
	}


	public boolean matches(String value) {
		return matcher.matches(value);
	}

	public static Collection<DirectiveParameterType> getAllCandidates(String value) {
		return Arrays.stream(values()).filter(target -> target.matches(value)).collect(Collectors.toSet());
	}
}
