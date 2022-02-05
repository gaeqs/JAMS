/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.simulation.singlecycle;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryAllocateMemoryEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.COP0RegistersBits;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.simulation.change.*;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;

import java.util.LinkedList;
import java.util.Optional;

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
public class SingleCycleSimulation extends MIPSSimulation<SingleCycleArchitecture> {

    public static final int MAX_CHANGES = 10000;

    private final LinkedList<StepChanges<SingleCycleArchitecture>> changes;
    //Hard reference. Do not convert to local variable.
    @SuppressWarnings("FieldCanBeLocal")
    private final Listeners listeners;
    private StepChanges<SingleCycleArchitecture> currentStepChanges;
    private long instructions;
    private long start;

    /**
     * Creates the single-cycle simulation.
     *
     * @param architecture the architecture of the simulation. This should be given by a simulation subclass.
     * @param data         the build data of this simulation.
     */
    public SingleCycleSimulation(SingleCycleArchitecture architecture, MIPSSimulationData data) {
        super(architecture, data, true, true);
        changes = undoEnabled ? new LinkedList<>() : null;
        listeners = new Listeners();

        registers.registerListeners(listeners, true);
        files.registerListeners(listeners, true);
        Optional<Memory> current = Optional.of(memory);
        while (current.isPresent()) {
            current.get().registerListeners(listeners, true);
            current = current.get().getNextLevelMemory();
        }
    }

    @Override
    public void requestExit(int exitCode, long executionId) {
        this.exitCode = exitCode;
        exit();
    }

    @Override
    public long getExecutedInstructions() {
        return cycles;
    }

    @Override
    public void reset() throws InterruptedException {
        super.reset();
        if (changes != null) {
            changes.clear();
        }
    }

    @Override
    public boolean resetCaches() {
        if (!super.resetCaches()) return false;
        if (!undoEnabled) return true;

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
    public boolean undoLastStep() throws InterruptedException {
        if (!undoEnabled) return false;

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
    protected synchronized void runStep(boolean first) {
        if (finished) return;
        int pc = registers.getProgramCounter().getValue();

        if (breakpoints.contains(pc) && !first) {
            interruptThread();
            return;
        }

        if (undoEnabled) {
            currentStepChanges = new StepChanges<>();
        }

        registers.getProgramCounter().setValue(pc + 4);
        SingleCycleExecution<?> execution = null;
        try {
            //Fetch and Decode
            execution = (SingleCycleExecution<?>) fetch(pc);

            //Send before event
            if (canCallEvents) {
                SingleCycleInstructionExecutionEvent.Before before =
                        callEvent(new SingleCycleInstructionExecutionEvent.Before(this, cycles, pc, execution == null ? null : execution.getInstruction(), execution));
                if (before.isCancelled()) return;

                //Gets the modifies execution. This may be null.
                execution = before.getExecution().orElse(null);

            }

            if (execution == null) {
                currentStepChanges = null;
                throw new MIPSAddressException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION, pc);
            }

            execution.execute();

        } catch (MIPSInterruptException ex) {
            if (!checkThreadInterrupted()) {
                requestSoftwareInterrupt(ex);
            }
        }

        //Check thread, if interrupted, return to the previous cycle.
        if (checkThreadInterrupted()) {
            currentStepChanges = null;
            registers.getProgramCounter().setValue(pc);
            return;
        }

        manageInterrupts();

        addCycleCount();

        if (canCallEvents) {
            callEvent(new SingleCycleInstructionExecutionEvent.After(this, cycles, pc, execution == null ? null : execution.getInstruction(), execution));

            if (undoEnabled) {
                changes.add(currentStepChanges);
                if (changes.size() > MAX_CHANGES) changes.removeFirst();
                currentStepChanges = null;
            }
        }

        boolean check = isKernelMode()
                ? Integer.compareUnsigned(registers.getProgramCounter().getValue(), kernelStackBottom) > 0
                : Integer.compareUnsigned(registers.getProgramCounter().getValue(), instructionStackBottom) > 0;

        if (check && !finished) {
            finished = true;
            if (getLog() != null) {
                getLog().println();
                getLog().printWarningLn("Execution finished. Dropped off bottom.");
                getLog().println();
                exitCode = 0;
            }
            callEvent(new SimulationFinishedEvent(this));
        }
    }

    @Override
    protected void manageInterrupts() {
        if (!arePendingInterrupts()) return;

        int level = externalInterruptController.getRequestedIPL();
        causeRegister.modifyBits(level, COP0RegistersBits.CAUSE_RIPL, 6);

        InterruptCause cause;
        MIPSInterruptException exception;

        if (level == 1) {
            causeRegister.modifyBits(0, COP0RegistersBits.CAUSE_IP, 1);
            exception = externalInterruptController.getSoftwareInterrupt();
            cause = exception.getInterruptCause();
        } else {
            exception = null;
            cause = InterruptCause.INTERRUPT;
        }

        invokeInterrupt(cause, exception, false, registers.getProgramCounter().getValue());
    }

    //region change listeners

    private class Listeners {

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
