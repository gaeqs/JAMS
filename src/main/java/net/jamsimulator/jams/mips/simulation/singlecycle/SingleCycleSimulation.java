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
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.event.MemoryAllocateMemoryEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;
import net.jamsimulator.jams.mips.simulation.change.*;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;
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

	public static final int MAX_CHANGES = 10000;

	private final LinkedList<StepChanges<SingleCycleArchitecture>> changes;
	private StepChanges<SingleCycleArchitecture> currentStepChanges;

	private int instructions;
	private long start;

	/**
	 * Creates the single-cycle simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 */
	public SingleCycleSimulation(SingleCycleArchitecture architecture, InstructionSet instructionSet, Registers registers, Memory memory, int instructionStackBottom, SimulationData data) {
		super(architecture, instructionSet, registers, memory, instructionStackBottom, data);
		changes = data.isUndoEnabled() ? new LinkedList<>() : null;
	}

	@Override
	public void nextStep() {
		if (finished || running) return;
		running = true;
		interrupted = false;

		memory.enableEventCalls(data.canCallEvents());
		registers.enableEventCalls(data.canCallEvents());

		thread = new Thread(() -> {
			runStep(true);
			synchronized (finishedRunningLock) {
				running = false;

				memory.enableEventCalls(true);
				registers.enableEventCalls(true);

				finishedRunningLock.notifyAll();
				callEvent(new SimulationStopEvent(this));
			}
		});
		callEvent(new SimulationStartEvent(this));
		thread.start();
	}

	@Override
	public void reset() {
		super.reset();
		if (changes != null) {
			changes.clear();
		}
	}

	@Override
	public void executeAll() {
		if (finished || running) return;
		running = true;
		interrupted = false;

		memory.enableEventCalls(data.canCallEvents());
		registers.enableEventCalls(data.canCallEvents());

		thread = new Thread(() -> {
			instructions = 0;
			start = System.nanoTime();

			boolean first = true;
			while (!finished && !checkInterrupted()) {
				runStep(first);
				first = false;
				instructions++;
			}

			long millis = (System.nanoTime() - start) / 1000000;

			if (getConsole() != null) {
				getConsole().println();
				getConsole().printInfoLn(instructions + " instructions executed in " + millis + " millis.");

				int performance = (int) (instructions / (((double) millis) / 1000));
				getConsole().printInfoLn(performance + " inst/s");
				getConsole().println();
				if (memory instanceof Cache) {
					getConsole().printInfoLn(((Cache) memory).getStats());
					getConsole().println();
				}
			}

			synchronized (finishedRunningLock) {
				running = false;
				memory.enableEventCalls(true);
				registers.enableEventCalls(true);
				finishedRunningLock.notifyAll();
				callEvent(new SimulationStopEvent(this));
			}
		});
		callEvent(new SimulationStartEvent(this));
		thread.start();
	}

	@Override
	public boolean undoLastStep() {
		if (!data.isUndoEnabled()) return false;

		if (callEvent(new SimulationUndoStepEvent.Before(this)).isCancelled()) return false;

		stop();
		waitForExecutionFinish();

		if (changes.isEmpty()) return false;
		finished = false;
		changes.removeLast().restore(this);
		currentCycle--;

		callEvent(new SimulationUndoStepEvent.After(this));

		return true;
	}

	private void runStep(boolean first) {
		if (finished) return;
		int pc = registers.getProgramCounter().getValue();

		if (breakpoints.contains(pc) && !first) {
			interrupt();
			return;
		}

		if (data.isUndoEnabled()) {
			currentStepChanges = new StepChanges<>();
		}

		currentCycle++;


		//Fetch and Decode
		registers.getProgramCounter().setValue(pc + 4);
		SingleCycleExecution<?> execution = (SingleCycleExecution<?>) fetch(pc);

		if (execution == null) {
			int code = memory.getWord(pc, false, true);
			currentStepChanges = null;
			throw new InstructionNotFoundException("Couldn't decode instruction 0x" +
					StringUtils.addZeros(Integer.toHexString(code), 8) +
					"at 0x" + StringUtils.addZeros(Integer.toHexString(pc), 8) +
					". (" + StringUtils.addZeros(Integer.toBinaryString(code), 32) + ")");
		}

		//Send before event
		if (data.canCallEvents()) {
			SingleCycleInstructionExecutionEvent.Before before =
					callEvent(new SingleCycleInstructionExecutionEvent.Before(this, pc, execution.getInstruction(), execution));
			if (before.isCancelled()) return;

			//Gets the modifies execution. This may be null.
			execution = before.getExecution().orElse(null);
		}

		if (execution == null) {
			throw new InstructionNotFoundException("Couldn't decode instruction " +
					StringUtils.addZeros(Integer.toHexString(fetch(pc).getInstruction().getCode()), 8) + ".");
		}

		execution.execute();

		//Check thread, if interrupted, return to the previous cycle.
		if (checkInterrupted()) {
			currentStepChanges = null;
			registers.getProgramCounter().setValue(pc);
			return;
		}

		if (data.canCallEvents()) {
			callEvent(new SingleCycleInstructionExecutionEvent.After(this, pc, execution.getInstruction(), execution));

			if (data.isUndoEnabled()) {
				changes.add(currentStepChanges);
				if (changes.size() > MAX_CHANGES) changes.removeFirst();
				currentStepChanges = null;
			}
		}


		if (registers.getProgramCounter().getValue() > instructionStackBottom && !finished) {
			finished = true;
			if (getConsole() != null) {
				getConsole().println();
				getConsole().printWarningLn("Execution finished. Dropped off bottom.");
				getConsole().println();
				callEvent(new SimulationFinishedEvent(this));
			}
		}
	}

	//region change listeners

	@Listener
	private void onMemoryChange(MemoryWordSetEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryWord(event.getAddress(), event.getOldValue()));
	}

	@Listener
	private void onMemoryChange(MemoryByteSetEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryByte(event.getAddress(), event.getOldValue()));
	}

	@Listener
	private void onRegisterChange(RegisterChangeValueEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeRegister(event.getRegister(), event.getOldValue()));
	}

	@Listener
	private void onEndiannessChange(MemoryEndiannessChange.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeMemoryEndianness(!event.isNewEndiannessBigEndian()));
	}

	@Listener
	private void onReserve(MemoryAllocateMemoryEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeAllocatedMemory(event.getOldCurrentData()));
	}

	@Listener
	private void onFileOpen(SimulationFileOpenEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeFileOpen(event.getSimulationFile().getId()));
	}

	@Listener
	private void onFileClose(SimulationFileCloseEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeFileClose(event.getFile()));
	}

	@Listener
	private void onFileWrite(SimulationFileWriteEvent.After event) {
		if (currentStepChanges == null) return;
		currentStepChanges.addChange(new SimulationChangeFileWrite(event.getFile(), event.getData().length));
	}

	//endregion

}
