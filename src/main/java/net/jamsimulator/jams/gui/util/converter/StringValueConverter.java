package net.jamsimulator.jams.gui.util.converter;

import java.util.Optional;

public class StringValueConverter extends ValueConverter<String> {

	public static final String NAME = "string";

	@Override
	public String toString(String value) {
		return value;
	}

	@Override
	public Optional<String> fromStringSafe(String value) {
		return Optional.of(value);
	}

	@Override
	public Class<?> conversionClass() {
		return String.class;
	}
}
