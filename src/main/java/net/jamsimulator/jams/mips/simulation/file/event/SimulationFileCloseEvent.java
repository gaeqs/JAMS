package net.jamsimulator.jams.mips.simulation.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.utils.Validate;

/**
 * This event is called when a simulation file is closed.
 */
public class SimulationFileCloseEvent extends Event {

	protected SimulationFile file;

	/**
	 * Creates the event.
	 *
	 * @param file the {@link SimulationFile} to close.
	 */
	private SimulationFileCloseEvent(SimulationFile file) {
		Validate.notNull(file, "File cannot be null!");
		this.file = file;
	}

	/**
	 * Returns the {@link SimulationFile} to open.
	 *
	 * @return the {@link SimulationFile}.
	 */
	public SimulationFile getFile() {
		return file;
	}

	/**
	 * Event called before closing a file.
	 */
	public static class Before extends SimulationFileCloseEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the event.
		 *
		 * @param file the {@link SimulationFile} to close.
		 */
		public Before(SimulationFile file) {
			super(file);
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
	 * Event called after closing a file.
	 */
	public static class After extends SimulationFileCloseEvent {


		/**
		 * Creates the event.
		 *
		 * @param file the closed {@link SimulationFile}.
		 */
		public After(SimulationFile file) {
			super(file);
		}
	}
}
