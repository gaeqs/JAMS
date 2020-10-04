package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionSetSeed implements SyscallExecution {

	public static final String NAME = "SET_SEED";
	private final int generatorRegister, seedRegister;

	public SyscallExecutionSetSeed(int generatorRegister, int seedRegister) {
		this.generatorRegister = generatorRegister;
		this.seedRegister = seedRegister;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register genRegister = simulation.getRegisters().getRegister(this.generatorRegister).orElse(null);
		if (genRegister == null) throw new IllegalStateException("Register " + this.generatorRegister + " not found");
		Register seedRegister = simulation.getRegisters().getRegister(this.seedRegister).orElse(null);
		if (seedRegister == null) throw new IllegalStateException("Register " + this.seedRegister + " not found");

		var generator = simulation.getNumberGenerators().getGenerator(genRegister.getValue());
		generator.setSeed(seedRegister.getValue());
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		int index = execution.value(generatorRegister);
		int seed = execution.value(seedRegister);

		execution.getSimulation().getNumberGenerators().getGenerator(index).setSeed(seed);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionSetSeed> {

		private final IntegerProperty generatorRegister, seedRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(generatorRegister = new SimpleIntegerProperty(null, "GENERATOR_REGISTER", 4));
			properties.add(seedRegister = new SimpleIntegerProperty(null, "SEED_REGISTER", 5));
		}

		@Override
		public SyscallExecutionSetSeed build() {
			return new SyscallExecutionSetSeed(generatorRegister.get(), seedRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionSetSeed> makeNewInstance() {
			return new Builder();
		}
	}
}
