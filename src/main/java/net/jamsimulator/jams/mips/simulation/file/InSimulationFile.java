package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileReadEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class InSimulationFile implements SimulationFile {

	protected final SimulationFiles files;
	protected final int id;
	protected final byte[] data;
	protected int current;

	public InSimulationFile(SimulationFiles files, int id, File file) throws IOException {
		this.files = files;
		this.id = id;
		data = Files.readAllBytes(file.toPath());
		current = 0;
	}

	public InSimulationFile(SimulationFiles files, int id, byte[] data) {
		this.files = files;
		this.id = id;
		this.data = data;
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

		byte[] array = new byte[Math.min(bytes, data.length - current)];

		for (int i = 0; i < array.length; i++) {
			array[i] = data[current++];
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
		current -= bytes;
		if (current < 0) current = 0;
	}

	@Override
	public void close() {

	}
}
