package net.jamsimulator.jams.mips.parameter.parse.exception;

import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

/**
 * This exception is thrown by a {@link ParameterParseResult}'s constructor when no register, label or offset is given.
 */
public class BadParameterParseResultException extends RuntimeException {


	public BadParameterParseResultException() {
	}

	public BadParameterParseResultException(String message) {
		super(message);
	}

	public BadParameterParseResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadParameterParseResultException(Throwable cause) {
		super(cause);
	}

	public BadParameterParseResultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
