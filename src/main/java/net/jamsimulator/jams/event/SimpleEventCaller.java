package net.jamsimulator.jams.event;

import java.lang.reflect.Method;
import java.util.TreeSet;

/**
 * Represents a simple event caller. An event caller allows to send
 * events to all the registered {@link Listener} the caller has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method)} or {@link #registerListeners(Object)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 */
public class SimpleEventCaller implements EventCaller {

	private TreeSet<ListenerMethod> registeredListeners;

	/**
	 * Creates a event caller.
	 */
	public SimpleEventCaller() {
		registeredListeners = new TreeSet<>(((o1, o2) -> {
			int val = o2.getListener().priority() - o1.getListener().priority();
			//Avoids override. Listeners registered first have priority.
			return val == 0 ? -1 : val;
		}));
	}

	public boolean registerListener(Object instance, Method method) {
		if (!method.isAccessible()) return false;
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

	public int registerListeners(Object instance) {
		int amount = 0;
		for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
			if (registerListener(instance, declaredMethod))
				amount++;
		}
		return amount;
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		return registeredListeners.removeIf(target -> target.matches(instance, method));
	}

	@Override
	public int unregisterListeners(Object instance) {
		int amount = 0;
		for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
			if (unregisterListener(instance, declaredMethod))
				amount++;
		}
		return amount;
	}

	public <T extends Event> T callEvent(T event) {
		//Sets the caller.
		event.setCaller(this);
		//For all listeners: filter and send.
		registeredListeners.stream().filter(target -> target.getEvent().isAssignableFrom(event.getClass()))
				.forEach(target -> {
					if (target.getListener().ignoreCancelled() ||
							!(event instanceof Cancellable) ||
							!((Cancellable) event).isCancelled())
						target.call(event);
				});
		return event;
	}

}
