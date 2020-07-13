package net.jamsimulator.jams.mips.simulation.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.utils.Validate;

/**
 * This event is called when data is written into a {@link SimulationFile}.
 */
public class SimulationFileWriteEvent extends Event {

	protected SimulationFile file;
	protected byte[] data;

	/**
	 * Creates the event.
	 *
	 * @param file the {@link SimulationFile}.
	 * @param data the data to write.
	 */
	private SimulationFileWriteEvent(SimulationFile file, byte[] data) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(data, "Data cannot be null!");
		this.file = file;
		this.data = data;
	}

	/**
	 * Returns the {@link SimulationFile}.
	 *
	 * @return the {@link SimulationFile}.
	 */
	public SimulationFile getFile() {
		return file;
	}


	/**
	 * Returns the data to write.
	 * This data is modifiable on the before event.
	 *
	 * @return the data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Event called before writing the data.
	 */
	public static class Before extends SimulationFileWriteEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the event.
		 *
		 * @param file the {@link SimulationFile}.
		 * @param data the data to write.
		 */
		public Before(SimulationFile file, byte[] data) {
			super(file, data);
		}

		/**
		 * Sets the data to write.
		 *
		 * @param data the data.
		 */
		public void setData(byte[] data) {
			Validate.notNull(data, "Data cannot be null!");
			this.data = data;
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
	 * Event called after writing the data.
	 */
	public static class After extends SimulationFileWriteEvent {

		/**
		 * Creates the event.
		 *
		 * @param file the {@link SimulationFile}.
		 * @param data the data to write.
		 */
		public After(SimulationFile file, byte[] data) {
			super(file, data);
		}
	}
}
