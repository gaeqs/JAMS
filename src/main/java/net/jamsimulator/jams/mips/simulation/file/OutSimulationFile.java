package net.jamsimulator.jams.mips.simulation.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class OutSimulationFile implements SimulationFile {

	protected final int id;
	protected final ArrayList<Byte> data;
	protected final File file;

	public OutSimulationFile(int id, File file, boolean append) throws IOException {
		this.id = id;
		this.data = new ArrayList<>();
		this.file = file;

		if (append) {
			for (byte b : Files.readAllBytes(file.toPath())) {
				data.add(b);
			}
		}
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
		for (byte b : bytes) {
			data.add(b);
		}
	}

	@Override
	public void revert(int bytes) {
		Iterator<Byte> iterator = data.iterator();
		int remaining = data.size() - bytes;

		while (iterator.hasNext()) {
			iterator.next();
			if (remaining > 0) {
				remaining--;
			} else {
				iterator.remove();
			}
		}
	}

	@Override
	public void close() throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		for (byte b : data) {
			out.write(b);
		}
		out.close();
	}
}
