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
import net.jamsimulator.jams.mips.instruction.basic.MemoryInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.multialupipelined.AbstractPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelineSlot;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.multialupipelined.exception.RAWHazardException;

import java.util.*;

public abstract class MultiCycleExecution<Arch extends MultiCycleArchitecture, Inst extends AssembledInstruction> extends InstructionExecution<Arch, Inst> {

    private final Map<Register, Integer> decodedRegisters = new HashMap<>();
    private final Map<Register, Integer> forwardingRegisters;
    private final Set<Register> lockedRegisters = new HashSet<>();

    protected final boolean forwardingEnabled;
    protected final boolean solveBranchesOnDecode;
    protected final boolean delaySlotsEnabled;
    protected final AbstractPipelinedSimulation pipelinedSimulation;

    protected final boolean executesMemory, executesWriteBack;

    protected long instructionId;

    protected boolean inDelaySlot;

    public MultiCycleExecution(MIPSSimulation<? extends Arch> simulation, Inst instruction, int address,
                               boolean executesMemory, boolean executesWriteBack) {
        super(simulation, instruction, address);

        if (simulation instanceof AbstractPipelinedSimulation s) {
            forwardingEnabled = s.isForwardingEnabled();
            solveBranchesOnDecode = s.solvesBranchesOnDecode();
            delaySlotsEnabled = s.isDelaySlotsEnabled();
            forwardingRegisters = new HashMap<>();
            pipelinedSimulation = s;
        } else {
            forwardingEnabled = false;
            solveBranchesOnDecode = false;
            delaySlotsEnabled = false;
            forwardingRegisters = null;
            pipelinedSimulation = null;
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

    public void requires(int identifier, boolean requiredOnMemory) {
        requires(register(identifier), requiredOnMemory);
    }

    public void requiresCOP0(int identifier, boolean requiredOnMemory) {
        requires(registerCOP0(identifier), requiredOnMemory);
    }

    public void requiresCOP0(int identifier, int sel, boolean requiredOnMemory) {
        requires(registerCOP0(identifier, sel), requiredOnMemory);
    }

    public void requiresCOP1(int identifier, boolean requiredOnMemory) {
        requires(registerCOP1(identifier), requiredOnMemory);
    }

    public void requiresCOP1Double(int identifier, boolean requiredOnMemory) {
        requires(registerCOP1(identifier), requiredOnMemory);
        requires(registerCOP1(identifier + 1), requiredOnMemory);
    }

    public void requires(Register register, boolean requiredOnMemory) {
        if (!register.isLocked() || register.isLockedOnlyBy(this)) {
            decodedRegisters.put(register, register.getValue());
            return;
        }

        if (forwardingEnabled) {
            // The value will ALWAYS be available on the memory step
            // (forwarded by the WB step or given by the registrer).
            if (requiredOnMemory) return;

            // The value will be required ON THE NEXT STEP. The value will be already forwarded in this step.
            // (The decode step is always the last step to be executed).
            // We can check the forwarded value now!
            var forwarded = pipelinedSimulation.forward(register, this, false);
            if (forwarded.isPresent()) {
                decodedRegisters.put(register, forwarded.getAsInt());
                return;
            }
        }

        throw new RAWHazardException(register);
    }

    //endregion

    //region value

    @Override
    public int value(int identifier) {
        return value(register(identifier));
    }

    @Override
    public int valueCOP0(int identifier) {
        return value(registerCOP0(identifier));
    }

    @Override
    public int valueCOP0(int identifier, int sel) {
        return value(registerCOP0(identifier, sel));
    }

    @Override
    public int valueCOP1(int identifier) {
        return value(registerCOP1(identifier));
    }

    public int value(Register register) {
        Integer value = decodedRegisters.get(register);
        if (value != null) return value;

        // The value might be a value required by the memory step. Let's check the register:
        if (!register.isLockedBeforeId(instructionId)) {
            return register.getValue();
        }

        // The register is locked by the WB execution. Let's check it:
        var forwarded = pipelinedSimulation.forward(register, this, true);
        if (forwarded.isPresent()) {
            decodedRegisters.put(register, forwarded.getAsInt());
            return forwarded.getAsInt();
        }

        throw new IllegalStateException("Value of register (" + register.getIdentifier() +
                ") is still locked! Locked by: " + register.printLockingExecutions());
    }

    //endregion value

    //region lock

    public boolean canMoveToMemory(MultiALUPipelineSlot[] execute,
                                   MultiCycleExecution<?, ?> memory,
                                   MultiCycleExecution<?, ?> writeback) {

        // Check for older memory instructions if this instruction is a memory instruction.
        // Ignore RAR 'hazards'.
        if (instruction.getBasicOrigin() instanceof MemoryInstruction memInstruction) {
            for (MultiALUPipelineSlot current : execute) {
                if (current == null || current.execution == this) continue;
                if (current.execution.instructionId < instructionId &&
                        current.execution.instruction.getBasicOrigin() instanceof MemoryInstruction memC) {
                    if (memC.isWriteInstruction() || memInstruction.isWriteInstruction()) {
                        return false;
                    }
                }
            }
        }

        // Check for older branch instructions if branch on decode is not enabled.
        if (!solveBranchesOnDecode) {
            for (MultiALUPipelineSlot current : execute) {
                if (current == null || current.execution == this) continue;
                if (current.execution.instructionId < instructionId &&
                        current.execution.instruction.getBasicOrigin() instanceof ControlTransferInstruction) {
                    return false;
                }
            }
        }

        return lockedRegisters.stream().allMatch(it ->
                !it.isLockedBy(this) ||
                        it.isFirstLockedIgnoringMemoryAndWriteback(this, memory, writeback));
    }

    public void lock(int identifier) {
        lock(register(identifier));
    }

    public void lockCOP0(int identifier) {
        lock(registerCOP0(identifier));
    }

    public void lockCOP0(int identifier, int sel) {
        lock((registerCOP0(identifier, sel)));
    }

    public void lockCOP1(int identifier) {
        lock(registerCOP1(identifier));
    }

    public void lockCOP1Double(int identifier) {
        lock(registerCOP1(identifier));
        lock(registerCOP1(identifier + 1));
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
        registerCOP0(identifier).unlock(this);
    }

    public void unlockCOP0(int identifier, int sel) {
        registerCOP0(identifier, sel).unlock(this);
    }

    public void unlockCOP1(int identifier) {
        registerCOP1(identifier).unlock(this);
    }

    public void unlock(Register register) {
        register.unlock(this);
    }

    public void unlockAll() {
        lockedRegisters.forEach(r -> r.unlock(this));
    }

    //endregion

    //region set and unlock

    public void setAndUnlock(int identifier, int value) {
        setAndUnlock(register(identifier), value);
    }

    public void setAndUnlockCOP0(int identifier, int value) {
        setAndUnlock(registerCOP0(identifier), value);
    }

    public void setAndUnlockCOP0(int identifier, int sel, int value) {
        setAndUnlock(registerCOP0(identifier, sel), value);
    }

    public void setAndUnlockCOP1(int identifier, int value) {
        setAndUnlock(registerCOP1(identifier), value);
    }

    public void setAndUnlockCOP1(int identifier, float value) {
        setAndUnlock(registerCOP1(identifier), Float.floatToIntBits(value));
    }

    public void setAndUnlock(Register register, int value) {
        register.unlock(this);
        register.setValue(value);
    }

    //endregion

    //region forward

    public void forward(int identifier, int value) {
        forward(register(identifier), value);
    }

    public void forwardCOP0(int identifier, int value) {
        forward(registerCOP0(identifier), value);
    }

    public void forwardCOP0(int identifier, int sel, int value) {
        forward(registerCOP0(identifier, sel), value);
    }

    public void forwardCOP1(int identifier, int value) {
        forward(registerCOP1(identifier), value);
    }

    public void forwardCOP1(int identifier, float value) {
        forward(registerCOP1(identifier), Float.floatToIntBits(value));
    }

    public void forward(Register register, int value) {
        if (forwardingEnabled) {
            forwardingRegisters.put(register, value);
        }
    }

    public OptionalInt getForwardedValue(Register register) {
        var i = forwardingRegisters.get(register);
        return i == null ? OptionalInt.empty() : OptionalInt.of(i);
    }

    //endregion

    public void jump(int address) {
        //The instruction should be a control transfer instruction to perform a jump.
        if (!(instruction.getBasicOrigin() instanceof ControlTransferInstruction))
            throw new IllegalStateException("The instruction " + instruction.getBasicOrigin() + " is not a control transfer instruction!");

        if (simulation instanceof MultiALUPipelinedSimulation) {
            if (!solveBranchesOnDecode) {
                setAndUnlock(pc(), address);
                ((MultiALUPipelinedSimulation) simulation).getPipeline().executeFullJumpRemoval(instructionId);
            } else if (!delaySlotsEnabled || ((ControlTransferInstruction) instruction.getBasicOrigin()).isCompact()) {
                ((MultiALUPipelinedSimulation) simulation).getPipeline().removeFetch();
                setAndUnlock(pc(), address);
            } else {
                //The fetch is not cancelled. If there's an instruction to fetch,
                //the next one will be fetched at address + 4. We do not want that!
                //The instruction at the fetch slot will always be null, so we check its PC instead.
                boolean willFetch = ((MultiALUPipelinedSimulation) simulation)
                        .getPipeline().getFetch() != null;
                setAndUnlock(pc(), willFetch ? address - 4 : address);
            }
        } else {
            setAndUnlock(pc(), address);
        }
    }

    public abstract void decode();

    public abstract void execute();

    public abstract void memory();

    public abstract void writeBack();
}
