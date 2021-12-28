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

package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.MultiAPUPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.AbstractPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelineForwarding;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.exception.RAWHazardException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MultiCycleExecution<Arch extends MultiCycleArchitecture, Inst extends AssembledInstruction> extends InstructionExecution<Arch, Inst> {

    private final Map<Register, Integer> decodedRegisters = new HashMap<>();
    private final Set<Register> lockedRegisters = new HashSet<>();

    protected final boolean forwardingEnabled;
    protected final boolean solveBranchesOnDecode;
    protected final boolean delaySlotsEnabled;
    private final PipelineForwarding forwarding;

    protected final boolean executesMemory, executesWriteBack;

    /**
     * Result of the decode step.
     * DO NOT use this to storage register values:
     * they will be stored separatly using the method requires().
     */
    protected int[] decodeResult;

    /**
     * Result of the execution step.
     */
    protected int[] executionResult;

    /**
     * Result of the memory step.
     */
    protected int[] memoryResult;

    protected long instructionId;

    protected boolean inDelaySlot;

    public MultiCycleExecution(MIPSSimulation<? extends Arch> simulation, Inst instruction, int address,
                               boolean executesMemory, boolean executesWriteBack) {
        super(simulation, instruction, address);

        if (simulation instanceof AbstractPipelinedSimulation s) {
            forwardingEnabled = s.isForwardingEnabled();
            solveBranchesOnDecode = s.solvesBranchesOnDecode();
            delaySlotsEnabled = s.isDelaySlotsEnabled();
            forwarding = forwardingEnabled ? s.getForwarding() : null;
        } else {
            forwardingEnabled = false;
            solveBranchesOnDecode = false;
            delaySlotsEnabled = false;
            forwarding = null;
        }

        this.executesMemory = executesMemory;
        this.executesWriteBack = executesWriteBack;
        this.inDelaySlot = false;
    }

    /**
     * Returns the multi-cycle id of this instruction.
     * This is used by the flow section.
     *
     * @return the id.
     */
    public long getInstructionId() {
        return instructionId;
    }

    /**
     * THIS METHOD CAN BE USED ONLY BY SIMULATIONS!
     * <p>
     * Sets the multi-cycle id of this instruction.
     *
     * @param instructionId the id.
     */
    public void setInstructionId(long instructionId) {
        this.instructionId = instructionId;
    }

    /**
     * Returns whether this instruction is being executed in a delay slot.
     *
     * @return whether this instruction is being executed in a delay slot.
     */
    public boolean isInDelaySlot() {
        return inDelaySlot;
    }

    /**
     * Sets whether this instruction is being executed in a delay slot.
     *
     * @param inDelaySlot whether this instruction is being executed in a delay slot.
     */
    public void setInDelaySlot(boolean inDelaySlot) {
        this.inDelaySlot = inDelaySlot;
    }

    public boolean executesMemory() {
        return executesMemory;
    }

    public boolean executesWriteBack() {
        return executesWriteBack;
    }

    public boolean solveBranchOnDecode() {
        return solveBranchesOnDecode;
    }

    //region requires

    public void requires(int identifier) {
        requires(register(identifier));
    }

    public void requiresCOP0(int identifier) {
        requires(registerCop0(identifier));
    }

    public void requiresCOP0(int identifier, int sel) {
        requires(registerCop0(identifier, sel));
    }

    public void requiresCOP1(int identifier) {
        requires(registerCop1(identifier));
    }

    public void requires(Register register) {
        if (!register.isLocked()) {
            decodedRegisters.put(register, register.getValue());
            return;
        }

        if (forwarding != null) {
            var forwarded = forwarding.get(register);
            if (forwarded.isPresent()) {
                decodedRegisters.put(register, forwarded.getAsInt());
                return;
            }
        }

        throw new RAWHazardException(register);
    }

    //endregion

    //region value

    public int value(int identifier) {
        return value(register(identifier));
    }

    public int valueCOP0(int identifier) {
        return value(registerCop0(identifier));
    }

    public int valueCOP0(int identifier, int sel) {
        return value(registerCop0(identifier, sel));
    }

    public int valueCOP1(int identifier) {
        return value(registerCop1(identifier));
    }

    public int value(Register register) {
        Integer value = decodedRegisters.get(register);
        if (value != null) return value;
        throw new IllegalStateException("Use of value(" + register.getIdentifier() + ") without using requires()");
    }

    //endregion value

    //region lock

    public boolean canMoveToMemory() {
        return lockedRegisters.stream().allMatch(it -> it.isFirstLocked(this));
    }

    public void lock(int identifier) {
        lock(register(identifier));
    }

    public void lockCOP0(int identifier) {
        lock(registerCop0(identifier));
    }

    public void lockCOP0(int identifier, int sel) {
        lock((registerCop0(identifier, sel)));
    }

    public void lockCOP1(int identifier) {
        lock(registerCop1(identifier));
    }

    public void lock(Register register) {
        register.lock(this);
        lockedRegisters.add(register);
    }

    //endregion

    //region unlock

    public void unlock(int identifier) {
        register(identifier).unlock(this);
    }

    public void unlockCOP0(int identifier) {
        registerCop0(identifier).unlock(this);
    }

    public void unlockCOP0(int identifier, int sel) {
        registerCop0(identifier, sel).unlock(this);
    }

    public void unlockCOP1(int identifier) {
        registerCop1(identifier).unlock(this);
    }

    public void unlock(Register register) {
        lockedRegisters.remove(register);
        register.unlock(this);
    }

    public void unlockAll() {
        lockedRegisters.forEach(r -> r.unlock(this));
        lockedRegisters.clear();
    }

    //endregion

    //region set and unlock

    public void setAndUnlock(int identifier, int value) {
        setAndUnlock(register(identifier), value);
    }

    public void setAndUnlockCOP0(int identifier, int value) {
        setAndUnlock(registerCop0(identifier), value);
    }

    public void setAndUnlockCOP0(int identifier, int sel, int value) {
        setAndUnlock(registerCop0(identifier, sel), value);
    }

    public void setAndUnlockCOP1(int identifier, int value) {
        setAndUnlock(registerCop1(identifier), value);
    }

    public void setAndUnlock(Register register, int value) {
        lockedRegisters.remove(register);
        register.unlock(this);
        register.setValue(value);
    }

    //endregion

    //region forward

    public void forward(int identifier, int value, boolean memory) {
        forward(register(identifier), value, memory);
    }

    public void forwardCOP0(int identifier, int value, boolean memory) {
        forward(registerCop0(identifier), value, memory);
    }

    public void forwardCOP0(int identifier, int sel, int value, boolean memory) {
        forward(registerCop0(identifier, sel), value, memory);
    }

    public void forwardCOP1(int identifier, int value, boolean memory) {
        forward(registerCop1(identifier), value, memory);
    }

    public void forward(Register register, int value, boolean memory) {
        if (forwarding != null) {
            if (register.isLastLocked(this)) {
                forwarding.forward(register, value, memory);
            }
        }
    }

    //endregion

    public void jump(int address) {
        //The instruction should be a control transfer instruction to perform a jump.
        if (!(instruction.getBasicOrigin() instanceof ControlTransferInstruction))
            throw new IllegalStateException("The instruction " + instruction.getBasicOrigin() + " is not a control transfer instruction!");

        if (simulation instanceof PipelinedSimulation) {
            if (!delaySlotsEnabled || ((ControlTransferInstruction) instruction.getBasicOrigin()).isCompact()) {
                ((PipelinedSimulation) simulation).getPipeline().removeFetch();

                setAndUnlock(pc(), address);
            } else {
                //The fetch is not cancelled. If there's an instruction to fetch,
                //the next one will be fetched at address + 4. We do not want that!
                //The instruction at the fetch slot will always be null, so we check its PC instead.
                boolean willFetch = ((PipelinedSimulation) simulation).getPipeline().getPc(MultiCycleStep.FETCH) != 0;
                setAndUnlock(pc(), willFetch ? address - 4 : address);
            }
        } else {
            setAndUnlock(pc(), address);
        }

        if (simulation instanceof MultiAPUPipelinedSimulation) {
            if (!delaySlotsEnabled || ((ControlTransferInstruction) instruction.getBasicOrigin()).isCompact()) {
                ((MultiAPUPipelinedSimulation) simulation).getPipeline().removeFetch();

                setAndUnlock(pc(), address);
            } else {
                //The fetch is not cancelled. If there's an instruction to fetch,
                //the next one will be fetched at address + 4. We do not want that!
                //The instruction at the fetch slot will always be null, so we check its PC instead.
                boolean willFetch = ((MultiAPUPipelinedSimulation) simulation)
                        .getPipeline().getFetch() != null;
                setAndUnlock(pc(), willFetch ? address - 4 : address);
            }
        }
    }

    public abstract void decode();

    public abstract void execute();

    public abstract void memory();

    public abstract void writeBack();
}
