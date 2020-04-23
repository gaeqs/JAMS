/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a {@link Register} set. An instance of this class stores all
 * {@link Register}s used by a {@link net.jamsimulator.jams.mips.simulation.Simulation}.
 * <p>
 * Registers ProgramCounter, HighRegister and LowRegister are always present.
 */
public class Registers {

	protected final Set<Character> validRegistersStarts;

	protected Set<Register> registers;
	protected Set<Register> coprocessor0Registers;
	protected Set<Register> coprocessor1Registers;

	protected Register programCounter;

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
	public Registers(Set<Character> validRegistersStarts, Set<Register> registers,
					 Set<Register> coprocessor0Registers, Set<Register> coprocessor1Registers) {
		Validate.notNull(validRegistersStarts, "Valid registers starts cannot be null!");
		this.validRegistersStarts = validRegistersStarts;
		this.registers = registers == null ? new HashSet<>() : registers;
		this.coprocessor0Registers = coprocessor0Registers == null ? new HashSet<>() : coprocessor0Registers;
		this.coprocessor1Registers = coprocessor1Registers == null ? new HashSet<>() : coprocessor1Registers;
		loadEssentialRegisters();
	}

	/**
	 * Returns the {@link Set} of valid registers' starts.
	 * This is is unmodifiable.
	 * <p>
	 * Every register should start using any of these characters.
	 *
	 * @return the {@link Set}
	 */
	public Set<Character> getValidRegistersStarts() {
		return Collections.unmodifiableSet(validRegistersStarts);
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
	 * Returns an unmodifiable {@link Set} with all registers inside this set.
	 *
	 * @return the {@link Set}.
	 */
	public Set<Register> getRegisters() {
		Set<Register> registers = new HashSet<>(this.registers);
		registers.add(programCounter);
		registers.addAll(coprocessor0Registers);
		registers.addAll(coprocessor1Registers);
		return Collections.unmodifiableSet(registers);
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
	public Registers copy() {
		Set<Register> newRegisters = new HashSet<>();
		Set<Register> newCop0Registers = new HashSet<>();
		Set<Register> newCop1Registers = new HashSet<>();
		registers.forEach(target -> newRegisters.add(target.copy()));
		coprocessor0Registers.forEach(target -> newCop0Registers.add(target.copy()));
		coprocessor1Registers.forEach(target -> newCop1Registers.add(target.copy()));

		Registers set = new Registers(validRegistersStarts, newRegisters, newCop0Registers, newCop1Registers);
		set.programCounter.setValue(programCounter.getValue());
		return set;
	}

	protected void loadEssentialRegisters() {
		programCounter = new Register(-1, 0x00400000, true, "pc");
	}
}
