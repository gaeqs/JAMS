package net.jamsimulator.jams.mips.simulation.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.mips.simulation.file.SimulationFiles;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

/**
 * This event is called when a simulation file is open.
 */
public class SimulationFileOpenEvent extends Event {

	protected final SimulationFiles files;
	protected File file;

	/**
	 * Creates the event.
	 *
	 * @param files the {@link SimulationFiles} opening the given {@link File}.
	 * @param file  the {@link File} to open.
	 */
	private SimulationFileOpenEvent(SimulationFiles files, File file) {
		Validate.notNull(files, "Files cannot be null!");
		Validate.notNull(file, "File cannot be null!");
		this.files = files;
		this.file = file;
	}

	/**
	 * Returns the {@link SimulationFiles} opening the {@link File}.
	 *
	 * @return the {@link SimulationFiles}.
	 */
	public SimulationFiles getFiles() {
		return files;
	}

	/**
	 * Returns the {@link File} to open.
	 *
	 * @return the {@link File}.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Event called before opening a file.
	 */
	public static class Before extends SimulationFileOpenEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the event.
		 *
		 * @param files the {@link SimulationFiles} opening the given {@link File}.
		 * @param file  the {@link File} to open.
		 */
		public Before(SimulationFiles files, File file) {
			super(files, file);
		}

		/**
		 * Sets the {@link File} to open.
		 *
		 * @param file the {@link File}.
		 */
		public void setFile(File file) {
			Validate.notNull(file, "File cannot be null!");
			this.file = file;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}
	}

	/**
	 * Event called after opening a file.
	 */
	public static class After extends SimulationFileOpenEvent {

		private final SimulationFile simulationFile;


		/**
		 * Creates the event.
		 *
		 * @param files          the {@link SimulationFiles} opening the given {@link File}.
		 * @param file           the open {@link File}
		 * @param simulationFile the {@link SimulationFile} representing the open {@link File}.
		 */
		public After(SimulationFiles files, File file, SimulationFile simulationFile) {
			super(files, file);
			Validate.notNull(simulationFile, "Simulation file cannot be null!");
			this.simulationFile = simulationFile;
		}

		/**
		 * Returns the {@link SimulationFile} representing the open {@link File}.
		 *
		 * @return the {@link SimulationFile}.
		 */
		public SimulationFile getSimulationFile() {
			return simulationFile;
		}
	}
}
