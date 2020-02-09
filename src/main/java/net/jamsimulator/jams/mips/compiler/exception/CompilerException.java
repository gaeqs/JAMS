package net.jamsimulator.jams.mips.compiler.exception;

/**
 * Represents a compiler exception.
 */
public class CompilerException extends RuntimeException {

	public CompilerException() {
	}

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilerException(int line, String message) {
		super("Error at line " + (line + 1) + ": " + message);
	}

	public CompilerException(int line, String message, Throwable cause) {
		super("Error at line " + (line + 1) + ": " + message, cause);
	}

	public CompilerException(Throwable cause) {
		super(cause);
	}

	public CompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
