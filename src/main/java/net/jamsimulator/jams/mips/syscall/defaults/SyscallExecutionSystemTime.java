package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class SyscallExecutionSystemTime implements SyscallExecution {

	public static final String NAME = "SYSTEM_TIME";
	private final int lowOrderRegister, highOrderRegister;

	public SyscallExecutionSystemTime(int lowOrderRegister, int highOrderRegister) {
		this.lowOrderRegister = lowOrderRegister;
		this.highOrderRegister = highOrderRegister;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register low = simulation.getRegisters().getRegister(lowOrderRegister).orElse(null);
		if (low == null) throw new IllegalStateException("Register " + lowOrderRegister + " not found");
		Register high = simulation.getRegisters().getRegister(highOrderRegister).orElse(null);
		if (high == null) throw new IllegalStateException("Register " + highOrderRegister + " not found");

		int[] values = NumericUtils.longToInts(System.currentTimeMillis());
		low.setValue(values[0]);
		high.setValue(values[1]);
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		int[] values = NumericUtils.longToInts(System.currentTimeMillis());
		execution.setAndUnlock(lowOrderRegister, values[0]);
		execution.setAndUnlock(highOrderRegister, values[1]);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionSystemTime> {

		private final IntegerProperty low, high;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(low = new SimpleIntegerProperty(null, "LOW_REGISTER", 4));
			properties.add(high = new SimpleIntegerProperty(null, "HIGH_REGISTER", 5));
		}

		@Override
		public SyscallExecutionSystemTime build() {
			return new SyscallExecutionSystemTime(low.get(), high.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionSystemTime> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionSystemTime> copy() {
			var builder = new Builder();
			builder.low.setValue(low.getValue());
			builder.high.setValue(high.getValue());
			return builder;
		}
	}
}
