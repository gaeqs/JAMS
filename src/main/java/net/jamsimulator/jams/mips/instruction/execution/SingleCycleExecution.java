package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.Simulation;

public abstract class SingleCycleExecution<Inst extends AssembledInstruction> extends InstructionExecution<SingleCycleArchitecture, Inst> {

	public SingleCycleExecution(Simulation<SingleCycleArchitecture> simulation, Inst instruction) {
		super(simulation, instruction);
	}

	public abstract void execute();
}
