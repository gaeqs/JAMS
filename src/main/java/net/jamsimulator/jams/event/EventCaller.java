package net.jamsimulator.jams.event;

import java.lang.reflect.Method;
import java.util.TreeSet;

/**
 * Represents an event caller. An event caller allows to send
 * events to all the registered {@link Listener} the caller has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method)} or {@link #registerListeners(Object)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 */
public class EventCaller {

	private TreeSet<ListenerMethod> registeredListeners;

	/**
	 * Creates a event caller.
	 */
	public EventCaller() {
		registeredListeners = new TreeSet<>(((o1, o2) -> {
			int val = o2.getListener().priority() - o1.getListener().priority();
			//Avoids override. Listeners registered first have priority.
			return val == 0 ? -1 : val;
		}));
	}

	/**
	 * Registers a listener of an instance. A listener is a non-static method with
	 * only one parameter. This parameter must be a {@link Event} or any subclass.
	 * The method must also have one {@link Listener} annotation.
	 *
	 * @param instance the instance.
	 * @param method   the listener.
	 * @return true whether the listener has been register.
	 */
	public boolean registerListener(Object instance, Method method) {
		if (!method.trySetAccessible()) return false;
		if (method.getParameterCount() != 1) return false;
		Class<?> clazz = method.getParameters()[0].getType();
		if (!Event.class.isAssignableFrom(clazz)) return false;
		Class<? extends Event> type = (Class<? extends Event>) clazz;

		Listener[] annotations = method.getAnnotationsByType(Listener.class);
		if (annotations.length != 1) return false;
		Listener annotation = annotations[0];

		ListenerMethod listenerMethod = new ListenerMethod(instance, method, type, annotation);
		registeredListeners.add(listenerMethod);
		return true;
	}

	/**
	 * Registers all listener of an instance. See {@link #registerListener(Object, Method)} for
	 * more information.
	 *
	 * @param instance the instance.
	 * @return the amount of listeners registered.
	 * @see #registerListener(Object, Method)
	 */
	public int registerListeners(Object instance) {
		int amount = 0;
		for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
			if (registerListener(instance, declaredMethod))
				amount++;
		}
		return amount;
	}

	/**
	 * Calls all listeners compatible with the given event.
	 *
	 * @param event the event.
	 */
	public void callEvent(Event event) {
		registeredListeners.stream().filter(target -> target.getEvent().isAssignableFrom(event.getClass()))
				.forEach(target -> target.call(event));
	}

}
