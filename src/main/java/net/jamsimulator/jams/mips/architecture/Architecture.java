package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * Architectures tell JAMS how instructions are executed.
 * <p>
 * Each architecture is made by different elements and can run instructions
 * in a completely different way.
 */
public abstract class Architecture {

	private final String name;

	public Architecture(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the architecture. This name must be unique.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates a simulation of this architecture using the given parameters.
	 *
	 * @param instructionSet the {@link InstructionSet} to use.
	 * @param registers      the {@link Registers}.
	 * @param memory         the {@link Memory}.
	 * @return the {@link Simulation}.
	 */
	public abstract Simulation<? extends Architecture> createSimulation(InstructionSet instructionSet, Registers registers, Memory memory);
}
