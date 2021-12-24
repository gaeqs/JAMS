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

package net.jamsimulator.jams.mips.simulation.multicycle.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * This event is called when a cycle is executed in a {@link MultiCycleSimulation}.
 */
public class MultiCycleStepEvent extends MultiCycleSimulationEvent {

    protected final long cycle, instructionNumber;
    protected final int instructionAddress;
    protected final MultiCycleStep executedStep;
    protected final AssembledInstruction instruction;
    protected MultiCycleExecution<?, ?> execution;


    /**
     * Creates the step event.
     *
     * @param simulation         the {@link MultiCycleSimulation} executing the step.
     * @param cycle              the current cycle.
     * @param instructionNumber  the amount of instructions executed before this instruction.
     * @param instructionAddress the address of the instruction to execute.
     * @param executedStep       the current step of the instruction's execution.
     * @param instruction        the instruction to execute.
     * @param execution          the execution of the instruction.
     */
    public MultiCycleStepEvent(MultiCycleSimulation simulation, long cycle, long instructionNumber, int instructionAddress,
                               MultiCycleStep executedStep,
                               AssembledInstruction instruction,
                               MultiCycleExecution<?, ?> execution) {
        super(simulation);
        this.cycle = cycle;
        this.instructionNumber = instructionNumber;
        this.instructionAddress = instructionAddress;
        this.executedStep = executedStep;
        this.instruction = instruction;
        this.execution = execution;
    }

    /**
     * Returns the current cycle of the simulation.
     *
     * @return the current cycle.
     */
    public long getCycle() {
        return cycle;
    }

    /**
     * Returns the amount of instructions executed before this instruction.
     *
     * @return the amount.
     */
    public long getInstructionNumber() {
        return instructionNumber;
    }

    /**
     * Returns the address of the instruction to execute.
     *
     * @return the address.
     */
    public int getInstructionAddress() {
        return instructionAddress;
    }

    /**
     * Returns the current step of the instruction's execution.
     *
     * @return the current step.
     */
    public MultiCycleStep getExecutedStep() {
        return executedStep;
    }

    /**
     * Returns the instruction to execute.
     *
     * @return the instruction.
     */
    public AssembledInstruction getInstruction() {
        return instruction;
    }

    /**
     * Returns the execution of the instruction, if present.
     *
     * @return the execution, if present.
     */
    public Optional<MultiCycleExecution<?, ?>> getExecution() {
        return Optional.ofNullable(execution);
    }

    /**
     * Sets the execution of the instruction. This may be null.
     * <p>
     * WARNING! This change will only be effective if the current step is {@link MultiCycleStep#FETCH}.
     *
     * @param execution the execution.
     */
    public void setExecution(MultiCycleExecution<?, ?> execution) {
        this.execution = execution;
    }

    public static class Before extends MultiCycleStepEvent implements Cancellable {

        private boolean cancelled;

        /**
         * Creates the before step event.
         *
         * @param simulation         the {@link MultiCycleSimulation} executing the step.
         * @param cycle              the current cycle.
         * @param instructionNumber  the amount of instructions executed before this instruction.
         * @param instructionAddress the address of the instruction to execute.
         * @param executedStep       the current step of the instruction's execution.
         * @param instruction        the instruction to execute.
         * @param execution          the execution of the instruction.
         */
        public Before(MultiCycleSimulation simulation, long cycle, long instructionNumber, int instructionAddress,
                      MultiCycleStep executedStep,
                      AssembledInstruction instruction,
                      MultiCycleExecution<?, ?> execution) {
            super(simulation, cycle, instructionNumber, instructionAddress, executedStep, instruction, execution);
            cancelled = false;
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


    public static class After extends MultiCycleStepEvent {


        /**
         * Creates the after step event.
         *
         * @param simulation         the {@link MultiCycleSimulation} executing the step.
         * @param cycle              the current cycle.
         * @param instructionNumber  the amount of instructions executed before this instruction.
         * @param instructionAddress the address of the instruction to execute.
         * @param executedStep       the current step of the instruction's execution.
         * @param instruction        the instruction to execute.
         * @param execution          the execution of the instruction.
         */
        public After(MultiCycleSimulation simulation, long cycle, long instructionNumber, int instructionAddress,
                     MultiCycleStep executedStep,
                     AssembledInstruction instruction,
                     MultiCycleExecution<?, ?> execution) {
            super(simulation, cycle, instructionNumber, instructionAddress, executedStep, instruction, execution);
            Validate.notNull(execution, "Execution cannot be null!");
        }

        public MultiCycleExecution<?, ?> getExecutionSafe() {
            return execution;
        }

    }

}
