package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

public abstract class Manager<Type extends Labeled> extends HashSet<Type> implements EventBroadcast {

	protected final SimpleEventBroadcast broadcast;

	protected final Function<Type, Event> beforeRegisterEventBuilder;
	protected final Function<Type, Event> afterRegisterEventBuilder;
	protected final Function<Type, Event> afterUnregisterEventBuilder;
	protected final Function<Type, Event> beforeUnregisterEventBuilder;

	public Manager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
				   Function<Type, Event> afterUnregisterEventBuilder, Function<Type, Event> beforeUnregisterEventBuilder) {

		this.broadcast = new SimpleEventBroadcast();
		this.beforeRegisterEventBuilder = beforeRegisterEventBuilder;
		this.afterRegisterEventBuilder = afterRegisterEventBuilder;
		this.afterUnregisterEventBuilder = afterUnregisterEventBuilder;
		this.beforeUnregisterEventBuilder = beforeUnregisterEventBuilder;
		loadDefaultElements();
	}

	public Optional<Type> get(String name) {
		return stream().filter(t -> t.getName().equals(name)).findAny();
	}

	public Type getOrNull(String name) {
		return get(name).orElse(null);
	}

	@Override
	public boolean add(Type type) {
		if (type == null) return false;
		var before = callEvent(beforeRegisterEventBuilder.apply(type));
		if (before instanceof Cancellable && ((Cancellable) before).isCancelled()) return false;
		if (super.add(type)) {
			onElementAddition(type);
			callEvent(afterRegisterEventBuilder.apply(type));
			return true;
		}
		return false;
	}

	protected void onElementAddition(Type type) {
	}

	@Override
	public boolean remove(Object o) {
		if (o == null) return false;
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

	protected void onElementRemoval(Type type) {
	}

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
