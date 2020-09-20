package net.jamsimulator.jams.mips.simulation.pipelined.exception;

import net.jamsimulator.jams.mips.register.Register;

/**
 * Represents a Read After Write hazard.
 */
public class RAWHazardException extends RuntimeException {

	private final Register register;

	/**
	 * Creates the hazard.
	 *
	 * @param register the {@link Register} that caused the RAW hazard.
	 */
	public RAWHazardException(Register register) {
		super("RAW hazard at register " + register);
		this.register = register;
	}

	/**
	 * Returns the {@link Register} that caused the hazard.
	 *
	 * @return the {@link Register}.
	 */
	public Register getRegister() {
		return register;
	}
}
