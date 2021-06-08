package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.io.IOException;
import java.util.LinkedList;

public class SyscallExecutionCloseFile implements SyscallExecution {

	public static final String NAME = "CLOSE_FILE";

	private final int idRegister;

	public SyscallExecutionCloseFile(int idRegister) {
		this.idRegister = idRegister;
	}

	@Override
	public void execute(MIPSSimulation<?> simulation) {
		Register idRegister = simulation.getRegisters().getRegister(this.idRegister).orElse(null);
		if (idRegister == null) throw new IllegalStateException("Register " + this.idRegister + " not found");

		try {
			simulation.getFiles().close(idRegister.getValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void executeMultiCycle(MultiCycleExecution<?> execution) {
		var id = execution.value(idRegister);

		try {
			execution.getSimulation().getFiles().close(id);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionCloseFile> {

		private final IntegerProperty idRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(idRegister = new SimpleIntegerProperty(null, "ID_REGISTER", 4));
		}

		@Override
		public SyscallExecutionCloseFile build() {
			return new SyscallExecutionCloseFile(idRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionCloseFile> makeNewInstance() {
			return new Builder();
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionCloseFile> copy() {
			var builder = new Builder();
			builder.idRegister.setValue(idRegister.getValue());
			return builder;
		}
	}
}
