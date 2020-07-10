package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionReadFloat implements SyscallExecution {

	public static final String NAME = "READ_FLOAT";
	private final boolean lineJump;
	private final int register;

	public SyscallExecutionReadFloat(boolean lineJump, int register) {
		this.lineJump = lineJump;
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getCoprocessor1Register(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");


		simulation.popInputOrLock(value -> {
			try {
				float input = Float.parseFloat(value);
				register.setValue(Float.floatToIntBits(input));

				simulation.getConsole().printDone(value);
				if (lineJump) simulation.getConsole().println();

				return true;
			} catch (NumberFormatException ignore) {
				return false;
			}
		});

	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadFloat> {

		private final BooleanProperty lineJump;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 0));
		}

		@Override
		public SyscallExecutionReadFloat build() {
			return new SyscallExecutionReadFloat(lineJump.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadFloat> makeNewInstance() {
			return new Builder();
		}
	}
}
