package net.jamsimulator.jams.mips.syscall.defaults;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.syscall.SyscallExecution;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class SyscallExecutionOpenFile implements SyscallExecution {

	public static final String NAME = "OPEN_FILE";

	private final int nameRegister, flagRegister, modeRegister, resultRegister;

	public SyscallExecutionOpenFile(int nameRegister, int flagRegister, int modeRegister, int resultRegister) {
		this.nameRegister = nameRegister;
		this.flagRegister = flagRegister;
		this.modeRegister = modeRegister;
		this.resultRegister = resultRegister;
	}

	@Override
	public void execute(Simulation<?> simulation) {
		Register nameRegister = simulation.getRegisters().getRegister(this.nameRegister).orElse(null);
		if (nameRegister == null) throw new IllegalStateException("Register " + this.nameRegister + " not found");
		Register flagRegister = simulation.getRegisters().getRegister(this.flagRegister).orElse(null);
		if (flagRegister == null) throw new IllegalStateException("Register " + this.flagRegister + " not found");
		Register modeRegister = simulation.getRegisters().getRegister(this.modeRegister).orElse(null);
		if (modeRegister == null) throw new IllegalStateException("Register " + this.modeRegister + " not found");
		Register resultRegister = simulation.getRegisters().getRegister(this.resultRegister).orElse(null);
		if (resultRegister == null) throw new IllegalStateException("Register " + this.resultRegister + " not found");

		String name = getString(simulation, nameRegister.getValue());
		if (name.isEmpty()) {
			resultRegister.setValue(-1);
			return;
		}

		File file;
		Path path = Paths.get(name);
		if (path.isAbsolute()) {
			file = path.toFile();
		} else {
			file = new File(simulation.getWorkingDirectory(), name);
		}

		boolean write, append;

		switch (flagRegister.getValue()) {
			case 0:
				write = false;
				append = false;
				break;
			case 1:
				write = true;
				append = false;
				break;
			case 9:
				write = true;
				append = true;
				break;
			default:
				resultRegister.setValue(-1);
				return;
		}

		try {
			resultRegister.setValue(simulation.getFiles().open(file, write, append));
		} catch (IOException ex) {
			resultRegister.setValue(-1);
		}

	}

	private String getString(Simulation<?> simulation, int address) {
		Memory memory = simulation.getMemory();
		char[] chars = new char[1024];
		int amount = 0;
		char c;
		while ((c = (char) memory.getByte(address++)) != '\0' && amount < 1024) {
			chars[amount++] = c;
		}
		return new String(chars, 0, amount);
	}

	public static class Builder extends SyscallExecutionBuilder<SyscallExecutionOpenFile> {

		private final IntegerProperty nameRegister;
		private final IntegerProperty flagRegister;
		private final IntegerProperty modeRegister;
		private final IntegerProperty resultRegister;

		public Builder() {
			super(NAME, new LinkedList<>());
			properties.add(nameRegister = new SimpleIntegerProperty(null, "NAME_REGISTER", 4));
			properties.add(flagRegister = new SimpleIntegerProperty(null, "FLAG_REGISTER", 5));
			properties.add(modeRegister = new SimpleIntegerProperty(null, "MODE_REGISTER", 6));
			properties.add(resultRegister = new SimpleIntegerProperty(null, "RESULT_REGISTER", 2));
		}

		@Override
		public SyscallExecutionOpenFile build() {
			return new SyscallExecutionOpenFile(nameRegister.get(), flagRegister.get(), modeRegister.get(), resultRegister.get());
		}

		@Override
		public SyscallExecutionBuilder<SyscallExecutionOpenFile> makeNewInstance() {
			return new Builder();
		}
	}
}
