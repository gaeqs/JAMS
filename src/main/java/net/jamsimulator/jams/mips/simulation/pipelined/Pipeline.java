package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the instructions' pipeline of a {@link PipelinedSimulation}.
 */
public class Pipeline {

    private final PipelinedSimulation simulation;

    private final MultiCycleExecution<?>[] instructions;
    private final int[] pcs;
    private final MIPSInterruptException[] exceptions;

    /**
     * Creates the pipeline.
     *
     * @param simulation the {@link PipelinedSimulation} of this pipeline.
     * @param initialPc  the address of the first instruction to fetch.
     */
    public Pipeline(PipelinedSimulation simulation, int initialPc) {
        this.simulation = simulation;
        instructions = new MultiCycleExecution[MultiCycleStep.values().length];
        pcs = new int[MultiCycleStep.values().length];
        exceptions = new MIPSInterruptException[MultiCycleStep.values().length];
        pcs[0] = initialPc;
    }

    /**
     * Returns a {@link Map} containing all instructions inside this pipeline.
     * <p>
     * The returned {@link Map} is created at the execution of this method, as
     * instructions are stored inside an array in the implementation of the pipeline.
     *
     * @return the {@link Map}.
     */
    public Map<MultiCycleStep, MultiCycleExecution<?>> getAll() {
        var map = new HashMap<MultiCycleStep, MultiCycleExecution<?>>();
        for (int i = 0; i < instructions.length; i++) {
            if (instructions[i] != null) map.put(MultiCycleStep.values()[i], instructions[i]);
        }
        return map;
    }

    /**
     * Returns the {@link MultiCycleExecution instruction} located at the given {@link MultiCycleStep step}.
     * <p>
     * The instruction may be null.
     *
     * @param step the {@link MultiCycleStep step}.
     * @return the {@link MultiCycleExecution instruction} or {@code null}.
     */
    public MultiCycleExecution<?> get(MultiCycleStep step) {
        return instructions[step.ordinal()];
    }

    /**
     * Returns the {@link MultiCycleExecution instruction} located at the given {@link MultiCycleStep}, if present.
     *
     * @param step the {@link MultiCycleStep step}.
     * @return the {@link MultiCycleExecution instruction} or {@code Optional.empty()} if not present.
     */
    public Optional<MultiCycleExecution<?>> getSafe(MultiCycleStep step) {
        return Optional.ofNullable(instructions[step.ordinal()]);
    }

    /**
     * Returns the address of the {@link MultiCycleExecution instruction}
     * located at the given {@link MultiCycleStep step}.
     * <p>
     * If the instruction is null, this method returns 0.
     *
     * @param step the {@link MultiCycleStep step}.
     * @return the address.
     */
    public int getPc(MultiCycleStep step) {
        return pcs[step.ordinal()];
    }

    /**
     * Returns the {@link MIPSInterruptException} of the {@link MultiCycleExecution instruction}
     * located at the given {@link MultiCycleStep step}.
     * <p>
     * This exception may be null if no exceptions were thrown or whether the instruction is {@code null}.
     *
     * @param step the {@link MultiCycleStep step}.
     * @return the {@link MIPSInterruptException} or null.
     */
    public MIPSInterruptException getException(MultiCycleStep step) {
        return exceptions[step.ordinal()];
    }

    /**
     * Returns the {@link MIPSInterruptException} of the {@link MultiCycleExecution instruction}
     * located at the given {@link MultiCycleStep step} if present.
     * <p>
     * This exception may be {@code Optional.empty()} if no exceptions were thrown
     * or whether the instruction is {@code null}.
     *
     * @param step the {@link MultiCycleStep step}.
     * @return the {@link MIPSInterruptException} if present.
     */
    public Optional<MIPSInterruptException> getExceptionSafe(MultiCycleStep step) {
        return Optional.ofNullable(exceptions[step.ordinal()]);
    }

    /**
     * Returns the {@link MultiCycleStep step} of the {@link MultiCycleExecution instruction} that
     * matches the given address.
     * <p>
     * This step may be null if no {@link MultiCycleExecution instruction} matches the address.
     *
     * @param pc the address.
     * @return the {@link MultiCycleStep step}.
     */
    public MultiCycleStep getStepOf(int pc) {
        for (int i = 0; i < pcs.length; i++) {
            if (pc == pcs[i]) return MultiCycleStep.values()[i];
        }
        return null;
    }

    /**
     * Returns whether the pipeline has no instructions inside.
     *
     * @return whether the pipeline has no instructions inside.
     */
    public boolean isEmpty() {
        for (int pc : pcs) {
            if (pc != 0) return false;
        }
        return true;
    }

    /**
     * Resets the pipeline, removing any {@link MultiCycleExecution instruction}.
     *
     * @param initialPc the address of the first instruction to fetch.
     */
    public void reset(int initialPc) {
        Arrays.fill(instructions, null);
        Arrays.fill(pcs, 0);
        Arrays.fill(exceptions, null);
        pcs[0] = initialPc;
    }

    /**
     * Shifts the {@link MultiCycleExecution instruction}s inside this pipeline to their next step.
     * <p>
     * Starting at the WriteBack step, this method will shift the given amount of instructions.
     * <p>
     * If {@code amount} is 0, no instructions will be shifted.
     * If {@code amount} is 5, all instructions will be shifted.
     *
     * @param pc     the address of the next instruction to fetch. This value will only be used if {@code amount} is 5.
     * @param amount the amount of instructions to move.
     */
    public void shift(int pc, int amount) {
        if (simulation.getData().canCallEvents()) {
            simulation.callEvent(new PipelineShiftEvent.Before(simulation, this, pc, amount));
            shift0(pc, amount);
            simulation.callEvent(new PipelineShiftEvent.After(simulation, this, pc, amount));
        } else {
            shift0(pc, amount);
        }
    }

    /**
     * Fetches the given {@link MultiCycleExecution instruction}, placing it inside the pipeline.
     *
     * @param execution the {@link MultiCycleExecution instruction} to fetch.
     */
    public void fetch(MultiCycleExecution<?> execution) {
        instructions[0] = execution;
    }

    /**
     * Sets the given {@link MIPSInterruptException} to the {@link MultiCycleExecution instruction}
     * located at the given {@link MultiCycleStep step}.
     * <p>
     * This method will override the stored {@link MIPSInterruptException}.
     *
     * @param step the {@link MultiCycleStep step}.
     * @param ex   the {@link MIPSInterruptException}.
     */
    public void setException(MultiCycleStep step, MIPSInterruptException ex) {
        exceptions[step.ordinal()] = ex;
    }

    /**
     * Removes the {@link MultiCycleExecution instruction} to fetch.
     * <p>
     * This method is used when a branch jumps and forwarding is enabled or when an exit syscall is executed.
     */
    public void removeFetch() {
        instructions[0] = null;
        pcs[0] = 0;
        exceptions[0] = null;
    }

    /**
     * Removes the {@link MultiCycleExecution instruction} at the decode step.
     * <p>
     * This method is used when an exit syscall is executed.
     */
    public void removeDecode() {
        instructions[1] = null;
        pcs[1] = 0;
        exceptions[1] = null;
    }

    /**
     * Returns the {@link MultiCycleStep step} of the oldest instruction inside this pipeline.
     * <p>
     * If no instructions are found, this method returns FETCH.
     *
     * @return the {@link MultiCycleStep step}.
     */
    public MultiCycleStep getOldestStep() {
        for (int i = pcs.length - 1; i >= 0; i--) {
            if (pcs[i] != 0) return MultiCycleStep.values()[i];
        }
        return MultiCycleStep.FETCH;
    }

    /**
     * Clones this pipeline.
     *
     * @return the cloned pipeline.
     */
    public Pipeline clone() {
        Pipeline clone = new Pipeline(simulation, 0);
        System.arraycopy(instructions, 0, clone.instructions, 0, instructions.length);
        System.arraycopy(pcs, 0, clone.pcs, 0, pcs.length);
        System.arraycopy(exceptions, 0, clone.exceptions, 0, exceptions.length);
        return clone;
    }

    /**
     * Matches the data of the given pipeline.
     *
     * @param clone the pipeline to match
     */
    public void restore(Pipeline clone) {
        System.arraycopy(clone.instructions, 0, instructions, 0, instructions.length);
        System.arraycopy(clone.pcs, 0, pcs, 0, pcs.length);
        System.arraycopy(clone.exceptions, 0, exceptions, 0, exceptions.length);
    }

    private void shift0(int pc, int amount) {
        if (amount == 0) return;
        if (amount == 1) {
            instructions[instructions.length - 1] = null;
            pcs[instructions.length - 1] = 0;
            exceptions[instructions.length - 1] = null;
        }

        int i = 1;
        int index = instructions.length - 2;

        while (i < amount && index >= 0) {
            instructions[index + 1] = instructions[index];
            pcs[index + 1] = pcs[index];
            exceptions[index + 1] = exceptions[index];

            i++;
            index--;
        }

        instructions[index + 1] = null;
        pcs[index + 1] = index == -1 ? pc : 0;
        exceptions[index + 1] = null;
    }

}
