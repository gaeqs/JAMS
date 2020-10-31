package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class SelectableManager<Type extends Labeled> extends DefaultValuableManager<Type> {


	protected final BiFunction<Type, Type, Event> beforeSelectSelectedEventBuilder;
	protected final BiFunction<Type, Type, Event> afterSelectSelectedEventBuilder;

	protected Type selected;

	public SelectableManager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
							 Function<Type, Event> afterUnregisterEventBuilder, Function<Type, Event> beforeUnregisterEventBuilder,
							 BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder, BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder,
							 BiFunction<Type, Type, Event> beforeSelectSelectedEventBuilder, BiFunction<Type, Type, Event> afterSelectSelectedEventBuilder) {
		super(beforeRegisterEventBuilder, afterRegisterEventBuilder,
				beforeUnregisterEventBuilder, afterUnregisterEventBuilder,
				beforeSelectDefaultEventBuilder, afterSelectDefaultEventBuilder);
		this.beforeSelectSelectedEventBuilder = beforeSelectSelectedEventBuilder;
		this.afterSelectSelectedEventBuilder = afterSelectSelectedEventBuilder;
		this.selected = loadSelectedElement();
	}

	public Type getSelected() {
		return selected;
	}

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

	protected abstract Type loadSelectedElement();
}
