package net.jamsimulator.jams.mips.simulation.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;
import net.jamsimulator.jams.utils.Validate;

/**
 * This event is called when data is read from a {@link SimulationFile}.
 */
public class SimulationFileReadEvent extends Event {

	protected SimulationFile file;
	protected int amount;

	/**
	 * Creates the event.
	 *
	 * @param file   the {@link SimulationFile}.
	 * @param amount the amount of bytes to read.
	 */
	private SimulationFileReadEvent(SimulationFile file, int amount) {
		Validate.notNull(file, "File cannot be null!");
		this.file = file;
		this.amount = amount;
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
	 * Returns the amount of bytes to read.
	 *
	 * @return the amount of bytes.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Event called before reading the data.
	 */
	public static class Before extends SimulationFileReadEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the event.
		 *
		 * @param file   the {@link SimulationFile}.
		 * @param amount the amount of bytes to read.
		 */
		public Before(SimulationFile file, int amount) {
			super(file, amount);
		}

		/**
		 * Sets the amount of bytes to read.
		 *
		 * @param amount the amount of bytes.
		 */
		public void setAmount(int amount) {
			this.amount = amount;
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
	 * Event called after reading the data.
	 */
	public static class After extends SimulationFileReadEvent {

		protected byte[] data;

		/**
		 * Creates the event.
		 *
		 * @param file   the {@link SimulationFile}.
		 * @param amount the amount of bytes to read.
		 * @param data   the read data.
		 */
		public After(SimulationFile file, int amount, byte[] data) {
			super(file, amount);
			Validate.notNull(data, "Data cannot be null!");
			this.data = data;
		}

		/**
		 * Returns the data to write.
		 * This data is modifiable.
		 *
		 * @return the data.
		 */
		public byte[] getData() {
			return data;
		}

		/**
		 * Sets the read data.
		 *
		 * @param data the data.
		 */
		public void setData(byte[] data) {
			Validate.notNull(data, "Data cannot be null!");
			this.data = data;
		}
	}
}
