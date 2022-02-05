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

package net.jamsimulator.jams.mips.simulation.multialupipelined;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.instruction.alu.ALUCollectionSnapshot;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryAllocateMemoryEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.COP0RegistersBits;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.register.event.RegisterLockEvent;
import net.jamsimulator.jams.mips.register.event.RegisterUnlockEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.simulation.change.*;
import net.jamsimulator.jams.mips.simulation.change.multialupipelined.MultiALUPipelinedSimulationChangePipeline;
import net.jamsimulator.jams.mips.simulation.change.multialupipelined.MultiALUPipelinedSimulationExitRequest;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;

import java.util.LinkedList;
import java.util.Optional;
import java.util.OptionalInt;

public class MultiALUPipelinedSimulation
        extends MIPSSimulation<MultiALUPipelinedArchitecture>
        implements AbstractPipelinedSimulation {

    public static final int MAX_CHANGES = 10000;

    private final Listeners listeners;
    private final LinkedList<StepChanges<MultiALUPipelinedArchitecture>> changes;
    private final MultiALUPipeline pipeline;

    private final boolean forwardingEnabled;
    private final boolean solveBranchesOnDecode;
    private final boolean delaySlotsEnabled;

    private boolean exitRequested;
    private StepChanges<MultiALUPipelinedArchitecture> currentStepChanges;

    public MultiALUPipelinedSimulation(MultiALUPipelinedArchitecture architecture, MIPSSimulationData data) {
        super(architecture, data, false, true);

        exitRequested = false;
        changes = undoEnabled ? new LinkedList<>() : null;

        forwardingEnabled = data.configuration().getNodeValue(MIPSSimulationConfigurationPresets.FORWARDING_ENABLED);
        solveBranchesOnDecode = data.configuration().getNodeValue(MIPSSimulationConfigurationPresets.BRANCH_ON_DECODE);
        delaySlotsEnabled = solveBranchesOnDecode && (boolean) data.configuration()
                .getNodeValue(MIPSSimulationConfigurationPresets.DELAY_SLOTS_ENABLED);

        ALUCollectionSnapshot list = data.configuration().getNodeValue(MIPSSimulationConfigurationPresets.ALUS);

        pipeline = new MultiALUPipeline(this, delaySlotsEnabled, list);

        listeners = new Listeners();

        registers.registerListeners(listeners, true);
        files.registerListeners(listeners, true);
        var current = Optional.of(memory);
        while (current.isPresent()) {
            current.get().registerListeners(listeners, true);
            current = current.get().getNextLevelMemory();
        }
    }

    public void removeExitRequest() {
        exitRequested = false;
    }

    public MultiALUPipeline getPipeline() {
        return pipeline;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    @Override
    public boolean isForwardingEnabled() {
        return forwardingEnabled;
    }

    @Override
    public boolean solvesBranchesOnDecode() {
        return solveBranchesOnDecode;
    }

    @Override
    public boolean isDelaySlotsEnabled() {
        return delaySlotsEnabled;
    }

    @Override
    public OptionalInt forward(Register register, MultiCycleExecution<?, ?> execution, boolean checkWriteback) {
        return pipeline.forward(register, execution, checkWriteback);
    }

    @Override
    public void requestExit(int exitCode, long executionId) {
        this.exitCode = exitCode;
        if (currentStepChanges != null) {
            currentStepChanges.addChange(new MultiALUPipelinedSimulationExitRequest());
        }
        pipeline.executeFullJumpRemoval(executionId);
        exitRequested = true;
    }

    @Override
    public long getExecutedInstructions() {
        return pipeline.getInstructionsFinished();
    }

    @Override
    public void reset() throws InterruptedException {
        super.reset();
        if (changes != null) {
            changes.clear();
        }
        exitRequested = false;
        pipeline.reset();
    }

    @Override
    public boolean resetCaches() {
        if (!super.resetCaches()) return false;
        if (!undoEnabled) return true;

        var last = memory.getBottomMemory();

        for (StepChanges<?> change : changes) {
            change.removeCacheChanges(last);
        }

        return true;
    }

    @Override
    protected void invokeInterrupt(InterruptCause type, MIPSInterruptException exception, boolean delaySlot, int pc) {
        pipeline.reset();
        registers.unlockAllRegisters();
        super.invokeInterrupt(type, exception, delaySlot, pc);
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

        var oldest = pipeline.getOldestStep().orElse(null);
        if (oldest == null) {
            invokeInterrupt(cause, exception, false, registers.getProgramCounter().getValue());
        } else {
            invokeInterrupt(cause, exception, oldest.execution.isInDelaySlot(), oldest.pc);
        }
    }

    @Override
    protected void runStep(boolean first) {
        if (finished) return;
        if (undoEnabled) currentStepChanges = new StepChanges<>();

        var decode = pipeline.getDecode();
        if (decode != null && breakpoints.contains(decode.pc) && !first) {
            if (currentStepChanges != null) currentStepChanges = null;
            interruptThread();
            return;
        }

        if (currentStepChanges != null) {
            currentStepChanges.addChange(new MultiALUPipelinedSimulationChangePipeline(pipeline.copy()));
        }

        pipeline.executeAllSteps();

        if (checkThreadInterrupted()) {
            if (currentStepChanges != null) {
                var temp = currentStepChanges;
                currentStepChanges = null;
                temp.restore(this);
            }
            return;
        }

        pipeline.shift();
        manageInterrupts();
        addCycleCount();


        if (pipeline.getFetch() == null) {
            checkExit();
        }
        if (undoEnabled && currentStepChanges != null) {
            changes.add(currentStepChanges);
            if (changes.size() > MAX_CHANGES) changes.removeFirst();
            currentStepChanges = null;
        }
    }

    private void checkExit() {
        if (pipeline.isEmpty()) {
            if (getLog() != null && !exitRequested) {
                getLog().println();
                getLog().printWarningLn("Execution finished. Dropped off bottom.");
                getLog().println();
                exitCode = 0;
            }
            callEvent(new SimulationFinishedEvent(this));
        }
    }

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

}
