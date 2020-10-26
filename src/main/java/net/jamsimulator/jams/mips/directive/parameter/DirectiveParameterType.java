package net.jamsimulator.jams.mips.directive.parameter;

import net.jamsimulator.jams.mips.directive.parameter.matcher.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents all types of parameters a directive can have.
 * <p>
 * Each type have their {@link DirectiveParameterMatcher}, allowing to check whether a parameter is valid.
 */
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


	/**
	 * Returns whether the given parameter is valid for this directive parameter type.
	 *
	 * @param value the parameter.
	 * @return whether is valid.
	 */
	public boolean matches(String value) {
		return matcher.matches(value);
	}

	public boolean mayBeLabel() {
		return this == LABEL || this == INT_OR_LABEL;
	}

	/**
	 * Returns all directive parameter types that matches the given parameter.
	 *
	 * @param value the given parameter.
	 * @return the {@link java.util.Set} with all directives.
	 */
	public static Collection<DirectiveParameterType> getAllCandidates(String value) {
		return Arrays.stream(values()).filter(target -> target.matches(value)).collect(Collectors.toSet());
	}
}
