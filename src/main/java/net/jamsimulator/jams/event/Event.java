package net.jamsimulator.jams.event;

/**
 * Represents an event. Events are used by {@link EventCaller}s to
 * send notifications to {@link Listener} methods.
 * <p>
 * You can create you own event creating a child class of {@link Event}.
 * To listen your event, create a method with the event as a parameter
 * and a {@link Listener} annotation.
 */
public class Event {

	private EventCaller caller;

	/**
	 * Creates an event.
	 * Send it to listeners through {@link EventCaller#callEvent(Event)}.
	 */
	public Event() {
	}


	/**
	 * Returns the {@link EventCaller} the event
	 * was sent through.
	 *
	 * @return the {@link EventCaller}.
	 */
	public EventCaller getCaller() {
		return caller;
	}

	void setCaller(EventCaller caller) {
		this.caller = caller;
	}
}
