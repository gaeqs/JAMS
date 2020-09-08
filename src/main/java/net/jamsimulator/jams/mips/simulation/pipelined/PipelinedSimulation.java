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

package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
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
import net.jamsimulator.jams.mips.simulation.change.pipelined.PipelinedSimulationChangePipeline;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.exception.RAWHazardException;
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
public class PipelinedSimulation extends Simulation<PipelinedArchitecture> implements ForwardingSupporter {

	public static final int MAX_CHANGES = 10000;

	private long executedInstructions;

	private final LinkedList<StepChanges<PipelinedArchitecture>> changes;
	private StepChanges<PipelinedArchitecture> currentStepChanges;

	private final Pipeline pipeline;
	private final PipelineForwarding forwarding;

	/**
	 * Creates the single-cycle simulation.
	 *
	 * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
	 * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
	 * @param registers              the registers to use on this simulation.
	 * @param memory                 the memory to use in this simulation.
	 * @param instructionStackBottom the address of the bottom of the instruction stack.
	 */
	public PipelinedSimulation(PipelinedArchitecture architecture, InstructionSet instructionSet, Registers registers, Memory memory, int instructionStackBottom, int kernelStackBottom, SimulationData data) {
		super(architecture, instructionSet, registers, memory, instructionStackBottom, kernelStackBottom, data, false);
		executedInstructions = 0;
		changes = data.isUndoEnabled() ? new LinkedList<>() : null;

		pipeline = new Pipeline(this, registers.getProgramCounter().getValue());
		forwarding = new PipelineForwarding();
	}

	/**
	 * Returns the {@link Pipeline} of this simulation.
	 *
	 * @return the {@link Pipeline}.
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	@Override
	public PipelineForwarding getForwarding() {
		return forwarding;
	}

	@Override
	public void reset() {
		super.reset();
		if (changes != null) {
			changes.clear();
		}
		executedInstructions = 0;
		pipeline.reset(registers.getProgramCounter().getValue());
	}


	@Override
	public void manageMIPSInterrupt(RuntimeInstructionException exception, int pc) {
		pipeline.reset(registers.getProgramCounter().getValue());
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

		//BREAKPOINTS
		if (breakpoints.contains(pipeline.getPc(MultiCycleStep.FETCH)) && !first) {
			if (currentStepChanges != null) {
				currentStepChanges = null;
			}
			interruptThread();
			return;
		}

		if (currentStepChanges != null) {
			currentStepChanges.addChange(new PipelinedSimulationChangePipeline(pipeline.clone()));
		}
		registers.getProgramCounter().setValue(registers.getProgramCounter().getValue() + 4);

		int amount = 0;
		try {
			writeBack();
			amount++;
			memory();
			amount++;
			if (execute()) return;
			amount++;
			decode();
			amount++;
			if (fetch()) return;
			amount++;
		} catch (RAWHazardException ignore) {
		}

		if (checkThreadInterrupted()) {
			if (currentStepChanges != null) {
				currentStepChanges.restore(this);
				currentStepChanges = null;
			}
			return;
		}

		pipeline.shift(registers.getProgramCounter().getValue(), amount);

		addCycleCount();

		if (data.isUndoEnabled() && currentStepChanges != null) {
			changes.add(currentStepChanges);
			if (changes.size() > MAX_CHANGES) changes.removeFirst();
			currentStepChanges = null;
		}
	}

	private boolean fetch() {
		int pc = pipeline.getPc(MultiCycleStep.FETCH);

		boolean check = isKernelMode()
				? Integer.compareUnsigned(pc, kernelStackBottom) > 0
				: Integer.compareUnsigned(pc, instructionStackBottom) > 0;

		if (!check) {
			MultiCycleExecution<?> newExecution = (MultiCycleExecution<?>) fetch(pc);
			if (newExecution == null) {
				pipeline.setException(MultiCycleStep.FETCH, new RuntimeAddressException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION, pc));
			} else {
				pipeline.fetch(newExecution);
			}

		} else {
			if (pipeline.isEmpty()) {
				finished = true;
				if (getConsole() != null) {
					getConsole().println();
					getConsole().printWarningLn("Execution finished. Dropped off bottom.");
					getConsole().println();
				}
				callEvent(new SimulationFinishedEvent(this));
			}
		}

		return false;
	}

	private void decode() {
		try {
			var execution = pipeline.get(MultiCycleStep.DECODE);
			if (execution == null) return;
			execution.decode();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.DECODE, ex);
		}
	}

	private boolean execute() {
		try {
			var execution = pipeline.get(MultiCycleStep.EXECUTE);
			if (execution == null) return false;
			execution.execute();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.EXECUTE, ex);
		}

		if (checkThreadInterrupted()) {
			if (currentStepChanges != null) {
				currentStepChanges.restore(this);
				currentStepChanges = null;
			}
			return true;
		}
		return false;
	}

	private void memory() {
		try {
			var execution = pipeline.get(MultiCycleStep.MEMORY);
			if (execution == null) return;
			execution.memory();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.MEMORY, ex);
		}
	}

	private void writeBack() {

		RuntimeInstructionException exception = pipeline.getException(MultiCycleStep.WRITE_BACK);
		if (exception != null) {
			exception.printStackTrace();
			manageMIPSInterrupt(exception, pipeline.getPc(MultiCycleStep.WRITE_BACK));
			return;
		}

		try {
			var execution = pipeline.get(MultiCycleStep.WRITE_BACK);
			if (execution == null) return;
			execution.writeBack();
		} catch (RuntimeInstructionException ex) {
			if (getConsole() != null) {
				getConsole().printWarningLn("Found exception '" + ex.getMessage() + "' when the instruction was on WriteBack.");
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
