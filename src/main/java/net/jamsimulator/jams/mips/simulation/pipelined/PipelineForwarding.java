package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.register.Register;

import java.util.OptionalInt;

public class PipelineForwarding {

	private Register execution, memory;
	private int executionValue, memoryValue;

	public void forward(Register register, int value, boolean memory) {
		if (memory) {
			this.memory = register;
			this.memoryValue = value;
		} else {
			this.execution = register;
			this.executionValue = value;
		}
	}

	public OptionalInt get(Register register) {
		if (register.equals(execution)) {
			return OptionalInt.of(executionValue);
		}
		if (register.equals(memory)) {
			return OptionalInt.of(memoryValue);
		}

		return OptionalInt.empty();
	}

}
