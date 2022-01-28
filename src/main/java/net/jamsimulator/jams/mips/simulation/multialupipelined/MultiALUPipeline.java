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

import net.jamsimulator.jams.mips.instruction.alu.ALU;
import net.jamsimulator.jams.mips.instruction.alu.ALUCollection;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.multialupipelined.event.MultiALUPipelineShiftEvent;
import net.jamsimulator.jams.mips.simulation.multialupipelined.exception.RAWHazardException;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiALUPipeline {

    private final MultiALUPipelinedSimulation simulation;
    private final boolean delaySlotsEnabled;
    private final ALUCollection alus;

    private long instructionsStarted, instructionsFinished;

    private MultiALUPipelineSlot fetch;
    private MultiALUPipelineSlot decode;
    private final MultiALUPipelineSlot[] execute;
    private MultiALUPipelineSlot memory;
    private MultiALUPipelineSlot writeback;
    private MultiALUPipelineSlot finished;

    private final ALU[] assignedALUs;
    private final int[] timesExecuted;

    private long raws, waws, otherStalls;

    public MultiALUPipeline(
            MultiALUPipelinedSimulation simulation,
            boolean delaySlotsEnabled,
            List<ALU> alus
    ) {
        this.simulation = simulation;
        this.delaySlotsEnabled = delaySlotsEnabled;
        this.alus = new ALUCollection(alus);
        execute = new MultiALUPipelineSlot[alus.size()];
        this.assignedALUs = new ALU[alus.size()];
        this.timesExecuted = new int[alus.size()];
    }

    private MultiALUPipeline(
            MultiALUPipelinedSimulation simulation,
            boolean delaySlotsEnabled,
            ALUCollection alus
    ) {
        this.simulation = simulation;
        this.delaySlotsEnabled = delaySlotsEnabled;
        this.alus = alus.copy();

        var aluSize = alus.getAlus().size();
        execute = new MultiALUPipelineSlot[aluSize];
        this.assignedALUs = new ALU[aluSize];
        this.timesExecuted = new int[aluSize];
    }

    public ALUCollection getAlus() {
        return alus;
    }

    public MultiALUPipelineSlot getFetch() {
        return fetch;
    }

    public MultiALUPipelineSlot getDecode() {
        return decode;
    }

    public MultiALUPipelineSlot getMemory() {
        return memory;
    }

    public MultiALUPipelineSlot getFinished() {
        return finished;
    }

    public long getRAWs() {
        return raws;
    }

    public long getWAWs() {
        return waws;
    }

    public long getOtherStalls() {
        return otherStalls;
    }

    public long getInstructionsStarted() {
        return instructionsStarted;
    }

    public long getInstructionsFinished() {
        return instructionsFinished;
    }

    public Set<MultiALUPipelineSlot> getExecute() {
        return Arrays.stream(execute).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public MultiALUPipelineSlot getWriteback() {
        return writeback;
    }

    public Optional<MultiALUPipelineSlot> getOldestStep() {
        return Stream.concat(Stream.of(fetch, decode, memory, writeback), Arrays.stream(execute))
                .filter(it -> it != null && it.execution != null)
                .min(Comparator.comparing(it -> it.execution.getInstructionId()));
    }

    public boolean isEmpty() {
        return fetch == null && decode == null && memory == null && writeback == null
                && Arrays.stream(execute).allMatch(Objects::isNull);
    }

    public void executeAllSteps() {
        fetch();
        writeBack();
        execute();
        memory();
        decode();
    }

    public void shift() {
        simulation.callEvent(new MultiALUPipelineShiftEvent.Before(simulation, this));
        finished = null;

        if (writeback != null) {
            switch (writeback.status) {
                case REMOVED -> {
                    if (writeback.execution != null) writeback.execution.unlockAll();
                    writeback = null;
                }
                case EXECUTED -> {
                    finished = writeback;
                    writeback = null;
                    instructionsFinished++;
                }
            }
        }

        if (memory != null) {
            switch (memory.status) {
                case REMOVED -> {
                    if (memory.execution != null) memory.execution.unlockAll();
                    memory = null;
                }
                case EXECUTED -> {
                    if (writeback == null) {
                        writeback = memory;
                        memory = null;
                    } else {
                        memory.status = MultiALUPipelineSlotStatus.STALL;
                        otherStalls++;
                    }
                }
            }
        }

        if (memory == null) {
            // MOVE INSTRUCTION TO MEMORY
            shiftExecutionToMemory();
        } else {
            for (var e : execute) {
                if (e != null && e.status == MultiALUPipelineSlotStatus.EXECUTED) {
                    e.status = MultiALUPipelineSlotStatus.STALL;
                    otherStalls++;
                }
            }
        }

        if (decode != null) {
            switch (decode.status) {
                case REMOVED -> {
                    if (decode.execution != null) decode.execution.unlockAll();
                    decode = null;
                }
                case EXECUTED -> shiftDecodeToExecute();
            }
        }

        if (fetch != null) {
            switch (fetch.status) {
                case REMOVED -> {
                    if (fetch.execution != null) fetch.execution.unlockAll();
                    fetch = null;
                }
                case EXECUTED -> {
                    if (decode == null) {
                        decode = fetch;
                        fetch = null;
                        var pc = simulation.getRegisters().getProgramCounter();
                        pc.setValue(pc.getValue() + 4);
                    } else {
                        fetch.status = MultiALUPipelineSlotStatus.STALL;
                        otherStalls++;
                    }
                }
            }
        }

        simulation.callEvent(new MultiALUPipelineShiftEvent.After(simulation, this));
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
        Arrays.fill(assignedALUs, null);
        Arrays.fill(timesExecuted, 0);
        alus.reset();

        raws = 0;
        waws = 0;
        otherStalls = 0;
    }

    public void removeFetch() {
        if (fetch != null) {
            fetch.status = MultiALUPipelineSlotStatus.STALL;
            otherStalls++;
        }
    }

    public void executeFullJumpRemoval(long instructionId) {
        // We are sure fetch and decore are newer then the jump instruction.
        if (fetch != null) fetch.status = MultiALUPipelineSlotStatus.REMOVED;
        if (decode != null) decode.status = MultiALUPipelineSlotStatus.REMOVED;


        for (int i = 0; i < execute.length; i++) {
            var current = execute[i];
            if (current != null && current.execution.getInstructionId() > instructionId) {
                alus.releaseALU(i);
                current.status = MultiALUPipelineSlotStatus.REMOVED;
            }
        }


        if (memory != null && memory.execution.getInstructionId() > instructionId) {
            memory.status = MultiALUPipelineSlotStatus.REMOVED;
        }
    }

    public void removeFetchAndDecode() {
        if (fetch != null) fetch.status = MultiALUPipelineSlotStatus.REMOVED;
        if (decode != null) decode.status = MultiALUPipelineSlotStatus.REMOVED;

    }

    public OptionalInt forward(Register register, MultiCycleExecution<?, ?> execution, boolean checkWriteback) {
        boolean fromMemory = memory != null && memory.execution == execution;

        if (!fromMemory) {
            for (MultiALUPipelineSlot slot : execute) {
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

    public MultiALUPipeline copy() {
        var copy = new MultiALUPipeline(simulation, delaySlotsEnabled, alus);
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

        System.arraycopy(assignedALUs, 0, copy.assignedALUs, 0, assignedALUs.length);
        System.arraycopy(timesExecuted, 0, copy.timesExecuted, 0, timesExecuted.length);

        copy.raws = raws;
        copy.waws = waws;
        copy.otherStalls = otherStalls;

        return copy;
    }

    public void restore(MultiALUPipeline old) {
        alus.restore(old.alus);
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

        System.arraycopy(old.assignedALUs, 0, assignedALUs, 0, assignedALUs.length);
        System.arraycopy(old.timesExecuted, 0, timesExecuted, 0, timesExecuted.length);

        raws = old.raws;
        waws = old.waws;
        otherStalls = old.otherStalls;
    }

    private void writeBack() {
        if (writeback == null || writeback.status == MultiALUPipelineSlotStatus.REMOVED) return;
        var execution = writeback.execution;
        var exception = writeback.exception;
        if (exception != null) {
            simulation.requestSoftwareInterrupt(exception);
            writeback.status = MultiALUPipelineSlotStatus.EXECUTED;
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
        writeback.status = MultiALUPipelineSlotStatus.EXECUTED;
    }

    private void memory() {
        if (memory == null || memory.status == MultiALUPipelineSlotStatus.REMOVED) return;
        if (memory.exception != null) {
            memory.status = MultiALUPipelineSlotStatus.EXECUTED;
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
        memory.status = MultiALUPipelineSlotStatus.EXECUTED;
    }

    private void execute() {
        for (int i = 0; i < execute.length; i++) {
            var execution = execute[i];
            if (execution == null || execution.status == MultiALUPipelineSlotStatus.REMOVED) continue;
            if (execution.exception != null) {
                execution.status = MultiALUPipelineSlotStatus.EXECUTED;
                continue;
            }

            var required = assignedALUs[i].cyclesRequired();
            if (required == timesExecuted[i]) {
                execution.status = MultiALUPipelineSlotStatus.EXECUTED;
            } else if (required - 1 > timesExecuted[i]) {
                timesExecuted[i]++;
                execution.status = MultiALUPipelineSlotStatus.RUNNING;
            } else {
                try {
                    execution.execution.execute();
                } catch (MIPSInterruptException ex) {
                    execution.exception = ex;
                }

                if (simulation.checkThreadInterrupted()) return;

                timesExecuted[i] = required;
                execution.status = MultiALUPipelineSlotStatus.EXECUTED;
            }
        }

    }

    private void decode() {
        if (decode == null || decode.status == MultiALUPipelineSlotStatus.REMOVED) return;
        if (decode.exception != null) {
            memory.status = MultiALUPipelineSlotStatus.EXECUTED;
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
                decode.status = MultiALUPipelineSlotStatus.EXECUTED;
            } catch (RAWHazardException ex) {
                decode.status = MultiALUPipelineSlotStatus.RAW;
                raws++;
            } catch (MIPSInterruptException ex) {
                decode.exception = ex;
                decode.status = MultiALUPipelineSlotStatus.EXECUTED;
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
            fetch.status = MultiALUPipelineSlotStatus.EXECUTED;
            return;
        }

        fetch = new MultiALUPipelineSlot(null, pcv, null, MultiALUPipelineSlotStatus.EXECUTED);


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
            if (e == null) continue;
            if (e.status == MultiALUPipelineSlotStatus.REMOVED) {
                alus.releaseALU(i);
                execute[i] = null;
                if (e.execution != null) e.execution.unlockAll();
                continue;
            }
            if (e.status != MultiALUPipelineSlotStatus.EXECUTED) continue;
            if (!e.execution.canMoveToMemory(
                    execute,
                    memory == null ? null : memory.execution,
                    writeback == null ? null : writeback.execution
            )) {
                e.status = MultiALUPipelineSlotStatus.WAW;
                waws++;
                continue;
            }
            if (!move || executionId > e.execution.getInstructionId()) {
                if (move) {
                    execute[executionToMove].status = MultiALUPipelineSlotStatus.STALL;
                    otherStalls++;
                }
                move = true;
                executionToMove = i;
                executionId = e.execution.getInstructionId();
            } else {
                e.status = MultiALUPipelineSlotStatus.STALL;
                otherStalls++;
            }
        }

        if (move) {
            alus.releaseALU(executionToMove);
            memory = execute[executionToMove];
            execute[executionToMove] = null;
        }
    }

    private void shiftDecodeToExecute() {
        var pair = alus.requestALU(decode.execution
                .getInstruction().getBasicOrigin().getDefaultALUType());
        if (pair.isPresent()) {
            var index = pair.get().getKey();
            var alu = pair.get().getValue();
            assignedALUs[index] = alu;
            timesExecuted[index] = 0;
            execute[index] = decode;
            decode = null;
        } else {
            decode.status = MultiALUPipelineSlotStatus.STALL;
            otherStalls++;
        }
    }

}
