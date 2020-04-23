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
import net.jamsimulator.jams.mips.instruction.exception.RuntimeInstructionException;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

public abstract class InstructionExecution<Arch extends Architecture, Inst extends AssembledInstruction> {

	protected final Simulation<Arch> simulation;
	protected final Inst instruction;

	public InstructionExecution(Simulation<Arch> simulation, Inst instruction) {
		Validate.notNull(simulation, "Simulation cannot be null!");
		Validate.notNull(instruction, "Instruction cannot be null!");
		this.simulation = simulation;
		this.instruction = instruction;
	}

	public Simulation<Arch> getSimulation() {
		return simulation;
	}

	public Inst getInstruction() {
		return instruction;
	}

	/**
	 * Throws a {@link RuntimeInstructionException} with the given message.
	 *
	 * @param message the message.
	 */
	protected void error(String message) {
		throw new RuntimeInstructionException(message);
	}


	/**
	 * Throws a {@link RuntimeInstructionException} with the given message.
	 *
	 * @param message the message.
	 * @param ex      the cause.
	 */
	protected void error(String message, Exception ex) {
		throw new RuntimeInstructionException(message, ex);
	}
}
