package net.jamsimulator.jams.mips.simulation.file;

import java.io.IOException;

public interface SimulationFile {

	SimulationFiles getFiles();

	int getId();

	byte[] read(int bytes);

	void write(byte[] bytes);

	void revert(int bytes);

	void close() throws IOException;

}
