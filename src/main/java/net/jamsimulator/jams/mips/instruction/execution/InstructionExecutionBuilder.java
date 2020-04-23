package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.Simulation;

public interface InstructionExecutionBuilder<Arch extends Architecture, Inst extends AssembledInstruction> {

	InstructionExecution<Arch, Inst> create(Simulation<Arch> simulation, Inst instruction);

}
