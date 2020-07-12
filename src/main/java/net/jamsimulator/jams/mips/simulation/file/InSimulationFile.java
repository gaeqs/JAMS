package net.jamsimulator.jams.mips.simulation.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class InSimulationFile implements SimulationFile {

	protected final int id;
	protected final byte[] data;
	protected int current;

	public InSimulationFile(int id, File file) throws IOException {
		this.id = id;
		data = Files.readAllBytes(file.toPath());
		current = 0;
	}

	public InSimulationFile(int id, byte[] data) {
		this.id = id;
		this.data = data;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public byte[] read(int bytes) {
		byte[] array = new byte[Math.min(bytes, data.length - current)];

		for (int i = 0; i < array.length; i++) {
			array[i] = data[current++];
		}

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
