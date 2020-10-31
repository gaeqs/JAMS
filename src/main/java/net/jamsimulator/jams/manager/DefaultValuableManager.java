package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.utils.Validate;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class DefaultValuableManager<Type extends Labeled> extends Manager<Type> {


	protected final BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder;
	protected final BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder;

	protected Type defaultValue;

	public DefaultValuableManager(Function<Type, Event> beforeRegisterEventBuilder, Function<Type, Event> afterRegisterEventBuilder,
								  Function<Type, Event> beforeUnregisterEventBuilder, Function<Type, Event> afterUnregisterEventBuilder,
								  BiFunction<Type, Type, Event> beforeSelectDefaultEventBuilder, BiFunction<Type, Type, Event> afterSelectDefaultEventBuilder) {
		super(beforeRegisterEventBuilder, afterRegisterEventBuilder, afterUnregisterEventBuilder, beforeUnregisterEventBuilder);

		this.beforeSelectDefaultEventBuilder = beforeSelectDefaultEventBuilder;
		this.afterSelectDefaultEventBuilder = afterSelectDefaultEventBuilder;
		this.defaultValue = loadDefaultElement();
	}

	public Type getDefault() {
		return defaultValue;
	}

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

	protected abstract Type loadDefaultElement();
}
