package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class SimulationFiles {

	private final HashMap<Integer, SimulationFile> files;

	public SimulationFiles(Simulation<?> simulation) {
		files = new HashMap<>();

		files.put(0, new ConsoleInSimulationFile(0, simulation));
		files.put(1, new ConsoleOutSimulationFile(1, simulation.getConsole(), false));
		files.put(2, new ConsoleOutSimulationFile(2, simulation.getConsole(), true));
	}

	public int open(File file, boolean write, boolean append) throws IOException {
		Validate.notNull(file, "File cannot be null!");
		int id = getFirstId();

		if (write) {
			files.put(id, new OutSimulationFile(id, file, append));
		} else {
			files.put(id, new InSimulationFile(id, file));
		}

		return id;
	}

	public Optional<SimulationFile> get(int id) {
		return Optional.ofNullable(files.get(id));
	}

	public void close(int id) throws IOException {
		SimulationFile file = files.remove(id);
		if (file == null) return;
		file.close();
	}

	private int getFirstId() {
		int next = 3;
		while (files.containsKey(next)) next++;
		return next;
	}
}
