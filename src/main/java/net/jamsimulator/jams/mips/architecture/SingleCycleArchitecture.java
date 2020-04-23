package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SingleCycleArchitecture extends Architecture {

	public static final SingleCycleArchitecture INSTANCE = new SingleCycleArchitecture();

	public static final String NAME = "Single Cycle";

	private SingleCycleArchitecture() {
		super(NAME);
	}

	@Override
	public Simulation<Architecture> createSimulation() {
		return null;
	}
}
