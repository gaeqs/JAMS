package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class SyscallExecutionReadString implements SyscallExecution {

	public static final String NAME = "READ_STRING";
	private final boolean lineJump;
	private final int addressRegister, maxCharsRegister;

	public SyscallExecutionReadString(boolean lineJump, int addressRegister, int maxCharsRegister) {
		this.lineJump = lineJump;
		this.addressRegister = addressRegister;
		this.maxCharsRegister = maxCharsRegister;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register maxCharsReg = simulation.getRegisters().getRegister(this.maxCharsRegister).orElse(null);
		if (maxCharsReg == null) throw new IllegalStateException("Register " + this.maxCharsRegister + " not found");

		int maxChars = maxCharsReg.getValue();
		if (maxChars < 1) return;

		Register addressReg = simulation.getRegisters().getRegister(this.addressRegister).orElse(null);
		if (addressReg == null) throw new IllegalStateException("Register " + this.addressRegister + " not found");


		String value = simulation.popInputOrLock();
		if (simulation.checkThreadInterrupted()) return;

		Memory memory = simulation.getMemory();
		int address = addressReg.getValue();

		int amount = 0;
		for (char c : value.toCharArray()) {
			if (amount >= maxChars - 1) break;
			memory.setByte(address, (byte) c);
			amount++;
			address++;
		}
		memory.setByte(address, (byte) 0);

		simulation.getConsole().printDone(value);
		if (lineJump) simulation.getConsole().println();
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var simulation = execution.getSimulation();
		var maxChars = execution.value(maxCharsRegister);
		if (maxChars < 1) return;

		var address = execution.value(addressRegister);

		String value = simulation.popInputOrLock();
		if (simulation.checkThreadInterrupted()) return;

		Memory memory = simulation.getMemory();

		int amount = 0;
		for (char c : value.toCharArray()) {
			if (amount >= maxChars - 1) break;
			memory.setByte(address, (byte) c);
			amount++;
			address++;
		}
		memory.setByte(address, (byte) 0);

		simulation.getConsole().printDone(value);
		if (lineJump) simulation.getConsole().println();
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionReadString> {

		private final BooleanProperty lineJump;
		private final IntegerProperty addressRegister;
		private final IntegerProperty maxCharsRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(lineJump = new SimpleBooleanProperty(null, "LINE_JUMP", false));
			properties.add(addressRegister = new SimpleIntegerProperty(null, "ADDRESS_REGISTER", 4));
			properties.add(maxCharsRegister = new SimpleIntegerProperty(null, "MAX_CHARS_REGISTER", 5));
		}

		@Override
		public SyscallExecutionReadString build() {
			return new SyscallExecutionReadString(lineJump.get(), addressRegister.get(), maxCharsRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadString> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionReadString> copy() {
			var builder = new Builder();
			builder.lineJump.setValue(lineJump.getValue());
			builder.addressRegister.setValue(addressRegister.getValue());
			builder.maxCharsRegister.setValue(maxCharsRegister.getValue());
			return builder;
		}
	}
}
