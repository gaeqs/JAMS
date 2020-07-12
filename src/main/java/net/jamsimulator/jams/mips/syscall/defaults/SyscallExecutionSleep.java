package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionSleep implements SyscallExecution {

	public static final String NAME = "SLEEP";
	private final int register;

	public SyscallExecutionSleep(int register) {
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

		try {
			Thread.sleep(register.getValue());
		} catch (InterruptedException e) {
			simulation.interrupt();
		}
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionSleep> {

		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
		}

		@Override
		public SyscallExecutionSleep build() {
			return new SyscallExecutionSleep(register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionSleep> makeNewInstance() {
			return new Builder();
		}
	}
}
