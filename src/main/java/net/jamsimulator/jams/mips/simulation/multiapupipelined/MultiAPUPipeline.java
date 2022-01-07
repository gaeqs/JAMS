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

package net.jamsimulator.jams.mips.simulation.multiapupipelined;

import net.jamsimulator.jams.mips.instruction.apu.APU;
import net.jamsimulator.jams.mips.instruction.apu.APUCollection;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.event.MultiAPUPipelineShiftEvent;
import net.jamsimulator.jams.mips.simulation.pipelined.exception.RAWHazardException;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiAPUPipeline {

    private final MultiAPUPipelinedSimulation simulation;
    private final boolean delaySlotsEnabled;
    private final APUCollection apus;

    private long instructionsStarted, instructionsFinished;

    private MultiAPUPipelineSlot fetch;
    private MultiAPUPipelineSlot decode;
    private final MultiAPUPipelineSlot[] execute;
    private MultiAPUPipelineSlot memory;
    private MultiAPUPipelineSlot writeback;
    private MultiAPUPipelineSlot finished;

    private final APU[] assignedAPUs;
    private final int[] timesExecuted;

    public MultiAPUPipeline(
            MultiAPUPipelinedSimulation simulation,
            boolean delaySlotsEnabled,
            Set<APU> apus
    ) {
        this.simulation = simulation;
        this.delaySlotsEnabled = delaySlotsEnabled;
        this.apus = new APUCollection(apus);
        execute = new MultiAPUPipelineSlot[apus.size()];
        this.assignedAPUs = new APU[apus.size()];
        this.timesExecuted = new int[apus.size()];
    }

    private MultiAPUPipeline(
            MultiAPUPipelinedSimulation simulation,
            boolean delaySlotsEnabled,
            APUCollection apus
    ) {
        this.simulation = simulation;
        this.delaySlotsEnabled = delaySlotsEnabled;
        this.apus = apus.copy();

        var apuSize = apus.getApus().size();
        execute = new MultiAPUPipelineSlot[apuSize];
        this.assignedAPUs = new APU[apuSize];
        this.timesExecuted = new int[apuSize];
    }

    public APUCollection getApus() {
        return apus;
    }

    public MultiAPUPipelineSlot getFetch() {
        return fetch;
    }

    public MultiAPUPipelineSlot getDecode() {
        return decode;
    }

    public MultiAPUPipelineSlot getMemory() {
        return memory;
    }

    public MultiAPUPipelineSlot getFinished() {
        return finished;
    }

    public Set<MultiAPUPipelineSlot> getExecute() {
        return Arrays.stream(execute).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public MultiAPUPipelineSlot getWriteback() {
        return writeback;
    }

    public Optional<MultiAPUPipelineSlot> getOldestStep() {
        return Stream.concat(Stream.of(fetch, decode, memory, writeback), Arrays.stream(execute))
                .filter(it -> it != null && it.execution != null)
                .min(Comparator.comparing(it -> it.execution.getInstructionId()));
    }

    public boolean isEmpty() {
        return fetch == null && decode == null && memory == null && writeback == null
                && Arrays.stream(execute).allMatch(Objects::isNull);
    }

    public void executeAllSteps() {
        writeBack();
        fetch();
        execute();
        memory();
        decode();
    }

    public void shift() {
        simulation.callEvent(new MultiAPUPipelineShiftEvent.Before(simulation, this));
        finished = null;
        if (writeback != null && writeback.status == MultiAPUPipelineSlotStatus.EXECUTED) {
            finished = writeback;
            writeback = null;
            instructionsFinished++;
        }

        if (memory != null && memory.status == MultiAPUPipelineSlotStatus.EXECUTED) {
            if (writeback == null) {
                writeback = memory;
                memory = null;
            } else {
                memory.status = MultiAPUPipelineSlotStatus.STALL;
            }
        }

        if (memory == null) {
            // MOVE INSTRUCTION TO MEMORY
            shiftExecutionToMemory();
        } else {
            for (var e : execute) {
                if (e != null) e.status = MultiAPUPipelineSlotStatus.STALL;
            }
        }

        if (decode != null && decode.status == MultiAPUPipelineSlotStatus.EXECUTED) {
            shiftDecodeToExecute();
        }

        if (fetch != null && fetch.status == MultiAPUPipelineSlotStatus.EXECUTED) {
            if (decode == null) {
                decode = fetch;
                fetch = null;
                Register pc = simulation.getRegisters().getProgramCounter();
                pc.setValue(pc.getValue() + 4);
            } else {
                fetch.status = MultiAPUPipelineSlotStatus.STALL;
            }
        }
        simulation.callEvent(new MultiAPUPipelineShiftEvent.After(simulation, this));
    }

    public void reset() {
        finished = null;
        writeback = null;
        memory = null;
        Arrays.fill(execute, null);
        decode = null;
        fetch = null;
        instructionsStarted = 0;
        instructionsFinished = 0;
        Arrays.fill(assignedAPUs, null);
        Arrays.fill(timesExecuted, 0);
        apus.reset();
    }

    public void stallFetch() {
        if (fetch != null) {
            fetch.status = MultiAPUPipelineSlotStatus.STALL;
        }
    }

    public void removeFetchAndDecode() {
        fetch = null;
        decode = null;
    }

    public OptionalInt forward(Register register, MultiCycleExecution<?, ?> execution, boolean checkWriteback) {
        boolean fromMemory = memory != null && memory.execution == execution;

        if (!fromMemory) {
            for (MultiAPUPipelineSlot slot : execute) {
                if (slot != null && slot.execution != null && slot.execution != execution) {
                    if (register.isLastLockedBeforeId(slot.execution, execution.getInstructionId())) {
                        var optional = slot.execution.getForwardedValue(register);
                        if (optional.isPresent()) return optional;
                    }
                }
            }

            if (memory != null && memory.execution != null) {
                if (register.isLastLockedBeforeId(memory.execution, execution.getInstructionId())) {
                    var optional = memory.execution.getForwardedValue(register);
                    if (optional.isPresent()) return optional;
                }
            }
        }

        if (checkWriteback && writeback != null && writeback.execution != null) {
            if (fromMemory || register.isLastLockedBeforeId(writeback.execution, execution.getInstructionId())) {
                var optional = writeback.execution.getForwardedValue(register);
                if (optional.isPresent()) return optional;
            }
        }

        return OptionalInt.empty();
    }

    public MultiAPUPipeline copy() {
        var copy = new MultiAPUPipeline(simulation, delaySlotsEnabled, apus);
        copy.instructionsStarted = instructionsStarted;
        copy.instructionsFinished = instructionsFinished;

        copy.fetch = fetch == null ? null : fetch.copy();
        copy.decode = decode == null ? null : decode.copy();
        copy.memory = memory == null ? null : memory.copy();
        copy.writeback = writeback == null ? null : writeback.copy();
        copy.finished = finished == null ? null : finished.copy();

        for (int i = 0; i < copy.execute.length; i++) {
            copy.execute[i] = execute[i] == null ? null : execute[i].copy();
        }

        System.arraycopy(assignedAPUs, 0, copy.assignedAPUs, 0, assignedAPUs.length);
        System.arraycopy(timesExecuted, 0, copy.timesExecuted, 0, timesExecuted.length);

        return copy;
    }

    public void restore(MultiAPUPipeline old) {
        apus.restore(old.apus);
        instructionsStarted = old.instructionsStarted;
        instructionsFinished = old.instructionsFinished;
        fetch = old.fetch == null ? null : old.fetch.copy();
        decode = old.decode == null ? null : old.decode.copy();
        memory = old.memory == null ? null : old.memory.copy();
        writeback = old.writeback == null ? null : old.writeback.copy();
        finished = old.finished == null ? null : old.finished.copy();

        for (int i = 0; i < execute.length; i++) {
            execute[i] = old.execute[i] == null ? null : old.execute[i].copy();
        }

        System.arraycopy(old.assignedAPUs, 0, assignedAPUs, 0, assignedAPUs.length);
        System.arraycopy(old.timesExecuted, 0, timesExecuted, 0, timesExecuted.length);
    }

    private void writeBack() {
        if (writeback == null) return;
        var execution = writeback.execution;
        var exception = writeback.exception;
        if (exception != null) {
            simulation.requestSoftwareInterrupt(exception);
            writeback.status = MultiAPUPipelineSlotStatus.EXECUTED;
            return;
        }

        try {
            execution.writeBack();
        } catch (MIPSInterruptException ex) {
            var console = simulation.getConsole();
            if (console != null) {
                console.printWarningLn("Found exception '" + ex.getMessage()
                        + "' when the instruction was on WriteBack");
            }
        }
        writeback.status = MultiAPUPipelineSlotStatus.EXECUTED;
    }

    private void memory() {
        if (memory == null) return;
        if (memory.exception != null) {
            memory.status = MultiAPUPipelineSlotStatus.EXECUTED;
            return;
        }
        try {
            memory.execution.memory();
        } catch (MIPSInterruptException ex) {
            memory.exception = ex;
        } catch (Exception ex) {
            var execution = memory.execution;
            System.err.println("Found exception at 0x" + StringUtils.addZeros(Integer.toHexString(execution.getAddress()), 8));
            System.err.println("Instruction " + execution.getInstruction().getBasicOrigin().getMnemonic());
            System.err.println("Exception " + ex);
            ex.printStackTrace();
        }
        memory.status = MultiAPUPipelineSlotStatus.EXECUTED;
    }

    private void execute() {
        for (int i = 0; i < execute.length; i++) {
            var execution = execute[i];
            if (execution == null) continue;
            if (execution.exception != null) {
                execution.status = MultiAPUPipelineSlotStatus.EXECUTED;
                continue;
            }

            var required = assignedAPUs[i].cyclesRequired();
            if (required == timesExecuted[i]) {
                execution.status = MultiAPUPipelineSlotStatus.EXECUTED;
            } else if (required - 1 > timesExecuted[i]) {
                timesExecuted[i]++;
                execution.status = MultiAPUPipelineSlotStatus.RUNNING;
            } else {
                try {
                    execution.execution.execute();
                } catch (MIPSInterruptException ex) {
                    execution.exception = ex;
                }

                if (simulation.checkThreadInterrupted()) return;

                timesExecuted[i] = required;
                execution.status = MultiAPUPipelineSlotStatus.EXECUTED;
            }
        }

    }

    private void decode() {
        if (decode == null) return;
        if (decode.exception != null) {
            memory.status = MultiAPUPipelineSlotStatus.EXECUTED;
            return;
        }
        if (decode.execution.isInDelaySlot() && decode.execution.getInstruction().getBasicOrigin()
                instanceof ControlTransferInstruction) {
            //Release 6: If a control transfer instruction (CTI) is executed in the delay slot of a branch or jump,
            //Release 6 implementations are required to signal a Reserved Instruction exception.
            decode.exception = new MIPSInterruptException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION);
        } else {
            try {
                decode.execution.decode();
                decode.status = MultiAPUPipelineSlotStatus.EXECUTED;
            } catch (RAWHazardException ex) {
                decode.status = MultiAPUPipelineSlotStatus.RAW;
            } catch (MIPSInterruptException ex) {
                decode.exception = ex;
                decode.status = MultiAPUPipelineSlotStatus.EXECUTED;
            }
        }
    }

    private void fetch() {
        var pc = simulation.getRegisters().getProgramCounter();
        var pcv = pc.getValue();
        boolean stackBottomReached = simulation.isKernelMode()
                ? Integer.compareUnsigned(pcv, simulation.getKernelStackBottom()) > 0
                : Integer.compareUnsigned(pcv, simulation.getInstructionStackBottom()) > 0;

        if (stackBottomReached || simulation.isExitRequested()) {
            return;
        }

        var execution = (MultiCycleExecution<?, ?>) simulation.fetch(pcv);
        if (fetch != null && fetch.execution != null
                && fetch.execution.getInstruction().equals(execution.getInstruction())) {
            fetch.status = pc.isLocked() ? MultiAPUPipelineSlotStatus.RAW : MultiAPUPipelineSlotStatus.EXECUTED;
            return;
        }

        fetch = new MultiAPUPipelineSlot(null, pcv, null,
                pc.isLocked() ? MultiAPUPipelineSlotStatus.RAW : MultiAPUPipelineSlotStatus.EXECUTED);


        if (execution == null) {
            fetch.exception = new MIPSAddressException(InterruptCause.RESERVED_INSTRUCTION_EXCEPTION, fetch.pc);
        } else {
            boolean inDelaySlot = delaySlotsEnabled
                    && decode != null && decode.execution != null &&
                    decode.execution.getInstruction().getBasicOrigin() instanceof ControlTransferInstruction;
            execution.setInstructionId(instructionsStarted);
            execution.setInDelaySlot(inDelaySlot);
            instructionsStarted++;
            fetch.execution = execution;
        }
    }

    private void shiftExecutionToMemory() {
        boolean move = false;
        int executionToMove = 0;
        long executionId = 0;

        for (int i = 0; i < execute.length; i++) {
            var e = execute[i];
            if (e == null || e.status != MultiAPUPipelineSlotStatus.EXECUTED) continue;
            if (!e.execution.canMoveToMemory(
                    memory == null ? null : memory.execution,
                    writeback == null ? null : writeback.execution
            )) {
                e.status = MultiAPUPipelineSlotStatus.WAW;
                continue;
            }
            if (!move || executionId < e.execution.getInstructionId()) {
                if (move) {
                    execute[executionToMove].status = MultiAPUPipelineSlotStatus.STALL;
                }
                move = true;
                executionToMove = i;
                executionId = e.execution.getInstructionId();
            } else {
                e.status = MultiAPUPipelineSlotStatus.STALL;
            }
        }

        if (move) {
            apus.releaseAPU(assignedAPUs[executionToMove]);
            memory = execute[executionToMove];
            execute[executionToMove] = null;
        }
    }

    private void shiftDecodeToExecute() {
        var apu = apus.requestAPU(decode.execution
                .getInstruction().getBasicOrigin().getDefaultAPUType());
        if (apu.isPresent()) {
            int index = apu.get().id();
            assignedAPUs[index] = apu.get();
            timesExecuted[index] = 0;
            execute[index] = decode;
            decode = null;
        } else {
            decode.status = MultiAPUPipelineSlotStatus.STALL;
        }
    }

}
