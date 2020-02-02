package net.jamsimulator.jams.mips.parameter.parse.exception;

/**
 * This exception is thrown by {@link net.jamsimulator.jams.mips.parameter.parse.matcher.ParameterMatcher} when
 * it tries to parse a parameter.
 */
public class ParameterParseException extends RuntimeException {


	public ParameterParseException() {
	}

	public ParameterParseException(String message) {
		super(message);
	}

	public ParameterParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParameterParseException(Throwable cause) {
		super(cause);
	}

	public ParameterParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
