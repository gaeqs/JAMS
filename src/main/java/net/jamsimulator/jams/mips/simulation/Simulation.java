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

package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.ByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.WordSetEvent;
import net.jamsimulator.jams.mips.register.Registers;

import java.util.Optional;

public abstract class Simulation<Arch extends Architecture> extends SimpleEventBroadcast {

	protected final Arch architecture;
	protected final InstructionSet instructionSet;

	protected final Registers registerSet;
	protected final Memory memory;

	protected int instructionStackBottom;

	public Simulation(Arch architecture, InstructionSet instructionSet, Registers registerSet, Memory memory, int instructionStackBottom) {
		this.architecture = architecture;
		this.instructionSet = instructionSet;
		this.registerSet = registerSet;
		this.memory = memory;
		this.instructionStackBottom = instructionStackBottom;
		memory.registerListeners(this, true);
	}

	public Arch getArchitecture() {
		return architecture;
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	public Registers getRegisterSet() {
		return registerSet;
	}

	public Memory getMemory() {
		return memory;
	}

	public int getInstructionStackBottom() {
		return instructionStackBottom;
	}

	public AssembledInstruction fetch(int pc) {
		int data = memory.getWord(pc);
		Optional<? extends BasicInstruction<?>> optional = instructionSet.getInstructionByInstructionCode(data);
		if (!optional.isPresent()) return null;
		BasicInstruction<?> instruction = optional.get();
		return instruction.assembleFromCode(data);
	}

	public abstract void nextStep();

	public abstract void executeAll();


	@Listener
	private void onMemoryChange(ByteSetEvent.After event) {
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = event.getAddress() >> 2 << 2;
		}
	}

	@Listener
	private void onMemoryChange(WordSetEvent.After event) {
		if (event.getMemorySection().getName().equals("Text") && instructionStackBottom < event.getAddress()) {
			instructionStackBottom = event.getAddress();
		}
	}

}
