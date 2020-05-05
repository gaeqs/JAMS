/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.event;

import java.lang.reflect.Method;
import java.util.TreeSet;

/**
 * Represents a simple event caller. An event caller allows to send
 * events to all the registered {@link Listener} the caller has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method, boolean)} or {@link #registerListeners(Object, boolean)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 */
public class SimpleEventBroadcast implements EventBroadcast {

	private final TreeSet<ListenerMethod> registeredListeners;

	/**
	 * Creates a event caller.
	 */
	public SimpleEventBroadcast() {
		registeredListeners = new TreeSet<>(((o1, o2) -> {
			int val = o2.getListener().priority() - o1.getListener().priority();
			//Avoids override. Listeners registered first have priority.
			return val == 0 ? -1 : val;
		}));
	}

	public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
		if (method.getParameterCount() != 1) return false;
		Class<?> clazz = method.getParameters()[0].getType();
		if (!Event.class.isAssignableFrom(clazz)) return false;
		Class<? extends Event> type = (Class<? extends Event>) clazz;

		Listener[] annotations = method.getAnnotationsByType(Listener.class);
		if (annotations.length != 1) return false;
		Listener annotation = annotations[0];

		method.setAccessible(true);

		ListenerMethod listenerMethod = new ListenerMethod(instance, method, type, annotation, useWeakReferences);
		registeredListeners.add(listenerMethod);
		return true;
	}

	public int registerListeners(Object instance, boolean useWeakReferences) {
		int amount = 0;

		Class<?> c = instance.getClass();
		while (c != null) {
			for (Method declaredMethod : c.getDeclaredMethods()) {
				if (registerListener(instance, declaredMethod, useWeakReferences))
					amount++;
			}
			c = c.getSuperclass();
		}
		return amount;
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		//This method also uses the loop to remove listeners with invalid references.
		return registeredListeners.removeIf(target -> !target.isReferenceValid() || target.matches(instance, method));
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
		return callEvent(event, this);
	}


	/**
	 * Calls the given event setting the caller as the given broadcast.
	 * This is useful for broadcasts that can't extends this class and use
	 * it as a parameter.
	 *
	 * @param event     the event to call.
	 * @param broadcast the broadcast.
	 * @param <T>       the type of event.
	 * @return the given event.
	 * @see #callEvent(Event)
	 */
	public <T extends Event> T callEvent(T event, EventBroadcast broadcast) {
		//Removes invalid events
		registeredListeners.removeIf(target -> !target.isReferenceValid());
		//Sets the caller.
		event.setCaller(broadcast);
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

	/**
	 * Removes all listeners from this broadcast.
	 */
	public void clear() {
		registeredListeners.clear();
	}

}
