package net.jamsimulator.jams.mips.syscall.defaults;

import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionExit implements SyscallExecution {

	public static final String NAME = "EXIT";

	public SyscallExecutionExit() {
	}

	@Override
	public void execute(Simulation<?> simulation) {
		simulation.exit();
		simulation.getConsole().println();
		simulation.getConsole().printDoneLn("Execution finished successfully");
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
	}
}
