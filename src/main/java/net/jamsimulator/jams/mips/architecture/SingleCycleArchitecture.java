package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SingleCycleSimulation;

/**
 * Represents the Single cycle architecture.
 * <p>
 * In this architecture every instruction takes one cycle to execute.
 */
public class SingleCycleArchitecture extends Architecture {

	public static final SingleCycleArchitecture INSTANCE = new SingleCycleArchitecture();

	public static final String NAME = "Single Cycle";

	private SingleCycleArchitecture() {
		super(NAME);
	}

	@Override
	public Simulation<SingleCycleArchitecture> createSimulation(InstructionSet instructionSet, Registers registers, Memory memory) {
		return new SingleCycleSimulation(this, instructionSet, registers, memory);
	}
}
