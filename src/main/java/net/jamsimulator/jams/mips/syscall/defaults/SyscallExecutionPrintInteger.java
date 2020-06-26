package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionPrintInteger implements SyscallExecution {

	public static final String NAME = "RUN_EXCEPTION_HANDLER";

	private final boolean printHex;

	public SyscallExecutionPrintInteger(boolean printHex) {
		this.printHex = printHex;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register a0 = simulation.getRegisters().getRegister("a0").orElse(null);
		if (a0 == null) throw new IllegalStateException("Register a0 not found");
		int value = a0.getValue();
		String toPrint = printHex ? "0x" + Integer.toHexString(value) : String.valueOf(value);
		simulation.getLog().print(toPrint);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintInteger> {

		private final BooleanProperty hexProperty;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(hexProperty = new SimpleBooleanProperty(false));
		}

		@Override
		public SyscallExecutionPrintInteger build() {
			return new SyscallExecutionPrintInteger(hexProperty.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintInteger> makeNewInstance() {
			return new Builder();
		}
	}
}
