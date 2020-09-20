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

public class SyscallExecutionReadDouble implements SyscallExecution {

	public static final String NAME = "READ_DOUBLE";
	private final boolean lineJump;
	private final int register;

	public SyscallExecutionReadDouble(boolean lineJump, int register) {
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		if (this.register % 2 != 0) throw new IllegalStateException("Register " + this.register + " is not even!");
		Register register = simulation.getRegisters().getCoprocessor1Register(this.register).orElse(null);
		Register register2 = simulation.getRegisters().getCoprocessor1Register(this.register + 1).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");
		if (register2 == null) throw new IllegalStateException("Register " + (this.register + 1) + " not found");

		boolean done = false;
		while (!done) {
			String value = simulation.popInputOrLock();
			if (simulation.checkThreadInterrupted()) return;

			try {
				double input = Double.parseDouble(value);

				int[] ints = NumericUtils.doubleToInts(input);

				register.setValue(ints[0]);
				register2.setValue(ints[1]);

				simulation.getConsole().printDone(value);
				if (lineJump) simulation.getConsole().println();
				done = true;
			} catch (NumberFormatException ignore) {
			}
		}
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		boolean done = false;
		while (!done) {
			String value = simulation.popInputOrLock();
			if (simulation.checkThreadInterrupted()) return;

			try {
				double input = Double.parseDouble(value);

				int[] ints = NumericUtils.doubleToInts(input);

				execution.setAndUnlockCOP1(register, ints[0]);
				execution.setAndUnlockCOP1(register + 1, ints[1]);

				simulation.getConsole().printDone(value);
				if (lineJump) simulation.getConsole().println();
				done = true;
			} catch (NumberFormatException ignore) {
			}
		}
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadDouble> {

		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 0));
		}

		@Override
		public SyscallExecutionReadDouble build() {
			return new SyscallExecutionReadDouble(lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadDouble> makeNewInstance() {
			return new Builder();
		}
	}
}
