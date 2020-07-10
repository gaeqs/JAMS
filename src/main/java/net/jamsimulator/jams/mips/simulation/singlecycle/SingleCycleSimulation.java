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
import net.jamsimulator.jams.gui.util.log.Console;
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
import net.jamsimulator.jams.mips.simulation.event.SimulationUnlockEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.LinkedList;

/**
 * Represents the execution of a set of instruction inside a MIPS32 single-cycle computer.
 * <p>
 * This architecture executes one instruction per cycle, starting and finishing the
 * execution of an instruction on the same cycle. This makes this architecture slow,
 * having high seconds per cycle.
 * <p>
 * This is also the easiest architecture to implement.
 *
 * @see SingleCycleArchitecture
 */
public class SingleCycleSimulation extends Simulation<SingleCycleArchitecture> {

	private final LinkedList<StepChanges<SingleCycleArchitecture>> changes;
	private StepChanges<SingleCycleArchitecture> currentStepChanges;

	/**
	 * Creates the single-cycle simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 */
	public SingleCycleSimulation(SingleCycleArchitecture architecture, InstructionSet instructionSet,
								 Registers registers, Memory memory, SimulationSyscallExecutions syscallExecutions, Console log, int instructionStackBottom) {
		super(architecture, instructionSet, registers, memory, syscallExecutions, log, instructionStackBottom);
		changes = new LinkedList<>();
	}

	@Override
	public synchronized void nextStep() {
		if (locked || finished) return;
		currentStepChanges = new StepChanges<>();
		int pc = registers.getProgramCounter().getValue();

		//Fetch and Decode
		registers.getProgramCounter().setValue(pc + 4);
		AssembledInstruction instruction = fetch(pc);

		if (instruction == null) {
			int code = memory.getWord(pc);
			currentStepChanges = null;
			throw new InstructionNotFoundException("Couldn't decode instruction 0x" +
					StringUtils.addZeros(Integer.toHexString(code), 8) + ". (" + StringUtils.addZeros(Integer.toBinaryString(code), 32) + ")");
		}

		//Execute, Memory and Write

		SingleCycleExecution<?> execution = (SingleCycleExecution<?>)
				instruction.getBasicOrigin().generateExecution(this, instruction).orElse(null);

		//Send before event
		SingleCycleInstructionExecutionEvent.Before before = callEvent(new SingleCycleInstructionExecutionEvent.Before(this, pc, instruction, execution));
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

		if (pc + 4 > instructionStackBottom) {
			finished = true;
			getConsole().println();
			getConsole().printWarningLn("Execution finished. Dropped off bottom.");
		}
	}

	@Override
	public synchronized void reset() {
		super.reset();
		changes.clear();
	}

	@Override
	public synchronized void executeAll() {
		if (locked || finished) return;
		shouldResumeExecutionOnUnlock = true;
		while (!finished) {
			nextStep();
			if (locked) return;
		}
		shouldResumeExecutionOnUnlock = false;
	}

	@Override
	public synchronized boolean undoLastStep() {
		if (changes.isEmpty()) return false;
		callEvent(new SimulationUnlockEvent(this));
		locked = false;
		finished = false;
		shouldResumeExecutionOnUnlock = false;
		changes.removeLast().restore(this);
		return true;
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
