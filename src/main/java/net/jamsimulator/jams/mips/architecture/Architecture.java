package net.jamsimulator.jams.mips.architecture;

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

	public abstract Simulation<Architecture> createSimulation();
}
