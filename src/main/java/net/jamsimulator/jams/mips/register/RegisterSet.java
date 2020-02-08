package net.jamsimulator.jams.mips.register;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a {@link Register} set. An instance of this class stores all
 * {@link Register}s used by a {@link net.jamsimulator.jams.mips.simulation.Simulation}.
 * <p>
 * Registers ProgramCounter, HighRegister and LowRegister are always present.
 */
public class RegisterSet {

	protected Set<Register> registers;
	protected Set<Register> coprocessor0Registers;
	protected Set<Register> coprocessor1Registers;

	protected Register programCounter, highRegister, lowRegister;

	/**
	 * Creates a new Register set using the general registers, the coprocessor 0 registers and the coprocessor 1 registers.
	 * If any of the {@link Set}s is null, the method will create a {@link HashSet} for the parameter.
	 * <p>
	 * Remember: ProgramCounter, High and Low registers are automatically created.
	 *
	 * @param registers             the general registers.
	 * @param coprocessor0Registers the coprocessor 0 registers.
	 * @param coprocessor1Registers the coprocessor 1 registers.
	 */
	public RegisterSet(Set<Register> registers, Set<Register> coprocessor0Registers, Set<Register> coprocessor1Registers) {
		this.registers = registers == null ? new HashSet<>() : registers;
		this.coprocessor0Registers = coprocessor0Registers == null ? new HashSet<>() : coprocessor0Registers;
		this.coprocessor1Registers = coprocessor1Registers == null ? new HashSet<>() : coprocessor1Registers;
		loadEssentialRegisters();
	}


	/**
	 * Returns the program counter {@link Register}.
	 *
	 * @return the program counter.
	 */
	public Register getProgramCounter() {
		return programCounter;
	}

	/**
	 * Returns the program high {@link Register}.
	 *
	 * @return the high {@link Register}.
	 */
	public Register getHighRegister() {
		return highRegister;
	}

	/**
	 * Returns the program low {@link Register}.
	 *
	 * @return the low {@link Register}.
	 */
	public Register getLowRegister() {
		return lowRegister;
	}

	/**
	 * Get the general {@link Register} whose name matches the given string, if present.
	 *
	 * @param name the name.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getRegister(String name) {
		return registers.stream().filter(target -> target.hasName(name)).findFirst();
	}

	/**
	 * Get the general {@link Register} whose identifier matches the given int, if present.
	 *
	 * @param identifier the identifier.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getRegister(int identifier) {
		return registers.stream().filter(target -> target.getIdentifier() == identifier).findFirst();
	}

	/**
	 * Get the coprocessor 0 {@link Register} whose name matches the given string, if present.
	 *
	 * @param name the name.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getCoprocessor0Register(String name) {
		return coprocessor0Registers.stream().filter(target -> target.hasName(name)).findFirst();
	}


	/**
	 * Get the coprocessor 0 {@link Register} whose identifier matches the given int, if present.
	 *
	 * @param identifier the identifier.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getCoprocessor0Register(int identifier) {
		return coprocessor0Registers.stream().filter(target -> target.getIdentifier() == identifier).findFirst();
	}


	/**
	 * Get the coprocessor 1 {@link Register} whose name matches the given string, if present.
	 *
	 * @param name the name.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getCoprocessor1Register(String name) {
		return coprocessor1Registers.stream().filter(target -> target.hasName(name)).findFirst();
	}

	/**
	 * Get the coprocessor 1 {@link Register} whose identifier matches the given int, if present.
	 *
	 * @param identifier the identifier.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getCoprocessor1Register(int identifier) {
		return coprocessor1Registers.stream().filter(target -> target.getIdentifier() == identifier).findFirst();
	}

	/**
	 * Creates a copy of this register set.
	 *
	 * @return the copy.
	 */
	public RegisterSet copy() {
		Set<Register> newRegisters = new HashSet<>();
		Set<Register> newCop0Registers = new HashSet<>();
		Set<Register> newCop1Registers = new HashSet<>();
		registers.forEach(target -> newRegisters.add(target.copy()));
		coprocessor0Registers.forEach(target -> newCop0Registers.add(target.copy()));
		coprocessor1Registers.forEach(target -> newCop1Registers.add(target.copy()));

		RegisterSet set = new RegisterSet(newRegisters, newCop0Registers, newCop1Registers);
		set.programCounter.setValue(programCounter.getValue());
		set.highRegister.setValue(highRegister.getValue());
		set.lowRegister.setValue(lowRegister.getValue());
		return set;
	}

	protected void loadEssentialRegisters() {
		programCounter = new Register(-1, 0x00400000, true, "pc");
		highRegister = new Register(-1, "hi");
		lowRegister = new Register(-1, "lo");
	}
}
