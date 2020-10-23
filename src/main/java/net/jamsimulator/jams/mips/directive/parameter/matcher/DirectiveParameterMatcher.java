package net.jamsimulator.jams.mips.directive.parameter.matcher;

/**
 * Represents the matcher for a {@link net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType}.
 * This interface allows types to check whether a parameter is valid.
 */
public interface DirectiveParameterMatcher {

	/**
	 * Returns whether the given parameter is valid.
	 *
	 * @param value the parameter.
	 * @return whether the parameter is valid.
	 */
	boolean matches(String value);

}
