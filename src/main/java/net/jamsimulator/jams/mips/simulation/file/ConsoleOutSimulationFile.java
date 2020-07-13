package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;

public class ConsoleOutSimulationFile implements SimulationFile {

	protected final SimulationFiles files;
	protected final int id;
	protected final Console console;
	protected final boolean error;

	public ConsoleOutSimulationFile(SimulationFiles files, int id, Console console, boolean error) {
		this.files = files;
		this.id = id;
		this.console = console;
		this.error = error;
	}

	@Override
	public SimulationFiles getFiles() {
		return files;
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
		SimulationFileWriteEvent.Before before = files.callEvent(new SimulationFileWriteEvent.Before(this, bytes));
		if (before.isCancelled()) return;
		bytes = before.getData();

		if (error) {
			console.printError(new String(bytes));
		} else {
			console.print(new String(bytes));
		}

		files.callEvent(new SimulationFileWriteEvent.After(this, bytes));
	}

	@Override
	public void revert(int bytes) {

	}

	@Override
	public void close() {
		throw new RuntimeException("This file cannot be closed.");
	}
}
