package net.jamsimulator.jams.mips.syscall.defaults;

import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.RuntimeInstructionException;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.Collections;

public class SyscallExecutionRunExceptionHandler implements SyscallExecution {

	public static final String NAME = "RUN_EXCEPTION_HANDLER";

	@Override
	public void execute(Simulation<?> simulation) {
		throw new RuntimeInstructionException(InterruptCause.SYSCALL_EXCEPTION);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRunExceptionHandler> {

		public Builder() {
			super(NAME, Collections.emptyList());
		}

		@Override
		public SyscallExecutionRunExceptionHandler build() {
			return new SyscallExecutionRunExceptionHandler();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRunExceptionHandler> makeNewInstance() {
			return new Builder();
		}
	}
}
