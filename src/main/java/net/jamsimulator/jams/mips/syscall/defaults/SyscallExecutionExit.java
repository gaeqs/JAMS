package net.jamsimulator.jams.mips.syscall.defaults;

import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionExit implements SyscallExecution {

	public static final String NAME = "EXIT";

	public SyscallExecutionExit() {
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		simulation.requestExit();
		if (simulation.getConsole() != null) {
			simulation.getConsole().println();
			simulation.getConsole().printDoneLn("Execution finished successfully");
			simulation.getConsole().println();
		}
		simulation.callEvent(new SimulationFinishedEvent(simulation));
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		execute(execution.getSimulation());
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionExit> {

		public Builder() {
			super(NAME, new LinkedList<>());
		}

		@Override
		public SyscallExecutionExit build() {
			return new SyscallExecutionExit();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionExit> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionExit> copy() {
			return new Builder();
		}
	}
}
