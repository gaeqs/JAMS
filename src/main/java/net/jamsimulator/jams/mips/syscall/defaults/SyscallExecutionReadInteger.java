package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class SyscallExecutionReadInteger implements SyscallExecution {

	public static final String NAME = "READ_INTEGER";
	private final boolean lineJump;
	private final int register;

	public SyscallExecutionReadInteger(boolean lineJump, int register) {
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

		boolean done = false;
		while (!done) {
			String value = simulation.popInputOrLock();
			if (simulation.checkThreadInterrupted()) return;

			try {
				int input = NumericUtils.decodeInteger(value);
				register.setValue(input);

				simulation.getConsole().printDone(value);
				if (lineJump) simulation.getConsole().println();
				done = true;
			} catch (NumberFormatException ignore) {
			}
		}

	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();

		boolean done = false;
		while (!done) {
			String value = simulation.popInputOrLock();
			if (simulation.checkThreadInterrupted()) return;

			try {
				int input = NumericUtils.decodeInteger(value);
				execution.setAndUnlock(register, input);

				simulation.getConsole().printDone(value);
				if (lineJump) simulation.getConsole().println();
				done = true;
			} catch (NumberFormatException ignore) {
			}
		}
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadInteger> {

		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 2));
		}

		@Override
		public SyscallExecutionReadInteger build() {
			return new SyscallExecutionReadInteger(lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadInteger> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadInteger> copy() {
			var builder = new Builder();
			builder.lineJump.setValue(lineJump.getValue());
			builder.register.setValue(register.getValue());
			return builder;
		}
	}
}
