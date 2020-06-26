package net.jamsimulator.jams.mips.syscall;

import net.jamsimulator.jams.mips.simulation.Simulation;

public interface SyscallExecution {

	void execute(Simulation<?> simulation);

}
