package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionPrintInteger implements SyscallExecution {

	public static final String NAME = "PRINT_INTEGER";

	private final boolean printHex, lineJump;

	public SyscallExecutionPrintInteger(boolean printHex, boolean lineJump) {
		this.printHex = printHex;
		this.lineJump = lineJump;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register a0 = simulation.getRegisters().getRegister("a0").orElse(null);
		if (a0 == null) throw new IllegalStateException("Register a0 not found");
		int value = a0.getValue();
		String toPrint = printHex ? "0x" + Integer.toHexString(value) : String.valueOf(value);
		simulation.getLog().print(toPrint);
		if (lineJump) simulation.getLog().println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintInteger> {

		private final BooleanProperty hexProperty;
		private final BooleanProperty lineJump;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(hexProperty = new SimpleBooleanProperty(false, "PRINT_HEX"));
			properties.add(lineJump = new SimpleBooleanProperty(false, "LINE_JUMP"));
		}

		@Override
		public SyscallExecutionPrintInteger build() {
			return new SyscallExecutionPrintInteger(hexProperty.get(), lineJump.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintInteger> makeNewInstance() {
			return new Builder();
		}
	}
}
