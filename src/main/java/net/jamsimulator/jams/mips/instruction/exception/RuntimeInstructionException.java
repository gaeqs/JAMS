package net.jamsimulator.jams.mips.instruction.exception;

public class RuntimeInstructionException extends RuntimeException {

	public RuntimeInstructionException() {
	}

	public RuntimeInstructionException(String message) {
		super(message);
	}

	public RuntimeInstructionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeInstructionException(Throwable cause) {
		super(cause);
	}

	public RuntimeInstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
