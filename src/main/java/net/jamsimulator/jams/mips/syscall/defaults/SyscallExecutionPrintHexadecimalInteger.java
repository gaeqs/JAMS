package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionPrintHexadecimalInteger implements SyscallExecution {

	public static final String NAME = "PRINT_HEXADECIMAL_INTEGER";

	private final boolean lineJump;
	private final int register;

	public SyscallExecutionPrintHexadecimalInteger(boolean lineJump, int register) {
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
		int value = register.getValue();
		String toPrint = Integer.toHexString(value);
		simulation.getConsole().print(toPrint);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var value = execution.value(register);
		var console = execution.getSimulation().getConsole();
		String toPrint = Integer.toHexString(value);
		console.print(toPrint);
		if (lineJump) console.println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintHexadecimalInteger> {

		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
		}

		@Override
		public SyscallExecutionPrintHexadecimalInteger build() {
			return new SyscallExecutionPrintHexadecimalInteger(lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintHexadecimalInteger> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintHexadecimalInteger> copy() {
			var builder = new Builder();
			builder.lineJump.setValue(lineJump.getValue());
			builder.register.setValue(register.getValue());
			return builder;
		}
	}
}
