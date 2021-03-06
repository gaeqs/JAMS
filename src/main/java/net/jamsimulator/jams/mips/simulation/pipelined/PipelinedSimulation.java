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
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.RuntimeAddressException;
import net.jamsimulator.jams.mips.interrupt.RuntimeInstructionException;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryAllocateMemoryEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.register.event.RegisterLockEvent;
import net.jamsimulator.jams.mips.register.event.RegisterUnlockEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;
import net.jamsimulator.jams.mips.simulation.change.*;
import net.jamsimulator.jams.mips.simulation.change.pipelined.PipelinedSimulationChangePipeline;
import net.jamsimulator.jams.mips.simulation.change.pipelined.PipelinedSimulationExitRequest;
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
import java.util.Optional;

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

	private long instructionsStarted, instructionsFinished;
	private boolean exitRequested;

	private final LinkedList<StepChanges<PipelinedArchitecture>> changes;
	private StepChanges<PipelinedArchitecture> currentStepChanges;

	private final Pipeline pipeline;
	private final PipelineForwarding forwarding;

	//Hard reference. Do not convert to local variable.
	@SuppressWarnings("FieldCanBeLocal")
	private static Listeners listeners;

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
		instructionsStarted = 0;
		instructionsFinished = 0;
		changes = data.isUndoEnabled() ? new LinkedList<>() : null;

		pipeline = new Pipeline(this, registers.getProgramCounter().getValue());
		forwarding = new PipelineForwarding();
		exitRequested = false;

		listeners = new Listeners();

		registers.registerListeners(listeners, true);
		files.registerListeners(listeners, true);
		Optional<Memory> current = Optional.of(memory);
		while (current.isPresent()) {
			current.get().registerListeners(listeners, true);
			current = current.get().getNextLevelMemory();
		}
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
	public void requestExit() {
		if (currentStepChanges != null) {
			currentStepChanges.addChange(new PipelinedSimulationExitRequest());
		}
		pipeline.removeFetch();
		pipeline.removeDecode();
		exitRequested = true;
	}

	public void removeExitRequest() {
		exitRequested = false;
	}

	@Override
	public void reset() {
		super.reset();
		instructionsStarted = 0;
		instructionsFinished = 0;
		if (changes != null) {
			changes.clear();
		}
		exitRequested = false;
		pipeline.reset(registers.getProgramCounter().getValue());
	}

	@Override
	public boolean resetCaches() {
		if (!super.resetCaches()) return false;
		if (!data.isUndoEnabled()) return true;

		//Gets the last memory level.
		var last = memory;
		Optional<Memory> current = Optional.of(memory);
		while (current.isPresent()) {
			current = current.get().getNextLevelMemory();
			if (current.isPresent()) {
				last = current.get();
			}
		}

		for (StepChanges<?> change : changes) {
			change.removeCacheChanges(last);
		}
		return true;
	}

	@Override
	public void manageMIPSInterrupt(RuntimeInstructionException exception, InstructionExecution<?, ?> execution, int pc) {
		pipeline.reset(MIPS32Memory.EXCEPTION_HANDLER);
		registers.unlockAllRegisters();
		super.manageMIPSInterrupt(exception, execution, pc);
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
			}

			synchronized (finishedRunningLock) {
				running = false;
				memory.enableEventCalls(true);
				registers.enableEventCalls(true);
				finishedRunningLock.notifyAll();
				callEvent(new SimulationStopEvent(this));
				getConsole().flush();
			}
		});
		callEvent(new SimulationStartEvent(this));
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	@Override
	public boolean undoLastStep() {
		if (!data.isUndoEnabled()) return false;

		if (callEvent(new SimulationUndoStepEvent.Before(this, cycles - 1)).isCancelled()) return false;

		stop();
		waitForExecutionFinish();

		var oldFetch = pipeline.get(MultiCycleStep.FETCH);

		if (changes.isEmpty()) return false;
		finished = false;

		changes.removeLast().restore(this);
		cycles--;

		var newFetch = pipeline.get(MultiCycleStep.FETCH);

		if (pipeline.get(MultiCycleStep.WRITE_BACK) != null) instructionsFinished--;
		if (oldFetch != null && oldFetch != newFetch) instructionsStarted--;

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
		if (breakpoints.contains(pipeline.getPc(MultiCycleStep.DECODE)) && !first) {
			if (currentStepChanges != null) {
				currentStepChanges = null;
			}
			interruptThread();
			return;
		}

		if (currentStepChanges != null) {
			currentStepChanges.addChange(new PipelinedSimulationChangePipeline(pipeline.clone()));
		}

		var check = false;

		int amount = 0;
		try {
			writeBack();
			amount++;
			memory();
			amount++;
			if (!execute()) {
				amount++;
				decode();
				amount++;

				var pcv = pipeline.getPc(MultiCycleStep.FETCH);
				check = exitRequested || (isKernelMode()
						? Integer.compareUnsigned(pcv, kernelStackBottom) > 0
						: Integer.compareUnsigned(pcv, instructionStackBottom) > 0);

				if (!check) {
					if (!fetch()) {
						amount++;
					}
				} else {
					amount++;
				}
			}
		} catch (RAWHazardException ignore) {
		}

		if (checkThreadInterrupted()) {
			if (currentStepChanges != null) {
				var temp = currentStepChanges;
				currentStepChanges = null;
				temp.restore(this);
			}
			return;
		}

		var pcv = registers.getProgramCounter().getValue();
		var nextCheck = check || (isKernelMode()
				? Integer.compareUnsigned(pcv, kernelStackBottom) > 0
				: Integer.compareUnsigned(pcv, instructionStackBottom) > 0);

		pipeline.shift(nextCheck ? 0 : pcv, amount);
		if (nextCheck) checkExit();

		addCycleCount();

		if (data.isUndoEnabled() && currentStepChanges != null) {
			changes.add(currentStepChanges);
			if (changes.size() > MAX_CHANGES) changes.removeFirst();
			currentStepChanges = null;
		}
	}

	private void checkExit() {
		if (pipeline.isEmpty()) {
			finished = true;
			if (getConsole() != null && !exitRequested) {
				getConsole().println();
				getConsole().printWarningLn("Execution finished. Dropped off bottom.");
				getConsole().println();
			}
			callEvent(new SimulationFinishedEvent(this));
		}
	}

	private boolean fetch() {
		var pc = registers.getProgramCounter();
		if (pc.isLocked()) return true;

		var pcv = pipeline.getPc(MultiCycleStep.FETCH);
		if (pcv == 0) return false;

		//Fetches the stored execution, but it can be a execution in a delay slot. That's why the new PC is not pcv + 4.
		pc.setValue(pc.getValue() + 4);

		MultiCycleExecution<?> newExecution = (MultiCycleExecution<?>) fetch(pcv);
		if (newExecution == null) {
			pipeline.setException(MultiCycleStep.FETCH, new RuntimeAddressException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION, pcv));
		} else {

			var onDecode = pipeline.get(MultiCycleStep.DECODE);
			boolean isInDelaySlot = data.areDelaySlotsEnabled()
					&& onDecode != null && onDecode.getInstruction().getBasicOrigin() instanceof ControlTransferInstruction;

			newExecution.setInstructionId(instructionsStarted);
			newExecution.setInDelaySlot(isInDelaySlot);
			pipeline.fetch(newExecution);
			instructionsStarted++;
		}

		return false;
	}

	private void decode() {
		try {
			var execution = pipeline.get(MultiCycleStep.DECODE);
			if (execution == null) return;

			//Release 6: If a control transfer instruction (CTI) is executed in the delay slot of a branch or jump,
			//Release 6 implementations are required to signal a Reserved Instruction exception.
			if (execution.isInDelaySlot() && execution.getInstruction().getBasicOrigin() instanceof ControlTransferInstruction) {
				throw new RuntimeInstructionException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION);
			}

			execution.decode();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.DECODE, ex);
		}
	}

	private boolean execute() {
		try {
			var execution = pipeline.get(MultiCycleStep.EXECUTE);
			if (execution == null) return false;

			//Don't execute if there's a exception. This may cause an unhandled exception.
			if (pipeline.getException(MultiCycleStep.EXECUTE) != null) return false;

			execution.execute();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.EXECUTE, ex);
		}


		if (checkThreadInterrupted()) {
			if (currentStepChanges != null) {
				var temp = currentStepChanges;
				currentStepChanges = null;
				temp.restore(this);
			}
			return true;
		}
		return false;
	}

	private void memory() {
		var execution = pipeline.get(MultiCycleStep.MEMORY);
		if (execution == null) return;

		//Don't execute if there's a exception. This may cause an unhandled exception.
		if (pipeline.getException(MultiCycleStep.MEMORY) != null) return;

		try {
			execution.memory();
		} catch (RuntimeInstructionException ex) {
			pipeline.setException(MultiCycleStep.MEMORY, ex);
		} catch (Exception ex) {
			System.err.println("Found exception at 0x" + StringUtils.addZeros(Integer.toHexString(execution.getAddress()), 8));
			System.err.println("Instruction " + execution.getInstruction().getBasicOrigin().getMnemonic());
			System.err.println("Exception " + ex);
			ex.printStackTrace();
		}
	}

	private void writeBack() {
		var execution = pipeline.get(MultiCycleStep.WRITE_BACK);
		if (execution == null) return;

		RuntimeInstructionException exception = pipeline.getException(MultiCycleStep.WRITE_BACK);
		if (exception != null) {
			manageMIPSInterrupt(exception, execution, pipeline.getPc(MultiCycleStep.WRITE_BACK));
			return;
		}

		try {
			execution.writeBack();
			instructionsFinished++;
		} catch (RuntimeInstructionException ex) {
			if (getConsole() != null) {
				getConsole().printWarningLn("Found exception '" + ex.getMessage() + "' when the instruction was on WriteBack.");
			}
		}
	}

	//region change listeners

	public class Listeners {

		@Listener
		private void onMemoryChange(MemoryWordSetEvent.After event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeMemoryWord(event.getMemory(), event.getAddress(), event.getOldValue()));
		}

		@Listener
		private void onMemoryChange(MemoryByteSetEvent.After event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeMemoryByte(event.getMemory(), event.getAddress(), event.getOldValue()));
		}

		@Listener
		private void onRegisterChange(RegisterChangeValueEvent.After event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeRegister(event.getRegister(), event.getOldValue()));
		}

		@Listener
		private void onRegisterLock(RegisterLockEvent.After event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeRegisterLock(event.getRegister(), event.getExecution()));
		}

		@Listener
		private void onRegisterUnlock(RegisterUnlockEvent.After event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeRegisterUnlock(event.getRegister(), event.getExecution()));
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
		private void onCacheOperation(CacheOperationEvent event) {
			if (currentStepChanges == null) return;
			currentStepChanges.addChange(new SimulationChangeCacheOperation(event.getCache(), event.isHit(),
					event.getBlockIndex(), event.getOldBlock()));
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
	}

	//endregion

}
