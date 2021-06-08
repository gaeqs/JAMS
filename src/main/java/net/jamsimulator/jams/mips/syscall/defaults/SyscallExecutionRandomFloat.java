package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionRandomFloat implements SyscallExecution {

	public static final String NAME = "RANDOM_FLOAT";
	private final int generatorRegister, valueRegister;

	public SyscallExecutionRandomFloat(int generatorRegister, int valueRegister) {
		this.generatorRegister = generatorRegister;
		this.valueRegister = valueRegister;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
		if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
		Register valueRegister = simulation.getRegisters().getCoprocessor1Register(this.valueRegister).orElse(null);
		if (valueRegister == null) throw new IllegalStateException("Register " + this.valueRegister + " not found");

		var value = simulation.getNumberGenerators().getGenerator(genRegister.getValue()).nextFloat();
		valueRegister.setValue(Float.floatToIntBits(value));
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		var index = execution.value(generatorRegister);

		var value = simulation.getNumberGenerators().getGenerator(index).nextFloat();
		execution.setAndUnlockCOP1(valueRegister, Float.floatToIntBits(value));
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRandomFloat> {

		private final IntegerProperty generatorRegister, valueRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
			properties.add(valueRegister = new SimpleIntegerProperty(null, "VALUE_REGISTER", 0));
		}

		@Override
		public SyscallExecutionRandomFloat build() {
			return new SyscallExecutionRandomFloat(generatorRegister.get(), valueRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRandomFloat> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRandomFloat> copy() {
			var builder = new Builder();
			builder.generatorRegister.setValue(generatorRegister.getValue());
			builder.valueRegister.setValue(valueRegister.getValue());
			return builder;
		}
	}
}
