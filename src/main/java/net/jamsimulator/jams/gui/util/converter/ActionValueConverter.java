package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;

import java.util.Optional;

public class ActionValueConverter extends ValueConverter<Action> {

	public static final String NAME = "action";

	@Override
	public String toString(Action value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Optional<Action> fromStringSafe(String value) {
		return JamsApplication.getActionManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return Action.class;
	}
}
