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
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class SyscallExecutionPrintDouble implements SyscallExecution {

	public static final String NAME = "PRINT_DOUBLE";

	private final boolean printHex, lineJump;
	private final int register;

	public SyscallExecutionPrintDouble(boolean printHex, boolean lineJump, int register) {
		this.printHex = printHex;
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		if (register % 2 != 0) {
			throw new IllegalStateException("Register " + register + " has not an even identifier!");
		}

		Register register1 = simulation.getRegisters().getCoprocessor1Register(register).orElse(null);
		if (register1 == null)
			throw new IllegalStateException("Floating point register " + register + " not found");
		Register register2 = simulation.getRegisters().getCoprocessor1Register(register + 1).orElse(null);
		if (register2 == null)
			throw new IllegalStateException("Floating point register " + (register + 1) + " not found");


		String toPrint;
		if (printHex) {
			long value = (((long) register2.getValue()) << 32) + register1.getValue();
			toPrint = Long.toHexString(value);
		} else {
			double value = NumericUtils.intsToDouble(register1.getValue(), register2.getValue());
			toPrint = String.valueOf(value);
		}

		simulation.getConsole().print(toPrint);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		if (register % 2 != 0) {
			throw new IllegalStateException("Register " + register + " has not an even identifier!");
		}

		var value1 = execution.valueCOP1(register);
		var value2 = execution.valueCOP1(register + 1);

		String toPrint;
		if (printHex) {
			long value = (((long) value2) << 32) + value1;
			toPrint = Long.toHexString(value);
		} else {
			double value = NumericUtils.intsToDouble(value1, value2);
			toPrint = String.valueOf(value);
		}

		var console = execution.getSimulation().getConsole();

		console.print(toPrint);
		if (lineJump) console.println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintDouble> {

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
		public SyscallExecutionPrintDouble build() {
			return new SyscallExecutionPrintDouble(hexProperty.get(), lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintDouble> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintDouble> copy() {
			var builder = new Builder();
			builder.hexProperty.setValue(hexProperty.getValue());
			builder.lineJump.setValue(lineJump.getValue());
			builder.register.setValue(register.getValue());
			return builder;
		}
	}
}
