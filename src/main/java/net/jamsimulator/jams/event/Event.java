package net.jamsimulator.jams.event;

public class Event {

	private EventCaller generator;

	public Event(EventCaller generator) {
		this.generator = generator;
	}

	public EventCaller getGenerator() {
		return generator;
	}

}
