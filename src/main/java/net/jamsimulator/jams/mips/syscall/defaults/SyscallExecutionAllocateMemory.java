package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.LinkedList;

public class SyscallExecutionAllocateMemory implements SyscallExecution {

	public static final String NAME = "ALLOCATE_MEMORY";
	private final int amountRegister, addressRegister;

	public SyscallExecutionAllocateMemory(int amountRegister, int addressRegister) {
		this.amountRegister = amountRegister;
		this.addressRegister = addressRegister;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register amountReg = simulation.getRegisters().getRegister(this.amountRegister).orElse(null);
		if (amountReg == null) throw new IllegalStateException("Register " + this.amountRegister + " not found");

		Register addressReg = simulation.getRegisters().getRegister(this.addressRegister).orElse(null);
		if (addressReg == null) throw new IllegalStateException("Register " + this.addressRegister + " not found");

		int address = simulation.getMemory().allocateMemory(amountReg.getValue());
		addressReg.setValue(address);
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var amount = execution.value(amountRegister);
		var address = execution.getSimulation().getMemory().allocateMemory(amount);
		execution.setAndUnlock(addressRegister, address);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionAllocateMemory> {

		private final IntegerProperty amountRegister;
		private final IntegerProperty addressRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(amountRegister = new SimpleIntegerProperty(null, "AMOUNT_REGISTER", 4));
			properties.add(addressRegister = new SimpleIntegerProperty(null, "ADDRESS_REGISTER", 2));
		}

		@Override
		public SyscallExecutionAllocateMemory build() {
			return new SyscallExecutionAllocateMemory(amountRegister.get(), addressRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionAllocateMemory> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionAllocateMemory> copy() {
			var builder = new Builder();
			builder.amountRegister.setValue(amountRegister.getValue());
			builder.addressRegister.setValue(addressRegister.getValue());
			return builder;
		}
	}
}
