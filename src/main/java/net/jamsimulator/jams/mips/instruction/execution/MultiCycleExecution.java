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
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.AbstractPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.exception.RAWHazardException;

public abstract class MultiCycleExecution<Arch extends MultiCycleArchitecture, Inst extends AssembledInstruction> extends InstructionExecution<Arch, Inst> {

    protected final boolean forwardingEnabled;
    protected final boolean solveBranchesOnDecode;
    protected final boolean delaySlotsEnabled;

    protected int[] decodeResult;
    protected int[] executionResult;
    protected int[] memoryResult;
    protected long instructionId;

    protected boolean executesMemory, executesWriteBack;

    protected boolean inDelaySlot;

    public MultiCycleExecution(MIPSSimulation<? extends Arch> simulation, Inst instruction, int address,
                               boolean executesMemory, boolean executesWriteBack) {
        super(simulation, instruction, address);

        if (simulation instanceof AbstractPipelinedSimulation s) {
            forwardingEnabled = s.isForwardingEnabled();
            solveBranchesOnDecode = s.solvesBranchesOnDecode();
            delaySlotsEnabled = s.isDelaySlotsEnabled();
        } else {
            forwardingEnabled = false;
            solveBranchesOnDecode = false;
            delaySlotsEnabled = false;
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
        var supportsForwarding = simulation instanceof AbstractPipelinedSimulation && forwardingEnabled;

        if (register.isLocked() && !supportsForwarding) {
            throw new RAWHazardException(register);
        }
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
        if (!register.isLocked(this)) {
            return register.getValue();
        }

        if (simulation instanceof AbstractPipelinedSimulation) {
            var optional = ((AbstractPipelinedSimulation) simulation).getForwarding().get(register);
            if (optional.isPresent()) return optional.getAsInt();
        }

        throw new RAWHazardException(register);
    }

    //endregion value

    //region lock

    public void lock(int identifier) {
        register(identifier).lock(this);
    }

    public void lockCOP0(int identifier) {
        registerCop0(identifier).lock(this);
    }

    public void lockCOP0(int identifier, int sel) {
        registerCop0(identifier, sel).lock(this);
    }

    public void lockCOP1(int identifier) {
        registerCop1(identifier).lock(this);
    }

    public void lock(Register register) {
        register.lock(this);
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
        register.unlock(this);
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
        register.setValue(value);
        register.unlock(this);
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
        if (simulation instanceof AbstractPipelinedSimulation) {
            ((AbstractPipelinedSimulation) simulation).getForwarding().forward(register, value, memory);
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
    }

    public abstract void decode();

    public abstract void execute();

    public abstract void memory();

    public abstract void writeBack();
}
