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

import java.util.LinkedList;

public class SyscallExecutionPrintInteger implements SyscallExecution {

	public static final String NAME = "PRINT_INTEGER";

	private final boolean printHex, lineJump;
	private final int register;

	public SyscallExecutionPrintInteger(boolean printHex, boolean lineJump, int register) {
		this.printHex = printHex;
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
		int value = register.getValue();
		String toPrint = printHex ? Integer.toHexString(value) : String.valueOf(value);
		simulation.getConsole().print(toPrint);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var value = execution.value(register);
		var console = execution.getSimulation().getConsole();
		String toPrint = printHex ? Integer.toHexString(value) : String.valueOf(value);
		console.print(toPrint);
		if (lineJump) console.println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintInteger> {

		private final BooleanProperty hexProperty;
		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(hexProperty = new SimpleBooleanProperty(null, "PRINT_HEX", false));
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
		}

		@Override
		public SyscallExecutionPrintInteger build() {
			return new SyscallExecutionPrintInteger(hexProperty.get(), lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintInteger> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintInteger> copy() {
			var builder = new Builder();
			builder.hexProperty.setValue(hexProperty.getValue());
			builder.lineJump.setValue(lineJump.getValue());
			builder.register.setValue(register.getValue());
			return builder;
		}
	}
}
