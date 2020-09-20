package net.jamsimulator.jams.mips.simulation.pipelined;

import net.jamsimulator.jams.mips.register.Register;

import java.util.OptionalInt;

/**
 * This class implements the forwarding algorithm for {@link PipelinedSimulation}s.
 */
public class PipelineForwarding {

	private Register execution, memory;
	private int executionValue, memoryValue;

	/**
	 * Forwards the given value for the given {@link Register}.
	 * <p>
	 * If {@code memory} is true this value will be stored at the memory slot.
	 * Else it will be stored at the execution slot.
	 *
	 * @param register the {@link Register}.
	 * @param value    the value to forward.
	 * @param memory   whether the value should be stored at the memory slot.
	 */
	public void forward(Register register, int value, boolean memory) {
		if (memory) {
			this.memory = register;
			this.memoryValue = value;
		} else {
			this.execution = register;
			this.executionValue = value;
		}
	}

	/**
	 * Returns the forwarded value of the given {@link Register} if present.
	 *
	 * @param register the {@link Register}.
	 * @return the value if present.
	 */
	public OptionalInt get(Register register) {
		if (register.equals(memory)) {
			return OptionalInt.of(memoryValue);
		}

		if (register.equals(execution)) {
			return OptionalInt.of(executionValue);
		}

		return OptionalInt.empty();
	}

}
