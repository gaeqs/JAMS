package net.jamsimulator.jams.mips.assembler.exception;

/**
 * Represents a assembler exception.
 */
public class AssemblerException extends RuntimeException {

	public AssemblerException() {
	}

	public AssemblerException(String message) {
		super(message);
	}

	public AssemblerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssemblerException(int line, String message) {
		super("Error at line " + (line + 1) + ": " + message);
	}

	public AssemblerException(int line, String message, Throwable cause) {
		super("Error at line " + (line + 1) + ": " + message, cause);
	}

	public AssemblerException(Throwable cause) {
		super(cause);
	}

	public AssemblerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
