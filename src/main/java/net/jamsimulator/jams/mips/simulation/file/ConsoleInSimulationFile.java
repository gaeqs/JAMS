package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileReadEvent;

public class ConsoleInSimulationFile implements SimulationFile {

	protected final SimulationFiles files;
	protected final int id;
	protected final MIPSSimulation<?> simulation;

	public ConsoleInSimulationFile(SimulationFiles files, int id, MIPSSimulation<?> simulation) {
		this.files = files;
		this.id = id;
		this.simulation = simulation;
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

		SimulationFileReadEvent.Before before = files.callEvent(new SimulationFileReadEvent.Before(this, bytes));
		if (before.isCancelled()) return new byte[0];
		bytes = before.getAmount();

		if (bytes < 0) {
			throw new RuntimeException("Invalid amount: " + bytes);
		}
		byte[] array = new byte[bytes];
		int current = 0;

		char c;
		while (current < array.length) {
			c = simulation.popCharOrLock();
			array[current++] = (byte) c;
		}

		files.callEvent(new SimulationFileReadEvent.After(this, bytes, array));

		return array;
	}

	@Override
	public void write(byte[] bytes) {
		throw new RuntimeException("Couldn't write to this file.");
	}

	@Override
	public void revert(int bytes) {

	}

	@Override
	public void close() {
		throw new RuntimeException("This file cannot be closed.");
	}
}
