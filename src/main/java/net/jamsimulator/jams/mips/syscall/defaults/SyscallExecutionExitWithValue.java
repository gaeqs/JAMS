package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionExitWithValue implements SyscallExecution {

	public static final String NAME = "EXIT_WITH_VALUE";

	private final int register;

	public SyscallExecutionExitWithValue(int register) {
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

		simulation.exit();
		simulation.getConsole().println();
		simulation.getConsole().printDoneLn("Execution finished with code " + register.getValue());
		simulation.getConsole().println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionExitWithValue> {

		private final SimpleIntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
		}

		@Override
		public SyscallExecutionExitWithValue build() {
			return new SyscallExecutionExitWithValue(register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionExitWithValue> makeNewInstance() {
			return new Builder();
		}
	}
}