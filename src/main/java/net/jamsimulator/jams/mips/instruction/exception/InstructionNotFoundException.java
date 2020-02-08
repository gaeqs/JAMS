package net.jamsimulator.jams.mips.instruction.exception;

public class InstructionNotFoundException extends RuntimeException {


	public InstructionNotFoundException() {
	}

	public InstructionNotFoundException(String message) {
		super(message);
	}

	public InstructionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstructionNotFoundException(Throwable cause) {
		super(cause);
	}

	public InstructionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
