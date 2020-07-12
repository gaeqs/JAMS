package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class ConsoleInSimulationFile implements SimulationFile {

	protected final int id;
	protected final Simulation<?> simulation;

	public ConsoleInSimulationFile(int id, Simulation<?> simulation) {
		this.id = id;
		this.simulation = simulation;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public byte[] read(int bytes) {
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
