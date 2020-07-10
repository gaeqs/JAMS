package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionPrintString implements SyscallExecution {

	public static final String NAME = "PRINT_STRING";

	private final boolean lineJump;
	private final int maxChars, register;

	public SyscallExecutionPrintString(boolean lineJump, int maxChars, int register) {
		this.lineJump = lineJump;
		this.maxChars = Math.max(maxChars, 0);
		this.register = register;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register register = simulation.getRegisters().getRegister(this.register).orElse(null);
		if (register == null) throw new IllegalStateException("Register " + this.register + " not found");

		Memory memory = simulation.getMemory();
		int address = register.getValue();
		char[] chars = new char[maxChars];
		int amount = 0;
		char c;
		while ((c = (char) memory.getByte(address++)) != '\0' && amount < maxChars) {
			chars[amount++] = c;
		}

		if (amount > 0) {
			String string = new String(chars, 0, maxChars);
			simulation.getConsole().print(string);
		}

		if (lineJump) simulation.getConsole().println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionPrintString> {

		private final BooleanProperty lineJump;
		private final IntegerProperty maxChars;
		private final IntegerProperty register;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(maxChars = new SimpleIntegerProperty(null, "MAX_CHARACTERS", 4096));
			properties.add(register = new SimpleIntegerProperty(null, "REGISTER", 4));
		}

		@Override
		public SyscallExecutionPrintString build() {
			return new SyscallExecutionPrintString(lineJump.get(), maxChars.get(), register.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionPrintString> makeNewInstance() {
			return new Builder();
		}
	}
}
