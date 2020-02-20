package net.jamsimulator.jams.event;

/**
 * Represents an event. Events are used by {@link EventBroadcast}s to
 * send notifications to {@link Listener} methods.
 * <p>
 * You can create you own event creating a child class of {@link Event}.
 * To listen your event, create a method with the event as a parameter
 * and a {@link Listener} annotation.
 */
public class Event {

	private EventBroadcast caller;

	/**
	 * Creates an event.
	 * Send it to listeners through {@link EventBroadcast#callEvent(Event)}.
	 */
	public Event() {
	}


	/**
	 * Returns the {@link EventBroadcast} the event
	 * was sent through.
	 *
	 * @return the {@link EventBroadcast}.
	 */
	public EventBroadcast getCaller() {
		return caller;
	}

	void setCaller(EventBroadcast caller) {
		this.caller = caller;
	}
}
