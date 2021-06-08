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

public class SyscallExecutionRandomDouble implements SyscallExecution {

	public static final String NAME = "RANDOM_DOUBLE";
	private final int generatorRegister, valueRegister;

	public SyscallExecutionRandomDouble(int generatorRegister, int valueRegister) {
		this.generatorRegister = generatorRegister;
		this.valueRegister = valueRegister;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		if (this.valueRegister % 2 != 0)
			throw new IllegalStateException("Register " + this.valueRegister + " is not even!");

		Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
		if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
		Register valueRegister = simulation.getRegisters().getCoprocessor1Register(this.valueRegister).orElse(null);
		if (valueRegister == null) throw new IllegalStateException("Register " + this.valueRegister + " not found");
		Register valueRegister1 = simulation.getRegisters().getCoprocessor1Register(this.valueRegister + 1).orElse(null);
		if (valueRegister1 == null)
			throw new IllegalStateException("Register " + (this.valueRegister + 1) + " not found");

		var value = simulation.getNumberGenerators().getGenerator(genRegister.getValue()).nextDouble();
		var ints = NumericUtils.doubleToInts(value);
		valueRegister.setValue(ints[0]);
		valueRegister1.setValue(ints[1]);
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		var index = execution.value(generatorRegister);

		var value = simulation.getNumberGenerators().getGenerator(index).nextDouble();
		var ints = NumericUtils.doubleToInts(value);
		execution.setAndUnlockCOP1(valueRegister, ints[0]);
		execution.setAndUnlockCOP1(valueRegister + 1, ints[1]);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRandomDouble> {

		private final IntegerProperty generatorRegister, valueRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
			properties.add(valueRegister = new SimpleIntegerProperty(null, "VALUE_REGISTER", 0));
		}

		@Override
		public SyscallExecutionRandomDouble build() {
			return new SyscallExecutionRandomDouble(generatorRegister.get(), valueRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRandomDouble> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRandomDouble> copy() {
			var builder = new Builder();
			builder.generatorRegister.setValue(generatorRegister.getValue());
			builder.valueRegister.setValue(valueRegister.getValue());
			return builder;
		}
	}
}
