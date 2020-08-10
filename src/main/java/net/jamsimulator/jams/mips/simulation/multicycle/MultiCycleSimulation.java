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

package net.jamsimulator.jams.mips.simulation.multicycle;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.RuntimeAddressException;
import net.jamsimulator.jams.mips.interrupt.RuntimeInstructionException;
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
import net.jamsimulator.jams.mips.simulation.change.multicycle.MultiCycleSimulationChangeCurrentExecution;
import net.jamsimulator.jams.mips.simulation.change.multicycle.MultiCycleSimulationChangeStep;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.event.MultiCycleStepEvent;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.LinkedList;

/**
 * Represents the execution of a set of instruction inside a MIPS32 multi-cycle computer.
 * <p>
 * This architecture executes one instruction per 5 cycles. This makes this architecture slow, having to execute the instruction in several cycles.
 * <p>
 * This architecture is harder to implement than the single-cycle architecture.
 *
 * @see MultiCycleArchitecture
 */
public class MultiCycleSimulation extends Simulation<MultiCycleArchitecture> {

	public static final int MAX_CHANGES = 10000;

	private long executedInstructions;

	private final LinkedList<StepChanges<MultiCycleArchitecture>> changes;
	private StepChanges<MultiCycleArchitecture> currentStepChanges;

	private MultiCycleStep currentStep;
	private MultiCycleExecution<?> currentExecution;

	/**
	 * Creates the single-cycle simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 */
	public MultiCycleSimulation(MultiCycleArchitecture architecture, InstructionSet instructionSet, Registers registers, Memory memory, int instructionStackBottom, SimulationData data) {
		super(architecture, instructionSet, registers, memory, instructionStackBottom, data, true);
		executedInstructions = 0;
		changes = data.isUndoEnabled() ? new LinkedList<>() : null;
		currentStep = MultiCycleStep.FETCH;
	}

	/**
	 * Returns the current {@link MultiCycleStep} of this simulation.
	 *
	 * @return the current {@link MultiCycleStep}.
	 */
	public MultiCycleStep getCurrentStep() {
		return currentStep;
	}

	/**
	 * Forcibly changes the current {@link MultiCycleStep} of the simulation.
	 * <p>
	 * This should only be used to undo steps.
	 *
	 * @param step the new {@link MultiCycleStep}.
	 */
	public void forceStepChange(MultiCycleStep step) {
		currentStep = step;
	}

	/**
	 * Forcibly changes the current {@link MultiCycleExecution} of the simulation.
	 * <p>
	 * This should only be used to undo steps.
	 *
	 * @param execution the new {@link MultiCycleExecution}.
	 */
	public void forceCurrentExecutionChange(MultiCycleExecution<?> execution) {
		currentExecution = execution;
	}

	@Override
	public void reset() {
		super.reset();
		if (changes != null) {
			changes.clear();
		}
		executedInstructions = 0;
		currentStep = MultiCycleStep.FETCH;
	}

	@Override
	public void manageMIPSInterrupt(RuntimeInstructionException exception, int pc) {
		currentStep = MultiCycleStep.FETCH;
		super.manageMIPSInterrupt(exception, pc);
	}

	@Override
	public void executeAll() {
		if (finished || running) return;
		running = true;
		interrupted = false;

		memory.enableEventCalls(data.canCallEvents());
		registers.enableEventCalls(data.canCallEvents());

		thread = new Thread(() -> {
			long cycles = 0;
			long start = System.nanoTime();

			boolean first = true;
			try {
				while (!finished && !checkThreadInterrupted()) {
					runStep(first);
					first = false;
					cycles++;
				}
			} catch (Exception ex) {
				System.err.println("PC: 0x" + StringUtils.addZeros(Integer.toHexString(registers.getProgramCounter().getValue()), 8));
				ex.printStackTrace();
			}

			long millis = (System.nanoTime() - start) / 1000000;

			if (getConsole() != null) {
				getConsole().println();
				getConsole().printInfoLn(cycles + " cycles executed in " + millis + " millis.");

				int performance = (int) (cycles / (((double) millis) / 1000));
				getConsole().printInfoLn(performance + " cycle/s");
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

		if (callEvent(new SimulationUndoStepEvent.Before(this, cycles - 1)).isCancelled()) return false;

		stop();
		waitForExecutionFinish();

		if (changes.isEmpty()) return false;
		finished = false;

		if (currentStep == MultiCycleStep.FETCH) {
			executedInstructions--;
		}

		changes.removeLast().restore(this);
		cycles--;

		callEvent(new SimulationUndoStepEvent.After(this, cycles));

		return true;
	}

	@Override
	protected void runStep(boolean first) {
		if (finished) return;

		if (data.isUndoEnabled()) {
			currentStepChanges = new StepChanges<>();
		}

		if (data.canCallEvents()) {
			//If fetch, call the event on the fetch section.
			if (currentStep != MultiCycleStep.FETCH) {
				MultiCycleStepEvent.Before before = callEvent(new MultiCycleStepEvent.Before(this, cycles, executedInstructions,
						currentExecution.getAddress(), currentStep, currentExecution.getInstruction(), currentExecution));
				if (before.isCancelled()) return;
			}
		}

		MultiCycleStep executingStep = currentStep;
		long executedInstructionsLocal = executedInstructions;

		try {
			switch (executingStep) {
				case FETCH:
					fetch(first);
					break;
				case DECODE:
					decode();
					break;
				case MEMORY:
					memory();
					break;
				case EXECUTE:
					execute();
					break;
				case WRITE_BACK:
					writeBack();
					break;
			}
		} catch (RuntimeInstructionException ex) {
			if (!checkThreadInterrupted()) {
				manageMIPSInterrupt(ex, registers.getProgramCounter().getValue() - 4);
			}
		}

		if (checkThreadInterrupted()) {
			currentStepChanges = null;
			return;
		}

		addCycleCount();

		if (data.isUndoEnabled() && currentStepChanges != null) {
			changes.add(currentStepChanges);
			if (changes.size() > MAX_CHANGES) changes.removeFirst();
			currentStepChanges = null;
		}

		if (data.canCallEvents()) {
			callEvent(new MultiCycleStepEvent.After(this, cycles - 1, executedInstructionsLocal, currentExecution.getAddress(),
					executingStep, currentExecution.getInstruction(), currentExecution));
		}
	}

	private void fetch(boolean first) {
		int pc = registers.getProgramCounter().getValue();

		if (breakpoints.contains(pc) && !first) {
			currentStepChanges = null;
			interruptThread();
			return;
		}

		registers.getProgramCounter().setValue(pc + 4);

		if (currentStepChanges != null) {
			currentStepChanges.addChange(new MultiCycleSimulationChangeStep(currentStep));
			currentStepChanges.addChange(new MultiCycleSimulationChangeCurrentExecution(currentExecution));
		}

		MultiCycleExecution<?> newExecution = (MultiCycleExecution<?>) fetch(pc);

		if (data.canCallEvents()) {
			MultiCycleStepEvent.Before before = callEvent(new MultiCycleStepEvent.Before(this, cycles, executedInstructions,
					pc, currentStep, newExecution.getInstruction(), newExecution));
			if (before.isCancelled()) return;
			newExecution = before.getExecution().orElse(null);
		}
		currentExecution = newExecution;
		if (currentExecution == null) {
			currentStepChanges = null;
			throw new RuntimeAddressException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION, pc);
		}

		currentStep = MultiCycleStep.DECODE;
	}

	private void decode() {
		if (currentStepChanges != null) {
			currentStepChanges.addChange(new MultiCycleSimulationChangeStep(currentStep));
		}
		currentExecution.decode();
		currentStep = MultiCycleStep.EXECUTE;
	}

	private void execute() {

		if (currentStepChanges != null) {
			currentStepChanges.addChange(new MultiCycleSimulationChangeStep(currentStep));
		}

		currentExecution.execute();

		////Check thread, if interrupted, return to the previous cycle.
		if (checkThreadInterrupted()) {
			currentStepChanges = null;
			return;
		}

		if (currentExecution.executesMemory()) {
			currentStep = MultiCycleStep.MEMORY;
		} else {
			if (currentExecution.executesWriteBack()) {
				currentStep = MultiCycleStep.WRITE_BACK;
			} else {
				currentStep = MultiCycleStep.FETCH;
				executedInstructions++;
				checkFinished();
			}
		}
	}

	private void memory() {
		if (currentStepChanges != null) {
			currentStepChanges.addChange(new MultiCycleSimulationChangeStep(currentStep));
		}
		currentExecution.memory();
		if (currentExecution.executesWriteBack()) {
			currentStep = MultiCycleStep.WRITE_BACK;
		} else {
			currentStep = MultiCycleStep.FETCH;
			executedInstructions++;
			checkFinished();
		}
	}

	private void writeBack() {
		currentExecution.writeBack();
		if (currentStepChanges != null) {
			currentStepChanges.addChange(new MultiCycleSimulationChangeStep(currentStep));
		}
		currentStep = MultiCycleStep.FETCH;
		executedInstructions++;
		checkFinished();
	}


	private void checkFinished() {
		if (registers.getProgramCounter().getValue() > instructionStackBottom && !finished) {
			finished = true;
			if (getConsole() != null) {
				getConsole().println();
				getConsole().printWarningLn("Execution finished. Dropped off bottom.");
				getConsole().println();
			}
			callEvent(new SimulationFinishedEvent(this));
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
