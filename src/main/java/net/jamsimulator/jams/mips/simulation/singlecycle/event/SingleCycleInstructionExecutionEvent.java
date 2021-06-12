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

package net.jamsimulator.jams.mips.simulation.singlecycle.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.simulation.singlecycle.SingleCycleSimulation;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

public class SingleCycleInstructionExecutionEvent extends SingleCycleSimulationEvent {

    protected final long cycle;
    protected int instructionAddress;
    protected AssembledInstruction instruction;
    protected SingleCycleExecution<?> execution;

    SingleCycleInstructionExecutionEvent(SingleCycleSimulation simulation,
                                         long cycle,
                                         int instructionAddress,
                                         AssembledInstruction instruction,
                                         SingleCycleExecution<?> execution) {
        super(simulation);
        Validate.notNull(simulation, "Simulation cannot be null!");
        this.cycle = cycle;
        this.instructionAddress = instructionAddress;
        this.instruction = instruction;
        this.execution = execution;
    }

    public long getCycle() {
        return cycle;
    }

    public int getInstructionAddress() {
        return instructionAddress;
    }

    public Optional<AssembledInstruction> getInstruction() {
        return Optional.ofNullable(instruction);
    }

    public Optional<SingleCycleExecution<?>> getExecution() {
        return Optional.ofNullable(execution);
    }

    public static class Before extends SingleCycleInstructionExecutionEvent implements Cancellable {

        private boolean cancelled;

        public Before(SingleCycleSimulation simulation,
                      long cycle,
                      int instructionAddress,
                      AssembledInstruction instruction,
                      SingleCycleExecution<?> execution) {
            super(simulation, cycle, instructionAddress, instruction, execution);
        }

        public void setSingleCycleExecution(SingleCycleExecution<?> execution) {
            this.execution = execution;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After extends SingleCycleInstructionExecutionEvent {

        public After(SingleCycleSimulation simulation,
                     long cycle,
                     int instructionAddress,
                     AssembledInstruction instruction,
                     SingleCycleExecution<?> execution) {
            super(simulation, cycle, instructionAddress, instruction, execution);
        }
    }

}
