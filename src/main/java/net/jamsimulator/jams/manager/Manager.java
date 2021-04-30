package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.utils.Validate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents an storage for instances of the selected type.
 * You can add, remove and get elements from this manager.
 * <p>
 * Elements stored by a manager must implement {@link Labeled}.
 * <p>
 * Every addition will call a register event, and every removal will call a unregister event.
 * <p>
 * Instances of this class are used to manage the storage of several kind of elements inside JAMS.
 *
 * @param <Type> the type of the managed elements.
 * @see DefaultValuableManager
 * @see SelectableManager
 */
public abstract class Manager<Type extends Labeled> extends HashSet<Type> implements EventBroadcast {

	/**
	 * The event broadcast of the manager. This allows the manager to call events.
	 */
	protected final SimpleEventBroadcast broadcast;

	protected final Function<Type, Event> beforeRegisterEventBuilder;
	protected final Function<Type, Event> afterRegisterEventBuilder;
	protected final Function<Type, Event> afterUnregisterEventBuilder;
	protected final Function<Type, Event> beforeUnregisterEventBuilder;

	/**
	 * Creates the manager.
	 * These managers call events on addition and removal. You must provide the builder for these events.
	 *
	 * @param beforeRegisterEventBuilder   the builder that creates the event called before an element is added.
	 * @param afterRegisterEventBuilder    the builder that creates the event called after an element is added.
	 * @param afterUnregisterEventBuilder  the builder that creates the event called before an element is removed.
	 * @param beforeUnregisterEventBuilder the builder that creates the event called after an element is added.
	 */
	public Manager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
				   Function<Type, Event> beforeUnregisterEventBuilder, Function<Type, Event> afterUnregisterEventBuilder) {

		this.broadcast = new SimpleEventBroadcast();
		this.beforeRegisterEventBuilder = beforeRegisterEventBuilder;
		this.afterRegisterEventBuilder = afterRegisterEventBuilder;
		this.beforeUnregisterEventBuilder = beforeUnregisterEventBuilder;
		this.afterUnregisterEventBuilder = afterUnregisterEventBuilder;
		loadDefaultElements();
	}

	/**
	 * Returns the elements that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the element, if present.
	 */
	public Optional<Type> get(String name) {
		return stream().filter(t -> t.getName().equals(name)).findAny();
	}

	/**
	 * Returns the element that matched the given name.
	 * If the elements is not present in this manager, this method returns null.
	 * <p>
	 * For a safe-return method, use {@link #get(String)}.
	 *
	 * @param name the name.
	 * @return the element, or null if not present.
	 */
	public Type getOrNull(String name) {
		return get(name).orElse(null);
	}


	/**
	 * Attempts to register the given element.
	 * If the element is null this method throws an {@link NullPointerException}.
	 * If the element is already present or the register event is cancelled this method returns {@link false}.
	 * If the operation was successful the method returns {@link true}.
	 *
	 * @param element the element to register.
	 * @return whether the operation was successful.
	 * @throws NullPointerException when the given element is null.
	 * @see HashSet#add(Object)
	 */
	@Override
	public boolean add(Type element) {
		Validate.notNull(element, "The element cannot be null!");
		var before = callEvent(beforeRegisterEventBuilder.apply(element));
		if (before instanceof Cancellable && ((Cancellable) before).isCancelled()) return false;
		if (super.add(element)) {
			onElementAddition(element);
			callEvent(afterRegisterEventBuilder.apply(element));
			return true;
		}
		return false;
	}

	/**
	 * This method is called when the {@link #add(Labeled)}} execution was successful,
	 * just before the after event is called.
	 * <p>
	 * You can override this method to perform any extra operation when a element is added.
	 *
	 * @param type the element that was added.
	 */
	protected void onElementAddition(Type type) {
	}


	/**
	 * Attempts to unregister the given element.
	 * If the element is null this method throws a {@link NullPointerException}.
	 * If the element type is invalid this method returns {@link false}.
	 * If the element is not present or the unregister event is cancelled this method returns {@link false}.
	 * If the operation was successful the method returns {@link true}.
	 *
	 * @param o the element to unregister.
	 * @return whether the operation was successful.
	 * @throws NullPointerException when the given element is null.
	 * @see HashSet#remove(Object)
	 */
	@Override
	public boolean remove(Object o) {
		Validate.notNull(o, "The element cannot be null!");
		try {
			var before = callEvent(beforeUnregisterEventBuilder.apply((Type) o));
			if (before instanceof Cancellable && ((Cancellable) before).isCancelled()) return false;
			if (super.remove(o)) {
				onElementRemoval((Type) o);
				callEvent(afterUnregisterEventBuilder.apply((Type) o));
				return true;
			}
			return false;
		} catch (ClassCastException ex) {
			return false;
		}
	}

	/**
	 * This method is called when the {@link #remove(Object)}} execution was successful,
	 * just before the after event is called.
	 * <p>
	 * You can override this method to perform any extra operation when a element is removed.
	 *
	 * @param type the element that was added.
	 */
	protected void onElementRemoval(Type type) {
	}

	/**
	 * This method is called on construction.
	 * Implement this method to add all default values of this manager.
	 */
	protected abstract void loadDefaultElements();

	//region BROADCAST

	@Override
	public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
		return broadcast.registerListener(instance, method, useWeakReferences);
	}

	@Override
	public int registerListeners(Object instance, boolean useWeakReferences) {
		return broadcast.registerListeners(instance, useWeakReferences);
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		return broadcast.unregisterListener(instance, method);
	}

	@Override
	public int unregisterListeners(Object instance) {
		return broadcast.unregisterListeners(instance);
	}

	@Override
	public <T extends Event> T callEvent(T event) {
		return broadcast.callEvent(event, this);
	}

	//endregion
}
