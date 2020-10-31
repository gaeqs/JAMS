package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a {@link Manager} that has a default value.
 * <p>
 * The default value can be changed using {@link #setDefault(Labeled)}.
 * Implement {@link #loadDefaultElement()} to define the default value when the manager is created.
 *
 * @param <Type> the type of the managed elements.
 * @see Manager
 */
public abstract class DefaultValuableManager<Type extends Labeled> extends Manager<Type> {


	protected final BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder;
	protected final BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder;

	protected Type defaultValue;

	/**
	 * Creates the manager.
	 * These managers call events on addition, removal and default set. You must provide the builder for these events.
	 *
	 * @param beforeRegisterEventBuilder      the builder that creates the event called before an element is added.
	 * @param afterRegisterEventBuilder       the builder that creates the event called after an element is added.
	 * @param afterUnregisterEventBuilder     the builder that creates the event called before an element is removed.
	 * @param beforeUnregisterEventBuilder    the builder that creates the event called after an element is added.
	 * @param beforeSelectDefaultEventBuilder the builder that creates the event called before the default element is changed.
	 * @param afterSelectDefaultEventBuilder  the builder that creates the event called after the default element is changed.
	 */
	public DefaultValuableManager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
								  Function<Type, Event> beforeUnregisterEventBuilder, Function<Type, Event> afterUnregisterEventBuilder,
								  BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder, BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder) {
		super(beforeRegisterEventBuilder, afterRegisterEventBuilder, afterUnregisterEventBuilder, beforeUnregisterEventBuilder);

		this.beforeSelectDefaultEventBuilder = beforeSelectDefaultEventBuilder;
		this.afterSelectDefaultEventBuilder = afterSelectDefaultEventBuilder;
		this.defaultValue = loadDefaultElement();
	}

	/**
	 * Returns the default element of this manager.
	 * <p>
	 * This element cannot be removed from the manager.
	 *
	 * @return the default element.
	 */
	public Type getDefault() {
		return defaultValue;
	}

	/**
	 * Sets the default element of this manager.
	 * <p>
	 * If the given element is null, this method throws a {@link NullPointerException}.
	 * If the given element is not present in this manager, this method throws an {@link IllegalArgumentException}.
	 * <p>
	 * If the element is already the default value or the change event is cancelled, this method returns {@code false}.
	 * If the operation was successful, this method returns {@link true}.
	 *
	 * @param defaultValue the new default element.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(Type defaultValue) {
		Validate.notNull(defaultValue, "The default value cannot be null!");
		Validate.isTrue(contains(defaultValue), "The default value must be registered!");

		if (defaultValue == this.defaultValue) return false;

		var before = callEvent(beforeSelectDefaultEventBuilder.apply(defaultValue, this.defaultValue));
		if (before instanceof Cancellable && ((Cancellable) before).isCancelled()) return false;

		var old = this.defaultValue;
		this.defaultValue = defaultValue;

		callEvent(afterSelectDefaultEventBuilder.apply(old, defaultValue));
		return true;
	}

	/**
	 * Attempts to unregister the given element.
	 * <p>
	 * If the element is null this method throws a {@link NullPointerException}.
	 * If the element type is invalid this method returns {@link false}.
	 * If the element is not present or the unregister event is cancelled this method returns {@link false}.
	 * If the default element equals to the given element this method returns {@link false} and does nothing.
	 * If the operation was successful the method returns {@link true}.
	 *
	 * @param o the element to register.
	 * @return whether the operation was successful.
	 * @throws NullPointerException when the given element is null.
	 * @see HashSet#add(Object)
	 */
	@Override
	public boolean remove(Object o) {
		if (defaultValue.equals(o)) return false;
		return super.remove(o);
	}

	/**
	 * This method is called on construction.
	 * Implement this method to define the default value of this manager.
	 *
	 * @return the default element of this manager.
	 */
	protected abstract Type loadDefaultElement();
}
