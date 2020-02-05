package net.jamsimulator.jams.event;

import java.lang.reflect.Method;

class ListenerMethod {

	private Object instance;
	private Method method;
	private Class<? extends Event> event;
	private Listener listener;

	ListenerMethod(Object instance, Method method, Class<? extends Event> event, Listener listener) {
		this.instance = instance;
		this.method = method;
		this.event = event;
		this.listener = listener;
	}

	Class<? extends Event> getEvent() {
		return event;
	}

	Listener getListener() {
		return listener;
	}

	boolean matches(Object instance, Method method) {
		//We want to check that it's the same instance, not an equivalent one.
		return this.method.equals(method) && instance == this.instance;
	}

	void call(Event event) {
		try {
			method.invoke(instance, event);
		} catch (Exception e) {
			System.err.println("Error while calling listener " + method.getName() + "!");
			e.printStackTrace();
		}
	}
}
