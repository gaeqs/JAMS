package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionRandomInteger implements SyscallExecution {

	public static final String NAME = "RANDOM_INTEGER";
	private final int generatorRegister, valueRegister;

	public SyscallExecutionRandomInteger(int generatorRegister, int valueRegister) {
		this.generatorRegister = generatorRegister;
		this.valueRegister = valueRegister;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
		if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
		Register valueRegister = simulation.getRegisters().getRegister(this.valueRegister).orElse(null);
		if (valueRegister == null) throw new IllegalStateException("Register " + this.valueRegister + " not found");

		valueRegister.setValue(simulation.getNumberGenerators().getGenerator(genRegister.getValue()).nextInt());
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		var index = execution.value(generatorRegister);

		execution.setAndUnlock(valueRegister, simulation.getNumberGenerators().getGenerator(index).nextInt());
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionRandomInteger> {

		private final IntegerProperty generatorRegister, valueRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
			properties.add(valueRegister = new SimpleIntegerProperty(null, "VALUE_REGISTER", 4));
		}

		@Override
		public SyscallExecutionRandomInteger build() {
			return new SyscallExecutionRandomInteger(generatorRegister.get(), valueRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionRandomInteger> makeNewInstance() {
			return new Builder();
		}
	}
}
