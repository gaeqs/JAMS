package net.jamsimulator.jams.mips.syscall;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public interface SyscallExecution {

	void execute(MIPSSimulation<?> simulation);

	void executeMultiCycle(MultiCycleExecution<?> execution);

}
