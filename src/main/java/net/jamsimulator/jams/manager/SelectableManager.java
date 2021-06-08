package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a {@link Manager} that has a default value and a selected value.
 * <p>
 * The default value can be changed using {@link #setDefault(Labeled)}.
 * The selected value can be changed using {@link #setSelected(Labeled)}.
 * Implement {@link #loadDefaultElement()} to define the default value when the manager is created.
 * Implement {@link #loadSelectedElement()} to define the selected value when the manager is created.
 *
 * @param <Type> the type of the managed elements.
 * @see Manager
 * @see DefaultValuableManager
 */
public abstract class SelectableManager<Type extends Labeled> extends DefaultValuableManager<Type> {


	protected final BiFunction<Type, Type, Event> beforeSelectSelectedEventBuilder;
	protected final BiFunction<Type, Type, Event> afterSelectSelectedEventBuilder;

	protected Type selected;

	/**
	 * Creates the manager.
	 * These managers call events on addition, removal and default set. You must provide the builder for these events.
	 *
	 * @param beforeRegisterEventBuilder       the builder that creates the event called before an element is added.
	 * @param afterRegisterEventBuilder        the builder that creates the event called after an element is added.
	 * @param afterUnregisterEventBuilder      the builder that creates the event called before an element is removed.
	 * @param beforeUnregisterEventBuilder     the builder that creates the event called after an element is added.
	 * @param beforeSelectDefaultEventBuilder  the builder that creates the event called before the default element is changed.
	 * @param afterSelectDefaultEventBuilder   the builder that creates the event called after the default element is changed.
	 * @param beforeSelectSelectedEventBuilder the builder that creates the event called before the selected element is changed.
	 * @param afterSelectSelectedEventBuilder  the builder that creates the event called after the selected element is changed.
	 */
	public SelectableManager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
							 Function<Type, Event> beforeUnregisterEventBuilder, Function<Type, Event> afterUnregisterEventBuilder,
							 BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder, BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder,
							 BiFunction<Type, Type, Event> beforeSelectSelectedEventBuilder, BiFunction<Type, Type, Event> afterSelectSelectedEventBuilder) {
		super(beforeRegisterEventBuilder, afterRegisterEventBuilder,
				beforeUnregisterEventBuilder, afterUnregisterEventBuilder,
				beforeSelectDefaultEventBuilder, afterSelectDefaultEventBuilder);
		this.beforeSelectSelectedEventBuilder = beforeSelectSelectedEventBuilder;
		this.afterSelectSelectedEventBuilder = afterSelectSelectedEventBuilder;
		this.selected = loadSelectedElement();
	}

	/**
	 * Returns the selected element of this manager.
	 * <p>
	 * This element cannot be removed from the manager.
	 *
	 * @return the selected element.
	 */
	public Type getSelected() {
		return selected;
	}

	/**
	 * Sets the selected element of this manager.
	 * <p>
	 * If the given element is null, this method throws a {@link NullPointerException}.
	 * If the given element is not present in this manager, this method throws an {@link IllegalArgumentException}.
	 * <p>
	 * If the element is already the default value or the change event is cancelled, this method returns {@code false}.
	 * If the operation was successful, this method returns {@code true}.
	 *
	 * @param selected the new selected element.
	 * @return whether the operation was successful.
	 */
	public boolean setSelected(Type selected) {
		Validate.notNull(selected, "The default value cannot be null!");
		Validate.isTrue(contains(selected), "The default value must be registered!");

		if (selected == this.selected) return false;

		var before = callEvent(beforeSelectSelectedEventBuilder.apply(this.selected, selected));
		if (before instanceof Cancellable && ((Cancellable) before).isCancelled()) return false;

		var old = this.selected;
		this.selected = selected;

		callEvent(afterSelectSelectedEventBuilder.apply(old, selected));
		return true;
	}

	/**
	 * This method is called on construction.
	 * Implement this method to define the selected value of this manager.
	 *
	 * @return the selected element of this manager.
	 */
	protected abstract Type loadSelectedElement();
}
