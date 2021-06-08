package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileCloseEvent;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileOpenEvent;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class SimulationFiles extends SimpleEventBroadcast {

	private final MIPSSimulation<?> simulation;
	private final HashMap<Integer, SimulationFile> files;

	public SimulationFiles(MIPSSimulation<?> simulation) {
		this.simulation = simulation;
		files = new HashMap<>();

		files.put(0, new ConsoleInSimulationFile(this, 0, simulation));
		files.put(1, new ConsoleOutSimulationFile(this, 1, simulation.getConsole(), false));
		files.put(2, new ConsoleOutSimulationFile(this, 2, simulation.getConsole(), true));
	}

	public MIPSSimulation<?> getSimulation() {
		return simulation;
	}

	public int open(File file, boolean write, boolean append) throws IOException {
		Validate.notNull(file, "File cannot be null!");

		SimulationFileOpenEvent.Before before = callEvent(new SimulationFileOpenEvent.Before(this, file));
		if (before.isCancelled()) return -1;
		file = before.getFile();

		int id = getFirstId();

		SimulationFile simulationFile;
		if (write) {
			files.put(id, simulationFile = new OutSimulationFile(this, id, file, append));
		} else {
			files.put(id, simulationFile = new InSimulationFile(this, id, file));
		}

		callEvent(new SimulationFileOpenEvent.After(this, file, simulationFile));

		return id;
	}

	public Optional<SimulationFile> get(int id) {
		return Optional.ofNullable(files.get(id));
	}

	public void close(int id) throws IOException {
		SimulationFile file = files.get(id);
		if (file == null) return;

		SimulationFileCloseEvent.Before before = callEvent(new SimulationFileCloseEvent.Before(file));
		if (before.isCancelled()) return;

		files.remove(id);
		file.close();

		callEvent(new SimulationFileCloseEvent.After(file));
	}

	public void forceAdd(SimulationFile file) {
		files.put(file.getId(), file);
	}

	public boolean forceRemove(int id) {
		return files.remove(id) != null;
	}

	private int getFirstId() {
		int next = 3;
		while (files.containsKey(next)) next++;
		return next;
	}
}
