package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.interrupt.RuntimeInstructionException;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.Arrays;
import java.util.Optional;

public class Pipeline {

	private final PipelinedSimulation simulation;

	private final MultiCycleExecution<?>[] instructions;
	private final int[] pcs;
	private final RuntimeInstructionException[] exceptions;

	public Pipeline(PipelinedSimulation simulation, int initialPc) {
		this.simulation = simulation;
		instructions = new MultiCycleExecution[MultiCycleStep.values().length];
		pcs = new int[MultiCycleStep.values().length];
		exceptions = new RuntimeInstructionException[MultiCycleStep.values().length];
		pcs[0] = initialPc;
	}

	public MultiCycleExecution<?> get(MultiCycleStep step) {
		return instructions[step.ordinal()];
	}

	public Optional<MultiCycleExecution<?>> getSafe(MultiCycleStep step) {
		return Optional.ofNullable(instructions[step.ordinal()]);
	}

	public int getPc(MultiCycleStep step) {
		return pcs[step.ordinal()];
	}

	public RuntimeInstructionException getException(MultiCycleStep step) {
		return exceptions[step.ordinal()];
	}

	public Optional<RuntimeInstructionException> getExceptionSafe(MultiCycleStep step) {
		return Optional.ofNullable(exceptions[step.ordinal()]);
	}

	public MultiCycleStep getStepOf(int pc) {
		for (int i = 0; i < pcs.length; i++) {
			if (pc == pcs[i]) return MultiCycleStep.values()[i];
		}
		return null;
	}

	public boolean isEmpty() {
		for (MultiCycleExecution<?> instruction : instructions) {
			if (instruction != null) return false;
		}
		return true;
	}

	public void reset(int initialPc) {
		Arrays.fill(instructions, null);
		Arrays.fill(pcs, 0);
		Arrays.fill(exceptions, null);
		pcs[0] = initialPc;
	}

	public void shift(int pc, int amount) {
		if (simulation.getData().canCallEvents()) {
			simulation.callEvent(new PipelineShiftEvent.Before(simulation, this, pc));
			shift0(pc, amount);
			simulation.callEvent(new PipelineShiftEvent.After(simulation, this, pc));
		} else {
			shift0(pc, amount);
		}
	}

	public void fetch(MultiCycleExecution<?> execution) {
		instructions[0] = execution;
	}

	public void setException(MultiCycleStep step, RuntimeInstructionException ex) {
		exceptions[step.ordinal()] = ex;
	}

	public void removeFetch() {
		instructions[0] = null;
		pcs[0] = 0;
		exceptions[0] = null;
	}

	public Pipeline clone() {
		Pipeline clone = new Pipeline(simulation, 0);
		System.arraycopy(instructions, 0, clone.instructions, 0, instructions.length);
		System.arraycopy(pcs, 0, clone.pcs, 0, pcs.length);
		System.arraycopy(exceptions, 0, clone.exceptions, 0, exceptions.length);
		return clone;
	}

	public void restore(Pipeline clone) {
		System.arraycopy(clone.instructions, 0, instructions, 0, instructions.length);
		System.arraycopy(clone.pcs, 0, pcs, 0, pcs.length);
		System.arraycopy(clone.exceptions, 0, exceptions, 0, exceptions.length);
	}

	private void shift0(int pc, int amount) {
		int i;
		for (i = instructions.length - 2; i >= 0 && amount >= instructions.length - i - 1; i--) {
			instructions[i + 1] = instructions[i];
			pcs[i + 1] = pcs[i];
			exceptions[i + 1] = exceptions[i];
		}

		if (amount >= instructions.length) {
			instructions[0] = null;
			pcs[0] = pc;
			exceptions[0] = null;
		} else {
			instructions[i] = null;
			pcs[i] = 0;
			exceptions[i] = null;
		}
	}

}
