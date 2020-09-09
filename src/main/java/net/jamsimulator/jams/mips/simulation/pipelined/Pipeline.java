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
		for (int pc : pcs) {
			if (pc != 0) return false;
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

	public void removeDecode() {
		instructions[1] = null;
		pcs[1] = 0;
		exceptions[1] = null;
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
