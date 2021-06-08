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

public class SyscallExecutionPrintFloat implements SyscallExecution {

	public static final String NAME = "PRINT_FLOAT";

	private final boolean printHex, lineJump;
	private final int register;

	public SyscallExecutionPrintFloat(boolean printHex, boolean lineJump, int register) {
		this.printHex = printHex;
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register register = simulation.getRegisters().getCoprocessor1Register(this.register).orElse(null);
		if (register == null)
			throw new IllegalStateException("Floating point register " + this.register + " not found");

		int value = register.getValue();
		String toPrint = printHex ? Integer.toHexString(value) : String.valueOf(Float.intBitsToFloat(value));
		simulation.getConsole().print(toPrint);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var value = execution.valueCOP1(register);
		var console = execution.getSimulation().getConsole();

		String toPrint = printHex ? Integer.toHexString(value) : String.valueOf(Float.intBitsToFloat(value));
		console.print(toPrint);
		if (lineJump) console.println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintFloat> {

		private final BooleanProperty hexProperty;
		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(hexProperty = new SimpleBooleanProperty(null, "PRINT_HEX", false));
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 0));
		}

		@Override
		public SyscallExecutionPrintFloat build() {
			return new SyscallExecutionPrintFloat(hexProperty.get(), lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintFloat> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintFloat> copy() {
			var builder = new Builder();
			builder.hexProperty.setValue(hexProperty.getValue());
			builder.lineJump.setValue(lineJump.getValue());
			builder.register.setValue(register.getValue());
			return builder;
		}
	}
}
