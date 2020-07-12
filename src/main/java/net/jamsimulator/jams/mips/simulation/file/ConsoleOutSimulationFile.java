package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.gui.util.log.Console;

public class ConsoleOutSimulationFile implements SimulationFile {

	protected final int id;
	protected final Console console;
	protected final boolean error;

	public ConsoleOutSimulationFile(int id, Console console, boolean error) {
		this.id = id;
		this.console = console;
		this.error = error;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public byte[] read(int bytes) {
		throw new RuntimeException("Couldn't read from this file.");
	}

	@Override
	public void write(byte[] bytes) {
		if (error) {
			console.printError(new String(bytes));
		} else {
			console.print(new String(bytes));
		}
	}

	@Override
	public void revert(int bytes) {

	}

	@Override
	public void close() {
		throw new RuntimeException("This file cannot be closed.");
	}
}
