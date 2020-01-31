package net.jamsimulator.jams.mips.register;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a {@link Register}s' collection. An instance of this class stores all
 * {@link Register}s used by a {@link net.jamsimulator.jams.mips.simulation.Simulation}.
 * <p>
 * Registers ProgramCounter, HighRegister and LowRegister are always present.
 */
public class Registers {

	protected Set<Register> registers;
	protected Set<Register> coprocessor0Registers;
	protected Set<Register> coprocessor1Registers;

	protected Register programCounter, highRegister, lowRegister;

	/**
	 * Creates a new Registers using the general registers, the coprocessor 0 registers and the coprocessor 1 registers.
	 * If any of the {@link Set}s is null, the method will create a {@link HashSet} for the parameter.
	 * <p>
	 * Remember: ProgramCounter, High and Low registers are automatically created.
	 *
	 * @param registers             the general registers.
	 * @param coprocessor0Registers the coprocessor 0 registers.
	 * @param coprocessor1Registers the coprocessor 1 registers.
	 */
	public Registers(Set<Register> registers, Set<Register> coprocessor0Registers, Set<Register> coprocessor1Registers) {
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
	 * Get the coprocessor 0 {@link Register} whose name matches the given string, if present.
	 *
	 * @param name the name.
	 * @return the {@link Register}, if present.
	 */
	public Optional<Register> getCoprocessor0Register(String name) {
		return coprocessor0Registers.stream().filter(target -> target.hasName(name)).findFirst();
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

	protected void loadEssentialRegisters() {
		programCounter = new Register(0x00400000, true, "pc");
		highRegister = new Register("hi");
		lowRegister = new Register("lo");
	}
}
