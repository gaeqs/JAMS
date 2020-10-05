package net.jamsimulator.jams.gui.util.converter;

import java.util.Optional;

public class BooleanValueConverter extends ValueConverter<Boolean> {

	public static final String NAME = "boolean";

	@Override
	public String toString(Boolean value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Optional<Boolean> fromStringSafe(String value) {
		return Optional.of(Boolean.valueOf(value));
	}

	@Override
	public Class<?> conversionClass() {
		return Boolean.class;
	}
}
