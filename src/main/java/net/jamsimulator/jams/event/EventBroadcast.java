package net.jamsimulator.jams.event;

import java.lang.reflect.Method;

/**
 * Represents an event broadcast. An event broadcast allows to send
 * events to all the registered {@link Listener} the broadcast has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method)} or {@link #registerListeners(Object)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 */
public interface EventBroadcast {

	/**
	 * Registers a listener of an instance. A listener is a non-static method with
	 * only one parameter. This parameter must be a {@link Event} or any subclass.
	 * The method must also have one {@link Listener} annotation.
	 *
	 * @param instance the instance.
	 * @param method   the listener.
	 * @return true whether the listener was registered.
	 */
	boolean registerListener(Object instance, Method method);

	/**
	 * Registers all listeners of an instance. See {@link #registerListener(Object, Method)} for
	 * more information.
	 *
	 * @param instance the instance.
	 * @return the amount of registered listeners.
	 * @see #registerListener(Object, Method)
	 */
	int registerListeners(Object instance);

	/**
	 * Unregisters a listener of an instance.
	 *
	 * @param instance the instance.
	 * @param method   the listener.
	 * @return whether the listener was unregistered.
	 */
	boolean unregisterListener(Object instance, Method method);

	/**
	 * Unregisters all listeners of an instance.
	 *
	 * @param instance the instance.
	 * @return the amount of unregistered listeners.
	 */
	int unregisterListeners(Object instance);

	/**
	 * Calls all listeners compatible with the given {@link Event}.
	 *
	 * @param event the {@link Event}.
	 * @return the {@link Event}.
	 */
	<T extends Event> T callEvent(T event);
}
