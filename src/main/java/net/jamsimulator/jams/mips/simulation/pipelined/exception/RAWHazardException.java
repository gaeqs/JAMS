package net.jamsimulator.jams.mips.simulation.pipelined.exception;

import net.jamsimulator.jams.mips.register.Register;

public class RAWHazardException extends RuntimeException {

	private Register register;

	public RAWHazardException(Register register) {
		super("RAW hazard at register " + register);
		this.register = register;
	}

	public Register getRegister() {
		return register;
	}
}
