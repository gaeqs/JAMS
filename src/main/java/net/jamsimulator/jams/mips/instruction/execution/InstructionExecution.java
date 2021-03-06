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

package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.RuntimeInstructionException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

public abstract class InstructionExecution<Arch extends Architecture, Inst extends AssembledInstruction> {

	protected final Simulation<? extends Arch> simulation;
	protected final Registers registers;
	protected final Inst instruction;

	protected final int address;

	public InstructionExecution(Simulation<? extends Arch> simulation, Inst instruction, int address) {
		Validate.notNull(simulation, "Simulation cannot be null!");
		Validate.notNull(instruction, "Instruction cannot be null!");
		this.simulation = simulation;
		this.registers = simulation.getRegisters();
		this.instruction = instruction;
		this.address = address;
	}

	/**
	 * Returns the {@link Simulation} executing this instruction.
	 *
	 * @return the {@link Simulation}.
	 */
	public Simulation<? extends Arch> getSimulation() {
		return simulation;
	}

	/**
	 * Returns the execution instruction.
	 *
	 * @return the instruction.
	 */
	public Inst getInstruction() {
		return instruction;
	}

	/**
	 * Returns the address of the executing instruction.
	 *
	 * @return the address.
	 */
	public int getAddress() {
		return address;
	}

	/**
	 * Throws a {@link RuntimeInstructionException} with the given cause.
	 *
	 * @param cause the cause.
	 */
	protected void error(InterruptCause cause) {
		throw new RuntimeInstructionException(cause);
	}

	protected void evenFloatRegisterException() {
		throw new RuntimeInstructionException(InterruptCause.FLOATING_POINT_EXCEPTION);
	}

	/**
	 * Throws a {@link RuntimeInstructionException} with the given cause.
	 *
	 * @param cause the cause.
	 * @param ex    the cause.
	 */
	protected void error(InterruptCause cause, Exception ex) {
		throw new RuntimeInstructionException(cause, ex);
	}

	/**
	 * Returns the program counter of the simulation.
	 *
	 * @return the pc.
	 */
	protected Register pc() {
		return simulation.getRegisters().getProgramCounter();
	}

	/**
	 * Returns the register that matches the given identifier.
	 *
	 * @param identifier the identifier.
	 * @return the register.
	 * @throws RuntimeInstructionException if the register is not present.
	 */
	protected Register register(int identifier) {
		return registers.getRegisterUnchecked(identifier);
	}

	/**
	 * Returns the COP0 register that matches the given identifier.
	 *
	 * @param identifier the identifier.
	 * @return the register.
	 * @throws RuntimeInstructionException if the register is not present.
	 */
	protected Register registerCop0(int identifier) {
		return registers.getCoprocessor0RegisterUnchecked(identifier, 0);
	}

	/**
	 * Returns the COP0 register that matches the given identifier.
	 *
	 * @param identifier the identifier.
	 * @param sel        the sub-index.
	 * @return the register.
	 * @throws RuntimeInstructionException if the register is not present.
	 */
	protected Register registerCop0(int identifier, int sel) {
		return registers.getCoprocessor0RegisterUnchecked(identifier, sel);
	}

	/**
	 * Returns the COP1 register that matches the given identifier.
	 *
	 * @param identifier the identifier.
	 * @return the register.
	 * @throws RuntimeInstructionException if the register is not present.
	 */
	protected Register registerCop1(int identifier) {
		return registers.getCoprocessor1RegisterUnchecked(identifier);
	}
}
