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

package net.jamsimulator.jams.mips.simulation.singlecycle;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.Log;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.change.*;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.LinkedList;

public class SingleCycleSimulation extends Simulation<SingleCycleArchitecture> {

	private Log log;

	private final LinkedList<StepChanges<SingleCycleArchitecture>> changes;
	private StepChanges<SingleCycleArchitecture> currentStepChanges;

	public SingleCycleSimulation(SingleCycleArchitecture architecture, InstructionSet instructionSet, Registers registerSet, Memory memory, int instructionStackBottom) {
		super(architecture, instructionSet, registerSet, memory, instructionStackBottom);
		changes = new LinkedList<>();
	}

	public void setLog(Log log) {
		this.log = log;
	}

	@Override
	public synchronized void nextStep() {
		currentStepChanges = new StepChanges<>();
		int pc = registers.getProgramCounter().getValue();

		if (pc > instructionStackBottom) {
			currentStepChanges = null;
			throw new InstructionNotFoundException("Dropped off bottom.");
		}

		//Fetch and Decode
		registers.getProgramCounter().setValue(pc + 4);
		AssembledInstruction instruction = fetch(pc);

		if (instruction == null) {
			int code = memory.getWord(pc);
			currentStepChanges = null;
			throw new InstructionNotFoundException("Couldn't decode instruction 0x" +
					StringUtils.addZeros(Integer.toHexString(code), 8) + ". (" + StringUtils.addZeros(Integer.toBinaryString(code), 32) + ")");
		}

		sendInstructionLog(instruction, pc);

		//Execute, Memory and Write

		SingleCycleExecution<?> execution = (SingleCycleExecution<?>)
				instruction.getBasicOrigin().generateExecution(this, instruction).orElse(null);

		//Send before event
		SingleCycleInstructionExecutionEvent.Before before = callEvent(
				new SingleCycleInstructionExecutionEvent.Before(this, pc, instruction, execution));
		if (before.isCancelled()) return;

		//Gets the modifies execution. This may be null.
		execution = before.getExecution().orElse(null);

		if (execution == null) {
			throw new InstructionNotFoundException("Couldn't decode instruction " +
					StringUtils.addZeros(Integer.toHexString(instruction.getCode()), 8) + ".");
		}

		execution.execute();

		callEvent(new SingleCycleInstructionExecutionEvent.After(this, pc, instruction, execution));

		changes.add(currentStepChanges);
		currentStepChanges = null;
	}

	@Override
	public synchronized void reset() {
		super.reset();
		changes.clear();
	}

	@Override
	public synchronized void executeAll() {
		while (registers.getProgramCounter().getValue() <= instructionStackBottom) {
			nextStep();
		}
	}

	@Override
	public synchronized void undoLastStep() {
		if (changes.isEmpty()) return;
		changes.removeLast().restore(this);
	}


	private void sendInstructionLog(AssembledInstruction instruction, int pc) {
		String address = "0x" + StringUtils.addZeros(Integer.toHexString(pc), 8);
		String opCode = StringUtils.addZeros(Integer.toBinaryString(instruction.getOperationCode()), 6);
		String mnemonic = instruction.getBasicOrigin().getMnemonic();
		String code = "0x" + StringUtils.addZeros(Integer.toHexString(instruction.getCode()), 8);

		if (log == null) {
			System.out.println(address + "\t" + opCode + "\t" + mnemonic + " \t" + code);
		} else {
			log.println(address + "\t" + opCode + "\t" + mnemonic + " \t" + code);
		}
	}

	//region change listeners

	@Listener
	private synchronized void onMemoryChange(MemoryWordSetEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryWord(event.getAddress(), event.getOldValue()));
	}

	@Listener
	private synchronized void onMemoryChange(MemoryByteSetEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryByte(event.getAddress(), event.getOldValue()));
	}

	@Listener
	private synchronized void onRegisterChange(RegisterChangeValueEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeRegister(event.getRegister(), event.getOldValue()));
	}

	@Listener
	private synchronized void onEndiannessChange(MemoryEndiannessChange.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryEndianness(!event.isNewEndiannessBigEndian()));
	}

	//endregion

}
