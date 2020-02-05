package net.jamsimulator.jams.event;

/**
 * Represents some process that can be cancelled.
 */
public interface Cancellable {

	/**
	 * Returns whether the process is cancelled.
	 *
	 * @return whether the process is cancelled.
	 */
	boolean isCancelled();

	/**
	 * Sets whether the process is cancelled.
	 *
	 * @param cancelled whether the process is cancelled.
	 */
	void setCancelled(boolean cancelled);

}
