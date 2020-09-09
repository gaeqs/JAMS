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

import java.util.LinkedList;

public class SyscallExecutionReadCharacter implements SyscallExecution {

	public static final String NAME = "READ_CHARACTER";

	private final boolean lineJump;
	private final int register;

	public SyscallExecutionReadCharacter(boolean lineJump, int register) {
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

		char value = simulation.popCharOrLock();
		if (simulation.checkThreadInterrupted()) return;

		register.setValue(value);

		simulation.getConsole().printDone(value);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		char value = simulation.popCharOrLock();
		if (simulation.checkThreadInterrupted()) return;

		execution.setAndUnlock(register, value);

		simulation.getConsole().printDone(value);
		if (lineJump) simulation.getConsole().println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadCharacter> {

		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 2));
		}

		@Override
		public SyscallExecutionReadCharacter build() {
			return new SyscallExecutionReadCharacter(lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadCharacter> makeNewInstance() {
			return new Builder();
		}
	}
}
