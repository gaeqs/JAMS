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

	Object getInstance() {
		return instance;
	}

	Method getMethod() {
		return method;
	}

	Class<? extends Event> getEvent() {
		return event;
	}

	Listener getListener() {
		return listener;
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
